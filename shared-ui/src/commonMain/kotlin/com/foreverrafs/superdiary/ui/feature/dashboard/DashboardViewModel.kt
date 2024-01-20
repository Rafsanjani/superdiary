package com.foreverrafs.superdiary.ui.feature.dashboard

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.foreverrafs.superdiary.diary.diaryai.DiaryAI
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.model.Streak
import com.foreverrafs.superdiary.diary.model.WeeklySummary
import com.foreverrafs.superdiary.diary.usecase.AddWeeklySummaryUseCase
import com.foreverrafs.superdiary.diary.usecase.CalculateStreakUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetWeeklySummaryUseCase
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class DashboardViewModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val calculateStreakUseCase: CalculateStreakUseCase,
    private val addWeeklySummaryUseCase: AddWeeklySummaryUseCase,
    private val getWeeklySummaryUseCase: GetWeeklySummaryUseCase,
    private val diaryAI: DiaryAI,
) :
    StateScreenModel<DashboardViewModel.DashboardScreenState>(DashboardScreenState.Loading) {
    sealed interface DashboardScreenState {
        data object Loading : DashboardScreenState
        data class Content(
            val latestEntries: List<Diary>,
            val totalEntries: Long,
            val weeklySummary: String?,
            val streak: Streak,
        ) : DashboardScreenState
    }

    fun loadDashboardContent() = screenModelScope.launch {
        Logger.i(Tag) {
            "Loading dashboard content"
        }
        getAllDiariesUseCase().catch {
            Logger.e(Tag, it) {
                "Error Loading dashboard content"
            }
            mutableState.update {
                DashboardScreenState.Loading
            }
        }.collect { diaries ->
            Logger.i(Tag) {
                "Dashboard content refreshed!"
            }
            mutableState.update {
                DashboardScreenState.Content(
                    latestEntries = diaries.sortedByDescending { it.date }.take(4),
                    totalEntries = diaries.size.toLong(),
                    weeklySummary = if (diaries.isEmpty()) {
                        """
                            In this panel, your weekly diary entries will be summarized.
                            Add your first entry to see how it works
                        """.trimIndent()
                    } else {
                        DEFAULT_SUMMARY_TEXT
                    },
                    streak = Streak(0, emptyList()),
                )
            }

            if (diaries.isNotEmpty()) {
                generateWeeklySummary(diaries)
                calculateStreak(diaries)
            }
        }
    }

    private fun updateContentState(func: (current: DashboardScreenState.Content) -> DashboardScreenState.Content) {
        mutableState.update { state ->
            val currentState = state as? DashboardScreenState.Content

            if (currentState != null) {
                val newState = func(currentState)
                Logger.d(Tag) {
                    "Updating app state: $newState"
                }
                newState
            } else {
                Logger.d(Tag) {
                    "Current State isn't Content. Not updating!"
                }
                state
            }
        }
    }

    private fun generateWeeklySummary(diaries: List<Diary>) = screenModelScope.launch {
        Logger.i(Tag) {
            "Fetching weekly summary for ${diaries.size} entries"
        }
        val latestWeeklySummary = getWeeklySummaryUseCase()

        latestWeeklySummary?.let {
            val difference = Clock.System.now() - latestWeeklySummary.date

            if (difference.inWholeDays <= 7L) {
                updateContentState { currentState ->
                    currentState.copy(weeklySummary = latestWeeklySummary.summary)
                }
                return@launch
            }
        }

        diaryAI.getWeeklySummary(diaries)
            .catch { exception ->
                Logger.e(Tag, exception) {
                    "An error occurred generating weekly summary"
                }
            }.onCompletion {
                (mutableState.value as? DashboardScreenState.Content)?.let { appState ->
                    if (appState.weeklySummary == DEFAULT_SUMMARY_TEXT) {
                        updateContentState { currentState ->
                            currentState.copy(weeklySummary = "Error generating weekly summary")
                        }
                        return@onCompletion
                    }

                    Logger.d(Tag) {
                        "Weekly summary generated!"
                    }
                    appState.weeklySummary?.let { summary ->
                        addWeeklySummaryUseCase(
                            weeklySummary = WeeklySummary(summary),
                        )
                    }
                }
            }.collect { chunk ->
                updateContentState { state ->
                    state.copy(
                        weeklySummary = if (state.weeklySummary == DEFAULT_SUMMARY_TEXT) {
                            chunk
                        } else {
                            state.weeklySummary + chunk
                        },
                    )
                }
            }
    }

    private fun calculateStreak(diaries: List<Diary>) = screenModelScope.launch {
        Logger.i(Tag) {
            "Calculating streak for ${diaries.size} entries"
        }
        val streak = calculateStreakUseCase(diaries)

        mutableState.update { state ->
            (state as? DashboardScreenState.Content)?.copy(
                streak = streak,
            ) ?: state
        }
    }

    companion object {
        private const val DEFAULT_SUMMARY_TEXT = "Generating weekly Summary..."
        private val Tag = DashboardViewModel::class.simpleName.orEmpty()
    }
}
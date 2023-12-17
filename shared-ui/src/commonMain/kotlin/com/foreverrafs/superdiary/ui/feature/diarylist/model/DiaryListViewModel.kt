package com.foreverrafs.superdiary.ui.feature.diarylist.model

import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.usecase.DeleteMultipleDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.usecase.UpdateDiaryUseCase
import com.foreverrafs.superdiary.diary.utils.toInstant
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

class DiaryListViewModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
    private val deleteMultipleDiariesUseCase: DeleteMultipleDiariesUseCase,
    private val updateDiaryUseCase: UpdateDiaryUseCase,
) : StateScreenModel<DiaryListViewState>(DiaryListViewState.Loading) {

    fun observeDiaries() = screenModelScope.launch {
        mutableState.update {
            DiaryListViewState.Loading
        }

        getAllDiariesUseCase()
            .catch { error ->
                mutableState.update {
                    DiaryListViewState.Error(error)
                }
            }
            .collect { diaries ->
                mutableState.update {
                    DiaryListViewState.Content(
                        diaries = diaries,
                        filtered = false,
                    )
                }
            }
    }

    fun filterByEntry(entry: String) = screenModelScope.launch {
        searchDiaryByEntryUseCase.invoke(entry).collect { diaries ->
            mutableState.update {
                DiaryListViewState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDate(date: LocalDate) = screenModelScope.launch {
        searchDiaryByDateUseCase.invoke(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListViewState.Content(
                    diaries = diaries,
                    filtered = true,
                )
            }
        }
    }

    fun filterByDateAndEntry(date: LocalDate, entry: String) = screenModelScope.launch {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListViewState.Content(
                    diaries = diaries.filter { it.entry.contains(entry, false) },
                    filtered = true,
                )
            }
        }
    }

    suspend fun deleteDiaries(diaries: List<Diary>): Boolean {
        val affectedRows = deleteMultipleDiariesUseCase(diaries)
        return affectedRows == diaries.size
    }

    suspend fun toggleFavorite(diary: Diary): Boolean {
        return updateDiaryUseCase(
            diary.copy(
                isFavorite = !diary.isFavorite,
            ),
        )
    }
}

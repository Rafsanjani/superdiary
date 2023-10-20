package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.StateScreenModel
import cafe.adriel.voyager.core.model.coroutineScope
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.foreverrafs.superdiary.diary.usecase.GetAllDiariesUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByDateUseCase
import com.foreverrafs.superdiary.diary.usecase.SearchDiaryByEntryUseCase
import com.foreverrafs.superdiary.diary.utils.toInstant
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryScreen
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

object DiaryListTab : Screen {

    @Composable
    override fun Content() {
        val screenModel: DiaryListScreenModel = getScreenModel()
        val screenState by screenModel.state.collectAsState()
        var diaryFilters by remember {
            mutableStateOf(DiaryFilters())
        }

        LaunchedEffect(diaryFilters) {
            // Filter by entry only
            if (diaryFilters.entry.isNotEmpty() && diaryFilters.date == null) {
                screenModel.filterByEntry(diaryFilters.entry)
                return@LaunchedEffect
            }

            // Filter by date only
            if (diaryFilters.date != null && diaryFilters.entry.isEmpty()) {
                screenModel.filterByDate(diaryFilters.date!!)
                return@LaunchedEffect
            }

            // Filter by both date and entry
            if (diaryFilters.date != null && diaryFilters.entry.isNotEmpty()) {
                screenModel.filterByDateAndEntry(diaryFilters.date!!, diaryFilters.entry)
                return@LaunchedEffect
            }

            // No filter applied
            screenModel.observeDiaries()
        }

        val navigator = LocalNavigator.currentOrThrow

        DiaryListScreen(
            modifier = Modifier
                .fillMaxSize(),
            state = screenState,
            onAddEntry = {
                navigator.push(
                    CreateDiaryScreen,
                )
            },
            onApplyFilters = { filters ->
                diaryFilters = filters
            },
            diaryFilters = diaryFilters,
        )
    }
}

class DiaryListScreenModel(
    private val getAllDiariesUseCase: GetAllDiariesUseCase,
    private val searchDiaryByEntryUseCase: SearchDiaryByEntryUseCase,
    private val searchDiaryByDateUseCase: SearchDiaryByDateUseCase,
) : StateScreenModel<DiaryListScreenState>(DiaryListScreenState.Loading) {

    init {
        observeDiaries()
    }

    fun observeDiaries() = coroutineScope.launch {
        getAllDiariesUseCase().collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(diaries)
            }
        }
    }

    fun filterByEntry(entry: String) = coroutineScope.launch {
        searchDiaryByEntryUseCase(entry).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(diaries)
            }
        }
    }

    fun filterByDate(date: LocalDate) = coroutineScope.launch {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(diaries)
            }
        }
    }

    fun filterByDateAndEntry(date: LocalDate, entry: String) = coroutineScope.launch {
        searchDiaryByDateUseCase(date.toInstant()).collect { diaries ->
            mutableState.update {
                DiaryListScreenState.Content(
                    diaries.filter { it.entry.contains(entry, false) },
                )
            }
        }
    }
}

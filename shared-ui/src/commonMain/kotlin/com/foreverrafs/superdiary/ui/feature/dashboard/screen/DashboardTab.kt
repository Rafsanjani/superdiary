package com.foreverrafs.superdiary.ui.feature.dashboard.screen

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.navigation.AppRoute
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DashboardTab(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState,
) {
    val screenModel: DashboardViewModel = koinViewModel()
    val screenState by screenModel.state.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    val settings by screenModel.settings.collectAsStateWithLifecycle(initialValue = null)

    settings?.let {
        DashboardScreenContent(
            state = screenState,
            onAddEntry = {
                navController.navigate(AppRoute.CreateDiaryScreen)
            },
            onSeeAll = {
                navController.navigate(AppRoute.DiaryListScreen)
            },
            onToggleFavorite = {
                coroutineScope.launch {
                    if (screenModel.toggleFavorite(it)) {
                        snackbarHostState.showSnackbar("Favorite Updated")
                    }
                }
            },
            settings = it,
            onChangeSettings = screenModel::updateSettings,
            onDiaryClick = { diary ->
                diary.id?.let {
                    navController.navigate(
                        AppRoute.DetailScreen(it.toString()),
                    )
                }
            },
        )
    }
}

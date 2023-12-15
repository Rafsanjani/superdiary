package com.foreverrafs.superdiary.ui.feature.diarychat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.VectorPainter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.tab.TabOptions
import com.foreverrafs.superdiary.ui.SuperDiaryScreen

object DiaryChatScreen : SuperDiaryScreen {
    @Composable
    override fun Content() {
        val screenModel = getScreenModel<DiaryChatScreenModel>()
        val screenState by screenModel.state.collectAsState()

        DiaryChatScreenContent(
            screenState = screenState,
            onQueryDiaries = screenModel::queryDiaries,
        )
    }

    override val selectedIcon: VectorPainter
        @Composable
        get() = rememberVectorPainter(Icons.Filled.Chat)
    override val options: TabOptions
        @Composable
        get() = TabOptions(
            index = 4u,
            title = "Diary AI",
            icon = rememberVectorPainter(Icons.Outlined.Chat),
        )
}

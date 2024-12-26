package com.foreverrafs.superdiary.ui.navigation

import com.foreverrafs.auth.model.UserInfo
import kotlinx.serialization.Serializable

sealed interface AppRoute {
    @Serializable
    data object LoginScreen : AppRoute

    @Serializable
    data object RegisterScreen : AppRoute

    @Serializable
    data object RegistrationConfirmationScreen : AppRoute

    @Serializable
    data class BottomNavigationScreen(
        val userInfo: UserInfo?,
    ) : AppRoute

    @Serializable
    data class DetailScreen(val diaryId: String) : AppRoute

    @Serializable
    data object CreateDiaryScreen : AppRoute

    @Serializable
    data object DiaryListScreen : AppRoute
}

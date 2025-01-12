package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.auth.di.authModule
import com.foreverrafs.superdiary.ai.di.diaryAiModule
import com.foreverrafs.superdiary.common.utils.di.utilsModule
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.location.di.locationModule
import com.foreverrafs.superdiary.core.location.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.di.platformModule
import com.foreverrafs.superdiary.di.useCaseModule
import com.foreverrafs.superdiary.profile.di.profileModule
import com.foreverrafs.superdiary.ui.AppViewModel
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.ui.feature.auth.register.DeeplinkContainer
import com.foreverrafs.superdiary.ui.feature.auth.register.RegisterScreenViewModel
import com.foreverrafs.superdiary.ui.feature.auth.reset.PasswordResetViewModel
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal val screensModule: Module = module {
    factoryOf(::LocationPermissionManager)
    singleOf(::DeeplinkContainer)

    viewModelOf(::DiaryListViewModel)
    viewModelOf(::CreateDiaryViewModel)
    viewModelOf(::FavoriteViewModel)
    viewModelOf(::DiaryChatViewModel)
    viewModelOf(::RegisterScreenViewModel)
    viewModelOf(::DetailsViewModel)
    viewModelOf(::LoginScreenViewModel)
    viewModelOf(::AppViewModel)
    viewModelOf(::PasswordResetViewModel)
    viewModelOf(::DashboardViewModel)
}

expect fun permissionModule(): Module

/** This is the only component that is exposed outside of this module */
fun compositeModule(
    analytics: AnalyticsTracker,
    logger: AggregateLogger,
): List<Module> = listOf(
    utilsModule,
    locationModule(),
    useCaseModule,
    permissionModule(),
    screensModule,
    platformModule(analyticsTracker = analytics, aggregateLogger = logger),
    authModule(),
    diaryAiModule,
    profileModule,
)

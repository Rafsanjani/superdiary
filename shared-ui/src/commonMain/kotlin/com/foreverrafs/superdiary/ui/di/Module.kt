package com.foreverrafs.superdiary.ui.di

import com.foreverrafs.auth.di.authModule
import com.foreverrafs.superdiary.core.analytics.AnalyticsTracker
import com.foreverrafs.superdiary.core.location.di.locationModule
import com.foreverrafs.superdiary.core.location.permission.LocationPermissionManager
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.di.utilsModule
import com.foreverrafs.superdiary.data.di.platformModule
import com.foreverrafs.superdiary.data.di.useCaseModule
import com.foreverrafs.superdiary.ui.AppViewModel
import com.foreverrafs.superdiary.ui.feature.auth.login.LoginScreenViewModel
import com.foreverrafs.superdiary.ui.feature.creatediary.CreateDiaryViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewModel
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.model.DiaryListViewModel
import com.foreverrafs.superdiary.ui.feature.favorites.FavoriteViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

internal fun screenModules(): Module = module {
    factoryOf(::LocationPermissionManager)
    factoryOf(::DiaryListViewModel)
    factoryOf(::CreateDiaryViewModel)
    factoryOf(::FavoriteViewModel)
    factoryOf(::DashboardViewModel)
    factoryOf(::DiaryChatViewModel)
    factoryOf(::DetailsViewModel)
    factoryOf(::LoginScreenViewModel)
    factoryOf(::AppViewModel)
}

expect fun permissionModule(): Module

/** This is the only component that is exposed outside of this module */
fun compositeModule(
    analytics: AnalyticsTracker,
    logger: AggregateLogger,
): Module = module {
    includes(
        utilsModule(),
        locationModule(),
        useCaseModule(),
        permissionModule(),
        screenModules(),
        platformModule(analyticsTracker = analytics, aggregateLogger = logger),
        authModule(),
    )
}

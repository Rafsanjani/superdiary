package com.foreverrafs.superdiary.ui // ktlint-disable filename

import androidx.compose.ui.window.singleWindowApplication
import com.foreverrafs.superdiary.diary.Database
import com.foreverrafs.superdiary.ui.inject.appModule
import com.foreverrafs.superdiary.ui.style.AppTheme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin

fun main() = singleWindowApplication {
    startKoin {
        modules(appModule())
    }
    initDatabase()

    AppTheme {
        App()
    }
}

fun initDatabase() = object : KoinComponent {
    private val database: Database by inject()

    init {
        database.createDatabase()
    }
}

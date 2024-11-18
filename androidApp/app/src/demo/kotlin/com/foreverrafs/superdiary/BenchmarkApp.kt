package com.foreverrafs.superdiary

import android.app.Application
import com.foreverrafs.auth.AuthApi
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.logging.KermitLogger
import com.foreverrafs.superdiary.core.logging.SentryLogger
import com.foreverrafs.superdiary.data.androidFilesDirectory
import com.foreverrafs.superdiary.ui.di.compositeModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

class BenchmarkApp : Application() {

    override fun onCreate() {
        super.onCreate()
        initializeKoin()
    }

    private fun initializeKoin() {
        startKoin {
            androidContext(this@BenchmarkApp)

            androidFilesDirectory = filesDir

            modules(
                modules = listOf(
                    compositeModule(
                        analytics = AndroidAnalytics(),
                        logger = AggregateLogger(
                            loggers = listOf(
                                SentryLogger(),
                                KermitLogger(),
                            ),
                        ),
                    ),
                    demoAuthModule(),
                ),
            )
        }
    }
}

private fun demoAuthModule(): Module = module {
    factoryOf(::BenchmarkAuth) { bind<AuthApi>() }
}

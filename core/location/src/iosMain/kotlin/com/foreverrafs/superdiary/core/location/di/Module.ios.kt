package com.foreverrafs.superdiary.core.location.di

import com.foreverrafs.superdiary.core.location.AppleLocationManager
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

actual fun locationModule(): Module = module {
    factoryOf(::AppleLocationManager)
}

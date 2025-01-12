package com.foreverrafs.auth.di

import com.foreverrafs.superdiary.core.SuperDiarySecret
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.serializer.KotlinXSerializer
import kotlinx.serialization.json.Json
import org.koin.dsl.module

private fun supabaseModule() = module {
    single<SupabaseClient> {
        createSupabaseClient(
            supabaseUrl = SuperDiarySecret.supabaseUrl,
            supabaseKey = SuperDiarySecret.supabaseKey,
        ) {
            install(Auth) {
                host = "api.nebulainnova.co.uk"
                scheme = "https"
            }
            install(Realtime)
            install(Postgrest) {
                serializer = KotlinXSerializer(
                    json = Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        useAlternativeNames = false
                    },
                )
            }
        }
    }
}

fun authModule() = module {
    includes(
        supabaseModule(),
        platformAuthModule(),
    )
}

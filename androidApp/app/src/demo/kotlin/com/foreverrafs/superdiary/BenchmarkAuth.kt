package com.foreverrafs.superdiary

import com.foreverrafs.auth.AuthApi
import com.foreverrafs.auth.DefaultSupabaseAuth
import com.foreverrafs.auth.model.SessionInfo
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.logging.AggregateLogger
import com.foreverrafs.superdiary.core.utils.ActivityWrapper
import io.github.jan.supabase.SupabaseClient
import kotlin.time.Duration.Companion.minutes
import kotlinx.datetime.Clock

class BenchmarkAuth(client: SupabaseClient, logger: AggregateLogger) :
    AuthApi by DefaultSupabaseAuth(client, logger) {
    private val session = SessionInfo(
        expiresAt = Clock.System.now().plus(10.minutes),
        accessToken = "access-token",
        refreshToken = "refresh-token",
        userInfo = UserInfo(
            id = "12345",
            avatarUrl = "avatarurl",
            name = "John Doe",
            email = "john@doe.com",
        ),
    )

    override suspend fun signIn(username: String, password: String): AuthApi.SignInStatus =
        AuthApi.SignInStatus.LoggedIn(
            sessionInfo = session,
        )

    override suspend fun signInWithGoogle(activityWrapper: ActivityWrapper?): AuthApi.SignInStatus =
        AuthApi.SignInStatus.LoggedIn(
            sessionInfo = session,
        )

    override suspend fun restoreSession(): AuthApi.SignInStatus = AuthApi.SignInStatus.LoggedIn(
        sessionInfo = session,
    )
}

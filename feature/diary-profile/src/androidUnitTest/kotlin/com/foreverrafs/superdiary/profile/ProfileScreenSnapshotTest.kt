package com.foreverrafs.superdiary.profile

import app.cash.paparazzi.Paparazzi
import com.android.ide.common.rendering.api.SessionParams
import com.foreverrafs.common.paparazzi.SnapshotDevice
import com.foreverrafs.superdiary.design.style.SuperDiaryPreviewTheme
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewData
import com.foreverrafs.superdiary.profile.presentation.screen.ProfileScreenContent
import com.foreverrafs.superdiary.utils.DiarySettings
import com.google.testing.junit.testparameterinjector.TestParameter
import com.google.testing.junit.testparameterinjector.TestParameterInjector
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(TestParameterInjector::class)
class ProfileScreenSnapshotTest(
    @TestParameter val snapshotDevice: SnapshotDevice,
) {

    @get:Rule
    val paparazzi = Paparazzi(
        deviceConfig = snapshotDevice.config,
        renderingMode = SessionParams.RenderingMode.NORMAL,
        useDeviceResolution = true,
    )

    @Test
    fun `Profile Screen - Idle`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = false,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                    onUpdateSettings = {},
                    settings = DiarySettings.Empty,
                    onNavigateBack = {},
                )
            }
        }
    }

    @Test
    fun `Profile Screen - Logout dialog visible`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = true,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                    onUpdateSettings = {},
                    settings = DiarySettings.Empty,
                    onNavigateBack = {},
                )
            }
        }
    }

    @Test
    fun `Profile Screen - error message visible`() {
        paparazzi.snapshot {
            SuperDiaryPreviewTheme {
                ProfileScreenContent(
                    viewState = ProfileScreenViewData(
                        name = "Rafsanjani Aziz",
                        email = "foreverrafs@gmail.com",
                        avatarUrl = "",
                        errorMessage = "Something went wrong",
                    ),
                    onConsumeErrorMessage = {},
                    isLogoutDialogVisible = false,
                    onLogout = {},
                    onLogoutDialogVisibilityChange = {},
                    onUpdateSettings = {},
                    settings = DiarySettings.Empty,
                    onNavigateBack = {},
                )
            }
        }
    }
}

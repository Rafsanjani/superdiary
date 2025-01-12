import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.foreverrafs.auth.model.UserInfo
import com.foreverrafs.superdiary.core.location.Location
import com.foreverrafs.superdiary.design.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.design.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewData
import com.foreverrafs.superdiary.profile.presentation.screen.ProfileScreenContent
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginScreenContent
import com.foreverrafs.superdiary.ui.feature.auth.login.screen.LoginViewState
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreenContent
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegisterScreenState
import com.foreverrafs.superdiary.ui.feature.auth.register.screen.RegistrationConfirmationScreen
import com.foreverrafs.superdiary.ui.feature.creatediary.screen.CreateDiaryScreenContent
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.dashboard.screen.DashboardScreenContent
import com.foreverrafs.superdiary.ui.feature.details.DetailsViewState
import com.foreverrafs.superdiary.ui.feature.details.screen.DetailScreenContent
import com.foreverrafs.superdiary.ui.feature.diarychat.DiaryChatViewModel
import com.foreverrafs.superdiary.ui.feature.diarychat.screen.DiaryChatScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryDatePicker
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryList
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListScreenContent
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryListViewState
import com.foreverrafs.superdiary.utils.DiarySettings
import com.foreverrafs.superdiary.utils.toDate
import dev.icerock.moko.permissions.PermissionState
import java.time.LocalDate
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.toKotlinLocalDate

private val diaryListActions = DiaryListActions(
    onAddEntry = {},
    onDeleteDiaries = { true },
    onToggleFavorite = { true },
    onApplyFilters = {},
    onDiaryClicked = {},
)

@Composable
fun TestAppContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    SuperDiaryTheme {
        Scaffold(
            modifier = modifier,
            topBar = {
                SuperDiaryAppBar()
            },
            contentColor = MaterialTheme.colorScheme.background,
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                content()
            }
        }
    }
}

@PreviewSuperDiary
@Composable
private fun ProfileScreenPreview() {
    ProfileScreenContent(
        viewState = ProfileScreenViewData(
            name = "Rafsanjani Aziz",
            email = "foreverrafs@gmail.com",
        ),
        onConsumeErrorMessage = {},
        onLogout = {},
        settings = DiarySettings.Empty,
        onUpdateSettings = {},
        onLogoutDialogVisibilityChange = {},
        isLogoutDialogVisible = false,
        onNavigateBack = {},
    )
}

@PreviewSuperDiary
@Composable
private fun ProfileScreenPreviewLogoutDialog() {
    ProfileScreenContent(
        viewState = ProfileScreenViewData(
            name = "Rafsanjani Aziz",
            email = "foreverrafs@gmail.com",
        ),
        onConsumeErrorMessage = {},
        onLogout = {},
        onLogoutDialogVisibilityChange = {},
        isLogoutDialogVisible = true,
        settings = DiarySettings.Empty,
        onUpdateSettings = {},
        onNavigateBack = {},
    )
}

@Composable
@PreviewSuperDiary
private fun RegistrationConfirmationPreview() {
    SuperDiaryTheme {
        SuperDiaryTheme {
            Surface(color = MaterialTheme.colorScheme.background) {
                RegistrationConfirmationScreen()
            }
        }
    }
}

@PreviewSuperDiary
@Composable
private fun DiaryChatPreview() {
    TestAppContainer {
        DiaryChatScreenContent(
            screenState = DiaryChatViewModel.DiaryChatViewState(
                isResponding = true,
            ),
        )
    }
}

@PreviewSuperDiary
@Composable
private fun LoadingDiariesPreview() {
    SuperDiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Loading,
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
                avatarUrl = "",
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun ErrorLoadingDiariesPreview() {
    SuperDiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Error(Error()),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
                avatarUrl = "",
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun EmptySearchDiaryListPreview() {
    TestAppContainer {
        DiaryList(
            diaries = listOf(),
            inSelectionMode = false,
            diaryFilters = DiaryFilters(),
            selectedIds = setOf(),
            onDeleteDiaries = {},
            onCancelSelection = {},
            diaryListActions = diaryListActions,
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@PreviewSuperDiary
@Composable
private fun EmptyDiaryListPreview() {
    SuperDiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            DiaryListScreenContent(
                state = DiaryListViewState.Content(listOf(), false),
                showSearchBar = false,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
                avatarUrl = "",
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun DiaryListPreview() {
    SuperDiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DiaryListScreenContent(
                state = DiaryListViewState.Content(
                    diaries = (0..10).map {
                        Diary(
                            id = Random.nextLong(),
                            entry = "Hello World $it",
                            date = Clock.System.now(),
                            isFavorite = false,
                            location = Location.Empty,
                        )
                    },
                    filtered = false,
                ),
                showSearchBar = true,
                diaryFilters = DiaryFilters(),
                diaryListActions = diaryListActions,
                avatarUrl = "",
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun CreateDiaryPreview() {
    SuperDiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onGenerateAI = { _, _ -> },
                onSaveDiary = {},
                isGeneratingFromAi = false,
                showLocationPermissionRationale = true,
                onRequestLocationPermission = {},
                onDontAskAgain = {},
                permissionState = PermissionState.Granted,
                userInfo = UserInfo(
                    id = "",
                    avatarUrl = "avatar-url",
                    name = "john@doe.com",
                    email = "john@email.com",
                ),
                showSaveDialog = false,
                onShowSaveDialogChange = {},
                onNavigateBack = {},
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun CreateDiaryPreviewNonEditable() {
    SuperDiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            CreateDiaryScreenContent(
                onGenerateAI = { _, _ -> },
                onSaveDiary = {},
                isGeneratingFromAi = false,
                onRequestLocationPermission = {},
                showLocationPermissionRationale = true,
                onDontAskAgain = {},
                permissionState = PermissionState.Granted,
                userInfo = UserInfo(
                    id = "",
                    avatarUrl = "avatar-url",
                    name = "john@doe.com",
                    email = "john@email.com",
                ),
                showSaveDialog = false,
                onShowSaveDialogChange = {},
                onNavigateBack = {},
            )
        }
    }
}

@PreviewSuperDiary
@Composable
private fun FilteredEmptyPreview() {
    SuperDiaryTheme {
        Surface(
            color = MaterialTheme.colorScheme.background,
        ) {
            Surface(
                color = MaterialTheme.colorScheme.background,
            ) {
                DiaryListScreenContent(
                    state = DiaryListViewState.Content(
                        diaries = listOf(),
                        filtered = true,
                    ),
                    showSearchBar = true,
                    diaryFilters = DiaryFilters(),
                    diaryListActions = diaryListActions,
                    avatarUrl = "",
                )
            }
        }
    }
}

@PreviewSuperDiary
@Composable
private fun SelectedDiariesPreview() {
    TestAppContainer {
        DiaryList(
            diaries = (0..10).map {
                Diary(
                    id = Random.nextLong(),
                    entry = "Hello World $it",
                    date = Clock.System.now(),
                    isFavorite = false,
                    location = Location.Empty,
                )
            },
            inSelectionMode = true,
            diaryFilters = DiaryFilters(),
            selectedIds = setOf(0, 1),
            onDeleteDiaries = {},
            onCancelSelection = {},
            diaryListActions = diaryListActions,
            snackbarHostState = SnackbarHostState(),
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DiaryDatePickerPreview() {
    TestAppContainer {
        DiaryDatePicker(
            onDismissRequest = {},
            onDateSelect = {},
            selectedDate = LocalDate.now().toKotlinLocalDate(),
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DeleteDialogPreview() {
    TestAppContainer {
        ConfirmDeleteDialog(
            onDismiss = {},
            onConfirm = {},
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DashboardPreview() {
    TestAppContainer {
        DashboardScreenContent(
            state = DashboardViewModel.DashboardScreenState.Content(
                (0..1).map {
                    Diary(
                        id = it.toLong(),
                        entry = "<strong>Awesome</strong> Diary",
                        date = Clock.System.now(),
                        isFavorite = false,
                        location = Location.Empty,
                    )
                },
                20,
                "",
                Streak(
                    0,
                    Clock.System.now().toDate(),
                    Clock.System.now().toDate(),
                ),
                bestStreak = Streak(
                    0,
                    Clock.System.now().toDate(),
                    Clock.System.now().toDate(),
                ),
            ),
            onAddEntry = {},
            onSeeAll = {},
            onToggleFavorite = {},
            settings = DiarySettings.Empty,
            onChangeSettings = {},
            onDiaryClick = {},
        )
    }
}

@Composable
@PreviewSuperDiary
private fun DetailPreview() {
    SuperDiaryTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            DetailScreenContent(
                onDeleteDiary = {},
                onNavigateBack = {},
                viewState = DetailsViewState.DiarySelected(
                    Diary(
                        entry = """
                            <p style="text-align:justify;">Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                                 Hello Diary, I did something awful today too.
                                I kept eating a very large bowl of rice till I couldn't take
                                it any much longer. I think this will go down in history as
                                the greatest rice eating bout of all time.
                            <p/>
                        """.trimIndent(),
                        id = 1000,
                        date = Clock.System.now(),
                        isFavorite = false,
                        location = Location.Empty,
                    ),
                ),
                avatarUrl = "avatar-url",
            )
        }
    }
}

@Composable
@PreviewSuperDiary
private fun LoginPreview() {
    SuperDiaryTheme {
        LoginScreenContent(
            onLoginWithGoogle = {},
            onLoginClick = { _, _ -> },
            onRegisterClick = {},
            viewState = LoginViewState.Idle,
            onSignInSuccess = {},
            isFromDeeplink = false,
            onResetPasswordClick = {},
        )
    }
}

@Composable
@PreviewSuperDiary
private fun RegisterPreview() {
    SuperDiaryTheme {
        RegisterScreenContent(
            onRegisterClick = { name, email, password -> },
            viewState = RegisterScreenState.Idle,
            onRegisterSuccess = {},
            onLoginClick = {},
        )
    }
}

@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "Night")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "Day")
annotation class PreviewSuperDiary

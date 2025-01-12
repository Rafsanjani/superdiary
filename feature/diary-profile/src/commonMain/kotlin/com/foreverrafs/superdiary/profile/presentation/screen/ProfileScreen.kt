package com.foreverrafs.superdiary.profile.presentation.screen

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.foreverrafs.superdiary.design.components.ConfirmLogoutDialog
import com.foreverrafs.superdiary.design.components.SuperDiaryImage
import com.foreverrafs.superdiary.design.style.SuperDiaryTheme
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewData
import com.foreverrafs.superdiary.profile.presentation.ProfileScreenViewModel
import com.foreverrafs.superdiary.utils.DiarySettings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(
    onLogoutComplete: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val viewModel: ProfileScreenViewModel = koinViewModel()
    val viewState by viewModel.viewState.collectAsStateWithLifecycle()
    var isLogoutDialogVisible by remember { mutableStateOf(false) }
    val currentOnLogoutComplete by rememberUpdatedState(onLogoutComplete)
    val settings by viewModel.settings.collectAsStateWithLifecycle()

    LaunchedEffect(viewState) {
        if (viewState.isLogoutSuccess == true) {
            currentOnLogoutComplete()
        }
    }

    ProfileScreenContent(
        viewState = viewState,
        onConsumeErrorMessage = viewModel::resetErrors,
        onLogout = {
            viewModel.onLogout()
        },
        onLogoutDialogVisibilityChange = {
            isLogoutDialogVisible = it
        },
        isLogoutDialogVisible = isLogoutDialogVisible,
        settings = settings,
        onUpdateSettings = viewModel::onSettingsUpdated,
        onNavigateBack = onNavigateBack,
    )
}

@Composable
fun ProfileScreenContent(
    viewState: ProfileScreenViewData,
    onConsumeErrorMessage: () -> Unit,
    onLogout: () -> Unit,
    onLogoutDialogVisibilityChange: (Boolean) -> Unit,
    onUpdateSettings: (DiarySettings) -> Unit,
    onNavigateBack: () -> Unit,
    isLogoutDialogVisible: Boolean,
    settings: DiarySettings,
    modifier: Modifier = Modifier,
) {
    val snackBarkHostState = remember { SnackbarHostState() }
    val currentOnConsumeErrorMessage by rememberUpdatedState(onConsumeErrorMessage)

    LaunchedEffect(viewState.errorMessage) {
        if (viewState.errorMessage != null) {
            snackBarkHostState.showSnackbar(viewState.errorMessage)
            currentOnConsumeErrorMessage()
        }
    }

    SuperDiaryTheme {
        Scaffold(
            modifier = modifier,
            contentColor = MaterialTheme.colorScheme.background,
            snackbarHost = {
                SnackbarHost(snackBarkHostState)
            },
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(it),
            ) {
                if (isLogoutDialogVisible) {
                    ConfirmLogoutDialog(
                        onLogout = {
                            onLogout()
                            onLogoutDialogVisibilityChange(false)
                        },
                        onDismiss = {
                            onLogoutDialogVisibilityChange(false)
                        },
                        onDismissRequest = {
                            onLogoutDialogVisibilityChange(false)
                        },
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.Start),
                    ) {
                        Icon(
                            modifier = Modifier.clip(CircleShape),
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "",
                        )
                    }
                    Spacer(modifier = Modifier.height(24.dp))

                    SuperDiaryImage(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape),
                        url = viewState.avatarUrl,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = viewState.name,
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = viewState.email,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Button(onClick = {}) {
                        Text("Edit Profile")
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    ProfileSection(
                        label = "Dashboard Cards",
                    ) {
                        CheckboxProfileItem(
                            label = "Weekly summary",
                            checked = settings.showWeeklySummary,
                            onCheckChange = {
                                onUpdateSettings(
                                    settings.copy(
                                        showWeeklySummary = it,
                                    ),
                                )
                            },
                        )

                        CheckboxProfileItem(
                            label = "On this day",
                            checked = true,
                            onCheckChange = {},
                        )

                        CheckboxProfileItem(
                            label = "Latest entries",
                            checked = settings.showLatestEntries,
                            onCheckChange = {
                                onUpdateSettings(
                                    settings.copy(
                                        showLatestEntries = it,
                                    ),
                                )
                            },
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .padding(horizontal = 16.dp),
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    TextButton(
                        onClick = {
                            onLogoutDialogVisibilityChange(true)
                        },
                        modifier = Modifier
                            .align(alignment = Alignment.CenterHorizontally),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp),
                        ) {
                            Icon(
                                painter = rememberVectorPainter(Icons.AutoMirrored.Filled.Logout),
                                contentDescription = null,
                                modifier = Modifier.size(24.dp),
                                tint = MaterialTheme.colorScheme.error,
                            )

                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                modifier = Modifier.padding(vertical = 8.dp),
                                text = "Sign out",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileSection(
    label: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(
            text = label,
            modifier = Modifier.align(Alignment.Start).padding(start = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )

        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .border(
                    width = 1.dp,
                    color = DividerDefaults.color,
                    shape = RoundedCornerShape(16.dp),
                ),
            content = content,
        )
    }
}

@Composable
private fun CheckboxProfileItem(
    label: String,
    checked: Boolean,
    onCheckChange: (Boolean) -> Unit,
    labelColor: Color = MaterialTheme.colorScheme.onBackground,
    leadingIcon: Painter? = null,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            leadingIcon?.let {
                Icon(
                    painter = it,
                    contentDescription = null,
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = labelColor,
            )
        }

        Checkbox(
            checked = checked,
            onCheckedChange = onCheckChange,
        )
    }
}

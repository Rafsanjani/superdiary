package com.foreverrafs.superdiary.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShareLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.foreverrafs.superdiary.core.location.permission.PermissionState
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.confirm_delete_diary_dialog_message
import superdiary.shared_ui.generated.resources.confirm_delete_diary_dialog_title
import superdiary.shared_ui.generated.resources.confirm_delete_diary_negative_button
import superdiary.shared_ui.generated.resources.confirm_delete_diary_positive_button
import superdiary.shared_ui.generated.resources.confirm_save_diary_dialog_message
import superdiary.shared_ui.generated.resources.confirm_save_diary_dialog_title
import superdiary.shared_ui.generated.resources.confirm_save_diary_negative_button
import superdiary.shared_ui.generated.resources.confirm_save_diary_positive_button

@Composable
fun ConfirmDeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(Res.string.confirm_delete_diary_dialog_title),
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = stringResource(Res.string.confirm_delete_diary_dialog_message),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = stringResource(Res.string.confirm_delete_diary_positive_button),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(Res.string.confirm_delete_diary_negative_button),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.primaryContainer,
    )
}

@Composable
fun ConfirmSaveDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    BasicSuperDiaryDialog(
        onDismiss = onDismiss,
        onConfirm = onConfirm,
        title = stringResource(Res.string.confirm_save_diary_dialog_title),
        message = stringResource(Res.string.confirm_save_diary_dialog_message),
        confirmButtonText = stringResource(Res.string.confirm_save_diary_positive_button),
        dismissButtonText = stringResource(Res.string.confirm_save_diary_negative_button),
    )
}

@Composable
private fun BasicSuperDiaryDialog(
    title: String,
    message: String,
    dismissButtonText: String,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    confirmButtonText: String,
    modifier: Modifier = Modifier,
    properties: DialogProperties = DialogProperties(),
) {
    AlertDialog(
        properties = properties,
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
            )
        },
        text = {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(
                    text = confirmButtonText,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = dismissButtonText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationRationaleDialog(
    permissionState: PermissionState,
    onRequestLocationPermission: () -> Unit,
    onDontAskAgain: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isPermissionDeniedAlways =
        permissionState == PermissionState.DeniedAlways || permissionState == PermissionState.NotGranted

    BasicAlertDialog(
        properties = DialogProperties(
            dismissOnBackPress = false,
            dismissOnClickOutside = false,
        ),
        onDismissRequest = {},
        content = {
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
                elevation = CardDefaults.elevatedCardElevation(),
            ) {
                Column {
                    Icon(
                        imageVector = Icons.Default.ShareLocation,
                        contentDescription = null,
                        modifier = Modifier
                            .padding(top = 35.dp)
                            .height(70.dp)
                            .fillMaxWidth(),

                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                    ) {
                        Text(
                            text = "Location Tags",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(top = 5.dp)
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.labelLarge,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )

                        val locationPermissionDialogMessage = if (isPermissionDeniedAlways) {
                            """
                                To use location tags in your entries, you need to enable location permission from your phone's settings menu
                            """.trimIndent()
                        } else {
                            "Allow location permission to use your location to personalise your entries"
                        }

                        Text(
                            text = locationPermissionDialogMessage,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .padding(
                                    top = 10.dp,
                                    start = 25.dp,
                                    end = 25.dp,
                                )
                                .fillMaxWidth(),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
                    ) {
                        if (isPermissionDeniedAlways) {
                            TextButton(
                                modifier = Modifier.weight(1f),
                                onClick = onDontAskAgain,
                            ) {
                                Text(
                                    text = "Don't ask again",
                                    color = Color.Red,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                                )
                            }
                        }

                        TextButton(
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_proceed_location"),
                            onClick = onRequestLocationPermission,
                        ) {
                            Text(
                                text = "Proceed",
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 5.dp, bottom = 5.dp),
                            )
                        }
                    }
                }
            }
        },
    )
}

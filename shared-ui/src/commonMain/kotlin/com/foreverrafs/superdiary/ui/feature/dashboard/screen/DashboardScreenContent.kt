package com.foreverrafs.superdiary.ui.feature.dashboard.screen

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.domain.model.Diary
import com.foreverrafs.superdiary.domain.model.Streak
import com.foreverrafs.superdiary.ui.feature.dashboard.DashboardViewModel
import com.foreverrafs.superdiary.ui.feature.diarylist.screen.DiaryItem
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.utils.DiarySettings
import com.valentinilk.shimmer.shimmer
import kotlin.random.Random
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.jetbrains.compose.resources.stringResource
import superdiary.shared_ui.generated.resources.Res
import superdiary.shared_ui.generated.resources.label_add_entry
import superdiary.shared_ui.generated.resources.label_button_retry
import superdiary.shared_ui.generated.resources.label_button_show_all
import superdiary.shared_ui.generated.resources.label_entries
import superdiary.shared_ui.generated.resources.label_glance_header_best_streak
import superdiary.shared_ui.generated.resources.label_glance_header_latest_entries
import superdiary.shared_ui.generated.resources.label_glance_header_streak
import superdiary.shared_ui.generated.resources.label_glance_header_weekly_summary
import superdiary.shared_ui.generated.resources.label_weekly_summary_error

private const val LATEST_ENTRIES_ID = "latestentries"
private const val AT_A_GLANCE_ID = "ataglance"
private const val WEEKLY_SUMMARY_ID = "weeklysummary"

@Composable
fun DashboardScreenContent(
    state: DashboardViewModel.DashboardScreenState,
    settings: DiarySettings,
    onChangeSettings: (DiarySettings) -> Unit,
    onAddEntry: () -> Unit,
    onSeeAll: () -> Unit,
    onDiaryClick: (diary: Diary) -> Unit,
    onToggleFavorite: (diary: Diary) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showShimmer by remember { mutableStateOf(false) }
    val dashboardItems = when (state) {
        is DashboardViewModel.DashboardScreenState.Content -> {
            showShimmer = false
            dashboardItems(
                state = state,
                onAddEntry = onAddEntry,
                onSeeAll = onSeeAll,
                onDiaryClicked = onDiaryClick,
                onToggleFavorite = onToggleFavorite,
                settings = settings,
            )
        }

        is DashboardViewModel.DashboardScreenState.Error -> TODO()
        is DashboardViewModel.DashboardScreenState.Loading -> {
            showShimmer = true
            dashboardPlaceholderItems(
                settings = settings,
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .testTag("dashboard_content_list")
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(items = dashboardItems, key = { it.id }) { content ->
            content.content(this) {
                when (content.id) {
                    AT_A_GLANCE_ID -> {
                        onChangeSettings(settings.copy(showAtAGlance = false))
                    }

                    WEEKLY_SUMMARY_ID -> {
                        onChangeSettings(settings.copy(showWeeklySummary = false))
                    }

                    LATEST_ENTRIES_ID -> {
                        onChangeSettings(settings.copy(showLatestEntries = false))
                    }
                }
            }
        }
    }
}

@Suppress("LongMethod")
private fun dashboardItems(
    state: DashboardViewModel.DashboardScreenState.Content,
    settings: DiarySettings,
    onAddEntry: () -> Unit,
    onSeeAll: () -> Unit,
    onToggleFavorite: (diary: Diary) -> Unit,
    onDiaryClicked: (diary: Diary) -> Unit,
): SnapshotStateList<DashboardSection> = mutableStateListOf<DashboardSection>().apply {
    if (settings.showAtAGlance) {
        add(
            DashboardSection(
                content = {
                    AtAGlance(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxWidth(),
                        state = state,
                    )
                },
                id = AT_A_GLANCE_ID,
            ),
        )
    }

    if (settings.showWeeklySummary) {
        add(
            DashboardSection(
                content = { onDismiss ->
                    WeeklySummaryCard(
                        modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxWidth()
                            .heightIn(max = 200.dp, min = 150.dp),
                        summary = state.weeklySummary,
                        onDismiss = onDismiss,
                    )
                },
                id = WEEKLY_SUMMARY_ID,
            ),
        )
    }

    if (settings.showLatestEntries) {
        add(
            DashboardSection(
                content = {
                    val itemCount = if (settings.showWeeklySummary) 2 else 4

                    if (state.latestEntries.isNotEmpty()) {
                        LatestEntries(
                            modifier = Modifier.animateItem(fadeInSpec = null, fadeOutSpec = null),
                            diaries = state.latestEntries.take(itemCount),
                            onSeeAll = onSeeAll,
                            onDiaryClick = onDiaryClicked,
                            onToggleFavorite = onToggleFavorite,
                        )
                    } else {
                        Button(
                            onClick = onAddEntry,
                            modifier = Modifier
                                .testTag("button_add_entry"),
                        ) {
                            Text(
                                text = stringResource(Res.string.label_add_entry),
                                style = MaterialTheme.typography.labelMedium,
                            )
                        }
                    }
                },
                id = LATEST_ENTRIES_ID,
            ),
        )
    }
}

@Suppress("LongMethod")
private fun dashboardPlaceholderItems(
    settings: DiarySettings,
): SnapshotStateList<DashboardSection> = mutableStateListOf<DashboardSection>().apply {
    if (settings.showAtAGlance) {
        add(
            DashboardSection(
                content = {
                    AtAGlance(
                        modifier = Modifier.shimmer(),
                        state = DashboardViewModel.DashboardScreenState.Content(
                            latestEntries = emptyList(),
                            totalEntries = 0,
                            weeklySummary = "",
                            currentStreak = Streak(
                                count = 0,
                                startDate = Clock.System.todayIn(TimeZone.UTC),
                                endDate = Clock.System.todayIn(TimeZone.UTC),
                            ),
                            bestStreak = Streak(
                                count = 0,
                                startDate = Clock.System.todayIn(TimeZone.UTC),
                                endDate = Clock.System.todayIn(TimeZone.UTC),
                            ),
                        ),
                    )
                },
                id = AT_A_GLANCE_ID,
            ),
        )
    }

    if (settings.showWeeklySummary) {
        add(
            DashboardSection(
                content = { onDismiss ->
                    WeeklySummaryCard(
                        modifier = Modifier
                            .shimmer()
                            .animateItem(fadeInSpec = null, fadeOutSpec = null)
                            .fillMaxWidth()
                            .heightIn(max = 200.dp, min = 150.dp),
                        summary = "",
                        title = "",
                        onDismiss = onDismiss,
                    )
                },
                id = WEEKLY_SUMMARY_ID,
            ),
        )
    }

    if (settings.showLatestEntries) {
        add(
            DashboardSection(
                content = {
                    LatestEntries(
                        modifier = Modifier
                            .shimmer()
                            .animateItem(fadeInSpec = null, fadeOutSpec = null),
                        diaries = (0..2).map {
                            Diary(id = Random.nextLong(), entry = "")
                        },
                        onSeeAll = {},
                        onDiaryClick = {},
                        onToggleFavorite = {},
                    )
                },
                id = LATEST_ENTRIES_ID,
            ),
        )
    }
}

@Composable
private fun AtAGlance(
    state: DashboardViewModel.DashboardScreenState.Content,
    modifier: Modifier = Modifier,
) {
    val animationState = remember { MutableTransitionState(false) }

    val currentStreakCount by animateIntAsState(
        targetValue = if (animationState.targetState) state.currentStreak.count else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    val bestStreakCount by animateIntAsState(
        targetValue = if (animationState.targetState) state.bestStreak.count else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    val totalEntries by animateIntAsState(
        targetValue = if (animationState.targetState) state.totalEntries.toInt() else 0,
        animationSpec = tween(durationMillis = 1000),
    )

    LaunchedEffect(Unit) {
        animationState.targetState = true
    }

    Column(modifier = modifier) {
        Text(
            text = "At a glance...",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(horizontal = 4.dp),
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val dashboardCardModifier = Modifier.weight(1f).aspectRatio(1f)

            fun streakCaption(streak: Streak): String {
                val dateFormatPattern = "MMM dd"
                return if (streak.count != 0) {
                    "${streak.startDate.format(dateFormatPattern)} - ${
                        streak.endDate.format(
                            dateFormatPattern,
                        )
                    }"
                } else {
                    "-"
                }
            }

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_entries),
                content = totalEntries.toString(),
            )

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_glance_header_streak),
                // Because formatted string resources do not cause recomposition
                content = "$currentStreakCount days",
                caption = streakCaption(streak = state.currentStreak),
            )

            GlanceCard(
                modifier = dashboardCardModifier,
                title = stringResource(Res.string.label_glance_header_best_streak),
                // Because formatted string resources do not cause recomposition
                content = "$bestStreakCount days",
                caption = streakCaption(state.bestStreak),
            )
        }
    }
}

@Composable
fun GlanceCard(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    caption: String = "",
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = title,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
            )

            Text(
                text = content,
                style = MaterialTheme.typography.displayMedium,
                textAlign = TextAlign.Center,
            )

            Text(
                text = caption,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }
    }
}

@Composable
private fun LatestEntries(
    diaries: List<Diary>,
    onSeeAll: () -> Unit,
    onToggleFavorite: (diary: Diary) -> Unit,
    onDiaryClick: (diary: Diary) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier) {
        Row(
            modifier = Modifier
                .clickable(
                    indication = null,
                    interactionSource = MutableInteractionSource(),
                    onClick = onSeeAll,
                )
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(Res.string.label_glance_header_latest_entries),
                style = MaterialTheme.typography.headlineMedium,
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                stringResource(Res.string.label_button_show_all),
                style = MaterialTheme.typography.labelSmall,
            )
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            diaries.forEachIndexed { index, diary ->
                DiaryItem(
                    diary = diary,
                    selected = false,
                    inSelectionMode = false,
                    modifier = Modifier
                        .clickable(onClick = { onDiaryClick(diary) })
                        .testTag("diary_list_item_$index"),
                    onToggleFavorite = { onToggleFavorite(diary) },
                )
            }
        }
    }
}

@Composable
private fun WeeklySummaryCard(
    summary: String?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String = stringResource(Res.string.label_glance_header_weekly_summary),
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = title,
                    textAlign = TextAlign.Start,
                    style = MaterialTheme.typography.headlineMedium,
                )

                Icon(
                    modifier = Modifier.clickable(onClick = onDismiss),
                    imageVector = Icons.Default.Close,
                    contentDescription = null,
                )
            }

            val textScrollState = rememberScrollState()
            val textStyle = MaterialTheme.typography.bodySmall

            val textLayoutResult = rememberTextMeasurer().measure(
                text = summary.orEmpty(),
                style = textStyle,
            )

            Text(
                modifier = Modifier
                    .verticalScroll(textScrollState)
                    .fadingEdges(
                        scrollState = textScrollState,
                        edgeHeight = textLayoutResult.getLineBottom(0) - textLayoutResult.getLineTop(
                            0,
                        ),
                    )
                    .padding(horizontal = 4.dp)
                    .padding(bottom = 4.dp),
                text = summary ?: stringResource(Res.string.label_weekly_summary_error),
                style = textStyle,
                textAlign = TextAlign.Justify,
                lineHeight = 28.sp,
            )

            if (summary == null) {
                TextButton(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 12.dp),
                    onClick = {},
                ) {
                    Text(stringResource(Res.string.label_button_retry))
                }
            }
        }
    }
}

data class DashboardSection(
    val content: @Composable LazyItemScope.(onDismiss: () -> Unit) -> Unit,
    val id: String,
)

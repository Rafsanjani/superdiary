package com.foreverrafs.superdiary.ui.feature.diarylist.screen

import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.foreverrafs.superdiary.diary.model.Diary
import com.foreverrafs.superdiary.diary.utils.groupByDate
import com.foreverrafs.superdiary.diary.utils.toDate
import com.foreverrafs.superdiary.ui.components.ConfirmDeleteDialog
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryFilters
import com.foreverrafs.superdiary.ui.feature.diarylist.DiaryListActions
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryFilterSheet
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiaryHeader
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiarySearchBar
import com.foreverrafs.superdiary.ui.feature.diarylist.components.DiarySelectionModifierBar
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.style.montserratAlternativesFontFamily
import com.mohamedrejeb.richeditor.model.rememberRichTextState
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

val DiaryListActions.Companion.Empty: DiaryListActions
    get() = DiaryListActions(
        onCancelSelection = {},
        onToggleSelection = {},
        onRemoveSelection = {},
        onAddSelection = {},
        onToggleFavorite = { true },
        onApplyFilters = {},
        onDeleteDiaries = { true },
        onAddEntry = {},
        onDiaryClicked = {},
    )

@Composable
fun DiaryListScreenContent(
    state: DiaryListScreenState,
    modifier: Modifier = Modifier,
    diaryFilters: DiaryFilters,
    showSearchBar: Boolean,
    diaryListActions: DiaryListActions,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
) {
    val screenModifier = modifier
        .fillMaxSize()

    Scaffold(
        topBar = {
            SuperDiaryAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = diaryListActions.onBackPressed,
                    ) {
                        Icon(
                            modifier = Modifier
                                .clip(CircleShape),
                            imageVector = Icons.Default.ArrowBackIosNew,
                            contentDescription = "Navigate back",
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = diaryListActions.onAddEntry,
                shape = RoundedCornerShape(4.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                )
            }
        },
    ) {
        Box(modifier = Modifier.padding(it)) {
            when (state) {
                is DiaryListScreenState.Content -> {
                    DiaryListContent(
                        modifier = screenModifier,
                        diaries = state.diaries,
                        isFiltered = state.filtered,
                        showSearchBar = showSearchBar,
                        onAddEntry = diaryListActions.onAddEntry,
                        diaryListActions = diaryListActions,
                        selectedIds = emptySet(),
                        diaryFilters = diaryFilters,
                        snackbarHostState = snackbarHostState,
                    )
                }

                is DiaryListScreenState.Error -> ErrorContent(
                    modifier = screenModifier,
                )

                is DiaryListScreenState.Loading -> LoadingContent(modifier = screenModifier)
            }
        }
    }
}

/**
 * Display a list of diaries from the database. We have different functions
 * for adding and removing instead of just using the toggl. There are
 * instances where we just want to add entries whether they exist or
 * not and other times where we want to remove entries at all costs.
 *
 * @param diaries The list of diaries to display
 * @param inSelectionMode Whether we are actively selecting items or not
 * @param diaryFilters The filters that will be applied to the diary list
 * @param selectedIds The list of ids of the selected diary entries remove
 *     it otherwise.
 * @param onDeleteDiaries Delete the selected diaries from the list diaries
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    inSelectionMode: Boolean,
    diaryFilters: DiaryFilters,
    selectedIds: Set<Long>,
    showSearchBar: Boolean = true,
    diaryListActions: DiaryListActions,
    snackbarHostState: SnackbarHostState,
    onDeleteDiaries: (selectedIds: Set<Long>) -> Unit,
    listState: LazyListState = rememberLazyListState(),
    onCancelSelection: () -> Unit,
) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .padding(8.dp),
    ) {
        var showFilterDiariesBottomSheet by remember {
            mutableStateOf(false)
        }

        // Search and selection modifier bars
        if (showSearchBar) {
            Box {
                DiarySearchBar(
                    inSelectionMode = !inSelectionMode,
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter),
                    onQueryChanged = {
                        diaryListActions.onApplyFilters(diaryFilters.copy(entry = it))
                    },
                    onFilterClicked = {
                        showFilterDiariesBottomSheet = true
                    },
                )

                DiarySelectionModifierBar(
                    inSelectionMode = inSelectionMode,
                    selectedIds = selectedIds,
                    onDelete = onDeleteDiaries,
                    onCancelSelection = onCancelSelection,
                )
            }
        }

        if (showFilterDiariesBottomSheet) {
            DiaryFilterSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
                onApplyFilters = diaryListActions.onApplyFilters,
                diaryFilters = diaryFilters,
            )
        }

        // When the user inputs a search query, we still want to show them
        // the search bar instead of the original empty screen
        if (diaries.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 12.dp),
                verticalArrangement = Arrangement.spacedBy(space = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                state = listState,
            ) {
                groupedDiaries.forEach { (date, diaries) ->
                    stickyHeader(key = date.label) {
                        val isGroupSelected by remember(selectedIds) {
                            mutableStateOf(selectedIds.containsAll(diaries.map { it.id }))
                        }

                        DiaryHeader(
                            modifier = Modifier.animateItemPlacement(),
                            text = date.label,
                            inSelectionMode = inSelectionMode,
                            selected = isGroupSelected,
                            selectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let(diaryListActions.onAddSelection)
                                }
                            },
                            deSelectGroup = {
                                diaries.forEach { diary ->
                                    diary.id?.let(diaryListActions.onRemoveSelection)
                                }
                            },
                        )
                    }

                    items(
                        items = diaries.sortedByDescending { it.id },
                        key = { item -> item.id.toString() },
                    ) { diary ->

                        DiaryItem(
                            modifier = Modifier
                                .animateItemPlacement()
                                .combinedClickable(
                                    onClick = {
                                        if (inSelectionMode) {
                                            diaryListActions.onToggleSelection(diary.id)
                                        } else {
                                            diaryListActions.onDiaryClicked(diary)
                                        }
                                    },
                                    onLongClick = {
                                        diaryListActions.onToggleSelection(diary.id)
                                    },
                                ),
                            diary = diary,
                            selected = diary.id in selectedIds,
                            inSelectionMode = inSelectionMode,
                            onToggleFavorite = {
                                coroutineScope.launch {
                                    if (diaryListActions.onToggleFavorite(diary)) {
                                        snackbarHostState.showSnackbar(
                                            message = "Favorite Updated!",
                                        )
                                    }
                                }
                            },
                        )
                    }
                }
            }
        } else {
            // When there is no diary entry from the search screen
            Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 64.dp),
                    text = "No entry found!",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 14.sp,
                )
            }
        }
    }
}

@Composable
private fun DiaryListContent(
    modifier: Modifier = Modifier,
    diaries: List<Diary>,
    isFiltered: Boolean,
    showSearchBar: Boolean,
    onAddEntry: () -> Unit,
    diaryListActions: DiaryListActions,
    selectedIds: Set<Long>,
    diaryFilters: DiaryFilters,
    snackbarHostState: SnackbarHostState,
) {
    @Suppress("NAME_SHADOWING")
    var selectedIds by rememberSaveable {
        mutableStateOf(selectedIds)
    }

    @Suppress("NAME_SHADOWING")
    val diaryListActions = remember {
        diaryListActions.copy(
            onAddSelection = { diaryId ->
                diaryId?.let {
                    selectedIds = selectedIds.plus(diaryId)
                }
            },
            onRemoveSelection = { diaryId ->
                diaryId?.let {
                    selectedIds = selectedIds.minus(diaryId)
                }
            },
            onToggleSelection = {
                selectedIds = selectedIds.addOrRemove(it)
            },
            onCancelSelection = {
                selectedIds = emptySet()
            },
        )
    }

    var showConfirmDeleteDialog by remember {
        mutableStateOf(false)
    }

    val coroutineScope = rememberCoroutineScope()

    if (showConfirmDeleteDialog) {
        ConfirmDeleteDialog(
            onDismiss = {
                showConfirmDeleteDialog = false
            },
            onConfirm = {
                coroutineScope.launch {
                    showConfirmDeleteDialog = false
                    val isSuccess = diaryListActions.onDeleteDiaries(
                        diaries.filter { selectedIds.contains(it.id) },
                    )

                    val message = if (isSuccess) {
                        "${selectedIds.size} item(s) deleted!"
                    } else {
                        "Error deleting diaries"
                    }

                    selectedIds = emptySet()

                    snackbarHostState.showSnackbar(
                        message,
                    )
                }
            },
        )
    }

    // We want to keep showing the search bar even for an empty list
    // if filters have been applied
    val filteredEmpty = diaries.isEmpty() && isFiltered

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        val listState = rememberLazyListState()

        if (diaries.isNotEmpty() || filteredEmpty) {
            DiaryList(
                modifier = Modifier.fillMaxSize(),
                diaries = diaries,
                inSelectionMode = selectedIds.isNotEmpty(),
                diaryFilters = diaryFilters,
                selectedIds = selectedIds,
                showSearchBar = showSearchBar,
                onDeleteDiaries = {
                    selectedIds = it
                    showConfirmDeleteDialog = true
                },
                onCancelSelection = diaryListActions.onCancelSelection,
                diaryListActions = diaryListActions,
                snackbarHostState = snackbarHostState,
                listState = listState,
            )
        } else {
            EmptyDiaryList(
                modifier = Modifier.fillMaxSize(),
                onAddEntry = onAddEntry,
            )
        }
    }
}

@Composable
private fun LoadingContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.padding(bottom = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(
            space = 8.dp,
            alignment = Alignment.CenterVertically,
        ),
    ) {
        CircularProgressIndicator()

        Text(
            text = "Loading Diaries",
            textAlign = TextAlign.Center,
        )
    }
}

@Composable
private fun EmptyDiaryList(
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
) {
    Column(
        modifier = modifier
            .padding(horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            textAlign = TextAlign.Center,
            text = "Uh Uhh, it's very lonely here 😔",
            style = MaterialTheme.typography.headlineMedium,
            fontSize = 20.sp,
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Why don't you start writing something...",
            style = MaterialTheme.typography.bodySmall,
            fontSize = 14.sp,
        )

        TextButton(
            onClick = onAddEntry,
        ) {
            Text("Add Entry")
        }
    }
}

@Composable
fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
    selected: Boolean,
    inSelectionMode: Boolean,
    onToggleFavorite: () -> Unit,
) {
    val transition = updateTransition(selected, label = "selected")
    val padding by transition.animateDp(label = "padding") { _ ->
        if (inSelectionMode) 4.dp else 0.dp
    }

    val roundedCornerShape by transition.animateDp(label = "corner") { _ ->
        if (selected) 16.dp else 0.dp
    }

    Box(
        modifier = modifier
            .height(110.dp)
            .padding(padding)
            .clip(RoundedCornerShape(roundedCornerShape))
            .fillMaxWidth(),
    ) {
        Card(
            shape = RoundedCornerShape(
                topStart = 0.dp,
                bottomStart = 12.dp,
                topEnd = 12.dp,
                bottomEnd = 0.dp,
            ),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .semantics {
                        stateDescription = if (diary.isFavorite) "Favorite" else "Not favorite"

                        customActions = listOf(
                            CustomAccessibilityAction(
                                label = "Toggle Favorite",
                                action = {
                                    onToggleFavorite()
                                    true
                                },
                            ),
                        )
                    }
                    .fillMaxSize(),
            ) {
                DateCard(diary.date.toDate())

                // Diary Entry
                Text(
                    modifier = Modifier
                        .clearAndSetSemantics { }
                        .padding(
                            start = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp,
                            top = 16.dp,
                        )
                        .align(Alignment.Top),
                    letterSpacing = (-0.3).sp,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Justify,
                    text = rememberRichTextState().apply { setHtml(diary.entry) }.annotatedString,
                )
            }
        }
        // Selection mode icon
        if (inSelectionMode) {
            val iconModifier = Modifier
                .padding(top = 12.dp, start = 4.dp)
                .size(20.dp)

            if (selected) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = iconModifier,
                )
            } else {
                Icon(
                    imageVector = Icons.Filled.RadioButtonUnchecked,
                    tint = Color.White.copy(alpha = 0.7f),
                    contentDescription = null,
                    modifier = iconModifier,
                )
            }
        }
    }
}

@Composable
private fun DateCard(date: LocalDate) {
    Box(
        modifier = Modifier
            .fillMaxHeight()
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = RoundedCornerShape(
                    topStart = 0.dp,
                    topEnd = 12.dp,
                    bottomStart = 12.dp,
                    bottomEnd = 0.dp,
                ),
            )
            .padding(horizontal = 25.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier
                .semantics {
                    contentDescription = "Entry for ${date.format("EEE dd MMMM yyyy")}"
                },
            text = annotatedString(date),
            textAlign = TextAlign.Center,
            lineHeight = 24.sp,
            style = MaterialTheme.typography.labelMedium,
        )
    }
}

@Composable
private fun annotatedString(date: LocalDate): AnnotatedString = buildAnnotatedString {
    withStyle(
        SpanStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
        ),
    ) {
        append(
            date.format("E")
                .uppercase(),
        )
    }

    appendLine()

    withStyle(
        SpanStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.ExtraBold,
            fontSize = 16.sp,
        ),
    ) {
        append(date.dayOfMonth.toString())
    }
    appendLine()

    withStyle(
        SpanStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
    ) {
        append(
            date.format("MMM")
                .uppercase(),
        )
    }
    appendLine()

    withStyle(
        SpanStyle(
            fontFamily = montserratAlternativesFontFamily(),
            fontWeight = FontWeight.Normal,
            fontSize = 14.sp,
        ),
    ) {
        append(date.year.toString())
    }
}

@Composable
fun FavoriteIcon(
    modifier: Modifier = Modifier,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
) {
    // favorite icon
    IconButton(
        onClick = onToggleFavorite,
        modifier = modifier
            .clearAndSetSemantics { }
            .clip(CircleShape)
            .padding(4.dp),
    ) {
        Icon(
            modifier = Modifier.clearAndSetSemantics { },
            imageVector = if (isFavorite) {
                Icons.Filled.Favorite
            } else {
                Icons.Filled.FavoriteBorder
            },
            contentDescription = null,
        )
    }
}

@Composable
private fun ErrorContent(modifier: Modifier) {
    Box(
        modifier = modifier.padding(bottom = 64.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "Error loading diaries",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

private fun Set<Long>.addOrRemove(id: Long?): Set<Long> {
    if (id == null) return this
    return if (this.contains(id)) this.minus(id) else this.plus(id)
}

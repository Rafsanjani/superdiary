package com.foreverrafs.superdiary.ui.feature.diarylist

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryBooks
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.foreverrafs.superdiary.ui.components.SuperDiaryAppBar
import com.foreverrafs.superdiary.ui.format
import com.foreverrafs.superdiary.ui.style.montserratAlternativesFontFamily
import kotlinx.datetime.LocalDateTime

@Composable
fun DiaryListScreen(
    state: DiaryListScreenState,
    modifier: Modifier = Modifier,
    onAddEntry: () -> Unit,
) {
    Column(
        modifier = modifier,
    ) {
        SuperDiaryAppBar()

        when (state) {
            is DiaryListScreenState.Content -> {
                if (state.diaries.isNotEmpty()) {
                    DiaryList(
                        modifier = Modifier.fillMaxSize(),
                        diaries = state.diaries,
                    )
                } else {
                    EmptyDiaryList(
                        onAddEntry = onAddEntry,
                    )
                }
            }

            is DiaryListScreenState.Error -> Error(modifier = Modifier.fillMaxWidth())

            is DiaryListScreenState.Loading -> Loading(modifier = Modifier.wrapContentSize())
        }
    }
}

@Composable
private fun Loading(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
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
            .fillMaxSize()
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryList(modifier: Modifier = Modifier, diaries: List<Diary>) {
    val groupedDiaries = remember(diaries) {
        diaries.groupByDate()
    }

    Box(
        modifier = modifier
            .padding(8.dp),
    ) {
        var showFilterDiariesBottomSheet by remember {
            mutableStateOf(false)
        }

        SearchBar(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            onQueryChanged = {},
            onFilterClicked = {
                showFilterDiariesBottomSheet = true
            },
        )

        if (showFilterDiariesBottomSheet) {
            FilterDiariesSheet(
                onDismissRequest = {
                    showFilterDiariesBottomSheet = false
                },
            )
        }
        // Offset this by the height of the Searchbar (when it isn't active)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 60.dp),
            verticalArrangement = Arrangement.spacedBy(space = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            state = rememberLazyListState(),
        ) {
            groupedDiaries.forEach { (date, diaries) ->
                stickyHeader(key = date.label) {
                    DiaryHeader(
                        text = date.label,
                    )
                }

                items(
                    items = diaries,
                    key = { item -> item.id.toString() },
                ) { diary ->
                    DiaryItem(
                        diary = diary,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    onQueryChanged: (query: String) -> Unit,
    onFilterClicked: () -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var active by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(query) {
        onQueryChanged(query)
    }

    DockedSearchBar(
        modifier = modifier,
        query = query,
        onQueryChange = { query = it },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            Text(
                text = "Search diary...",
                style = MaterialTheme.typography.labelLarge,
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
            )
        },
        trailingIcon = {
            Icon(
                modifier = Modifier
                    .clickable { onFilterClicked() }
                    .padding(8.dp),
                imageVector = Icons.Default.Sort,
                contentDescription = null,
            )
        },
        colors = SearchBarDefaults.colors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer,
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(4) { idx ->
                val resultText = "Suggestion $idx"

                ListItem(
                    headlineContent = { Text(resultText) },
                    supportingContent = { Text("Additional info") },
                    leadingContent = {
                        Icon(
                            Icons.Filled.LibraryBooks,
                            contentDescription = null,
                        )
                    },
                    modifier = Modifier.clickable {
                        query = resultText
                        active = false
                    },
                )
            }
        }
    }
}

@Composable
private fun Error(modifier: Modifier) {
    Text(
        text = "Error loading diaries",
        textAlign = TextAlign.Center,
        modifier = modifier,
        style = MaterialTheme.typography.headlineMedium,
    )
}

@Composable
private fun DiaryHeader(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.background,
        ),
    ) {
        Text(
            modifier = Modifier.padding(vertical = 8.dp),
            text = text,
            style = MaterialTheme.typography.headlineMedium,
        )
    }
}

@Composable
private fun DiaryItem(
    modifier: Modifier = Modifier,
    diary: Diary,
) {
    val defaultTextLines = 4

    // Used to determine whether to expand/collapse the card onClick
    var isExpanded by remember {
        mutableStateOf(false)
    }

    var maxLines by remember {
        mutableStateOf(defaultTextLines)
    }

    Card(
        modifier = modifier
            .height(120.dp)
            .animateContentSize(animationSpec = tween(easing = LinearEasing))
            .fillMaxWidth()
            .clickable {
                maxLines = if (!isExpanded) Int.MAX_VALUE else defaultTextLines
                isExpanded = !isExpanded
            },
        shape = RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 12.dp,
            topEnd = 12.dp,
            bottomEnd = 0.dp,
        ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxSize(),
        ) {
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
                    text = buildAnnotatedString {
                        val date = LocalDateTime.parse(diary.date).date

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
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
                                fontSize = 20.sp,
                            ),
                        ) {
                            append(date.dayOfMonth.toString())
                        }
                        appendLine()

                        withStyle(
                            SpanStyle(
                                fontFamily = montserratAlternativesFontFamily(),
                                fontWeight = FontWeight.Normal,
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
                            ),
                        ) {
                            append(date.year.toString())
                        }

                        toAnnotatedString()
                    },
                    textAlign = TextAlign.Center,
                    lineHeight = 20.sp,
                )
            }

            // Diary Entry
            Text(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.Top),
                text = diary.entry,
                letterSpacing = (-0.3).sp,
                maxLines = maxLines,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Justify,
            )
        }
    }
}

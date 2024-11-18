package me.saket.swipe

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import kotlin.math.roundToInt
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/** A composable that can be swiped left or right for revealing actions. */
@Composable
fun SwipeableActionBox(
    state: SwipeableActionState,
    action: SwipeAction,
    modifier: Modifier = Modifier,
    content: @Composable (BoxScope.() -> Unit),
) = BoxWithConstraints(modifier) {
    val scope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // Actual content
        Box(
            modifier = Modifier
                .zIndex(0.3f)
                .absoluteOffset { IntOffset(x = state.offset.value.roundToInt(), y = 0) }
                .draggable(
                    state = state.draggableState,
                    onDragStopped = {
                        state.finalizeDrag()
                    },
                    orientation = Orientation.Horizontal,
                ),
            content = content,
        )

        // The action icon which will be revealed
        IconButton(
            modifier = Modifier
                .testTag("swipeable_revealed_content")
                .background(color = Color.Transparent)
                .align(Alignment.CenterEnd)
                .width(defaultSwipeableActionIconSize),
            onClick = {
                scope.launch {
                    action.onClick()
                    delay(250)
                    state.resetDrag()
                }
            },
            content = { action.icon() },
        )
    }
}

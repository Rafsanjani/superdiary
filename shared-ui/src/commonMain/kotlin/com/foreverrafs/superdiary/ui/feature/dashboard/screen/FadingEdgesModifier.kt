package com.foreverrafs.superdiary.ui.feature.dashboard.screen

import androidx.compose.foundation.ScrollState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import kotlin.math.min

fun Modifier.fadingEdges(
    scrollState: ScrollState,
    edgeHeight: Float,
): Modifier {
    val topGradientColors = listOf(Color.Transparent, Color.Black)
    val bottomGradientColors = listOf(Color.Black, Color.Transparent)

    return this.then(
        Modifier
            .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            .drawWithContent {
                drawContent()

                // Top Gradient
                val topGradientHeight = min(edgeHeight, scrollState.value.toFloat())
                if (topGradientHeight > 0f) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = topGradientColors,
                            startY = scrollState.value.toFloat(),
                            endY = scrollState.value.toFloat() + topGradientHeight,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                }

                // Bottom Gradient
                val remainingScroll = scrollState.maxValue - scrollState.value
                val bottomGradientHeight = min(edgeHeight.toFloat(), remainingScroll.toFloat())
                if (bottomGradientHeight > 0f) {
                    val bottomEndY = size.height - remainingScroll
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = bottomGradientColors,
                            startY = bottomEndY - bottomGradientHeight,
                            endY = bottomEndY,
                        ),
                        blendMode = BlendMode.DstIn,
                    )
                }
            },
    )
}

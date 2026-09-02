package com.example.ui.util

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Applies a smooth top and bottom gradient alpha fade to any scrollable container.
 * This prevents sharp cutoffs when content scrolls into or out of view.
 */
fun Modifier.fadingEdge(
    topFadeHeight: Dp = 16.dp,
    bottomFadeHeight: Dp = 32.dp
): Modifier = this
    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
    .drawWithContent {
        drawContent()
        val topPx = topFadeHeight.toPx()
        val bottomPx = bottomFadeHeight.toPx()
        val height = size.height

        if (topPx > 0f) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color.Black),
                    startY = 0f,
                    endY = topPx
                ),
                blendMode = BlendMode.DstIn
            )
        }
        if (bottomPx > 0f && height > bottomPx) {
            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Black, Color.Transparent),
                    startY = height - bottomPx,
                    endY = height
                ),
                blendMode = BlendMode.DstIn
            )
        }
    }

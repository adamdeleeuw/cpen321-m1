package com.example.cpen321application.ui.home

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.max

/** how much a pooled wash thins out at its middle, where the pigment has drained from. */
private const val CORE_FADE = 0.45f

/** how much colour an unpooled wash still carries near its rim, before it fades to nothing. */
private const val RIM_FADE = 0.3f

private const val CORE_STOP = 0.55f
private const val RIM_STOP = 0.85f

/**
 * spec: one pool of colour. an oval radial gradient placed in fractions of the page, fading
 * to nothing at its rim. pooling (0..1) drains the middle and gathers the colour at the rim,
 * the way pigment dries at the edge of a wet stroke.
 */
private class Wash(
    val color: Color,
    val alpha: Float,
    val centerX: Float,
    val centerY: Float,
    val radius: Float,
    val stretchX: Float = 1f,
    val stretchY: Float = 1f,
    val pooling: Float = 0f
)

private val WASHES = listOf(
    // the colour gathers around the edges, each pool laid over the last
    Wash(HomeColors.Blush, 0.66f, 0.04f, 0.0f, 0.44f, stretchX = 1.3f, stretchY = 0.95f, pooling = 0.3f),
    Wash(HomeColors.Rose, 0.34f, 1.02f, 0.09f, 0.33f, stretchX = 0.95f, stretchY = 1.25f, pooling = 0.55f),
    Wash(HomeColors.Peach, 0.48f, -0.06f, 0.44f, 0.31f, stretchX = 1.35f, stretchY = 0.95f, pooling = 0.25f),
    Wash(HomeColors.Rose, 0.3f, 1.08f, 0.74f, 0.34f, stretchY = 1.15f, pooling = 0.5f),
    Wash(HomeColors.Mauve, 0.36f, 0.26f, 1.04f, 0.4f, stretchX = 1.3f, stretchY = 0.85f, pooling = 0.35f),
    Wash(HomeColors.Blush, 0.3f, 0.86f, 0.36f, 0.2f, stretchX = 1.2f, stretchY = 0.9f, pooling = 0.65f),
    // the paper is washed back in over the middle, where the buttons and their text sit
    Wash(HomeColors.Canvas, 0.72f, 0.44f, 0.5f, 0.44f, stretchX = 1.2f, stretchY = 1.05f),
    // and a few smaller strokes are laid on top, kept clear of the middle, with the pigment
    // gathered at their rims so they read as separate layers rather than more of the blend
    Wash(HomeColors.Rose, 0.26f, 0.32f, 0.17f, 0.21f, stretchX = 1.3f, stretchY = 0.75f, pooling = 0.85f),
    Wash(HomeColors.Blush, 0.4f, 0.79f, 0.08f, 0.17f, stretchX = 1.1f, stretchY = 0.95f, pooling = 0.7f),
    Wash(HomeColors.Peach, 0.34f, 0.17f, 0.8f, 0.19f, stretchX = 1.2f, stretchY = 0.85f, pooling = 0.65f),
    Wash(HomeColors.Mauve, 0.3f, 0.71f, 0.87f, 0.22f, stretchX = 1.15f, stretchY = 0.8f, pooling = 0.8f)
)

/**
 * spec: paints the home page: warm off-white paper under overlapping pools of colour, so the
 * page reads as layered watercolour rather than a gradient. every pool is kept faint on
 * purpose — the wash must never fight the buttons and text drawn over it.
 */
internal fun Modifier.watercolorWash(): Modifier = drawBehind {
    drawRect(HomeColors.Canvas)
    WASHES.forEach { drawWash(it) }
}

private fun DrawScope.drawWash(wash: Wash) {
    val center = Offset(size.width * wash.centerX, size.height * wash.centerY)
    val radius = max(size.width, size.height) * wash.radius
    // the brush is built untransformed, so the stretch below shapes the gradient with the circle
    val brush = Brush.radialGradient(
        colorStops = arrayOf(
            0f to wash.color.copy(alpha = wash.alpha * (1f - CORE_FADE * wash.pooling)),
            CORE_STOP to wash.color.copy(alpha = wash.alpha),
            RIM_STOP to wash.color.copy(alpha = wash.alpha * (RIM_FADE + (1f - RIM_FADE) * wash.pooling)),
            1f to wash.color.copy(alpha = 0f)
        ),
        center = center,
        radius = radius
    )
    withTransform({ scale(wash.stretchX, wash.stretchY, pivot = center) }) {
        drawCircle(brush = brush, radius = radius, center = center)
    }
}

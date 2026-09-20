package com.example.cpen321application.ui.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

private val RimWidth = 1.dp

/**
 * spec: the frosted pane every raised surface on the page is made of. a translucent white
 * sheet over the black canvas, rimmed by a hairline that catches the light along its top edge
 * and falls away by the bottom, so the pane reads as lifted rather than outlined.
 *
 * the page behind it is flat black, so there is nothing to blur behind the glass; the sheet's
 * own translucency plus the lit rim is what carries the frosting.
 *
 * [tint] washes an accent under the frosting, for a pane that has to say which action it is.
 */
internal fun Modifier.glassSurface(shape: Shape, tint: Color = Color.Transparent): Modifier = this
    // the tint goes down first so the white sheet frosts over it rather than beside it
    .background(color = tint, shape = shape)
    .background(
        brush = Brush.verticalGradient(listOf(TimerColors.GlassTop, TimerColors.GlassBottom)),
        shape = shape
    )
    .border(
        width = RimWidth,
        brush = Brush.verticalGradient(listOf(TimerColors.RimLit, TimerColors.Rim)),
        shape = shape
    )

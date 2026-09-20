package com.example.cpen321application.ui.websocket

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

private val FrameShape = RoundedCornerShape(6.dp)
private val FrameWidth = 18.dp
private val FrameLift = 14.dp
private val RebateWidth = 2.dp
private val RivetSize = 12.dp

/** centres a rivet head in the band of wood it sits on. */
private val RivetInset = (FrameWidth - RivetSize) / 2

private val RivetCorners = listOf(
    Alignment.TopStart,
    Alignment.TopEnd,
    Alignment.BottomStart,
    Alignment.BottomEnd
)

/**
 * spec: a dark walnut picture frame around the page's canvas. the wood runs from a lit grain to
 * a near-black one across the diagonal, with a bevel darkening its outer edge and a sunken
 * rebate holding the picture, and an ornamental gold rivet on the wood at each corner.
 */
@Composable
internal fun WalnutFrame(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(
        modifier = modifier
            .shadow(
                elevation = FrameLift,
                shape = FrameShape,
                ambientColor = WebSocketColors.FrameShadow,
                spotColor = WebSocketColors.FrameShadow
            )
            .background(
                brush = Brush.linearGradient(
                    listOf(WebSocketColors.WoodLit, WebSocketColors.Wood, WebSocketColors.WoodDark)
                ),
                shape = FrameShape
            )
            .border(1.dp, WebSocketColors.WoodEdge, FrameShape)
    ) {
        Box(
            modifier = Modifier
                .padding(FrameWidth)
                .border(RebateWidth, WebSocketColors.Rebate)
                .padding(RebateWidth)
        ) {
            content()
        }

        // drawn after the picture so the heads sit proud of the wood, not under it
        RivetCorners.forEach { Rivet(it) }
    }
}

/** one domed gold nail: a radial gradient lit from the top-left, so the head reads as round. */
@Composable
private fun BoxScope.Rivet(corner: Alignment) {
    Box(
        modifier = Modifier
            .align(corner)
            .padding(RivetInset)
            .size(RivetSize)
            .drawBehind {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            WebSocketColors.RivetLit,
                            WebSocketColors.Rivet,
                            WebSocketColors.RivetShade
                        ),
                        center = Offset(size.width * 0.35f, size.height * 0.3f),
                        radius = size.minDimension * 0.8f
                    )
                )
            }
    )
}

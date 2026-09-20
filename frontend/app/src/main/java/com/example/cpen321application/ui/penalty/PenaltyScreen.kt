package com.example.cpen321application.ui.penalty

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cpen321application.ui.theme.LightSystemBarIcons
import kotlin.math.hypot

/**
 * spec: page shown when the timer finishes. the player must hit the crossbar: touch the ball,
 * pull back, release. misses reset the ball and count as attempts (unlimited). a hit shows
 * "CLANG!" and then a "Back to timer" button, which is the only way out: the system Back
 * button is swallowed and there is no cancel.
 */
@Composable
fun PenaltyScreen(
    viewModel: PenaltyViewModel,
    onDone: () -> Unit,
    modifier: Modifier = Modifier
) {
    LightSystemBarIcons()
    BackHandler { } // the challenge cannot be skipped

    LaunchedEffect(viewModel) { viewModel.run() }

    val haptic = LocalHapticFeedback.current
    LaunchedEffect(viewModel.phase) {
        when (viewModel.phase) {
            PenaltyPhase.FLYING, PenaltyPhase.MISSED -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
            PenaltyPhase.SCORED -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            PenaltyPhase.AIMING -> Unit
        }
    }

    var canvasSize by remember { mutableStateOf(IntSize.Zero) }
    val currentSize by rememberUpdatedState(canvasSize)

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(PenaltyColors.Background)
    ) {
        PenaltyPitch(
            viewModel = viewModel,
            modifier = Modifier
                .onSizeChanged { canvasSize = it }
                .pointerInput(viewModel) {
                    detectDragGestures(
                        onDragStart = { start ->
                            // only a drag that begins on the ball starts an aim
                            val (wx, wy) = toWorld(start, currentSize)
                            val onBall = viewModel.phase == PenaltyPhase.AIMING &&
                                hypot(wx - viewModel.ballX, wy - viewModel.ballY) <= PenaltyWorld.GRAB_RADIUS
                            if (onBall) viewModel.aim(0f, 0f)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            if (viewModel.aiming) {
                                val (wx, wy) = toWorld(change.position, currentSize)
                                viewModel.aim(viewModel.ballX - wx, viewModel.ballY - wy)
                            }
                        },
                        onDragEnd = viewModel::release,
                        onDragCancel = viewModel::cancelAim
                    )
                }
        )

        Text(
            text = if (viewModel.aiming || viewModel.phase != PenaltyPhase.AIMING || viewModel.attempts > 0) {
                "Attempts: ${viewModel.attempts}"
            } else {
                "Hit the crossbar!"
            },
            color = PenaltyColors.Text,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 24.dp)
        )

        if (viewModel.phase == PenaltyPhase.SCORED && !viewModel.celebrating) {
            Text(
                text = "CLANG!",
                color = PenaltyColors.Accent,
                fontSize = 44.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        if (viewModel.celebrating) {
            Column(
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(PenaltyColors.Panel, RoundedCornerShape(24.dp))
                    .padding(horizontal = 40.dp, vertical = 32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Crossbar!",
                    color = PenaltyColors.Accent,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (viewModel.attempts == 0) "First try" else "${viewModel.attempts + 1} attempts",
                    color = PenaltyColors.Text,
                    fontSize = 17.sp
                )
                Spacer(Modifier.height(24.dp))
                Button(
                    onClick = onDone,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PenaltyColors.Start,
                        contentColor = Color.White
                    )
                ) {
                    Text(text = "Back to timer", fontSize = 17.sp, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}

// pixel position in the canvas to world units (see PenaltyWorld.fit)
private fun toWorld(position: Offset, size: IntSize): Pair<Float, Float> {
    val (scale, offsetX, offsetY) = PenaltyWorld.fit(size.width.toFloat(), size.height.toFloat())
    return (position.x - offsetX) / scale to (position.y - offsetY) / scale
}

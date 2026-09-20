package com.example.cpen321application.ui.timer

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cpen321application.ui.theme.LightSystemBarIcons

private val ControlSize = 88.dp
private val ControlGap = 24.dp // how far the controls sit under the stage
private val ControlSpacing = 40.dp // and how far apart from each other
private const val PRESSED_SCALE = 0.94f
private const val DISABLED_ALPHA = 0.35f
private const val TINT_ALPHA = 0.22f // how much accent shows through a control's frosting

/**
 * spec: page opened by the timer button. a black page titled "Timer" with either the
 * hour/minute/second picker or, once started, the countdown ring. Cancel stops a running
 * timer, or leaves the page when none is running; Start begins the picked duration.
 * the system Back button leaves the page and lets a running timer carry on.
 */
@Composable
fun TimerScreen(
    viewModel: TimerViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    LightSystemBarIcons()
    BackHandler(onBack = onBack)

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(TimerColors.Background)
            .padding(horizontal = 24.dp)
            .padding(bottom = 16.dp), // a floor under the controls on a short screen
    ) {
        Text(
            text = "Timer",
            color = TimerColors.Digits,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        // the stage and its controls are centred as one block, so the controls stay a fixed
        // step below the timer instead of being pushed to the foot of the page
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Crossfade(targetState = viewModel.running, label = "timer-mode") { running ->
                // both modes are given the picker's height, so switching doesn't shift the controls
                Box(
                    modifier = Modifier.fillMaxWidth().height(TimerPickerHeight),
                    contentAlignment = Alignment.Center
                ) {
                    if (running) {
                        TimerCountdown(viewModel.remainingMillis, viewModel.totalMillis)
                    } else {
                        TimerPicker(viewModel)
                    }
                }
            }

            Row(
                modifier = Modifier.padding(top = ControlGap),
                horizontalArrangement = Arrangement.spacedBy(ControlSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ControlButton(
                    label = "Cancel",
                    color = TimerColors.Cancel,
                    onClick = { if (viewModel.running) viewModel.cancel() else onBack() }
                )
                if (viewModel.running) {
                    Spacer(Modifier.size(ControlSize)) // keeps Cancel on the left, as when idle
                } else {
                    ControlButton(
                        label = "Start",
                        color = TimerColors.Start,
                        enabled = viewModel.canStart,
                        onClick = viewModel::start
                    )
                }
            }
        }
    }
}

/** one round glass pane, tinted and lettered in its action's accent. */
@Composable
private fun ControlButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    val interactions = remember { MutableInteractionSource() }
    val pressed by interactions.collectIsPressedAsState()
    // the pane sinks under the finger and eases back, so a press reads as glass giving way
    val press by animateFloatAsState(if (pressed) PRESSED_SCALE else 1f, label = "control-press")

    Button(
        onClick = onClick,
        enabled = enabled,
        interactionSource = interactions,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        elevation = null, // a shadow would only muddy the rim on a black page
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = color,
            disabledContainerColor = Color.Transparent,
            // the whole pane fades when disabled, so the label needs no separate dimming
            disabledContentColor = color
        ),
        modifier = Modifier
            .graphicsLayer {
                scaleX = press
                scaleY = press
                alpha = if (enabled) 1f else DISABLED_ALPHA
            }
            .size(ControlSize)
            .glassSurface(CircleShape, tint = color.copy(alpha = TINT_ALPHA))
    ) {
        Text(text = label, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}

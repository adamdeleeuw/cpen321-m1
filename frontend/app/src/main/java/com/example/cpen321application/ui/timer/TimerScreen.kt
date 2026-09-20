package com.example.cpen321application.ui.timer

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

private val ControlSize = 88.dp

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
    ) {
        Text(
            text = "Timer",
            color = TimerColors.Digits,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 16.dp)
        )

        Crossfade(
            targetState = viewModel.running,
            modifier = Modifier.weight(1f).fillMaxWidth(),
            label = "timer-mode"
        ) { running ->
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (running) {
                    TimerCountdown(viewModel.remainingMillis, viewModel.totalMillis)
                } else {
                    TimerPicker(viewModel)
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
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

@Composable
private fun ControlButton(
    label: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = CircleShape,
        contentPadding = PaddingValues(0.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
            disabledContainerColor = color.copy(alpha = 0.3f),
            disabledContentColor = Color.White.copy(alpha = 0.5f)
        ),
        modifier = Modifier.size(ControlSize)
    ) {
        Text(text = label, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}

/** the page is black whatever the system theme, so the status/navigation icons must be light */
@Composable
private fun LightSystemBarIcons() {
    val view = LocalView.current
    DisposableEffect(view) {
        val controller = WindowCompat.getInsetsController((view.context as Activity).window, view)
        val wasLightStatus = controller.isAppearanceLightStatusBars
        val wasLightNavigation = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = false
        controller.isAppearanceLightNavigationBars = false
        onDispose {
            controller.isAppearanceLightStatusBars = wasLightStatus
            controller.isAppearanceLightNavigationBars = wasLightNavigation
        }
    }
}

package com.example.cpen321application.ui.timer

import androidx.compose.runtime.Composable
import com.example.cpen321application.ui.home.HomeButton

@Composable
fun TimerButton(onClick: () -> Unit) {
    HomeButton(text = "Timer", onClick = onClick)
}

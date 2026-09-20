package com.example.cpen321application.ui.timer

import androidx.compose.ui.graphics.Color

/** spec: the timer page palette. black canvas, translucent white "glass", white digits. */
internal object TimerColors {
    val Background = Color.Black
    val Digits = Color.White
    val Label = Color.White.copy(alpha = 0.65f)
    val GlassTop = Color.White.copy(alpha = 0.16f)
    val GlassBottom = Color.White.copy(alpha = 0.07f)
    val GlassEdge = Color.White.copy(alpha = 0.14f)
    val Track = Color.White.copy(alpha = 0.08f)
    val Ring = Color(0xFF2F80ED)
    val Cancel = Color(0xFFE5383B)
    val Start = Color(0xFF2FA84F)
}

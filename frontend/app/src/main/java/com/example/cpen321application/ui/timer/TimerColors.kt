package com.example.cpen321application.ui.timer

import androidx.compose.ui.graphics.Color

/** spec: the timer page palette. black canvas, translucent white "glass", white digits. */
internal object TimerColors {
    val Background = Color.Black
    val Digits = Color.White
    val Label = Color.White.copy(alpha = 0.65f)
    val GlassTop = Color.White.copy(alpha = 0.16f) // the frosting, brightest where the pane faces the light
    val GlassBottom = Color.White.copy(alpha = 0.08f)
    val RimLit = Color.White.copy(alpha = 0.30f) // the lit top edge of a pane
    val Rim = Color.White.copy(alpha = 0.12f) // and the same edge where it has turned away
    val Track = Color.White.copy(alpha = 0.08f)
    val Ring = Color(0xFF2F80ED)
    // the accents now carry a glass pane's label instead of filling it, so they are pitched to
    // read against the frosting rather than against white text
    val Cancel = Color(0xFFFF6B6E)
    val Start = Color(0xFF4ADE80)
}

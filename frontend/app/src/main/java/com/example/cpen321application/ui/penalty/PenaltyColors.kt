package com.example.cpen321application.ui.penalty

import androidx.compose.ui.graphics.Color

/** spec: the penalty page palette. striped night-match pitch, white goal, white and black ball. */
internal object PenaltyColors {
    val Background = Color.Black
    val GrassLight = Color(0xFF2E8B45)
    val GrassDark = Color(0xFF277A3B)
    val Line = Color.White.copy(alpha = 0.55f)
    val Post = Color(0xFFF4F4F4)
    val Net = Color.White.copy(alpha = 0.22f)
    val Ball = Color.White
    val BallPatch = Color(0xFF1B1B1B)
    val Shadow = Color.Black.copy(alpha = 0.3f)
    val Guide = Color.White.copy(alpha = 0.85f)
    val Text = Color.White
    val Accent = Color(0xFFFFC83D)
    val Panel = Color.Black.copy(alpha = 0.72f)
    val Start = Color(0xFF2FA84F)
}

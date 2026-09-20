package com.example.cpen321application.ui.home

import androidx.compose.ui.graphics.Color

/**
 * spec: the home page palette. pools of blush, rose, peach and mauve washed over a warm
 * off-white page, with dusty-rose to plum buttons picked to carry white text over it.
 */
internal object HomeColors {
    val Canvas = Color(0xFFFCF8F7) // the warm off-white paper the washes sit on
    val Blush = Color(0xFFFAD2E1)
    val Rose = Color(0xFFF2A6BC)
    val Peach = Color(0xFFFBDCCB)
    val Mauve = Color(0xFFE2C9DF)
    val ButtonTop = Color(0xFFA85D79)
    val ButtonBottom = Color(0xFF7B3F58)
    val ButtonDisabled = Color(0xFFC3A7B2)
    val ButtonShadow = Color(0xFF7B3F58)
    val OnButton = Color(0xFFFFF7FA)
    val Error = Color(0xFF9B1C31) // the page is light in either theme, so the theme's error red won't do
}

package com.example.cpen321application.ui.home

import androidx.compose.ui.graphics.Color

/**
 * spec: the home page palette. pools of blush, rose, peach and mauve washed over a warm
 * off-white page, with chocolate brown buttons carrying crisp white text over it.
 */
internal object HomeColors {
    val Canvas = Color(0xFFFCF8F7) // the warm off-white paper the washes sit on
    val Blush = Color(0xFFFAD2E1)
    val Rose = Color(0xFFF2A6BC)
    val Peach = Color(0xFFFBDCCB)
    val Mauve = Color(0xFFE2C9DF)
    val Button = Color(0xFF411900)
    val ButtonDisabled = Color(0xFF8A6A57) // the same brown lifted off the page, so it still reads as brown
    val ButtonShadow = Color(0xFF411900)
    val OnButton = Color(0xFFFFFFFF)
    val Ink = Color(0xFF411900) // text on the wash, taken from the buttons so the pages match
    val Error = Color(0xFF9B1C31) // the page is light in either theme, so the theme's error red won't do
}

package com.example.cpen321application.ui.theme

import android.app.Activity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * spec: for pages that are dark whatever the system theme (timer, penalty). forces light
 * status/navigation bar icons while shown and restores the previous appearance on leave.
 */
@Composable
internal fun LightSystemBarIcons() = SystemBarIcons(light = false)

/**
 * spec: for pages that are light whatever the system theme (home, with its watercolour
 * wash). forces dark status/navigation bar icons while shown and restores them on leave.
 */
@Composable
internal fun DarkSystemBarIcons() = SystemBarIcons(light = true)

/** `light` means light *bars*, so the icons on them are dark. */
@Composable
private fun SystemBarIcons(light: Boolean) {
    val view = LocalView.current
    DisposableEffect(view, light) {
        val controller = WindowCompat.getInsetsController((view.context as Activity).window, view)
        val wasLightStatus = controller.isAppearanceLightStatusBars
        val wasLightNavigation = controller.isAppearanceLightNavigationBars
        controller.isAppearanceLightStatusBars = light
        controller.isAppearanceLightNavigationBars = light
        onDispose {
            controller.isAppearanceLightStatusBars = wasLightStatus
            controller.isAppearanceLightNavigationBars = wasLightNavigation
        }
    }
}

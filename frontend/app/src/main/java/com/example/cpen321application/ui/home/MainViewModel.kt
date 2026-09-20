package com.example.cpen321application.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** spec: which page is showing. */
enum class Screen { HOME, AUTH, WEBSOCKET, TIMER }

/** spec: the pages drawn on the home watercolour wash, which is light in either system theme. */
internal val Screen.onWatercolor: Boolean
    get() = this == Screen.HOME || this == Screen.AUTH || this == Screen.WEBSOCKET

class MainViewModel : ViewModel() {
    var screen by mutableStateOf(Screen.HOME)
}

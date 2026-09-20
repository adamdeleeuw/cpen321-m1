package com.example.cpen321application.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** spec: which page is showing. */
enum class Screen { HOME, AUTH, WEBSOCKET, TIMER }

class MainViewModel : ViewModel() {
    var screen by mutableStateOf(Screen.HOME)
}

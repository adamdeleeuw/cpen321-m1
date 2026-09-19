package com.example.cpen321application.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/** spec: which page is showing, plus placeholder state for the socket and timer features. */
enum class Screen { HOME, AUTH }

class MainViewModel : ViewModel() {
    var screen by mutableStateOf(Screen.HOME)
}

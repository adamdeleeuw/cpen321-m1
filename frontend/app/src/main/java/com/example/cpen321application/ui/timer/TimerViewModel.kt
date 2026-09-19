package com.example.cpen321application.ui.timer

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * Manage state for timer
 */

class TimerViewModel : ViewModel() {
    var timerRunning by mutableStateOf(false)
        private set

    fun timer() {
        timerRunning = true
    }
}
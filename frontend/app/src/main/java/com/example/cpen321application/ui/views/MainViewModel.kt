package com.example.cpen321application.ui.views

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainViewModel : ViewModel() {
    // state variables
    var authStatus by mutableStateOf("Not authenticated")
        private set // anyone can read it, only ViewModel() can write it

    var socketStatus by mutableStateOf("Not connected")
        private set

    var timerRunning by mutableStateOf(false)
        private set

    var surpriseStatus by mutableStateOf(false)
        private set

    // Button 1
    fun authenticateUser() {
        authStatus = "Authenticated"
    }

    // Button 2
    fun webSocket() {
        socketStatus = "socketing"
    }

    // Button 3
    fun timer() {
        timerRunning = true
        surpriseStatus = true
    }
}
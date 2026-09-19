package com.example.cpen321application.ui.websocket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

/**
 * State management for websocket
 */

class WebSocketViewModel : ViewModel() {
    var socketStatus by mutableStateOf("Not connected")
        private set

    fun webSocket() {
        socketStatus = "socketing"
    }
}
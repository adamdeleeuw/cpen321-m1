package com.example.cpen321application.ui.websocket

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cpen321application.data.PixelEvent
import com.example.cpen321application.data.PixelStream
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

const val GRID_SIZE = 16

/**
 * spec: state for the live pixel canvas. connect() clears the grid and paints one
 * cell per update from the backend stream; disconnect() stops listening. the grid
 * is row-major (index = y * GRID_SIZE + x). socketStatus is one of Not connected,
 * Connecting..., Connected, Disconnected.
 */
class WebSocketViewModel : ViewModel() {
    val grid = mutableStateListOf<Color>().apply { repeat(GRID_SIZE * GRID_SIZE) { add(BLANK) } }

    var socketStatus by mutableStateOf("Not connected")
        private set

    private var stream: Job? = null

    fun connect() {
        stream?.cancel()
        grid.indices.forEach { grid[it] = BLANK }
        socketStatus = "Connecting..."
        stream = viewModelScope.launch {
            PixelStream.connect()
                .catch { } // a failed connection ends the stream just like a server close
                .collect { event ->
                    when (event) {
                        PixelEvent.Opened -> socketStatus = "Connected"
                        is PixelEvent.Update -> paint(event)
                    }
                }
            socketStatus = "Disconnected" // not reached on cancel, so disconnect() keeps its own status
        }
    }

    fun disconnect() {
        stream?.cancel()
        socketStatus = "Not connected"
    }

    private fun paint(update: PixelEvent.Update) {
        // a bad coordinate from the server must not crash the app
        if (update.x !in 0 until GRID_SIZE || update.y !in 0 until GRID_SIZE) return
        grid[update.y * GRID_SIZE + update.x] = Color(update.color)
    }

    private companion object {
        val BLANK = Color.White
    }
}

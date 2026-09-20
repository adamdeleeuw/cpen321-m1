package com.example.cpen321application.ui.websocket

import androidx.compose.runtime.Composable
import com.example.cpen321application.ui.home.HomeButton

@Composable
fun WebSocketButton(onClick: () -> Unit) {
    HomeButton(text = "Connect to WebSocket", onClick = onClick)
}

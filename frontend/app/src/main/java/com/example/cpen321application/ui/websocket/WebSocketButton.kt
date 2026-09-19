package com.example.cpen321application.ui.websocket

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@Composable
fun WebSocketButton(viewModel: WebSocketViewModel) {
    Button(
        onClick = { viewModel.webSocket() },
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Connect to WebSocket",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}
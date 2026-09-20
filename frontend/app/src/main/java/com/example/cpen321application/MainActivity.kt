package com.example.cpen321application

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.cpen321application.ui.home.Screen
import com.example.cpen321application.ui.theme.CPEN321ApplicationTheme
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.activity.viewModels
import com.example.cpen321application.ui.home.MainScreen
import com.example.cpen321application.ui.home.MainViewModel
import com.example.cpen321application.ui.auth.AuthViewModel
import com.example.cpen321application.ui.auth.ConnectionInfoViewModel
import com.example.cpen321application.ui.timer.TimerViewModel
import com.example.cpen321application.ui.websocket.WebSocketViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val timerViewModel: TimerViewModel by viewModels()
    private val webSocketViewModel: WebSocketViewModel by viewModels()
    private val connectionInfoViewModel: ConnectionInfoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CPEN321ApplicationTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    // the timer page is black edge to edge, including behind the system bars
                    containerColor = if (viewModel.screen == Screen.TIMER) Color.Black
                    else MaterialTheme.colorScheme.background
                ) { innerPadding ->
                    MainScreen(
                        viewModel = viewModel,
                        authViewModel = authViewModel,
                        timerViewModel = timerViewModel,
                        webSocketViewModel = webSocketViewModel,
                        connectionInfoViewModel = connectionInfoViewModel,
                        modifier = Modifier.padding(innerPadding)
                        )
                }
            }
        }
    }
}

// unused, but leave for now since health route still exists in the backend
private suspend fun fetchHealthStatus(apiBaseUrl: String): String = withContext(Dispatchers.IO) {
    val healthUrl = "${apiBaseUrl.trimEnd('/')}/health"
    try {
        val connection = (URL(healthUrl).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 5_000
            readTimeout = 5_000
        }

        when (val code = connection.responseCode) {
            HttpURLConnection.HTTP_OK -> {
                val body = connection.inputStream.bufferedReader().use { it.readText() }
                "Backend healthy ($healthUrl): $body"
            }
            else -> {
                val errorBody = connection.errorStream?.bufferedReader()?.use { it.readText() }
                "Backend error ($healthUrl): HTTP $code${errorBody?.let { " — $it" } ?: ""}"
            }
        }
    } catch (e: Exception) {
        "Backend unreachable ($healthUrl): ${e.message ?: e.javaClass.simpleName}"
    }
}
package com.example.cpen321application.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cpen321application.ui.auth.AuthButton
import com.example.cpen321application.ui.auth.AuthViewModel
import com.example.cpen321application.ui.auth.ConnectionInfoScreen
import com.example.cpen321application.ui.auth.ConnectionInfoViewModel
import com.example.cpen321application.ui.timer.TimerButton
import com.example.cpen321application.ui.timer.TimerViewModel

/**
 * spec: root screen. HOME has 3 buttons (auth, websocket, timer); signing in
 * navigates to the AUTH page (connection info), signing out or a 401 returns home.
 * the "ATTENTION" notice is pinned to the bottom after an unauthorized attempt.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    timerViewModel: TimerViewModel,
    connectionInfoViewModel: ConnectionInfoViewModel,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(authViewModel.isSignedIn) {
        viewModel.screen = if (authViewModel.isSignedIn) Screen.AUTH else Screen.HOME
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (viewModel.screen == Screen.AUTH && authViewModel.isSignedIn) {
            ConnectionInfoScreen(
                authViewModel = authViewModel,
                viewModel = connectionInfoViewModel,
                onBack = { viewModel.screen = Screen.HOME },
                modifier = Modifier.weight(1f)
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Socket Status: ${viewModel.socketStatus}")
                Text(text = "Timer Running: ${timerViewModel.timerRunning}")

                Spacer(modifier = Modifier.height(32.dp))

                AuthButton(viewModel = authViewModel)

                Spacer(modifier = Modifier.height(32.dp))

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

                Spacer(modifier = Modifier.height(32.dp))

                TimerButton(viewModel = timerViewModel)
            }
        }

        authViewModel.notice?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

package com.example.cpen321application.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.auth.AuthButton
import com.example.cpen321application.ui.auth.AuthViewModel
import com.example.cpen321application.ui.auth.ConnectionInfoScreen
import com.example.cpen321application.ui.auth.ConnectionInfoViewModel
import com.example.cpen321application.ui.penalty.PenaltyScreen
import com.example.cpen321application.ui.penalty.PenaltyViewModel
import com.example.cpen321application.ui.theme.DarkSystemBarIcons
import com.example.cpen321application.ui.timer.TimerButton
import com.example.cpen321application.ui.timer.TimerScreen
import com.example.cpen321application.ui.timer.TimerViewModel
import com.example.cpen321application.ui.websocket.WebSocketButton
import com.example.cpen321application.ui.websocket.WebSocketScreen
import com.example.cpen321application.ui.websocket.WebSocketViewModel

/**
 * spec: root screen. HOME has 3 buttons (auth, websocket, timer); signing in
 * navigates to the AUTH page (connection info), which shares the home watercolour wash,
 * and signing out or a 401 returns home.
 * the websocket button opens the WEBSOCKET page (live pixel canvas) and connects;
 * its Back button disconnects and returns home. the timer button opens the TIMER page.
 * when the timer finishes, the penalty challenge takes over the whole screen, wherever the
 * user is, until the crossbar is hit; then the TIMER page shows again.
 * the "ATTENTION" notice is pinned to the bottom after an unauthorized attempt.
 */
@Composable
fun MainScreen(
    viewModel: MainViewModel,
    authViewModel: AuthViewModel,
    timerViewModel: TimerViewModel,
    penaltyViewModel: PenaltyViewModel,
    webSocketViewModel: WebSocketViewModel,
    connectionInfoViewModel: ConnectionInfoViewModel,
    modifier: Modifier = Modifier
) {

    LaunchedEffect(authViewModel.isSignedIn) {
        viewModel.screen = if (authViewModel.isSignedIn) Screen.AUTH else Screen.HOME
    }

    // the wash is light in either system theme, so text over it can't take the theme's colours
    val onWatercolor = !timerViewModel.finished && viewModel.screen.onWatercolor

    Column(modifier = modifier.fillMaxSize()) {
        if (onWatercolor) DarkSystemBarIcons()

        if (timerViewModel.finished) {
            PenaltyScreen(
                viewModel = penaltyViewModel,
                onDone = {
                    penaltyViewModel.reset()
                    timerViewModel.acknowledge()
                    viewModel.screen = Screen.TIMER
                },
                modifier = Modifier.weight(1f)
            )
        } else if (viewModel.screen == Screen.AUTH && authViewModel.isSignedIn) {
            ConnectionInfoScreen(
                authViewModel = authViewModel,
                viewModel = connectionInfoViewModel,
                onBack = { viewModel.screen = Screen.HOME },
                modifier = Modifier.weight(1f)
            )
        } else if (viewModel.screen == Screen.WEBSOCKET) {
            WebSocketScreen(
                viewModel = webSocketViewModel,
                onBack = {
                    webSocketViewModel.disconnect()
                    viewModel.screen = Screen.HOME
                },
                modifier = Modifier.weight(1f)
            )
        } else if (viewModel.screen == Screen.TIMER) {
            TimerScreen(
                viewModel = timerViewModel,
                onBack = { viewModel.screen = Screen.HOME },
                modifier = Modifier.weight(1f)
            )
        } else {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterVertically)
            ) {
                AuthButton(viewModel = authViewModel)

                WebSocketButton(onClick = {
                    webSocketViewModel.connect()
                    viewModel.screen = Screen.WEBSOCKET
                })

                TimerButton(onClick = { viewModel.screen = Screen.TIMER })
            }
        }

        authViewModel.notice?.let {
            Text(
                text = it,
                color = if (onWatercolor) HomeColors.Error else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
}

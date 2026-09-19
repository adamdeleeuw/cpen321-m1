package com.example.cpen321application.ui.auth

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * spec: page shown after sign-in. lists server ip/time, client ip/time,
 * the backend-provided name, and the signed-in google user's name.
 */
@Composable
fun ConnectionInfoScreen(
    authViewModel: AuthViewModel,
    viewModel: ConnectionInfoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val token = authViewModel.sessionToken
    LaunchedEffect(token) {
        if (token != null) viewModel.load(token, authViewModel::onUnauthorized)
    }

    val user = authViewModel.session?.user
    val info = viewModel.info
    val pending = "..."

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text("Server public IP: ${info?.serverIp ?: pending}")
        Text("Client IP: ${info?.clientIp ?: pending}")
        Text("Server time: ${info?.serverTime ?: pending}")
        Text("Client time: ${viewModel.clientTime.ifEmpty { pending }}")
        Text("Your name: ${viewModel.myName ?: pending}")
        Text("Google user: ${user?.let { "${it.firstName} ${it.lastName}" } ?: pending}")

        viewModel.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

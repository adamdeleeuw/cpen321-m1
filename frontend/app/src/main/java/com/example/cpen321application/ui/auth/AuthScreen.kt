package com.example.cpen321application.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

/**
 * spec: auth section of the home screen. shows sign-in status, the
 * sign-in/sign-up (or sign-out) button, and any sign-in error.
 */
@Composable
fun AuthScreen(viewModel: AuthViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current // activity context, needed to show the google picker

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        Button(
            onClick = {
                if (viewModel.isSignedIn) viewModel.signOut() else viewModel.signIn(context)
            },
            enabled = !viewModel.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = if (viewModel.isSignedIn) "Sign out" else "Sign in/Sign up with Google",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        viewModel.errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error)
        }
    }
}

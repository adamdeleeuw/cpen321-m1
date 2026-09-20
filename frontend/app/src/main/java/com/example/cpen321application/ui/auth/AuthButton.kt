package com.example.cpen321application.ui.auth

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.cpen321application.ui.home.HomeButton
import com.example.cpen321application.ui.home.HomeColors

/**
 * spec: auth section of the home screen. shows sign-in status, the
 * sign-in/sign-up (or sign-out) button, and any sign-in error.
 */
@Composable
fun AuthButton(viewModel: AuthViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current // activity context, needed to show the google picker

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {

        HomeButton(
            text = if (viewModel.isSignedIn) "Sign out" else "Sign in/Sign up with Google",
            onClick = {
                if (viewModel.isSignedIn) viewModel.signOut() else viewModel.signIn(context)
            },
            enabled = !viewModel.isLoading
        )

        viewModel.errorMessage?.let {
            Text(
                text = it,
                color = HomeColors.Error,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}

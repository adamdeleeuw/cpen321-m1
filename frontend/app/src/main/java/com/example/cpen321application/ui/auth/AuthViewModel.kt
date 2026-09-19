package com.example.cpen321application.ui.auth

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.credentials.exceptions.GetCredentialException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cpen321application.data.BackendApi
import com.example.cpen321application.data.GoogleSignInClient
import com.example.cpen321application.data.InvalidCredentialsException
import com.example.cpen321application.data.Session
import kotlinx.coroutines.launch

const val SIGN_IN_REQUIRED_MESSAGE = "ATTENTION: you must sign-in/sign-up before using this feature"

/**
 * spec: holds auth state for the UI (session in memory only).
 * - signIn: google picker -> backend -> session; sets error text on failure.
 * - signOut: drops the session.
 * - requireSignIn: gate for features; false + attention notice if not signed in.
 * - onUnauthorized: call when the backend returns 401 (e.g. expired token).
 */
class AuthViewModel : ViewModel() {
    var session by mutableStateOf<Session?>(null)
        private set

    var isLoading by mutableStateOf(false)
        private set

    // shown near the sign-in button ("Invalid credentials", etc.)
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // shown at the bottom of the screen when an unauthorized action is attempted
    var notice by mutableStateOf<String?>(null)
        private set

    // also treats an expired session as signed out
    val isSignedIn: Boolean
        get() = session?.let { it.expiresAtMillis > System.currentTimeMillis() } == true

    val sessionToken: String? get() = if (isSignedIn) session?.token else null

    val statusText: String
        get() = session
            ?.takeIf { isSignedIn }
            ?.let { "Signed in as ${it.user.firstName} ${it.user.lastName}" }
            ?: "Not authenticated"

    fun signIn(context: Context) {
        if (isLoading) return
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val idToken = GoogleSignInClient.getIdToken(context)
                session = BackendApi.signInWithGoogle(idToken)
                notice = null
            } catch (e: InvalidCredentialsException) {
                errorMessage = "Invalid credentials"
            } catch (e: GetCredentialException) {
                errorMessage = "Google sign-in was cancelled or failed"
            } catch (e: Exception) {
                errorMessage = "Could not reach the server"
            } finally {
                isLoading = false
            }
        }
    }

    fun signOut() {
        session = null
        errorMessage = null
    }

    fun requireSignIn(): Boolean {
        if (isSignedIn) return true
        notice = SIGN_IN_REQUIRED_MESSAGE
        return false
    }

    fun onUnauthorized() {
        session = null
        notice = SIGN_IN_REQUIRED_MESSAGE
    }
}

package com.example.cpen321application.data

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import com.example.cpen321application.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential

/**
 * spec: runs the google account picker (Credential Manager) and returns a
 * google id token. throws androidx.credentials.exceptions.GetCredentialException
 * if the user cancels or no account is available.
 * note: BuildConfig.GOOGLE_CLIENT_ID must be the web/backend client id (the
 * same value as the backend's GOOGLE_BACKEND_CLIENT_ID), not the android one.
 */
object GoogleSignInClient {
    // context must be an Activity so the picker can show
    suspend fun getIdToken(context: Context): String {
        val option = GetGoogleIdOption.Builder()
            .setServerClientId(BuildConfig.GOOGLE_CLIENT_ID)
            .setFilterByAuthorizedAccounts(false) // show all accounts, needed for sign-up
            .build()
        val request = GetCredentialRequest.Builder().addCredentialOption(option).build()

        val credential = CredentialManager.create(context).getCredential(context, request).credential
        if (credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ) {
            return GoogleIdTokenCredential.createFrom(credential.data).idToken
        }
        throw IllegalStateException("Unexpected credential type")
    }
}

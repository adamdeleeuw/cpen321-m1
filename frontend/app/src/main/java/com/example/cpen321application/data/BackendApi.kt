package com.example.cpen321application.data

import com.example.cpen321application.BuildConfig
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

/**
 * spec: http client for the backend. all calls run on Dispatchers.IO.
 * - signInWithGoogle: POST /api/auth/google, trades a google id token for a session.
 * - getMyName: GET /api/my-name with the session token (protected route).
 * - getConnectionInfo: GET /api/connection-info with the session token (protected route).
 * errors: InvalidCredentialsException (401 on sign-in), UnauthorizedException
 * (401 on protected route), IOException (network / other http errors).
 */

class InvalidCredentialsException : Exception("Invalid credentials")
class UnauthorizedException : Exception("Unauthorized")

data class UserInfo(
    val id: String,
    val email: String,
    val firstName: String,
    val lastName: String,
)

data class ConnectionInfo(
    val serverIp: String,
    val serverTime: String,
    val clientIp: String,
)

data class Session(
    val token: String,
    val expiresAtMillis: Long, // local clock; used to drop expired sessions client-side
    val isNewUser: Boolean,
    val user: UserInfo,
)

object BackendApi {
    private val baseUrl = BuildConfig.API_BASE_URL.trimEnd('/')

    suspend fun signInWithGoogle(idToken: String): Session {
        val body = JSONObject().put("idToken", idToken).toString()
        val (code, text) = request("POST", "/api/auth/google", body = body)
        if (code == HttpURLConnection.HTTP_UNAUTHORIZED) throw InvalidCredentialsException()
        if (code != HttpURLConnection.HTTP_OK) throw IOException("HTTP $code")

        val json = JSONObject(text)
        val user = json.getJSONObject("user")
        return Session(
            token = json.getString("sessionToken"),
            expiresAtMillis = System.currentTimeMillis() + json.getLong("expiresIn") * 1000,
            isNewUser = json.getBoolean("isNewUser"),
            user = UserInfo(
                id = user.getString("id"),
                email = user.getString("email"),
                firstName = user.getString("firstName"),
                lastName = user.getString("lastName"),
            ),
        )
    }

    suspend fun getMyName(sessionToken: String): String {
        val (code, text) = request("GET", "/api/my-name", sessionToken = sessionToken)
        if (code == HttpURLConnection.HTTP_UNAUTHORIZED) throw UnauthorizedException()
        if (code != HttpURLConnection.HTTP_OK) throw IOException("HTTP $code")

        val json = JSONObject(text)
        return "${json.getString("firstName")} ${json.getString("lastName")}"
    }

    suspend fun getConnectionInfo(sessionToken: String): ConnectionInfo {
        val (code, text) = request("GET", "/api/connection-info", sessionToken = sessionToken)
        if (code == HttpURLConnection.HTTP_UNAUTHORIZED) throw UnauthorizedException()
        if (code != HttpURLConnection.HTTP_OK) throw IOException("HTTP $code")

        val json = JSONObject(text)
        return ConnectionInfo(
            serverIp = json.getString("serverIp"),
            serverTime = json.getString("serverTime"),
            clientIp = json.getString("clientIp"),
        )
    }

    // returns (status code, body)
    // body is the error stream for non-2xx
    private suspend fun request(
        method: String,
        path: String,
        body: String? = null,
        sessionToken: String? = null,
    ): Pair<Int, String> = withContext(Dispatchers.IO) {
        val connection = (URL(baseUrl + path).openConnection() as HttpURLConnection).apply {
            requestMethod = method
            connectTimeout = 5_000
            readTimeout = 5_000
            if (sessionToken != null) setRequestProperty("Authorization", "Bearer $sessionToken")
            if (body != null) {
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
                outputStream.use { it.write(body.toByteArray()) }
            }
        }
        try {
            val code = connection.responseCode
            val stream = if (code < 400) connection.inputStream else connection.errorStream
            code to (stream?.bufferedReader()?.use { it.readText() } ?: "")
        } finally {
            connection.disconnect()
        }
    }
}

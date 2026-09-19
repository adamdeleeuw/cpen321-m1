package com.example.cpen321application.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cpen321application.data.BackendApi
import com.example.cpen321application.data.ConnectionInfo
import com.example.cpen321application.data.UnauthorizedException
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.launch

/**
 * spec: state for the auth page. load() fetches connection info and "my name"
 * from the backend; client time is formatted on-device as "HH:mm:ss GMT+hh:mm".
 */
class ConnectionInfoViewModel : ViewModel() {
    var info by mutableStateOf<ConnectionInfo?>(null)
        private set

    var myName by mutableStateOf<String?>(null)
        private set

    var clientTime by mutableStateOf("")
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun load(sessionToken: String, onUnauthorized: () -> Unit) {
        errorMessage = null
        clientTime = ZonedDateTime.now().format(CLIENT_TIME_FORMAT)
        viewModelScope.launch {
            try {
                info = BackendApi.getConnectionInfo(sessionToken)
                myName = BackendApi.getMyName(sessionToken)
            } catch (e: UnauthorizedException) {
                onUnauthorized()
            } catch (e: Exception) {
                errorMessage = "Could not reach the server"
            }
        }
    }

    private companion object {
        val CLIENT_TIME_FORMAT: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss 'GMT'xxx")
    }
}

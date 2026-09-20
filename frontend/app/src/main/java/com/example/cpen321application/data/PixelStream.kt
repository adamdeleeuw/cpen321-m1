package com.example.cpen321application.data

import android.graphics.Color
import com.example.cpen321application.BuildConfig
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import org.json.JSONObject

sealed interface PixelEvent {
    data object Opened : PixelEvent

    /** [color] is ARGB. */
    data class Update(val x: Int, val y: Int, val color: Int) : PixelEvent
}

/**
 * spec: client for the backend's pixel stream (WebSocket at /ws/pixels).
 * connect() is a cold flow: collecting opens the socket, cancelling closes it.
 * it emits Opened, then one Update per pixel message (anything that isn't a
 * valid pixel is skipped). it completes if the server closes the socket and
 * throws if the connection fails.
 */
object PixelStream {
    private const val PATH = "/ws/pixels"
    private const val NORMAL_CLOSURE = 1000

    // the stream is silent for ~5s between images, so no read timeout;
    // pings detect a dead connection instead
    private val client = OkHttpClient.Builder()
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(0, TimeUnit.SECONDS)
        .pingInterval(30, TimeUnit.SECONDS)
        .build()

    fun connect(): Flow<PixelEvent> = callbackFlow {
        // okhttp accepts an http(s) url for a websocket and upgrades it (https -> wss)
        val request = Request.Builder().url(BuildConfig.API_BASE_URL.trimEnd('/') + PATH).build()
        val socket = client.newWebSocket(request, object : WebSocketListener() {
            // blocking send: applies backpressure to the socket instead of dropping a pixel
            override fun onOpen(webSocket: WebSocket, response: Response) {
                trySendBlocking(PixelEvent.Opened)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                parse(text)?.let { trySendBlocking(it) }
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                close()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                close(t)
            }
        })
        awaitClose { socket.close(NORMAL_CLOSURE, null) }
    }

    private fun parse(text: String): PixelEvent.Update? = runCatching {
        val json = JSONObject(text)
        PixelEvent.Update(json.getInt("x"), json.getInt("y"), Color.parseColor(json.getString("color")))
    }.getOrNull()
}

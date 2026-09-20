package com.example.cpen321application.ui.timer

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * spec: state for the timer page. the user picks hours/minutes/seconds; start() counts
 * down from that duration and cancel() stops it. the countdown is measured against the
 * monotonic clock, so it stays accurate while the page is closed or the app is backgrounded.
 * when it reaches zero the timer returns to the picker and raises `finished`, which sends the
 * user to the penalty challenge; acknowledge() clears it once the challenge is beaten.
 */
class TimerViewModel : ViewModel() {
    var hours by mutableIntStateOf(0)
    var minutes by mutableIntStateOf(0)
    var seconds by mutableIntStateOf(0)

    var running by mutableStateOf(false)
        private set
    var totalMillis by mutableLongStateOf(0L)
        private set
    var remainingMillis by mutableLongStateOf(0L)
        private set

    var finished by mutableStateOf(false)
        private set

    val canStart: Boolean get() = hours + minutes + seconds > 0

    private var countdown: Job? = null

    fun start() {
        if (running || !canStart) return
        totalMillis = ((hours * 60L + minutes) * 60L + seconds) * 1000L
        remainingMillis = totalMillis
        running = true
        countdown = viewModelScope.launch {
            val endsAt = SystemClock.elapsedRealtime() + totalMillis
            while (remainingMillis > 0) {
                delay(FRAME_MILLIS)
                remainingMillis = (endsAt - SystemClock.elapsedRealtime()).coerceAtLeast(0L)
            }
            running = false
            finished = true
        }
    }

    fun cancel() {
        countdown?.cancel()
        running = false
    }

    fun acknowledge() {
        finished = false
    }

    private companion object {
        const val FRAME_MILLIS = 16L // ~60 fps, so the ring unravels smoothly
    }
}

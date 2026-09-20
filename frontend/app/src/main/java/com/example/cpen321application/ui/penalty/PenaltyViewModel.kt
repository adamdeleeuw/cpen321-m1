package com.example.cpen321application.ui.penalty

import android.os.SystemClock
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.delay

enum class PenaltyPhase { AIMING, FLYING, MISSED, SCORED }

/**
 * spec: state for the penalty challenge shown when the timer finishes. the player pulls the
 * ball back and releases (aim, release); the ball then flies under gravity. touching the
 * crossbar wins: the ball rebounds and, shortly after, `celebrating` turns on so the screen
 * can offer the way back. any other outcome (ground, off screen, too long in the air) is a
 * miss: the ball returns to the spot and `attempts` goes up. attempts are unlimited.
 * step() is pure game logic driven by run(), so it can be tested without a clock.
 */
class PenaltyViewModel : ViewModel() {
    var phase by mutableStateOf(PenaltyPhase.AIMING)
        private set
    var ballX by mutableFloatStateOf(PenaltyWorld.SPOT_X)
        private set
    var ballY by mutableFloatStateOf(PenaltyWorld.SPOT_Y)
        private set
    var spin by mutableFloatStateOf(0f)
        private set
    var attempts by mutableIntStateOf(0)
        private set

    // slingshot pull (ball minus finger), only meaningful while `aiming`
    var aiming by mutableStateOf(false)
        private set
    var pullX by mutableFloatStateOf(0f)
        private set
    var pullY by mutableFloatStateOf(0f)
        private set

    // crossbar reaction after a hit: wobble decays from 1 to 0, sinceScore drives its phase
    var wobble by mutableFloatStateOf(0f)
        private set
    var sinceScore by mutableFloatStateOf(0f)
        private set
    var celebrating by mutableStateOf(false)
        private set

    private var velocityX = 0f
    private var velocityY = 0f
    private var phaseSeconds = 0f

    fun aim(pullX: Float, pullY: Float) {
        if (phase != PenaltyPhase.AIMING) return
        val (x, y) = PenaltyWorld.clampPull(pullX, pullY)
        this.pullX = x
        this.pullY = y
        aiming = true
    }

    fun cancelAim() {
        aiming = false
    }

    fun release() {
        if (phase != PenaltyPhase.AIMING || !aiming) return
        aiming = false
        if (!PenaltyWorld.isValidKick(pullX, pullY)) return
        val (vx, vy) = PenaltyWorld.launchVelocity(pullX, pullY)
        velocityX = vx
        velocityY = vy
        phaseSeconds = 0f
        phase = PenaltyPhase.FLYING
    }

    /** back to a fresh challenge: ball on the spot, no attempts */
    fun reset() {
        resetBall()
        attempts = 0
        wobble = 0f
        sinceScore = 0f
        celebrating = false
    }

    fun step(dtSeconds: Float) {
        var left = dtSeconds
        while (left > 0f && phase != PenaltyPhase.AIMING) {
            val h = minOf(left, PenaltyWorld.SUBSTEP)
            advance(h)
            left -= h
        }
    }

    // ~60 fps loop, dt from the monotonic clock; cancelled with the screen's composition
    suspend fun run() {
        var last = SystemClock.elapsedRealtime()
        while (true) {
            delay(FRAME_MILLIS)
            val now = SystemClock.elapsedRealtime()
            step(((now - last) / 1000f).coerceAtMost(MAX_FRAME_SECONDS))
            last = now
        }
    }

    private fun advance(h: Float) {
        phaseSeconds += h
        when (phase) {
            PenaltyPhase.FLYING -> fly(h)
            PenaltyPhase.SCORED -> rebound(h)
            PenaltyPhase.MISSED -> if (phaseSeconds >= PenaltyWorld.MISS_RESET_SECONDS) resetBall()
            PenaltyPhase.AIMING -> Unit
        }
    }

    private fun move(h: Float) {
        velocityY += PenaltyWorld.GRAVITY * h
        ballX += velocityX * h
        ballY += velocityY * h
        spin += velocityX * h * SPIN_PER_UNIT
    }

    private fun fly(h: Float) {
        move(h)
        val onGround = ballY + PenaltyWorld.BALL_RADIUS >= PenaltyWorld.GROUND_Y
        val outside = ballX > PenaltyWorld.WIDTH + PenaltyWorld.BALL_RADIUS ||
            ballX < -PenaltyWorld.BALL_RADIUS
        if (PenaltyWorld.touchesBar(ballX, ballY)) {
            phase = PenaltyPhase.SCORED
            phaseSeconds = 0f
            sinceScore = 0f
            wobble = 1f
            velocityX *= HIT_KEEP_X
            velocityY = -velocityY * HIT_KEEP_Y
        } else if (onGround || outside || phaseSeconds > PenaltyWorld.MAX_FLIGHT_SECONDS) {
            phase = PenaltyPhase.MISSED
            phaseSeconds = 0f
            attempts++
        }
    }

    private fun rebound(h: Float) {
        move(h)
        sinceScore += h
        wobble *= (1f - WOBBLE_DECAY * h).coerceAtLeast(0f)
        if (ballY + PenaltyWorld.BALL_RADIUS >= PenaltyWorld.GROUND_Y && velocityY > 0f) {
            ballY = PenaltyWorld.GROUND_Y - PenaltyWorld.BALL_RADIUS
            velocityY = -velocityY * BOUNCE_KEEP
            velocityX *= GROUND_FRICTION
        }
        if (phaseSeconds >= PenaltyWorld.CELEBRATE_AFTER_SECONDS) celebrating = true
    }

    private fun resetBall() {
        phase = PenaltyPhase.AIMING
        ballX = PenaltyWorld.SPOT_X
        ballY = PenaltyWorld.SPOT_Y
        velocityX = 0f
        velocityY = 0f
        phaseSeconds = 0f
        aiming = false
        pullX = 0f
        pullY = 0f
    }

    private companion object {
        const val FRAME_MILLIS = 16L
        const val MAX_FRAME_SECONDS = 0.05f // a stalled frame must not fast-forward the ball
        const val SPIN_PER_UNIT = 12f
        const val HIT_KEEP_X = 0.5f
        const val HIT_KEEP_Y = 0.4f
        const val BOUNCE_KEEP = 0.4f
        const val GROUND_FRICTION = 0.8f
        const val WOBBLE_DECAY = 6f
    }
}

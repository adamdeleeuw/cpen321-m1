package com.example.cpen321application.ui.penalty

import kotlin.math.hypot
import kotlin.math.min

/**
 * spec: the fixed game space of the penalty challenge, in world units (x right, y down).
 * every device draws the same 1.0 x 1.6 world, scaled and centred, so the physics never
 * depends on screen size. the ball is kicked slingshot style: the pull-back vector
 * (ball minus finger) sets the launch velocity.
 */
internal object PenaltyWorld {
    const val WIDTH = 1f
    const val HEIGHT = 1.6f

    const val GROUND_Y = 1.3f
    const val BALL_RADIUS = 0.04f
    const val SPOT_X = 0.24f // clear of the left edge, so a pull-back never runs out of room
    const val SPOT_Y = GROUND_Y - BALL_RADIUS

    // the crossbar: a horizontal bar seen from the side, thickness centred on BAR_Y
    const val BAR_LEFT = 0.70f
    const val BAR_RIGHT = 0.96f
    const val BAR_Y = 0.65f
    const val BAR_THICKNESS = 0.03f

    const val GRAVITY = 1.5f
    const val POWER = 8f // launch speed per unit of pull
    const val MIN_PULL = 0.04f
    const val MAX_PULL = 0.25f
    const val GRAB_RADIUS = 0.15f // how close to the ball a drag must start

    const val SUBSTEP = 1f / 240f // fixed physics step: at top speed the ball moves under 0.01 per step
    const val MAX_FLIGHT_SECONDS = 6f
    const val MISS_RESET_SECONDS = 0.6f
    const val CELEBRATE_AFTER_SECONDS = 0.8f

    /** pull vector clamped to MAX_PULL, keeping its direction */
    fun clampPull(pullX: Float, pullY: Float): Pair<Float, Float> {
        val length = hypot(pullX, pullY)
        if (length <= MAX_PULL) return pullX to pullY
        val k = MAX_PULL / length
        return pullX * k to pullY * k
    }

    /** a kick needs a real pull, and must send the ball upward */
    fun isValidKick(pullX: Float, pullY: Float): Boolean =
        hypot(pullX, pullY) >= MIN_PULL && pullY < 0f

    /** launch velocity for a pull vector, as (vx, vy) */
    fun launchVelocity(pullX: Float, pullY: Float): Pair<Float, Float> {
        val (x, y) = clampPull(pullX, pullY)
        return x * POWER to y * POWER
    }

    /** true when a ball of BALL_RADIUS centred at (x, y) overlaps the crossbar */
    fun touchesBar(x: Float, y: Float): Boolean {
        val nearestX = x.coerceIn(BAR_LEFT, BAR_RIGHT)
        val nearestY = y.coerceIn(BAR_Y - BAR_THICKNESS / 2, BAR_Y + BAR_THICKNESS / 2)
        return hypot(x - nearestX, y - nearestY) <= BALL_RADIUS
    }

    /** scale (pixels per world unit) and top-left offset that letterbox the world into a canvas */
    fun fit(widthPx: Float, heightPx: Float): Triple<Float, Float, Float> {
        val scale = min(widthPx / WIDTH, heightPx / HEIGHT)
        return Triple(scale, (widthPx - WIDTH * scale) / 2, (heightPx - HEIGHT * scale) / 2)
    }
}

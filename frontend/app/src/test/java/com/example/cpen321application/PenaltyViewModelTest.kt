package com.example.cpen321application

import com.example.cpen321application.ui.penalty.PenaltyPhase
import com.example.cpen321application.ui.penalty.PenaltyViewModel
import com.example.cpen321application.ui.penalty.PenaltyWorld
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PenaltyViewModelTest {
    private fun kick(viewModel: PenaltyViewModel, pullX: Float, pullY: Float) {
        viewModel.aim(pullX, pullY)
        viewModel.release()
    }

    // (pullX, pullY) of a kick with the given pull length and launch angle in degrees
    private fun pull(length: Float, degrees: Int): Pair<Float, Float> {
        val radians = degrees * PI.toFloat() / 180f
        return length * cos(radians) to -length * sin(radians)
    }

    // every whole-degree, 0.01-length shot that ends touching the crossbar
    private fun hittingShots(): List<Pair<Float, Float>> {
        val hits = mutableListOf<Pair<Float, Float>>()
        for (degrees in 15..85) {
            for (hundredths in 10..25) {
                val (x, y) = pull(hundredths / 100f, degrees)
                val viewModel = PenaltyViewModel()
                kick(viewModel, x, y)
                viewModel.step(3f)
                if (viewModel.phase == PenaltyPhase.SCORED) hits += x to y
            }
        }
        return hits
    }

    @Test
    fun startsAimingOnTheSpot() {
        val viewModel = PenaltyViewModel()
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
        assertEquals(PenaltyWorld.SPOT_X, viewModel.ballX, 0f)
        assertEquals(PenaltyWorld.SPOT_Y, viewModel.ballY, 0f)
        assertEquals(0, viewModel.attempts)
    }

    @Test
    fun crossbarCanBeHitAndOnlySomeShotsHit() {
        val hits = hittingShots()
        assertTrue("no aimed shot hits the crossbar", hits.isNotEmpty())
        assertTrue("the crossbar is too easy to hit", hits.size < 71 * 16 / 4)
    }

    @Test
    fun hitReboundsThenCelebrates() {
        val (x, y) = hittingShots().first()
        val viewModel = PenaltyViewModel()
        kick(viewModel, x, y)
        viewModel.step(3f)
        assertEquals(PenaltyPhase.SCORED, viewModel.phase)
        assertTrue(viewModel.celebrating)
        assertEquals(0, viewModel.attempts)
    }

    @Test
    fun shortShotMissesResetsAndCountsAnAttempt() {
        val viewModel = PenaltyViewModel()
        val (x, y) = pull(0.1f, 45)
        kick(viewModel, x, y)
        assertEquals(PenaltyPhase.FLYING, viewModel.phase)

        viewModel.step(0.8f) // lands well short of the goal, a little before the ball resets
        assertEquals(PenaltyPhase.MISSED, viewModel.phase)
        assertEquals(1, viewModel.attempts)
        assertFalse(viewModel.celebrating)

        viewModel.step(PenaltyWorld.MISS_RESET_SECONDS + 0.1f)
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
        assertEquals(PenaltyWorld.SPOT_X, viewModel.ballX, 0f)
        assertEquals(PenaltyWorld.SPOT_Y, viewModel.ballY, 0f)
    }

    @Test
    fun shotOverTheGoalIsAMiss() {
        val viewModel = PenaltyViewModel()
        val (x, y) = pull(0.25f, 85)
        kick(viewModel, x, y)
        viewModel.step(PenaltyWorld.MAX_FLIGHT_SECONDS + 1f)
        assertTrue(viewModel.attempts >= 1)
        assertFalse(viewModel.celebrating)
    }

    @Test
    fun downwardOrTinyPullsDoNotKick() {
        val viewModel = PenaltyViewModel()
        kick(viewModel, 0.1f, 0.1f) // pulled up, so it would go down
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
        kick(viewModel, 0.01f, -0.01f) // barely pulled
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
        assertEquals(0, viewModel.attempts)
    }

    @Test
    fun releaseWithoutAimingDoesNothing() {
        val viewModel = PenaltyViewModel()
        viewModel.release()
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
    }

    @Test
    fun resetGivesAFreshChallenge() {
        val viewModel = PenaltyViewModel()
        val (x, y) = pull(0.1f, 45)
        kick(viewModel, x, y)
        viewModel.step(3f)
        viewModel.reset()
        assertEquals(PenaltyPhase.AIMING, viewModel.phase)
        assertEquals(0, viewModel.attempts)
        assertFalse(viewModel.celebrating)
    }

    @Test
    fun pullIsClampedToMaxPower() {
        val viewModel = PenaltyViewModel()
        viewModel.aim(5f, -5f)
        val length = kotlin.math.hypot(viewModel.pullX, viewModel.pullY)
        assertEquals(PenaltyWorld.MAX_PULL, length, 1e-4f)
    }

    @Test
    fun crossbarOverlapIsCircleAgainstRectangle() {
        val middle = (PenaltyWorld.BAR_LEFT + PenaltyWorld.BAR_RIGHT) / 2
        assertTrue(PenaltyWorld.touchesBar(middle, PenaltyWorld.BAR_Y))
        assertTrue(PenaltyWorld.touchesBar(middle, PenaltyWorld.BAR_Y + PenaltyWorld.BALL_RADIUS))
        assertFalse(PenaltyWorld.touchesBar(middle, PenaltyWorld.BAR_Y + 0.1f))
        assertFalse(PenaltyWorld.touchesBar(PenaltyWorld.BAR_LEFT - 0.1f, PenaltyWorld.BAR_Y))
    }
}

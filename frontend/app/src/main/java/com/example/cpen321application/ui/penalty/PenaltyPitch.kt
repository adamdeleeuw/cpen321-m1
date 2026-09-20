package com.example.cpen321application.ui.penalty

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private const val STRIPES = 12
private const val POST_WIDTH = 0.014f
private const val NET_SPACING = 0.035f
private const val WOBBLE_AMPLITUDE = 0.012f
private const val WOBBLE_RATE = 45f
private const val GUIDE_SECONDS = 0.28f // short on purpose: aiming stays a skill
private const val GUIDE_DOTS = 7
private const val SHADOW_FADE_HEIGHT = 0.9f

/**
 * spec: draws the penalty scene: striped pitch, goal with a net and the crossbar, the ball with
 * its shadow, and, while the player pulls back, a rubber band plus a short dotted aim guide.
 * game state is read inside the draw block, so a ball move redraws without recomposition.
 */
@Composable
internal fun PenaltyPitch(viewModel: PenaltyViewModel, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.fillMaxSize()) {
        drawGrass()
        val (scale, offsetX, offsetY) = PenaltyWorld.fit(size.width, size.height)
        // everything below is drawn in world units
        withTransform({
            translate(offsetX, offsetY)
            scale(scale, scale, pivot = Offset.Zero)
        }) {
            drawGoal(viewModel)
            drawShadow(viewModel)
            if (viewModel.aiming) drawAim(viewModel)
            drawBall(viewModel)
        }
    }
}

private fun DrawScope.drawGrass() {
    val band = size.height / STRIPES
    for (i in 0 until STRIPES) {
        drawRect(
            color = if (i % 2 == 0) PenaltyColors.GrassLight else PenaltyColors.GrassDark,
            topLeft = Offset(0f, i * band),
            size = Size(size.width, band + 1f) // +1 hides hairline seams between bands
        )
    }
}

private fun DrawScope.drawGoal(viewModel: PenaltyViewModel) {
    val left = PenaltyWorld.BAR_LEFT
    val right = PenaltyWorld.BAR_RIGHT
    val ground = PenaltyWorld.GROUND_Y

    // goal line across the whole pitch
    drawLine(PenaltyColors.Line, Offset(-1f, ground), Offset(2f, ground), strokeWidth = 0.008f)

    // net: a light grid between the posts, from the bar down to the ground
    var x = left
    while (x <= right) {
        drawLine(PenaltyColors.Net, Offset(x, PenaltyWorld.BAR_Y), Offset(x, ground), strokeWidth = 0.004f)
        x += NET_SPACING
    }
    var y = PenaltyWorld.BAR_Y
    while (y <= ground) {
        drawLine(PenaltyColors.Net, Offset(left, y), Offset(right, y), strokeWidth = 0.004f)
        y += NET_SPACING
    }

    drawLine(PenaltyColors.Post, Offset(left, PenaltyWorld.BAR_Y), Offset(left, ground), strokeWidth = POST_WIDTH)
    drawLine(PenaltyColors.Post, Offset(right, PenaltyWorld.BAR_Y), Offset(right, ground), strokeWidth = POST_WIDTH)

    // the crossbar, wobbling and glowing for a moment after a hit
    val wobble = viewModel.wobble
    val shake = sin(viewModel.sinceScore * WOBBLE_RATE) * wobble * WOBBLE_AMPLITUDE
    val barTop = PenaltyWorld.BAR_Y - PenaltyWorld.BAR_THICKNESS / 2 + shake
    if (wobble > 0.01f) {
        drawRect(
            color = PenaltyColors.Accent.copy(alpha = 0.55f * wobble),
            topLeft = Offset(left - 0.02f, barTop - 0.02f),
            size = Size(right - left + 0.04f, PenaltyWorld.BAR_THICKNESS + 0.04f)
        )
    }
    drawRect(
        color = PenaltyColors.Post,
        topLeft = Offset(left, barTop),
        size = Size(right - left, PenaltyWorld.BAR_THICKNESS)
    )
}

private fun DrawScope.drawShadow(viewModel: PenaltyViewModel) {
    val height = (PenaltyWorld.GROUND_Y - PenaltyWorld.BALL_RADIUS - viewModel.ballY).coerceAtLeast(0f)
    val fade = (1f - height / SHADOW_FADE_HEIGHT).coerceIn(0.15f, 1f)
    val radius = PenaltyWorld.BALL_RADIUS * (0.6f + 0.6f * fade)
    drawOval(
        color = PenaltyColors.Shadow.copy(alpha = PenaltyColors.Shadow.alpha * fade),
        topLeft = Offset(viewModel.ballX - radius, PenaltyWorld.GROUND_Y - radius * 0.3f),
        size = Size(radius * 2, radius * 0.6f)
    )
}

private fun DrawScope.drawBall(viewModel: PenaltyViewModel) {
    val centre = Offset(viewModel.ballX, viewModel.ballY)
    val r = PenaltyWorld.BALL_RADIUS
    drawCircle(PenaltyColors.Ball, radius = r, center = centre)

    // centre patch plus five seams, turned by the ball's spin so the rotation reads
    val patch = Path()
    for (i in 0 until 5) {
        val a = viewModel.spin + i * 2 * PI.toFloat() / 5 - PI.toFloat() / 2
        val point = Offset(centre.x + cos(a) * r * 0.42f, centre.y + sin(a) * r * 0.42f)
        if (i == 0) patch.moveTo(point.x, point.y) else patch.lineTo(point.x, point.y)
        val seamEnd = Offset(centre.x + cos(a) * r * 0.95f, centre.y + sin(a) * r * 0.95f)
        drawLine(PenaltyColors.BallPatch, point, seamEnd, strokeWidth = r * 0.12f)
    }
    patch.close()
    drawPath(patch, PenaltyColors.BallPatch)
    drawCircle(PenaltyColors.BallPatch, radius = r, center = centre, style = Stroke(width = r * 0.08f))
}

private fun DrawScope.drawAim(viewModel: PenaltyViewModel) {
    val ball = Offset(viewModel.ballX, viewModel.ballY)
    val finger = Offset(ball.x - viewModel.pullX, ball.y - viewModel.pullY)
    drawLine(PenaltyColors.Guide.copy(alpha = 0.4f), ball, finger, strokeWidth = 0.01f)
    drawCircle(PenaltyColors.Guide.copy(alpha = 0.4f), radius = 0.02f, center = finger)

    if (!PenaltyWorld.isValidKick(viewModel.pullX, viewModel.pullY)) return
    val (vx, vy) = PenaltyWorld.launchVelocity(viewModel.pullX, viewModel.pullY)
    for (i in 1..GUIDE_DOTS) {
        val t = GUIDE_SECONDS * i / GUIDE_DOTS
        val dot = Offset(ball.x + vx * t, ball.y + vy * t + 0.5f * PenaltyWorld.GRAVITY * t * t)
        val fade = 1f - (i - 1f) / GUIDE_DOTS
        drawCircle(PenaltyColors.Guide.copy(alpha = fade), radius = 0.012f * (0.5f + fade / 2), center = dot)
    }
}

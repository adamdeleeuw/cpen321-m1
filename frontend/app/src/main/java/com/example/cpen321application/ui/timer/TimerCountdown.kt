package com.example.cpen321application.ui.timer

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil

private const val TOP = -90f // arcs start at 3 o'clock; this is 12 o'clock

/**
 * spec: the running timer. the time left is centred inside a blue ring that unravels
 * clockwise: its leading end stays at 12 o'clock while its tail eats round the circle.
 */
@Composable
fun TimerCountdown(remainingMillis: Long, totalMillis: Long, modifier: Modifier = Modifier) {
    val remainingFraction = remainingMillis.toFloat() / totalMillis

    Box(modifier = modifier.size(300.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
            val inset = stroke.width / 2
            val arcTopLeft = Offset(inset, inset)
            val arcSize = Size(size.width - stroke.width, size.height - stroke.width)

            drawArc(TimerColors.Track, 0f, 360f, false, arcTopLeft, arcSize, style = stroke)
            drawArc(
                color = TimerColors.Ring,
                startAngle = TOP + 360f * (1 - remainingFraction),
                sweepAngle = 360f * remainingFraction,
                useCenter = false,
                topLeft = arcTopLeft,
                size = arcSize,
                style = stroke
            )
        }
        Text(
            text = clock(remainingMillis),
            color = TimerColors.Digits,
            fontSize = 60.sp,
            fontWeight = FontWeight.Light
        )
    }
}

/** h:mm:ss, or m:ss under an hour. rounds up so the clock reads 0:01 until the very end. */
private fun clock(millis: Long): String {
    val totalSeconds = ceil(millis / 1000.0).toLong()
    val hours = totalSeconds / 3600
    val minutes = totalSeconds % 3600 / 60
    val seconds = totalSeconds % 60
    return if (hours > 0) "%d:%02d:%02d".format(hours, minutes, seconds)
    else "%d:%02d".format(minutes, seconds)
}

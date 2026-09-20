package com.example.cpen321application.ui.websocket

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * spec: page opened by the websocket button. shows the connection status and a
 * 16x16 canvas that starts blank and paints one cell per pixel update as it arrives.
 */
@Composable
fun WebSocketScreen(
    viewModel: WebSocketViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Socket Status: ${viewModel.socketStatus}")

        PixelGrid(
            cells = viewModel.grid,
            // the border keeps the blank (white) canvas visible
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .border(1.dp, MaterialTheme.colorScheme.outline)
        )

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
    }
}

@Composable
private fun PixelGrid(cells: List<Color>, modifier: Modifier = Modifier) {
    // cells are read inside the draw block, so a paint redraws the canvas without recomposing
    Canvas(modifier = modifier) {
        val cell = size.width / GRID_SIZE
        // rounding the shared edges (not the cell size) tiles the grid with no gaps or overlaps
        fun edge(i: Int) = (i * cell).roundToInt().toFloat()

        for (row in 0 until GRID_SIZE) {
            for (col in 0 until GRID_SIZE) {
                drawRect(
                    color = cells[row * GRID_SIZE + col],
                    topLeft = Offset(edge(col), edge(row)),
                    size = Size(edge(col + 1) - edge(col), edge(row + 1) - edge(row))
                )
            }
        }
    }
}

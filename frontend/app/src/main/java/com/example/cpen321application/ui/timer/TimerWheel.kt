package com.example.cpen321application.ui.timer

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.GraphicsLayerScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val RowHeight = 52.dp
private val WheelWidth = 56.dp
private val BandShape = RoundedCornerShape(18.dp)
private const val ROWS_PER_SIDE = 3
private const val ROW_STEP_RADIANS = 25 * PI / 180 // how far each row is turned around the drum

/** the picker's full height, so the page can hold one stage height across both of its modes. */
internal val TimerPickerHeight = RowHeight * (2 * ROWS_PER_SIDE + 1)

/**
 * spec: the hours / minutes / seconds pickers side by side, sharing one glass band that
 * marks the selected row.
 */
@Composable
fun TimerPicker(viewModel: TimerViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.fillMaxWidth().height(RowHeight).glassSurface(BandShape))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TimerWheel(count = 24, selected = viewModel.hours, label = "hr") { viewModel.hours = it }
            TimerWheel(count = 60, selected = viewModel.minutes, label = "min") { viewModel.minutes = it }
            TimerWheel(count = 60, selected = viewModel.seconds, label = "sec") { viewModel.seconds = it }
        }
    }
}

/**
 * spec: one vertical wheel of the numbers 0 until [count]. it snaps to a row, and rows curl
 * away from the centre as if wrapped around a drum receding into the screen. the list is not
 * looped, so at 0 nothing sits above the selection and only the next few digits fall away below.
 */
@Composable
private fun TimerWheel(
    count: Int,
    selected: Int,
    label: String,
    onSelected: (Int) -> Unit
) {
    val state = rememberLazyListState(initialFirstVisibleItemIndex = selected)

    // report only once the wheel has settled, so a fling doesn't spam the view model.
    // snapping rests a row exactly at the scroll origin, so that row is the selection
    LaunchedEffect(state) {
        snapshotFlow { state.isScrollInProgress }
            .filter { !it }
            .map { state.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect(onSelected)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        LazyColumn(
            state = state,
            flingBehavior = rememberSnapFlingBehavior(state),
            // padding lets the first and last numbers scroll all the way to the centre row
            contentPadding = PaddingValues(vertical = RowHeight * ROWS_PER_SIDE),
            modifier = Modifier.width(WheelWidth).height(TimerPickerHeight)
        ) {
            items(count) { index ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(RowHeight)
                        .graphicsLayer { curl(index, state) },
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Text(
                        text = "%02d".format(index),
                        color = TimerColors.Digits,
                        // sits under the page title rather than level with it, and leaves the
                        // glass band room to read as a surface the digits rest on
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
        Text(
            text = label,
            color = TimerColors.Label,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 6.dp).width(30.dp)
        )
    }
}

/**
 * Wraps a row onto a drum: a row [rows] away from the centre is turned by rows * step, so
 * it is squeezed towards the centre (its layout slot is at rows * height), narrowed and faded.
 */
private fun GraphicsLayerScope.curl(index: Int, state: LazyListState) {
    val scrolledRows = state.firstVisibleItemIndex + state.firstVisibleItemScrollOffset / size.height
    val rows = index - scrolledRows
    val angle = (rows * ROW_STEP_RADIANS).coerceIn(-PI / 2, PI / 2)
    val drumRadius = size.height / ROW_STEP_RADIANS

    translationY = (drumRadius * sin(angle) - rows * size.height).toFloat()
    val facing = cos(angle).toFloat()
    scaleY = facing
    scaleX = 0.85f + 0.15f * facing
    alpha = facing * facing
}

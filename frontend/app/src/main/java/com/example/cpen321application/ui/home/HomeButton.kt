package com.example.cpen321application.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ButtonShape = RoundedCornerShape(22.dp)
private val ButtonPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp)
private val CompactPadding = PaddingValues(horizontal = 12.dp, vertical = 18.dp)
private val ButtonLift = 10.dp

/**
 * spec: the shared home page button. a flat chocolate-brown fill under crisp white text, with
 * the same corners, padding and soft shadow on every one of them so they read as a set.
 * `compact` keeps all of that but pulls the sides in and sizes the button to its own text,
 * for pages where a full-width button would take over.
 */
@Composable
internal fun HomeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false
) {
    val fill = if (enabled) HomeColors.Button else HomeColors.ButtonDisabled

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = if (compact) CompactPadding else ButtonPadding,
        elevation = null, // the shadow is drawn below instead, so it follows the fill
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = HomeColors.OnButton,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = HomeColors.OnButton.copy(alpha = 0.75f)
        ),
        modifier = modifier
            .then(if (compact) Modifier else Modifier.fillMaxWidth())
            .shadow(
                elevation = if (enabled) ButtonLift else 0.dp,
                shape = ButtonShape,
                ambientColor = HomeColors.ButtonShadow,
                spotColor = HomeColors.ButtonShadow
            )
            .background(color = fill, shape = ButtonShape)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

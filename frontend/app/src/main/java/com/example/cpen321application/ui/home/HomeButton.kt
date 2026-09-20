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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

private val ButtonShape = RoundedCornerShape(22.dp)
private val ButtonPadding = PaddingValues(horizontal = 24.dp, vertical = 18.dp)
private val ButtonLift = 10.dp

/**
 * spec: the shared home page button. a dusty-rose to plum fill under white text, with the
 * same corners, padding and soft shadow on every one of them so the three read as a set.
 */
@Composable
internal fun HomeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    val fill =
        if (enabled) listOf(HomeColors.ButtonTop, HomeColors.ButtonBottom)
        else listOf(HomeColors.ButtonDisabled, HomeColors.ButtonDisabled)

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = ButtonShape,
        contentPadding = ButtonPadding,
        elevation = null, // the shadow is drawn below instead, so it follows the gradient fill
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = HomeColors.OnButton,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = HomeColors.OnButton.copy(alpha = 0.75f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (enabled) ButtonLift else 0.dp,
                shape = ButtonShape,
                ambientColor = HomeColors.ButtonShadow,
                spotColor = HomeColors.ButtonShadow
            )
            .background(brush = Brush.verticalGradient(fill), shape = ButtonShape)
    ) {
        Text(
            text = text,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center
        )
    }
}

package com.example.lovebyte.ui.components.general

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// re-used pixel button :]
@Composable
fun PixelButton(
    text: String,
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    // allow to modify button color
    textColor: Color = Color.White
) {
    val pixelShape = CutCornerShape(8.dp) // simulate pixelization by cutting the edges
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .border(3.dp, Color(0xFF5D4037), pixelShape),
        shape = pixelShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color.Gray, // when the button is not enabled/usable, make it visually apparent
            contentColor = textColor
        ),
        // keep it "flat" to make it more "vintage"
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge, // so it will be pixelated
            color = textColor
        )
    }
}
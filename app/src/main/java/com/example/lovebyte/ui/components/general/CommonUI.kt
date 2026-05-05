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
    text: String? = null, // make nullable
    onClick: () -> Unit,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    textColor: Color = Color.White,
    content: (@Composable () -> Unit)? = null // NEW
) {
    val pixelShape = CutCornerShape(8.dp)

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier
            .height(56.dp)
            .border(3.dp, Color(0xFF5D4037), pixelShape),
        shape = pixelShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            disabledContainerColor = Color.Gray,
            contentColor = textColor
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
    ) {
        if (content != null) {
            content()
        } else if (text != null) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }
    }
}
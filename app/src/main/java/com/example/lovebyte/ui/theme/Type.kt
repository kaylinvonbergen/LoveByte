package com.example.lovebyte.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.lovebyte.R
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.unit.sp

// import the pixel font we're using
val PixelFont = FontFamily(
    Font(R.font.dot_gothic, FontWeight.Normal)
)


// map the custom font to the typography
val Typography = Typography(
    // dialogue - keep this as default for readability
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontSize = 16.sp
    ),
    // ui/headers : Pixel
    headlineMedium = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = PixelFont,
        fontSize = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = PixelFont,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
    )
)

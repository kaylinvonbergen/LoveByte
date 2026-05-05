package com.example.lovebyte.ui.components.general

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.R
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage

@Composable
fun LoveByteHeader(
    heroLanguage: ProgrammingLanguage,
    state: LoveByteState,
    deepPink: Color,
    inkBrown: Color,
    pixelWhite: Color,
    pixelRoundedShape: CutCornerShape
) {
    // profile photo + dynamic greeting from weather :3
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            modifier = Modifier
                .size(68.dp)
                .border(4.dp, deepPink, pixelRoundedShape),
            shape = pixelRoundedShape,
            color = pixelWhite
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (heroLanguage == ProgrammingLanguage.PYTHON) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_python),
                        contentDescription = "Python Logo",
                        modifier = Modifier.padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // fallback for languages without logos yet (like Kotlin)
                    Text(
                        text = heroLanguage.displayName.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineMedium, // pixelated
                        color = deepPink
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Surface(
            modifier = Modifier.weight(1f),
            shape = CutCornerShape(topStart = 0.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp),
            color = pixelWhite,
            border = BorderStroke(3.dp, deepPink)
        ) {
            Column(Modifier.padding(12.dp)) {
                // header label: now shows the language name (e.g. PYTHON)
                Text(
                    text = heroLanguage.displayName.uppercase(),
                    style = MaterialTheme.typography.labelLarge,
                    color = deepPink,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                val greetingText = if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                    "It's ${state.weatherDescription.lowercase()} in ${state.cityName}! Time for some ${heroLanguage.displayName}!"
                } else {
                    "Hey, you're back! Time to learn some ${heroLanguage.displayName}!"
                }

                Text(
                    text = greetingText,
                    style = MaterialTheme.typography.bodyLarge,
                    color = inkBrown
                )
            }
        }
    }
}
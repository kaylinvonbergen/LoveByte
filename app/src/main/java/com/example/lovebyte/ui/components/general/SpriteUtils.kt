package com.example.lovebyte.ui.components.general

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import com.example.lovebyte.R

@Composable
fun getSpriteForCharacter(character: String, emotion: String): Painter {
    val resId = when (character.uppercase()) {
        "PYTHON" -> when (emotion) {
            "Friendly" -> R.drawable.python_sprite_friendly
            "Blushing" -> R.drawable.python_sprite_blushing
            "Encouraging" -> R.drawable.python_sprite_encouraging
            "Excited" -> R.drawable.python_sprite_excited
            "Explaining" -> R.drawable.python_sprite_explaining
            "Laughing" -> R.drawable.python_sprite_laughing
            "Pensive" -> R.drawable.python_sprite_pensive
            "Serious" -> R.drawable.python_sprite_serious
            "Thinking" -> R.drawable.python_sprite_thinking
            "Neutral" -> R.drawable.python_sprite_neutral
            // default to friendly if an emotion string is missing or misspelled
            else -> R.drawable.python_sprite_friendly
        }
        // KOTLIN sprites don't actually exist, just a placeholder in case we add them

        //"KOTLIN" -> when (emotion) {
            //"Happy" -> R.drawable.kotlin_sprite_happy //
            //else -> R.drawable.kotlin_sprite_neutral
        //}
        else -> R.drawable.ic_launcher_foreground // built-in fallback
    }

    return painterResource(id = resId)
}
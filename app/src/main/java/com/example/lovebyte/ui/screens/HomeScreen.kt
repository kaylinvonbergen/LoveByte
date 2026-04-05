package com.example.lovebyte.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lovebyte.ui.theme.LoveByteTheme

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Chapter


@Composable
fun HomeScreen(
    state: LoveByteState, // this will come from the ViewModel
    onContinueClicked: () -> Unit,
    onSwapClicked: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 1. Header Section (Profile photo + Dynamic Greeting from weather)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // placeholder for "Profile Photo"
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                // we'll eventually put an Image() here, corresponding to current route
            }

            Spacer(modifier = Modifier.width(16.dp))

            // dynamic message based on Weather :3
            Text(
                text = "Hey, it's ${state.weatherDescription.lowercase()} in ${state.cityName}. Perfect time for some ${state.currentLanguage.displayName}!",
                style = MaterialTheme.typography.titleMedium
            )
        }

        // 2. Progress Section
        Card(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("Current Progress", style = MaterialTheme.typography.labelLarge)
                Spacer(Modifier.height(8.dp))

                // Progress Bar
                LinearProgressIndicator(
                    progress = { 0.4f }, // hardcoded for now, Anna will fix later
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))
                Text(
                    text = if (state.currentLanguage != ProgrammingLanguage.NONE)
                        "${state.currentLanguage.displayName}: Chapter ${state.currentChapter}"
                    else "No routes started!",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f)) // pushes buttons to the bottom

        // 3. Action Buttons
        Button(
            onClick = onContinueClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = state.currentLanguage != ProgrammingLanguage.NONE
        ) {
            Text(if (state.currentLanguage != ProgrammingLanguage.NONE) "Continue" else "Choose a Route")
        }

        OutlinedButton(
            onClick = onSwapClicked,
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Swap Routes")
        }
    }
}
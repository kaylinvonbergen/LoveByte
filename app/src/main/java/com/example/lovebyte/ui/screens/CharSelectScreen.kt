package com.example.lovebyte.ui.screens
//CharSelectScreen.kt

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch


import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape


import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward


import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CharSelectScreen(
    state: LoveByteState,
    onCharacterSelected: (ProgrammingLanguage) -> Unit,
    onBackPressed: () -> Unit
) {
    // 1. Setup the Pager state
    // inside CharSelectScreen
    val languages = ProgrammingLanguage.entries.filter { it != ProgrammingLanguage.NONE }

// this identifies the character message for the header
    val heroLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) {
        state.currentLanguage
    } else {
        ProgrammingLanguage.PYTHON
    }
    val pagerState = rememberPagerState { languages.size }
    val scope = rememberCoroutineScope()


    // state for the info popup
    var showInfoPopup by androidx.compose.runtime.remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // dynamic profile photo
            Surface(
                modifier = Modifier.size(64.dp),
                shape = CircleShape,
                // You can eventually map this to heroLanguage.themeColor
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    // placeholder: show the first letter of the hero language
                    Text(
                        text = heroLanguage.displayName.take(1),
                        style = MaterialTheme.typography.headlineSmall
                    )

                    /* Future:
                    Image(
                        painter = painterResource(id = heroLanguage.iconRes),
                        contentDescription = null
                    )
                    */
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            val headerText =
                if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                    "It's ${state.weatherDescription.lowercase()} in ${state.cityName}! Do some ${heroLanguage.displayName}!"
                } else {
                    "Hey, you're back! Time to learn some ${heroLanguage.displayName}!"
                }

            Text(
                text = headerText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(Modifier.height(16.dp))
        Text("Select your Language!", style = MaterialTheme.typography.headlineSmall)

        // 2. The carousel section
        Row(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left Arrow
            IconButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) }
                },
                enabled = pagerState.currentPage > 0
            ) {
                Icon(Icons.Default.ArrowBack, "Prev")
            }

            // The Swippable Sprite Area
            HorizontalPager(
                // https://developer.android.com/develop/ui/compose/layouts/pager
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                val lang = languages[page]
                val currentChapter = state.progressMap[lang] ?: 1
                val completedChapters = (currentChapter - 1).coerceAtLeast(0)
                val percent = ((completedChapters.toFloat() / lang.totalChapters.toFloat()) * 100).toInt()

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // placeholder for sprite
                    Surface(
                        modifier = Modifier.size(250.dp),
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.surfaceVariant
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("${lang.displayName} Sprite")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                    Text("$percent% completed with ${lang.displayName}!")
                }
            }

            // Right Arrow
            IconButton(
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                },
                enabled = pagerState.currentPage < languages.size - 1
            ) {
                Icon(androidx.compose.material.icons.Icons.Default.ArrowForward, "Next")
            }
        }

        // 3. Footer Buttons
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedButton(
                onClick = { showInfoPopup = true },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Info")
            }

            Button(
                onClick = { onCharacterSelected(languages[pagerState.currentPage]) },
                modifier = Modifier.weight(1f).height(56.dp)
            ) {
                Text("Select")
            }
        }
    }

    // 4. The Info Popup (AlertDialog)
    if (showInfoPopup) {
        val currentLang = languages[pagerState.currentPage]
     AlertDialog(
            onDismissRequest = { showInfoPopup = false },
            title = { Text(currentLang.displayName) },
            text = { Text(currentLang.description) },
            confirmButton = {
                TextButton(onClick = { showInfoPopup = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
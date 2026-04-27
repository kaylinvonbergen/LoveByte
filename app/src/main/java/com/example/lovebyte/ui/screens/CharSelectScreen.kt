package com.example.lovebyte.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import kotlinx.coroutines.launch
import com.example.lovebyte.ui.components.general.PixelButton

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharSelectScreen(
    state: LoveByteState,
    onCharacterSelected: (ProgrammingLanguage) -> Unit,
    onBackPressed: () -> Unit
) {
    // 1. Setup the Pager state
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
    var showInfoPopup by remember { mutableStateOf(false) }

    // color palette
    val sakuraPink = Color(0xFFFFB7C5)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val pixelRoundedShape = CutCornerShape(8.dp)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFFF5F7)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // header section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // dynamic profile photo
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .border(3.dp, deepPink, pixelRoundedShape),
                    shape = pixelRoundedShape,
                    color = pixelWhite
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        // placeholder: show the first letter of the hero language
                        Text(
                            text = heroLanguage.displayName.take(1),
                            style = MaterialTheme.typography.headlineMedium, // Pixelated
                            color = deepPink
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

                // speech bubble header
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = CutCornerShape(topStart = 0.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp),
                    color = pixelWhite,
                    border = BorderStroke(3.dp, deepPink)
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            text = "SYSTEM",
                            style = MaterialTheme.typography.labelLarge, // pixelated
                            color = deepPink,
                            fontSize = 12.sp
                        )
                        val headerText =
                            if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                                "It's ${state.weatherDescription.lowercase()} in ${state.cityName}! Do some ${heroLanguage.displayName}!"
                            } else {
                                "Hey, you're back! Time to learn some ${heroLanguage.displayName}!"
                            }

                        Text(
                            text = headerText,
                            style = MaterialTheme.typography.bodyLarge, // standard font
                            color = inkBrown
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = "SELECT YOUR LANGUAGE",
                style = MaterialTheme.typography.titleMedium, // pixelated
                color = deepPink
            )

            // 2. the carousel section
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
                    Icon(Icons.Default.ArrowBack, "Prev", tint = deepPink)
                }

                // the Swippable Sprite Area
                HorizontalPager(
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
                            modifier = Modifier
                                .size(280.dp)
                                .border(4.dp, deepPink.copy(alpha = 0.4f), pixelRoundedShape),
                            shape = pixelRoundedShape,
                            color = sakuraPink.copy(alpha = 0.1f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "${lang.displayName} Sprite",
                                    style = MaterialTheme.typography.labelLarge, // Pixelated
                                    color = deepPink
                                )
                            }
                        }

                        Spacer(Modifier.height(24.dp))

                        Text(
                            text = "$percent% COMPLETED",
                            style = MaterialTheme.typography.labelLarge, // Pixelated
                            color = inkBrown
                        )
                        Text(
                            text = lang.displayName.uppercase(),
                            style = MaterialTheme.typography.headlineMedium, // Pixelated
                            color = deepPink
                        )
                    }
                }

                // right arrow
                IconButton(
                    onClick = {
                        scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) }
                    },
                    enabled = pagerState.currentPage < languages.size - 1
                ) {
                    Icon(Icons.Default.ArrowForward, "Next", tint = deepPink)
                }
            }

            // 3. footer buttons
            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // secondary "Info" button
                PixelButton(
                    text = "INFO",
                    onClick = { showInfoPopup = true },
                    color = Color(0xFFB19CD9), // Lavender
                    modifier = Modifier.weight(1f)
                )

                // primary "Select" button
                PixelButton(
                    text = "SELECT",
                    onClick = { onCharacterSelected(languages[pagerState.currentPage]) },
                    color = deepPink,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    // 4. info popup (AlertDialog)
    if (showInfoPopup) {
        val currentLang = languages[pagerState.currentPage]
        AlertDialog(
            onDismissRequest = { showInfoPopup = false },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = {
                Text(
                    text = currentLang.displayName.uppercase(),
                    style = MaterialTheme.typography.titleMedium, // Pixelated
                    color = deepPink
                )
            },
            text = {
                Text(
                    text = currentLang.description,
                    style = MaterialTheme.typography.bodyLarge, // Standard font
                    color = inkBrown
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoPopup = false }) {
                    Text("GOT IT", style = MaterialTheme.typography.labelLarge, color = deepPink)
                }
            }
        )
    }
}


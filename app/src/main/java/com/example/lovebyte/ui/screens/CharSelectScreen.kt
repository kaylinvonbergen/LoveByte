package com.example.lovebyte.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.SentimentScore
import kotlinx.coroutines.launch
import com.example.lovebyte.ui.components.general.PixelButton

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
fun CharSelectScreen(
    state: LoveByteState,
    onCharacterSelected: (ProgrammingLanguage) -> Unit,
    onBackPressed: () -> Unit
) {
    val languages = ProgrammingLanguage.entries.filter { it != ProgrammingLanguage.NONE }
    val heroLanguage = if (state.currentLanguage != ProgrammingLanguage.NONE) state.currentLanguage else ProgrammingLanguage.PYTHON

    val pagerState = rememberPagerState { languages.size }
    val scope = rememberCoroutineScope()
    var showInfoPopup by remember { mutableStateOf(false) }

    // checks for screen configuration to adapt layout
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // set the cutiepie colors so we can use them later
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
            // 1. header section (profile photo + dynamic greeting from weather :3)
            HeaderSection(heroLanguage, state, deepPink, inkBrown, pixelWhite, pixelRoundedShape)

            Spacer(Modifier.height(if (isLandscape) 16.dp else 24.dp))

            Text(
                text = "SELECT YOUR LANGUAGE",
                style = MaterialTheme.typography.titleMedium, // pixelated
                color = deepPink
            )

            // 2. The Carousel/Selection Area
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (isLandscape) {
                    // LANDSCAPE: force a 50/50 split to ensure the sprite stays centered in its half
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // left: the sprite carousel
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            CarouselPager(
                                languages, pagerState, scope, deepPink, sakuraPink,
                                inkBrown, pixelRoundedShape, isLandscape, state
                            )
                        }

                        // right: language name/action buttons
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            val currentLang = languages[pagerState.currentPage]

                            Text(
                                text = currentLang.displayName.uppercase(),
                                style = MaterialTheme.typography.headlineMedium, // pixelated
                                color = deepPink
                            )

                            Spacer(Modifier.height(24.dp))

                            FooterButtons(
                                onInfoClick = { showInfoPopup = true },
                                onSelectClick = { onCharacterSelected(languages[pagerState.currentPage]) },
                                deepPink = deepPink,
                                modifier = Modifier.fillMaxWidth(0.8f) // narrower for better pixel-look
                            )
                        }
                    }
                } else {
                    // PORTRAIT: standard centered stack
                    CarouselPager(
                        languages, pagerState, scope, deepPink, sakuraPink,
                        inkBrown, pixelRoundedShape, isLandscape, state
                    )
                }
            }

            // 3. footer buttons (Portrait only)
            if (!isLandscape) {
                FooterButtons(
                    onInfoClick = { showInfoPopup = true },
                    onSelectClick = { onCharacterSelected(languages[pagerState.currentPage]) },
                    deepPink = deepPink,
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )
            }
        }
    }

    // 4. info popup (AlertDialog stays same)
    if (showInfoPopup) {
        val currentLang = languages[pagerState.currentPage]
        val sentiment = state.sentimentMap[currentLang] ?: SentimentScore()
        AlertDialog(
            onDismissRequest = { showInfoPopup = false },
            shape = pixelRoundedShape,
            containerColor = pixelWhite,
            modifier = Modifier.border(4.dp, deepPink, pixelRoundedShape),
            title = {
                Text(text = currentLang.displayName.uppercase(), style = MaterialTheme.typography.titleMedium, color = deepPink)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = currentLang.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = inkBrown
                    )

                    Spacer(Modifier.height(4.dp))

                    SentimentBar("LOVE", sentiment.love, deepPink, inkBrown)
                    SentimentBar("FRIEND", sentiment.friend, Color(0xFFB19CD9), inkBrown)
                    SentimentBar("HATE", sentiment.hate, MaterialTheme.colorScheme.error, inkBrown)
                }
            },
            confirmButton = {
                TextButton(onClick = { showInfoPopup = false }) {
                    Text("GOT IT", style = MaterialTheme.typography.labelLarge, color = deepPink)
                }
            }
        )
    }
}

@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Composable
private fun CarouselPager(
    languages: List<ProgrammingLanguage>,
    pagerState: androidx.compose.foundation.pager.PagerState,
    scope: kotlinx.coroutines.CoroutineScope,
    deepPink: Color,
    sakuraPink: Color,
    inkBrown: Color,
    pixelRoundedShape: CutCornerShape,
    isLandscape: Boolean,
    state: LoveByteState
) {
    // This row contains the arrows and the pager
    Row(
        modifier = Modifier.fillMaxWidth(), // fill allocated half of the screen
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // center the whole group
    ) {
        // Left Arrow
        IconButton(
            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
            enabled = pagerState.currentPage > 0
        ) {
            Icon(Icons.Default.ArrowBack, "Prev", tint = deepPink)
        }

        // Swippable Area - Using a Box to ensure the content inside stays centered
        Box(
            modifier = Modifier.width(if (isLandscape) 260.dp else 280.dp),
            contentAlignment = Alignment.Center
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth()
            ) { page ->
                val lang = languages[page]
                val currentChapter = state.progressMap[lang] ?: 1
                val completedChapters = (currentChapter - 1).coerceAtLeast(0)
                val percent = ((completedChapters.toFloat() / lang.totalChapters.toFloat()) * 100).toInt()

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // placeholder for sprite
                    Surface(
                        modifier = Modifier
                            .size(if (isLandscape) 200.dp else 280.dp)
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

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "$percent% COMPLETED",
                        style = MaterialTheme.typography.labelLarge,
                        color = inkBrown,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // right arrow
        IconButton(
            onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
            enabled = pagerState.currentPage < languages.size - 1
        ) {
            Icon(Icons.Default.ArrowForward, "Next", tint = deepPink)
        }
    }
}

@Composable
private fun HeaderSection(
    heroLanguage: ProgrammingLanguage,
    state: LoveByteState,
    deepPink: Color,
    inkBrown: Color,
    pixelWhite: Color,
    pixelRoundedShape: CutCornerShape
) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Surface(
            modifier = Modifier.size(64.dp).border(3.dp, deepPink, pixelRoundedShape),
            shape = pixelRoundedShape,
            color = pixelWhite
        ) {
            Box(contentAlignment = Alignment.Center) {
                // TODO: replace with sprite once we make them
                Text(text = heroLanguage.displayName.take(1), style = MaterialTheme.typography.headlineMedium, color = deepPink)
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
            modifier = Modifier.weight(1f),
            shape = CutCornerShape(topStart = 0.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp),
            color = pixelWhite,
            border = BorderStroke(3.dp, deepPink)
        ) {
            Column(Modifier.padding(12.dp)) {
                Text(text = "SYSTEM", style = MaterialTheme.typography.labelLarge, color = deepPink, fontSize = 12.sp)
                val headerText = if (state.cityName.isNotBlank() && state.weatherDescription.isNotBlank()) {
                    "It's ${state.weatherDescription.lowercase()} in ${state.cityName}! Do some ${heroLanguage.displayName}!"
                } else {
                    "Hey, you're back! Time to learn some ${heroLanguage.displayName}!"
                }
                Text(text = headerText, style = MaterialTheme.typography.bodyLarge, color = inkBrown)
            }
        }
    }
}

@Composable
private fun FooterButtons(
    onInfoClick: () -> Unit,
    onSelectClick: () -> Unit,
    deepPink: Color,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        PixelButton(
            text = "INFO",
            modifier = Modifier
                .weight(1f)
                .testTag("info_button"),
            onClick = onInfoClick,
            color = Color(0xFFB19CD9),

        )
        PixelButton(
            text = "SELECT",
            modifier = Modifier
                .weight(1f)
                .testTag("select_button"),
            onClick = onSelectClick,
            color = deepPink,

        )
    }
}

@Composable
private fun SentimentBar(
    label: String,
    value: Int,
    color: Color,
    textColor: Color
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )

            Text(
                text = "$value / 50",
                style = MaterialTheme.typography.labelLarge,
                color = textColor
            )
        }

        LinearProgressIndicator(
            progress = { value.coerceIn(0, 50) / 50f },
            modifier = Modifier.fillMaxWidth(),
            color = color,
            trackColor = color.copy(alpha = 0.2f)
        )
    }
}
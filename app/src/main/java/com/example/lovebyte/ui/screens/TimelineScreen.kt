package com.example.lovebyte.ui.screens

import android.content.res.Configuration
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.Chapter
import com.example.lovebyte.data.model.ProgrammingLanguage

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    state: LoveByteState,
    onChapterSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    val currentLang = if (state.currentLanguage != ProgrammingLanguage.NONE) {
        state.currentLanguage
    } else {
        ProgrammingLanguage.PYTHON
    }

    val sections = currentLang.sections
    // check for configuration
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // set the cutiepie colors so we can use them later
    val sakuraPink = Color(0xFFFFB7C5)
    val deepPink = Color(0xFFFF85A1)
    val inkBrown = Color(0xFF5D4037)
    val pixelWhite = Color(0xFFFFFFFF)
    val pixelRoundedShape = CutCornerShape(8.dp)

    Scaffold(
        containerColor = Color(0xFFFFF5F7),
        topBar = {
            Surface(
                modifier = Modifier
                    .statusBarsPadding()
                    .fillMaxWidth()
                    .height(if (isLandscape) 48.dp else 64.dp) // Slimmer top bar for landscape
                    .drawBehind {
                        val strokeWidth = 3.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = deepPink,
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    },
                color = pixelWhite
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Character Selection",
                            tint = deepPink
                        )
                    }
                    Text(
                        text = "TIMELINE",
                        style = MaterialTheme.typography.titleMedium,
                        color = deepPink
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Header: profile + name + total progress
            Surface(
                color = sakuraPink.copy(alpha = 0.2f),
                modifier = Modifier
                    .fillMaxWidth()
                    .drawBehind {
                        val strokeWidth = 2.dp.toPx()
                        val y = size.height - strokeWidth / 2
                        drawLine(
                            color = deepPink.copy(alpha = 0.3f),
                            start = Offset(0f, y),
                            end = Offset(size.width, y),
                            strokeWidth = strokeWidth
                        )
                    }
            ) {
                if (isLandscape) {
                    // LANDSCAPE HEADER: scrollable Row to save vertical space
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(horizontal = 24.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .border(2.dp, deepPink, pixelRoundedShape),
                            shape = pixelRoundedShape,
                            color = pixelWhite
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentLang.displayName.take(1),
                                    color = deepPink,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                        }

                        Spacer(Modifier.width(12.dp))

                        Text(
                            text = currentLang.displayName,
                            style = MaterialTheme.typography.titleMedium,
                            color = inkBrown
                        )

                        Spacer(Modifier.width(24.dp))

                        Column {
                            Text(
                                text = "Progress: ${state.progressPercentage}%",
                                style = MaterialTheme.typography.labelSmall,
                                color = deepPink
                            )
                            Spacer(Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { state.progressFraction },
                                modifier = Modifier
                                    .width(150.dp)
                                    .height(8.dp)
                                    .border(1.dp, deepPink),
                                color = deepPink,
                                trackColor = pixelWhite,
                                strokeCap = StrokeCap.Butt
                            )
                        }
                    }
                } else {
                    // PORTRAIT HEADER: keep original vertical layout
                    Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                modifier = Modifier
                                    .size(56.dp)
                                    .border(3.dp, deepPink, pixelRoundedShape),
                                shape = pixelRoundedShape,
                                color = pixelWhite
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = currentLang.displayName.take(1),
                                        color = deepPink,
                                        style = MaterialTheme.typography.headlineMedium
                                    )
                                }
                            }
                            Spacer(Modifier.width(16.dp))
                            Text(
                                text = currentLang.displayName,
                                style = MaterialTheme.typography.headlineMedium,
                                color = inkBrown
                            )
                        }

                        Spacer(Modifier.height(16.dp))

                        Text(
                            text = "Total Progress: ${state.progressPercentage}%",
                            style = MaterialTheme.typography.labelLarge,
                            color = deepPink
                        )
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { state.progressFraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(12.dp)
                                .border(2.dp, deepPink),
                            color = deepPink,
                            trackColor = pixelWhite,
                            strokeCap = StrokeCap.Butt
                        )
                    }
                }
            }

            // 2. Sections list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                sections.forEach { section ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = Color(0xFFFFF5F7)
                        ) {
                            Text(
                                text = section.title.uppercase(),
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = Color(0xFFB19CD9)
                            )
                        }
                    }

                    items(section.chapters) { chapter ->
                        val currentProgress = state.progressMap[currentLang] ?: 1
                        val isCompleted = chapter.id < currentProgress
                        val isCurrent = chapter.id == currentProgress
                        val isUnlocked = chapter.id <= currentProgress

                        ChapterCard(
                            chapter = chapter,
                            isCompleted = isCompleted,
                            isCurrent = isCurrent,
                            isUnlocked = isUnlocked,
                            deepPink = deepPink,
                            inkBrown = inkBrown,
                            pixelRoundedShape = pixelRoundedShape,
                            onClick = {
                                if (isUnlocked) {
                                    onChapterSelected(chapter.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ChapterCard(
    chapter: Chapter,
    isCompleted: Boolean,
    isCurrent: Boolean,
    isUnlocked: Boolean,
    deepPink: Color,
    inkBrown: Color,
    pixelRoundedShape: CutCornerShape,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        enabled = isUnlocked,
        modifier = Modifier.fillMaxWidth(),
        shape = pixelRoundedShape,
        color = if (isCurrent) Color(0xFFFFFDD0) else if (!isUnlocked) Color.LightGray.copy(alpha = 0.2f) else Color.White,
        border = BorderStroke(
            width = if (isCurrent) 4.dp else 2.dp,
            color = if (isCurrent) deepPink else if (!isUnlocked) Color.Gray else deepPink.copy(alpha = 0.5f)
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon indicating status - using pixel-friendly symbols instead of emojis
            Text(
                text = if (isCompleted) "★" else if (isCurrent) "▶" else "◆",
                style = MaterialTheme.typography.titleMedium, // pixel Font
                color = if (isUnlocked) deepPink else Color.Gray
            )

            Spacer(Modifier.width(16.dp))

            Column {
                Text(
                    text = "CHAPTER ${chapter.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isUnlocked) deepPink else Color.Gray,
                    fontSize = 12.sp
                )
                Text(
                    text = chapter.title,
                    style = MaterialTheme.typography.bodyLarge, // standard font for readability
                    color = if (isUnlocked) inkBrown else Color.Gray
                )
            }
        }
    }
}
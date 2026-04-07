package com.example.lovebyte.ui.screens
//TimelineScreen.kt
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.dp

import com.example.lovebyte.data.model.LoveByteState
import com.example.lovebyte.data.model.ProgrammingLanguage
import com.example.lovebyte.data.model.Section
import com.example.lovebyte.data.model.Chapter

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimelineScreen(
    state: LoveByteState,
    onChapterSelected: (Int) -> Unit,
    onBackPressed: () -> Unit
) {
    val currentLang =
        if (state.currentLanguage != ProgrammingLanguage.NONE) {
            state.currentLanguage
        } else {
            ProgrammingLanguage.PYTHON
        }

    val sections = currentLang.sections

    Scaffold(
        // top bar to allow a back button
        topBar = {
            TopAppBar(
                title = { Text("Timeline") },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to Character Selection"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    titleContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Header: profile + name + total progress
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(Modifier.padding(horizontal = 24.dp, vertical = 16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.primary
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = currentLang.displayName.take(1),
                                    color = MaterialTheme.colorScheme.onPrimary,
                                    style = MaterialTheme.typography.titleLarge
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Text(currentLang.displayName, style = MaterialTheme.typography.headlineMedium)
                    }

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = "Total Progress: ${state.progressPercentage}%",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Spacer(Modifier.height(4.dp))
                    LinearProgressIndicator(
                        progress = { state.progressFraction },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        strokeCap = StrokeCap.Round
                    )
                }
            }

            // 2. Sections list
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                sections.forEach { section ->
                    stickyHeader {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colorScheme.background
                        ) {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.padding(vertical = 8.dp),
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // the chapters
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
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = isUnlocked,
        colors = CardDefaults.cardColors(
            containerColor = if (isCurrent) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // icon indicating status
            Text(if (isCompleted) "✅" else if (isCurrent) "▶️" else "🔒")
            // TODO: actually limit access to locked chapters
            Spacer(Modifier.width(16.dp))
            Text("Chapter ${chapter.id}: ${chapter.title}")
        }
    }
}
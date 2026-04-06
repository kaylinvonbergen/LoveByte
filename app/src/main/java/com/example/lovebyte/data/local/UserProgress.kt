package com.example.lovebyte.data.local
// // Defines what one saved progress record looks like in the database.

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProgress(
    @PrimaryKey val language: String,
    val chapterId: Int,
    val dialogueIndex: Int
)
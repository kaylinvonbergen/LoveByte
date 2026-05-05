package com.example.lovebyte.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserProgress(
    @PrimaryKey val language: String,
    val chapterId: Int,
    val dialogueIndex: Int,

    val lovePoints: Int = 0,
    val friendPoints: Int = 0,
    val hatePoints: Int = 0
)
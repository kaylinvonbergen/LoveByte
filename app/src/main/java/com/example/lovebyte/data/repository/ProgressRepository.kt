package com.example.lovebyte.data.repository
// A middle layer so your ViewModel does not talk directly to the DAO everywhere.

import com.example.lovebyte.data.local.UserProgress
import com.example.lovebyte.data.local.UserProgressDao
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val userProgressDao: UserProgressDao
) {
    // flow that updates whenever progress for the given language changes
    // for UI that should automatically update
    fun getProgressForLanguage(language: String): Flow<UserProgress?> {
        return userProgressDao.getProgressForLanguage(language)
    }

    // static fetch of progress (non-reactive)
    // for initialization or logic that doesn't need continuous updates
    suspend fun getProgressForLanguageOnce(language: String): UserProgress? {
        return userProgressDao.getProgressForLanguageOnce(language)
    }

    // saves or updates user progress in the database
    suspend fun saveProgress(progress: UserProgress) {
        userProgressDao.saveProgress(progress)
    }
}
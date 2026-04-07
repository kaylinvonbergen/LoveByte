package com.example.lovebyte.data.repository
// A middle layer so your ViewModel does not talk directly to the DAO everywhere.

import com.example.lovebyte.data.local.UserProgress
import com.example.lovebyte.data.local.UserProgressDao
import kotlinx.coroutines.flow.Flow

class ProgressRepository(
    private val userProgressDao: UserProgressDao
) {
    fun getProgressForLanguage(language: String): Flow<UserProgress?> {
        return userProgressDao.getProgressForLanguage(language)
    }

    suspend fun getProgressForLanguageOnce(language: String): UserProgress? {
        return userProgressDao.getProgressForLanguageOnce(language)
    }

    suspend fun saveProgress(progress: UserProgress) {
        userProgressDao.saveProgress(progress)
    }
}
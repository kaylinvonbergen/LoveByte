package com.example.lovebyte.data.local
// Defines the functions for reading and writing progress.

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM UserProgress WHERE language = :language LIMIT 1")
    fun getProgressForLanguage(language: String): Flow<UserProgress?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: UserProgress)
}
package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomosensei.core.data.db.entity.CardProgressEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CardProgressDao {
    @Query("SELECT * FROM progress WHERE cardId = :cardId")
    suspend fun get(cardId: String): CardProgressEntity?

    @Query("SELECT * FROM progress WHERE due <= :now ORDER BY due ASC LIMIT :limit")
    fun observeDue(now: Long, limit: Int = 50): Flow<List<CardProgressEntity>>

    @Query("SELECT * FROM progress WHERE due <= :now ORDER BY due ASC LIMIT 1")
    suspend fun nextDue(now: Long): CardProgressEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(progress: CardProgressEntity)

    @Query("DELETE FROM progress")
    suspend fun clear()
}

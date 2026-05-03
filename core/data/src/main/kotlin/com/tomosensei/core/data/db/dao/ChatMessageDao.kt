package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tomosensei.core.data.db.entity.ChatMessageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatMessageDao {
    @Insert
    suspend fun insert(message: ChatMessageEntity): Long

    @Query("SELECT * FROM chat_history WHERE sessionId = :session ORDER BY timestamp ASC")
    fun observeSession(session: String): Flow<List<ChatMessageEntity>>

    @Query("DELETE FROM chat_history WHERE sessionId = :session")
    suspend fun clearSession(session: String)
}

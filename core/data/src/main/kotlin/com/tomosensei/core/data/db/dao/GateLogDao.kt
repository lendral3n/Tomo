package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.tomosensei.core.data.db.entity.GateLogEntity

@Dao
interface GateLogDao {
    @Insert
    suspend fun insert(log: GateLogEntity): Long

    @Query("SELECT COUNT(*) FROM gate_logs WHERE trigger = :trigger AND timestamp >= :sinceMillis")
    suspend fun countSince(trigger: String, sinceMillis: Long): Int

    @Query("SELECT * FROM gate_logs ORDER BY timestamp DESC LIMIT :limit")
    suspend fun recent(limit: Int = 50): List<GateLogEntity>
}

package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomosensei.core.data.db.entity.TriggerConfigEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TriggerConfigDao {
    @Query("SELECT * FROM trigger_configs WHERE triggerType = :type")
    suspend fun get(type: String): TriggerConfigEntity?

    @Query("SELECT * FROM trigger_configs")
    fun observeAll(): Flow<List<TriggerConfigEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(config: TriggerConfigEntity)
}

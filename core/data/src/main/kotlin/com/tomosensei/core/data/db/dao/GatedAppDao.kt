package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tomosensei.core.data.db.entity.GatedAppEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GatedAppDao {
    @Query("SELECT * FROM gated_apps WHERE enabled = 1")
    fun observeEnabled(): Flow<List<GatedAppEntity>>

    @Query("SELECT * FROM gated_apps WHERE packageName = :pkg")
    suspend fun get(pkg: String): GatedAppEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(app: GatedAppEntity)

    @Query("DELETE FROM gated_apps WHERE packageName = :pkg")
    suspend fun delete(pkg: String)
}

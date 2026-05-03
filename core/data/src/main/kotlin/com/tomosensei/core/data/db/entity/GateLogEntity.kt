package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gate_logs")
data class GateLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val trigger: String,
    val intensity: Int,
    val cardId: String,
    val outcome: String,
    val attemptsToPass: Int,
    val timeToPassMs: Long,
)

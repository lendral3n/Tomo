package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trigger_configs")
data class TriggerConfigEntity(
    @PrimaryKey val triggerType: String,
    val enabled: Boolean,
    val intensity: Int,
    val cooldownSeconds: Int,
    val dailyCap: Int,
    val frequencyMod: String,
)

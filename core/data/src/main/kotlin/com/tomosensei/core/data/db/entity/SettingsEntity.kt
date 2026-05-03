package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 0,
    val emergencyPinHash: String,
    val pauseSchedule: String,
    val modelLoaded: Boolean,
    val soundEnabled: Boolean,
    val hapticEnabled: Boolean,
)

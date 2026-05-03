package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "gated_apps")
data class GatedAppEntity(
    @PrimaryKey val packageName: String,
    val appName: String,
    val enabled: Boolean,
    val customIntensity: Int? = null,
)

package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey val id: String,
    val level: String,
    val type: String,
    val front: String,
    val reading: String,
    val meaning: String,
    val examples: String,
    val tags: String,
    val audioUrl: String? = null,
    val mnemonicHint: String? = null,
)

package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "progress")
data class CardProgressEntity(
    @PrimaryKey val cardId: String,
    val stability: Float,
    val difficulty: Float,
    val due: Long,
    val lastReview: Long?,
    val reps: Int,
    val lapses: Int,
    val state: Int,
)

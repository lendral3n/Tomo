package com.tomosensei.core.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_stats")
data class UserStatsEntity(
    @PrimaryKey val id: Int = 0,
    val streak: Int,
    val lastActiveDate: String,
    val totalReviews: Int,
    val cardsLearned: Int,
    val gatesPassed: Int,
    val targetLevel: String,
    val dailyGoal: Int,
)

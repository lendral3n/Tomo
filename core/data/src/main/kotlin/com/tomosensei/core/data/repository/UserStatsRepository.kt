package com.tomosensei.core.data.repository

import com.tomosensei.core.common.Clock
import com.tomosensei.core.data.db.dao.UserStatsDao
import com.tomosensei.core.data.db.entity.UserStatsEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class UserStatsRepository @Inject constructor(
    private val statsDao: UserStatsDao,
    private val clock: Clock,
) {
    fun observe(): Flow<UserStatsEntity?> = statsDao.observe()
    suspend fun get(): UserStatsEntity? = statsDao.get()
    suspend fun upsert(stats: UserStatsEntity) = statsDao.upsert(stats)

    /**
     * Increments review counters and rolls the streak based on the gap
     * between [lastActiveDate] and today (local timezone). One missed day
     * resets the streak to 1; the same day is a no-op for streak; the
     * very next day increments it.
     */
    suspend fun recordReview(passed: Boolean) {
        val current = statsDao.get() ?: defaultStats()
        val today = todayKey()
        val nextStreak = when {
            current.lastActiveDate.isBlank() -> 1
            current.lastActiveDate == today -> current.streak.coerceAtLeast(1)
            current.lastActiveDate == yesterdayKey() -> current.streak + 1
            else -> 1
        }
        val nextLearned = if (passed) current.cardsLearned + 1 else current.cardsLearned
        statsDao.upsert(
            current.copy(
                streak = nextStreak,
                lastActiveDate = today,
                totalReviews = current.totalReviews + 1,
                cardsLearned = nextLearned,
            ),
        )
    }

    suspend fun recordGatePassed() {
        val current = statsDao.get() ?: defaultStats()
        statsDao.upsert(current.copy(gatesPassed = current.gatesPassed + 1))
    }

    private fun defaultStats() = UserStatsEntity(
        id = 0,
        streak = 0,
        lastActiveDate = "",
        totalReviews = 0,
        cardsLearned = 0,
        gatesPassed = 0,
        targetLevel = "N5",
        dailyGoal = 15,
    )

    private fun todayKey(): String = formatDate(clock.nowMillis())
    private fun yesterdayKey(): String = formatDate(clock.nowMillis() - DAY_MILLIS)

    private fun formatDate(millis: Long): String {
        val fmt = SimpleDateFormat("yyyy-MM-dd", Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }
        return fmt.format(Date(millis))
    }

    companion object {
        private const val DAY_MILLIS = 24L * 60 * 60 * 1000
    }
}

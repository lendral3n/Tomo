package com.tomosensei.core.data.db.dao

import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Query
import com.tomosensei.core.data.db.entity.CardEntity

/**
 * Joined projection used by Stats > 'Vocab Sering Lupa' — Card metadata
 * + the lapse counter from progress, ordered desc.
 */
data class WeakCard(
    @Embedded val card: CardEntity,
    val lapses: Int,
)

@Dao
interface AnalyticsDao {
    @Query(
        """
        SELECT cards.*, progress.lapses AS lapses
        FROM cards
        INNER JOIN progress ON progress.cardId = cards.id
        WHERE progress.lapses > 0
        ORDER BY progress.lapses DESC, progress.due ASC
        LIMIT :limit
        """,
    )
    suspend fun weakCards(limit: Int = 5): List<WeakCard>

    @Query("DELETE FROM progress")
    suspend fun resetAllProgress()

    @Query("DELETE FROM gate_logs")
    suspend fun resetAllGateLogs()
}

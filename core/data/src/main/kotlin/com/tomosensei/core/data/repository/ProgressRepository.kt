package com.tomosensei.core.data.repository

import com.tomosensei.core.data.db.dao.CardProgressDao
import com.tomosensei.core.data.db.entity.CardProgressEntity
import com.tomosensei.core.srs.FsrsCard
import com.tomosensei.core.srs.FsrsConverter
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class ProgressRepository @Inject constructor(
    private val progressDao: CardProgressDao,
) {
    suspend fun get(cardId: String): CardProgressEntity? = progressDao.get(cardId)

    fun observeDue(now: Long, limit: Int = 50): Flow<List<CardProgressEntity>> =
        progressDao.observeDue(now, limit)

    suspend fun nextDue(now: Long): CardProgressEntity? = progressDao.nextDue(now)

    suspend fun upsert(progress: CardProgressEntity) = progressDao.upsert(progress)

    /**
     * Loads SRS state for [cardId]. If no row exists yet (a brand-new card)
     * returns a fresh [FsrsCard] in NEW state due immediately.
     */
    suspend fun fsrsCard(cardId: String, now: Long): FsrsCard {
        val row = progressDao.get(cardId)
        return if (row == null) {
            FsrsCard.newCard(cardId, now)
        } else {
            FsrsConverter.toCard(
                cardId = row.cardId,
                stability = row.stability,
                difficulty = row.difficulty,
                due = row.due,
                lastReview = row.lastReview,
                reps = row.reps,
                lapses = row.lapses,
                state = row.state,
            )
        }
    }

    suspend fun saveFsrsCard(card: FsrsCard) {
        progressDao.upsert(
            CardProgressEntity(
                cardId = card.cardId,
                stability = card.stability,
                difficulty = card.difficulty,
                due = card.due,
                lastReview = card.lastReview,
                reps = card.reps,
                lapses = card.lapses,
                state = card.state.value,
            ),
        )
    }

}

package com.tomosensei.core.data.repository

import com.tomosensei.core.data.db.dao.CardProgressDao
import com.tomosensei.core.data.db.entity.CardProgressEntity
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
}

package com.tomosensei.core.data.repository

import com.tomosensei.core.data.db.dao.CardDao
import com.tomosensei.core.data.db.entity.CardEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao,
) {
    suspend fun getById(id: String): CardEntity? = cardDao.getById(id)
    fun observeByLevel(level: String): Flow<List<CardEntity>> = cardDao.observeByLevel(level)
    suspend fun count(): Int = cardDao.count()
    suspend fun upsertAll(cards: List<CardEntity>) = cardDao.upsertAll(cards)
}

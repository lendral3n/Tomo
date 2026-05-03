package com.tomosensei.core.data.repository

import com.tomosensei.core.data.db.dao.UserStatsDao
import com.tomosensei.core.data.db.entity.UserStatsEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class UserStatsRepository @Inject constructor(
    private val statsDao: UserStatsDao,
) {
    fun observe(): Flow<UserStatsEntity?> = statsDao.observe()
    suspend fun get(): UserStatsEntity? = statsDao.get()
    suspend fun upsert(stats: UserStatsEntity) = statsDao.upsert(stats)
}

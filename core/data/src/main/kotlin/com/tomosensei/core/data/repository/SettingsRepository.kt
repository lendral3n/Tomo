package com.tomosensei.core.data.repository

import com.tomosensei.core.data.db.dao.SettingsDao
import com.tomosensei.core.data.db.entity.SettingsEntity
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class SettingsRepository @Inject constructor(
    private val settingsDao: SettingsDao,
) {
    fun observe(): Flow<SettingsEntity?> = settingsDao.observe()
    suspend fun get(): SettingsEntity? = settingsDao.get()
    suspend fun upsert(settings: SettingsEntity) = settingsDao.upsert(settings)
}

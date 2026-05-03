package com.tomosensei.core.data.di

import android.content.Context
import com.tomosensei.core.data.db.DatabaseFactory
import com.tomosensei.core.data.db.TomoSenseiDatabase
import com.tomosensei.core.data.db.dao.CardDao
import com.tomosensei.core.data.db.dao.CardProgressDao
import com.tomosensei.core.data.db.dao.ChatMessageDao
import com.tomosensei.core.data.db.dao.GateLogDao
import com.tomosensei.core.data.db.dao.GatedAppDao
import com.tomosensei.core.data.db.dao.SettingsDao
import com.tomosensei.core.data.db.dao.TriggerConfigDao
import com.tomosensei.core.data.db.dao.UserStatsDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): TomoSenseiDatabase =
        DatabaseFactory.build(context, encrypted = true)

    @Provides fun provideCardDao(db: TomoSenseiDatabase): CardDao = db.cardDao()
    @Provides fun provideCardProgressDao(db: TomoSenseiDatabase): CardProgressDao = db.cardProgressDao()
    @Provides fun provideTriggerConfigDao(db: TomoSenseiDatabase): TriggerConfigDao = db.triggerConfigDao()
    @Provides fun provideGatedAppDao(db: TomoSenseiDatabase): GatedAppDao = db.gatedAppDao()
    @Provides fun provideGateLogDao(db: TomoSenseiDatabase): GateLogDao = db.gateLogDao()
    @Provides fun provideUserStatsDao(db: TomoSenseiDatabase): UserStatsDao = db.userStatsDao()
    @Provides fun provideSettingsDao(db: TomoSenseiDatabase): SettingsDao = db.settingsDao()
    @Provides fun provideChatMessageDao(db: TomoSenseiDatabase): ChatMessageDao = db.chatMessageDao()
}

package com.tomosensei.core.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tomosensei.core.data.db.dao.AnalyticsDao
import com.tomosensei.core.data.db.dao.CardDao
import com.tomosensei.core.data.db.dao.CardProgressDao
import com.tomosensei.core.data.db.dao.ChatMessageDao
import com.tomosensei.core.data.db.dao.GateLogDao
import com.tomosensei.core.data.db.dao.GatedAppDao
import com.tomosensei.core.data.db.dao.SettingsDao
import com.tomosensei.core.data.db.dao.TriggerConfigDao
import com.tomosensei.core.data.db.dao.UserStatsDao
import com.tomosensei.core.data.db.entity.CardEntity
import com.tomosensei.core.data.db.entity.CardProgressEntity
import com.tomosensei.core.data.db.entity.ChatMessageEntity
import com.tomosensei.core.data.db.entity.GateLogEntity
import com.tomosensei.core.data.db.entity.GatedAppEntity
import com.tomosensei.core.data.db.entity.SettingsEntity
import com.tomosensei.core.data.db.entity.TriggerConfigEntity
import com.tomosensei.core.data.db.entity.UserStatsEntity

@Database(
    entities = [
        CardEntity::class,
        CardProgressEntity::class,
        TriggerConfigEntity::class,
        GatedAppEntity::class,
        GateLogEntity::class,
        UserStatsEntity::class,
        SettingsEntity::class,
        ChatMessageEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class TomoSenseiDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun cardProgressDao(): CardProgressDao
    abstract fun triggerConfigDao(): TriggerConfigDao
    abstract fun gatedAppDao(): GatedAppDao
    abstract fun gateLogDao(): GateLogDao
    abstract fun userStatsDao(): UserStatsDao
    abstract fun settingsDao(): SettingsDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun analyticsDao(): AnalyticsDao

    companion object {
        const val DATABASE_NAME = "tomo_sensei.db"
    }
}

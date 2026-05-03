package com.tomosensei.core.data.db

import android.content.Context
import androidx.room.Room
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

object DatabaseFactory {
    fun build(context: Context, encrypted: Boolean = true): TomoSenseiDatabase {
        val builder = Room.databaseBuilder(
            context.applicationContext,
            TomoSenseiDatabase::class.java,
            TomoSenseiDatabase.DATABASE_NAME,
        )
        if (encrypted) {
            SQLiteDatabase.loadLibs(context.applicationContext)
            val passphrase = DatabasePassphrase.derive(context.applicationContext)
            builder.openHelperFactory(SupportFactory(passphrase))
        }
        return builder
            .fallbackToDestructiveMigration()
            .build()
    }
}

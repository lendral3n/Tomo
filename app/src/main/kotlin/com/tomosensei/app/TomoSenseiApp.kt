package com.tomosensei.app

import android.app.Application
import com.tomosensei.core.content.DeckSeeder
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@HiltAndroidApp
class TomoSenseiApp : Application() {

    @Inject lateinit var deckSeeder: DeckSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            // Idempotent: skips when the cards table is already populated.
            deckSeeder.ensureSeeded(level = "n5")
        }
    }
}

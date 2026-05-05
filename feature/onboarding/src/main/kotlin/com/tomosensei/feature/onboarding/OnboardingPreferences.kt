package com.tomosensei.feature.onboarding

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.onboardingDataStore by preferencesDataStore(name = "onboarding_prefs")

@Singleton
class OnboardingPreferences @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val keyCompleted = booleanPreferencesKey("onboarding_completed")

    val isCompleted: Flow<Boolean> = context.onboardingDataStore.data
        .map { it[keyCompleted] ?: false }

    suspend fun markCompleted() {
        context.onboardingDataStore.edit { it[keyCompleted] = true }
    }
}

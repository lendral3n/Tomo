package com.tomosensei.feature.drill.di

import com.tomosensei.core.common.Clock
import com.tomosensei.core.common.SystemClock
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DrillModule {
    @Provides
    @Singleton
    fun provideClock(): Clock = SystemClock
}

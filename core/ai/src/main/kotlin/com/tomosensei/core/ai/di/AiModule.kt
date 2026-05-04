package com.tomosensei.core.ai.di

import com.tomosensei.core.ai.GemmaInferenceManager
import com.tomosensei.core.ai.StubGemmaInferenceManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt binding for [GemmaInferenceManager]. Phase 4 swap-point: replace
 * [StubGemmaInferenceManager] with the real LiteRT-LM-backed impl once
 * the bridge is verified against Google AI Edge Gallery on device.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    @Binds
    @Singleton
    abstract fun bindInferenceManager(impl: StubGemmaInferenceManager): GemmaInferenceManager
}

package com.tomosensei.core.ai.di

import com.tomosensei.core.ai.GemmaInferenceManager
import com.tomosensei.core.ai.ModelDownloader
import com.tomosensei.core.ai.RealGemmaInferenceManager
import com.tomosensei.core.ai.StubGemmaInferenceManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Hilt binding for [GemmaInferenceManager]. Decision is made at provide
 * time: if the Gemma file is on disk we use [RealGemmaInferenceManager];
 * otherwise the stub keeps the chat UI demo-able.
 *
 * Once the user kicks off the download in onboarding, the next process
 * launch will pick up the real binding automatically. We could also
 * dynamically swap, but for v0 the load-on-launch model is simpler and
 * matches the "model is a one-time install" mental model.
 */
@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    @Provides
    @Singleton
    fun provideInferenceManager(
        modelDownloader: ModelDownloader,
        real: Provider<RealGemmaInferenceManager>,
        stub: Provider<StubGemmaInferenceManager>,
    ): GemmaInferenceManager {
        return if (modelDownloader.isAvailable()) real.get() else stub.get()
    }
}

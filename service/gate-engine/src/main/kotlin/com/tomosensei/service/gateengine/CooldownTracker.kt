package com.tomosensei.service.gateengine

import com.tomosensei.core.common.Clock
import javax.inject.Inject
import javax.inject.Singleton
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks the last time each [Trigger] was honored. Provides simple "is this
 * trigger past its cooldown window?" semantics. In-memory only — restarting
 * the foreground service resets cooldowns, which is acceptable for v0.
 */
@Singleton
class CooldownTracker @Inject constructor(
    private val clock: Clock,
) {
    private val lastFiredMillis = ConcurrentHashMap<Trigger, Long>()

    fun isPast(trigger: Trigger, cooldownSeconds: Int): Boolean {
        val last = lastFiredMillis[trigger] ?: return true
        return clock.nowMillis() - last >= cooldownSeconds * 1000L
    }

    fun mark(trigger: Trigger) {
        lastFiredMillis[trigger] = clock.nowMillis()
    }
}

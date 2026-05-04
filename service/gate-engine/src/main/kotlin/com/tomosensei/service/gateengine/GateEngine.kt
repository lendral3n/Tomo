package com.tomosensei.service.gateengine

import android.content.Context
import android.content.Intent
import com.tomosensei.core.common.Clock
import com.tomosensei.core.data.db.entity.GateLogEntity
import com.tomosensei.core.data.db.dao.GateLogDao
import com.tomosensei.core.data.repository.CardRepository
import com.tomosensei.core.data.repository.ProgressRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first

/**
 * Pure-logic core of the gate pipeline:
 *  1. system event lands (from a [android.content.BroadcastReceiver] or
 *     foreground service ticker) and calls [handleTrigger]
 *  2. cooldown / daily-cap gates filter out spam
 *  3. next-due card is fetched from FSRS-ordered progress
 *  4. an intent to spawn the overlay is constructed and returned to the
 *     caller (caller decides how to start the overlay service — keeps this
 *     class free of platform startService quirks)
 *
 * For Phase 3 v0:
 *  - intensity defaults to ANSWER_TO_PASS (Lv 3, the safest non-blocking
 *    default for self-use)
 *  - daily cap is unlimited (will be wired into TriggerConfigEntity later)
 */
@Singleton
class GateEngine @Inject constructor(
    @ApplicationContext private val context: Context,
    private val cardRepository: CardRepository,
    private val progressRepository: ProgressRepository,
    private val gateLogDao: GateLogDao,
    private val cooldown: CooldownTracker,
    private val clock: Clock,
) {

    /**
     * Returns an Intent to start [GATE_OVERLAY_ACTION] with extras populated,
     * or null if the trigger was suppressed (cooldown / no due card).
     */
    suspend fun handleTrigger(
        trigger: Trigger,
        cooldownSeconds: Int = DEFAULT_COOLDOWN_SECONDS,
        intensity: IntensityLevel = IntensityLevel.ANSWER_TO_PASS,
    ): Intent? {
        if (!cooldown.isPast(trigger, cooldownSeconds)) return null

        val now = clock.nowMillis()

        // Prefer the next FSRS-due card; fall back to the first seeded N5
        // entry if no progress rows exist yet (cold-launch / fresh install).
        val nextDue = progressRepository.nextDue(now)
        val card = if (nextDue != null) {
            cardRepository.getById(nextDue.cardId) ?: return null
        } else {
            cardRepository.observeByLevel("N5")
                .filter { it.isNotEmpty() }
                .first()
                .firstOrNull() ?: return null
        }

        cooldown.mark(trigger)
        gateLogDao.insert(
            GateLogEntity(
                timestamp = now,
                trigger = trigger.key,
                intensity = intensity.value,
                cardId = card.id,
                outcome = OUTCOME_TRIGGERED,
                attemptsToPass = 0,
                timeToPassMs = 0,
            ),
        )

        return Intent(GATE_OVERLAY_ACTION).apply {
            setPackage(context.packageName)
            putExtra(EXTRA_TRIGGER, trigger.key)
            putExtra(EXTRA_INTENSITY, intensity.value)
            putExtra(EXTRA_CARD_ID, card.id)
            putExtra(EXTRA_CARD_FRONT, card.front)
            putExtra(EXTRA_CARD_READING, card.reading)
            putExtra(EXTRA_CARD_MEANING, card.meaning)
            putExtra(EXTRA_TRIGGERED_AT, now)
        }
    }

    suspend fun logOutcome(
        request: GateRequest,
        outcome: String,
        attemptsToPass: Int,
        timeToPassMs: Long,
    ) {
        gateLogDao.insert(
            GateLogEntity(
                timestamp = clock.nowMillis(),
                trigger = request.trigger.key,
                intensity = request.intensity.value,
                cardId = request.cardId,
                outcome = outcome,
                attemptsToPass = attemptsToPass,
                timeToPassMs = timeToPassMs,
            ),
        )
    }

    companion object {
        const val GATE_OVERLAY_ACTION = "com.tomosensei.action.SHOW_GATE"
        const val EXTRA_TRIGGER = "trigger"
        const val EXTRA_INTENSITY = "intensity"
        const val EXTRA_CARD_ID = "cardId"
        const val EXTRA_CARD_FRONT = "cardFront"
        const val EXTRA_CARD_READING = "cardReading"
        const val EXTRA_CARD_MEANING = "cardMeaning"
        const val EXTRA_TRIGGERED_AT = "triggeredAt"

        const val OUTCOME_TRIGGERED = "triggered"
        const val OUTCOME_PASSED = "passed"
        const val OUTCOME_FAILED = "failed"
        const val OUTCOME_SKIPPED = "skipped"
        const val OUTCOME_BYPASSED = "bypassed"

        const val DEFAULT_COOLDOWN_SECONDS = 60
    }
}

fun GateRequest.Companion.fromIntent(intent: Intent): GateRequest? {
    val triggerKey = intent.getStringExtra(GateEngine.EXTRA_TRIGGER) ?: return null
    val intensity = intent.getIntExtra(GateEngine.EXTRA_INTENSITY, 3)
    val cardId = intent.getStringExtra(GateEngine.EXTRA_CARD_ID) ?: return null
    val front = intent.getStringExtra(GateEngine.EXTRA_CARD_FRONT) ?: return null
    val reading = intent.getStringExtra(GateEngine.EXTRA_CARD_READING).orEmpty()
    val meaning = intent.getStringExtra(GateEngine.EXTRA_CARD_MEANING) ?: return null
    val triggeredAt = intent.getLongExtra(GateEngine.EXTRA_TRIGGERED_AT, 0L)
    return GateRequest(
        trigger = Trigger.entries.firstOrNull { it.key == triggerKey } ?: Trigger.MANUAL,
        intensity = IntensityLevel.fromValue(intensity),
        cardId = cardId,
        cardFront = front,
        cardReading = reading,
        cardMeaning = meaning,
        triggeredAt = triggeredAt,
    )
}


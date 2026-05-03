package com.tomosensei.core.srs

import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private const val MILLIS_PER_DAY: Long = 24L * 60 * 60 * 1000

/**
 * Free Spaced Repetition Scheduler v4. Reference:
 *   https://github.com/open-spaced-repetition/fsrs4anki/wiki/The-Algorithm
 *   https://github.com/open-spaced-repetition/ts-fsrs
 *
 * Pure & deterministic given (card, rating, now, params). All time inputs
 * in epoch millis; internal day arithmetic in floating-point days.
 */
@Singleton
class FsrsScheduler @Inject constructor(
    private val params: FsrsParameters = FsrsParameters(),
) {

    fun review(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        return when (card.state) {
            FsrsState.NEW -> reviewNew(card, rating, now)
            FsrsState.LEARNING, FsrsState.RELEARNING -> reviewLearning(card, rating, now)
            FsrsState.REVIEW -> reviewMature(card, rating, now)
        }
    }

    private fun reviewNew(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        val stability = initialStability(rating)
        val difficulty = initialDifficulty(rating)
        return advance(card, rating, stability, difficulty, now, justLearned = true)
    }

    private fun reviewLearning(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        val elapsedDays = elapsedDays(card, now)
        val retrievability = retrievability(card.stability, elapsedDays)
        val nextStability = if (rating == FsrsRating.AGAIN) {
            shortTermLapseStability(card.difficulty, card.stability)
        } else {
            shortTermStability(card.stability, rating)
        }
        val nextDifficulty = nextDifficulty(card.difficulty, rating)
        return advance(card, rating, nextStability, nextDifficulty, now, justLearned = false, retrievability = retrievability)
    }

    private fun reviewMature(card: FsrsCard, rating: FsrsRating, now: Long): FsrsCard {
        val elapsedDays = elapsedDays(card, now)
        val retrievability = retrievability(card.stability, elapsedDays)
        val nextStability = if (rating == FsrsRating.AGAIN) {
            lapseStability(card.difficulty, card.stability, retrievability)
        } else {
            recallStability(card.difficulty, card.stability, retrievability, rating)
        }
        val nextDifficulty = nextDifficulty(card.difficulty, rating)
        return advance(card, rating, nextStability, nextDifficulty, now, justLearned = false, retrievability = retrievability)
    }

    private fun advance(
        card: FsrsCard,
        rating: FsrsRating,
        nextStability: Float,
        nextDifficulty: Float,
        now: Long,
        justLearned: Boolean,
        retrievability: Float = 1f,
    ): FsrsCard {
        val nextState = when {
            justLearned && rating == FsrsRating.AGAIN -> FsrsState.LEARNING
            justLearned -> FsrsState.REVIEW
            rating == FsrsRating.AGAIN -> FsrsState.RELEARNING
            else -> FsrsState.REVIEW
        }
        val intervalDays = nextInterval(nextStability, nextState, rating)
        val nextDue = now + (intervalDays * MILLIS_PER_DAY).roundToLong()
        return card.copy(
            stability = nextStability.coerceAtLeast(0.01f),
            difficulty = nextDifficulty.coerceIn(1f, 10f),
            due = nextDue,
            lastReview = now,
            reps = card.reps + 1,
            lapses = card.lapses + if (rating == FsrsRating.AGAIN) 1 else 0,
            state = nextState,
        )
    }

    // ── FSRS formulas ─────────────────────────────────────────────────────

    private fun initialStability(rating: FsrsRating): Float =
        params.w[rating.value - 1].coerceAtLeast(0.1f)

    private fun initialDifficulty(rating: FsrsRating): Float {
        val raw = params.w[4] - exp(params.w[5] * (rating.value - 1).toFloat()) + 1f
        return raw.coerceIn(1f, 10f)
    }

    private fun nextDifficulty(current: Float, rating: FsrsRating): Float {
        val deltaD = -params.w[6] * (rating.value - 3).toFloat()
        val newD = current + linearDamping(deltaD, current)
        val target = params.w[4]
        val damped = meanReversion(target, newD)
        return damped.coerceIn(1f, 10f)
    }

    private fun linearDamping(deltaD: Float, oldD: Float): Float =
        deltaD * (10f - oldD) / 9f

    private fun meanReversion(init: Float, current: Float): Float =
        params.w[7] * init + (1f - params.w[7]) * current

    private fun retrievability(stability: Float, elapsedDays: Float): Float {
        if (stability <= 0f) return 1f
        return (1f + params.factor * elapsedDays / stability).pow(params.decay)
    }

    private fun recallStability(d: Float, s: Float, r: Float, rating: FsrsRating): Float {
        val hardPenalty = if (rating == FsrsRating.HARD) params.w[15] else 1f
        val easyBonus = if (rating == FsrsRating.EASY) params.w[16] else 1f
        val factor = exp(params.w[8]) *
            (11f - d) *
            s.pow(-params.w[9]) *
            (exp(params.w[10] * (1f - r)) - 1f)
        return s * (1f + factor * hardPenalty * easyBonus)
    }

    private fun lapseStability(d: Float, s: Float, r: Float): Float {
        return params.w[11] *
            d.pow(-params.w[12]) *
            ((s + 1f).pow(params.w[13]) - 1f) *
            exp(params.w[14] * (1f - r))
    }

    private fun shortTermStability(s: Float, rating: FsrsRating): Float {
        val grade = when (rating) {
            FsrsRating.HARD -> -1
            FsrsRating.GOOD -> 0
            FsrsRating.EASY -> 1
            FsrsRating.AGAIN -> -2
        }.toFloat()
        return s * exp(params.w[16] * (grade + 1f))
    }

    private fun shortTermLapseStability(d: Float, s: Float): Float {
        return max(0.01f, params.w[11] * d.pow(-params.w[12]) * (s.pow(params.w[13]) - 1f))
    }

    private fun nextInterval(stability: Float, state: FsrsState, rating: FsrsRating): Float {
        if (state == FsrsState.LEARNING || state == FsrsState.RELEARNING) {
            // Sub-day intervals: 10 minutes for AGAIN, then graduating.
            return when (rating) {
                FsrsRating.AGAIN -> minutes(10)
                FsrsRating.HARD -> minutes(15)
                FsrsRating.GOOD -> minutes(30)
                FsrsRating.EASY -> 1f // 1 day
            }
        }
        val ivl = stability * (params.requestRetention.pow(1f / params.decay) - 1f) / params.factor
        val rounded = ivl.roundToInt().toFloat()
        return min(max(rounded, 1f), params.maximumIntervalDays.toFloat())
    }

    private fun minutes(n: Int): Float = n / (24f * 60f)

    private fun elapsedDays(card: FsrsCard, now: Long): Float {
        val last = card.lastReview ?: return 0f
        return ((now - last).toFloat() / MILLIS_PER_DAY).coerceAtLeast(0f)
    }

    private fun Float.pow(p: Float): Float = this.toDouble().powSafe(p.toDouble()).toFloat()
}

private fun Double.powSafe(p: Double): Double = if (this <= 0.0) 0.0 else exp(p * ln(this))

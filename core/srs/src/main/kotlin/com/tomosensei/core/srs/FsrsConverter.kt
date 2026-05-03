package com.tomosensei.core.srs

/**
 * Bridge between FSRS internal state and Room storage. Lives in core:srs
 * (instead of core:data) so that core:srs has zero Android/Room deps and
 * stays unit-testable on plain JVM.
 *
 * core:data depends on this module only at the conversion boundary in
 * its repositories.
 */
object FsrsConverter {
    fun toCard(
        cardId: String,
        stability: Float,
        difficulty: Float,
        due: Long,
        lastReview: Long?,
        reps: Int,
        lapses: Int,
        state: Int,
    ): FsrsCard = FsrsCard(
        cardId = cardId,
        stability = stability,
        difficulty = difficulty,
        due = due,
        lastReview = lastReview,
        reps = reps,
        lapses = lapses,
        state = FsrsState.fromValue(state),
    )
}

package com.tomosensei.core.srs

/**
 * Snapshot of a card's SRS state, decoupled from Room. Pure data — the
 * scheduler reads this and produces a new instance.
 */
data class FsrsCard(
    val cardId: String,
    val stability: Float,
    val difficulty: Float,
    val due: Long,
    val lastReview: Long?,
    val reps: Int,
    val lapses: Int,
    val state: FsrsState,
) {
    companion object {
        fun newCard(cardId: String, now: Long): FsrsCard = FsrsCard(
            cardId = cardId,
            stability = 0f,
            difficulty = 0f,
            due = now,
            lastReview = null,
            reps = 0,
            lapses = 0,
            state = FsrsState.NEW,
        )
    }
}

package com.tomosensei.core.srs

/**
 * FSRS user rating after a review.
 *  AGAIN  — forgot, lapse triggered.
 *  HARD   — recalled with serious effort.
 *  GOOD   — recalled with normal effort. Default for "tau" in drill.
 *  EASY   — recalled trivially.
 *
 * Mapping note for Tomo Sensei drill UI (spec §10):
 *   swipe up "tau"   = GOOD
 *   swipe down "lupa"= AGAIN
 * The HARD/EASY ratings are reserved for the gate-config screen
 * (multi-button mode in higher intensity gates).
 */
enum class FsrsRating(val value: Int) {
    AGAIN(1),
    HARD(2),
    GOOD(3),
    EASY(4),
}

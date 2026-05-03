package com.tomosensei.core.srs

/**
 * FSRS v4 weights. Defaults are the published "average learner" weights
 * from open-spaced-repetition/fsrs4anki. Once we collect enough review
 * data per user we can fit personal weights via FSRS-Optimizer (V1.5+).
 *
 * Index legend (FSRS-4):
 *   w[0..3]  initial stability for AGAIN/HARD/GOOD/EASY on first review
 *   w[4]     initial difficulty
 *   w[5]     difficulty modifier
 *   w[6]     difficulty linear coefficient
 *   w[7]     difficulty mean-reversion target weight
 *   w[8]     stability growth multiplier
 *   w[9]     stability decay vs current S
 *   w[10]    retrievability sensitivity
 *   w[11..13] lapse stability formula coefficients
 *   w[14]    HARD penalty
 *   w[15]    EASY bonus
 *   w[16]    short-term stability adjustment (post-failure)
 */
data class FsrsParameters(
    val w: FloatArray = DEFAULT_WEIGHTS,
    val requestRetention: Float = 0.9f,
    val maximumIntervalDays: Int = 36500,
    val decay: Float = -0.5f,
    val factor: Float = 19f / 81f,
) {
    init {
        require(w.size == 17) { "FSRS-4 expects 17 weights, got ${w.size}" }
        require(requestRetention > 0f && requestRetention < 1f) {
            "requestRetention must be in (0, 1)"
        }
    }

    companion object {
        val DEFAULT_WEIGHTS = floatArrayOf(
            0.4072f, 1.1829f, 3.1262f, 15.4722f,
            7.2102f, 0.5316f, 1.0651f, 0.0234f,
            1.616f, 0.1544f, 1.0824f, 1.9813f,
            0.0953f, 0.2975f, 2.2042f, 0.2407f,
            2.9466f,
        )
    }
}

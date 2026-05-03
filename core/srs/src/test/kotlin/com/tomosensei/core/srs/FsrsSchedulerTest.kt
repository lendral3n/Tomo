package com.tomosensei.core.srs

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FsrsSchedulerTest {

    private val scheduler = FsrsScheduler()
    private val now = 1_700_000_000_000L
    private val msPerDay = 24L * 60 * 60 * 1000

    @Test
    fun `new card with GOOD rating graduates to REVIEW state`() {
        val card = FsrsCard.newCard("c1", now)
        val reviewed = scheduler.review(card, FsrsRating.GOOD, now)
        assertThat(reviewed.state).isEqualTo(FsrsState.REVIEW)
        assertThat(reviewed.reps).isEqualTo(1)
        assertThat(reviewed.lapses).isEqualTo(0)
        assertThat(reviewed.stability).isGreaterThan(0f)
        assertThat(reviewed.difficulty).isAtLeast(1f)
        assertThat(reviewed.difficulty).isAtMost(10f)
    }

    @Test
    fun `new card with AGAIN rating drops to LEARNING and short interval`() {
        val card = FsrsCard.newCard("c1", now)
        val reviewed = scheduler.review(card, FsrsRating.AGAIN, now)
        assertThat(reviewed.state).isEqualTo(FsrsState.LEARNING)
        assertThat(reviewed.lapses).isEqualTo(1)
        // AGAIN graduating interval is 10 minutes.
        val expectedDue = now + (10L * 60 * 1000)
        // Allow a couple of millis of float rounding wobble.
        assertThat(reviewed.due).isIn(
            (expectedDue - 1000L)..(expectedDue + 1000L),
        )
    }

    @Test
    fun `new card with EASY rating produces longer interval than GOOD`() {
        val cardA = FsrsCard.newCard("c1", now)
        val cardB = FsrsCard.newCard("c2", now)
        val good = scheduler.review(cardA, FsrsRating.GOOD, now)
        val easy = scheduler.review(cardB, FsrsRating.EASY, now)
        assertThat(easy.due - now).isGreaterThan(good.due - now)
    }

    @Test
    fun `mature card review with AGAIN sets state to RELEARNING and increments lapses`() {
        // Simulate a mature review-state card that was due today.
        val mature = FsrsCard(
            cardId = "c1",
            stability = 30f,
            difficulty = 5f,
            due = now,
            lastReview = now - (30L * msPerDay),
            reps = 5,
            lapses = 0,
            state = FsrsState.REVIEW,
        )
        val reviewed = scheduler.review(mature, FsrsRating.AGAIN, now)
        assertThat(reviewed.state).isEqualTo(FsrsState.RELEARNING)
        assertThat(reviewed.lapses).isEqualTo(1)
    }

    @Test
    fun `mature card review with GOOD increases stability and produces multi-day interval`() {
        val mature = FsrsCard(
            cardId = "c1",
            stability = 5f,
            difficulty = 5f,
            due = now,
            lastReview = now - (5L * msPerDay),
            reps = 3,
            lapses = 0,
            state = FsrsState.REVIEW,
        )
        val reviewed = scheduler.review(mature, FsrsRating.GOOD, now)
        assertThat(reviewed.state).isEqualTo(FsrsState.REVIEW)
        assertThat(reviewed.stability).isGreaterThan(mature.stability)
        // Should schedule at least 1 day out.
        assertThat(reviewed.due - now).isAtLeast(msPerDay)
    }

    @Test
    fun `difficulty stays within 1 to 10 across many reviews`() {
        var card = FsrsCard.newCard("c1", now)
        var t = now
        val ratings = listOf(
            FsrsRating.GOOD, FsrsRating.GOOD, FsrsRating.AGAIN,
            FsrsRating.HARD, FsrsRating.GOOD, FsrsRating.EASY,
            FsrsRating.AGAIN, FsrsRating.AGAIN, FsrsRating.GOOD,
        )
        for (r in ratings) {
            card = scheduler.review(card, r, t)
            assertThat(card.difficulty).isAtLeast(1f)
            assertThat(card.difficulty).isAtMost(10f)
            t = card.due
        }
    }

    @Test
    fun `interval never exceeds maximumIntervalDays`() {
        val params = FsrsParameters(maximumIntervalDays = 100)
        val capped = FsrsScheduler(params)
        var card = FsrsCard.newCard("c1", now)
        var t = now
        repeat(20) {
            card = capped.review(card, FsrsRating.EASY, t)
            t = card.due
            val intervalDays = (card.due - (card.lastReview ?: t)) / msPerDay
            assertThat(intervalDays).isAtMost(100L)
        }
    }
}

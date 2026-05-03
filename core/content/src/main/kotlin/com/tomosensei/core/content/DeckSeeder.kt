package com.tomosensei.core.content

import com.tomosensei.core.content.model.RawCard
import com.tomosensei.core.data.db.entity.CardEntity
import com.tomosensei.core.data.repository.CardRepository
import javax.inject.Inject
import javax.inject.Singleton
import org.json.JSONArray
import org.json.JSONObject

/**
 * Inserts a bundled deck into Room. Idempotent — `upsertAll` keys on
 * card id, so re-seeding on subsequent launches is safe. The drill
 * feature (Phase 2) calls this on first run before showing any cards.
 */
@Singleton
class DeckSeeder @Inject constructor(
    private val deckLoader: DeckLoader,
    private val cardRepository: CardRepository,
) {

    suspend fun ensureSeeded(level: String) {
        if (cardRepository.count() > 0) return
        seed(level)
    }

    suspend fun seed(level: String) {
        val deck = deckLoader.load(level)
        val entities = deck.cards.map { it.toEntity(level = deck.level) }
        cardRepository.upsertAll(entities)
    }

    private fun RawCard.toEntity(level: String): CardEntity = CardEntity(
        id = id,
        level = level,
        type = type,
        front = front,
        reading = reading,
        meaning = meaning,
        examples = examplesJson(),
        tags = tags.joinToString(","),
        audioUrl = audioUrl,
        mnemonicHint = mnemonic,
    )

    private fun RawCard.examplesJson(): String {
        val arr = JSONArray()
        examples.forEach { e ->
            arr.put(
                JSONObject()
                    .put("jp", e.jp)
                    .put("reading", e.reading)
                    .put("id", e.id),
            )
        }
        return arr.toString()
    }
}

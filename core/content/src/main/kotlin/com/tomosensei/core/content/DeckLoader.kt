package com.tomosensei.core.content

import android.content.Context
import com.tomosensei.core.content.model.DeckPayload
import com.tomosensei.core.content.model.RawCard
import com.tomosensei.core.content.model.RawExample
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Reads bundled deck JSONs from `assets/decks/{level}.json`. Uses the
 * platform `org.json` parser to keep the dependency surface small for
 * Phase 1 — when serialization stabilizes (Phase 2+) we can move to
 * kotlinx.serialization without touching callers.
 */
@Singleton
class DeckLoader @Inject constructor(
    @ApplicationContext private val context: Context,
) {

    suspend fun load(level: String): DeckPayload = withContext(Dispatchers.IO) {
        val raw = context.assets.open("decks/${level.lowercase()}.json").use { stream ->
            stream.bufferedReader().readText()
        }
        parse(raw)
    }

    internal fun parse(raw: String): DeckPayload {
        val root = JSONObject(raw)
        val cardsJson = root.getJSONArray("cards")
        val cards = ArrayList<RawCard>(cardsJson.length())
        for (i in 0 until cardsJson.length()) {
            cards += parseCard(cardsJson.getJSONObject(i))
        }
        return DeckPayload(
            version = root.getString("version"),
            level = root.getString("level"),
            language = root.optString("language", "ja"),
            interfaceLang = root.optString("interface_lang", "id"),
            cards = cards,
        )
    }

    private fun parseCard(obj: JSONObject): RawCard {
        return RawCard(
            id = obj.getString("id"),
            type = obj.getString("type"),
            front = obj.getString("front"),
            reading = obj.optString("reading", ""),
            meaning = obj.getString("meaning"),
            examples = parseExamples(obj.optJSONArray("examples")),
            tags = parseStringList(obj.optJSONArray("tags")),
            mnemonic = obj.optStringOrNull("mnemonic"),
            audioUrl = obj.optStringOrNull("audio_url"),
        )
    }

    private fun JSONObject.optStringOrNull(key: String): String? {
        if (!has(key) || isNull(key)) return null
        val value = optString(key, "")
        return value.takeIf { it.isNotBlank() }
    }

    private fun parseExamples(arr: JSONArray?): List<RawExample> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i ->
            val o = arr.getJSONObject(i)
            RawExample(
                jp = o.getString("jp"),
                reading = o.optString("reading", ""),
                id = o.getString("id"),
            )
        }
    }

    private fun parseStringList(arr: JSONArray?): List<String> {
        if (arr == null) return emptyList()
        return (0 until arr.length()).map { i -> arr.getString(i) }
    }
}

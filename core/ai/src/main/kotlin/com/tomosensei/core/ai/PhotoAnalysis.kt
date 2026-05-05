package com.tomosensei.core.ai

import org.json.JSONObject

data class PhotoAnalysis(
    val extractedText: String,
    val reading: String,
    val translation: String,
    val grammarNotes: List<String>,
    val vocabHighlights: List<VocabHighlight>,
)

data class VocabHighlight(
    val jp: String,
    val reading: String,
    val meaning: String,
    val level: String,
)

object PhotoAnalysisParser {

    /**
     * Parses Gemma's JSON output. The model is prompted with a strict
     * schema (see [PromptBuilder.photoPrompt]); when it deviates we fall
     * back to a best-effort plain-text wrap so the UI still has something
     * to show instead of crashing.
     */
    fun parse(rawResponse: String): PhotoAnalysis {
        val cleaned = rawResponse
            .substringAfter("{", "")
            .substringBeforeLast("}", "")
            .let { if (it.isBlank()) "" else "{$it}" }
            .ifBlank { return fallback(rawResponse) }
        return runCatching { parseStrict(cleaned) }.getOrElse { fallback(rawResponse) }
    }

    private fun parseStrict(json: String): PhotoAnalysis {
        val obj = JSONObject(json)
        val grammar = obj.optJSONArray("grammar_notes")
        val grammarList = if (grammar == null) emptyList() else
            (0 until grammar.length()).map { grammar.getString(it) }
        val vocab = obj.optJSONArray("vocab_highlights")
        val vocabList = if (vocab == null) emptyList() else
            (0 until vocab.length()).map { i ->
                val v = vocab.getJSONObject(i)
                VocabHighlight(
                    jp = v.optString("jp"),
                    reading = v.optString("reading"),
                    meaning = v.optString("meaning"),
                    level = v.optString("level", "N5"),
                )
            }
        return PhotoAnalysis(
            extractedText = obj.optString("extracted_text"),
            reading = obj.optString("reading"),
            translation = obj.optString("translation"),
            grammarNotes = grammarList,
            vocabHighlights = vocabList,
        )
    }

    private fun fallback(raw: String): PhotoAnalysis = PhotoAnalysis(
        extractedText = raw.take(200),
        reading = "",
        translation = "(model belum mengembalikan JSON valid)",
        grammarNotes = emptyList(),
        vocabHighlights = emptyList(),
    )
}

package com.tomosensei.core.content.model

/**
 * Snapshot of a deck JSON file decoded by [DeckLoader]. Mirrors the
 * structure documented in docs/06-CONTENT-STRATEGY.md §2.
 */
data class DeckPayload(
    val version: String,
    val level: String,
    val language: String,
    val interfaceLang: String,
    val cards: List<RawCard>,
)

data class RawCard(
    val id: String,
    val type: String,
    val front: String,
    val reading: String,
    val meaning: String,
    val examples: List<RawExample>,
    val tags: List<String>,
    val mnemonic: String? = null,
    val audioUrl: String? = null,
)

data class RawExample(
    val jp: String,
    val reading: String,
    val id: String,
)

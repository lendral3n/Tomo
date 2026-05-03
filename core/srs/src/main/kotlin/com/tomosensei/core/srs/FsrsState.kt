package com.tomosensei.core.srs

enum class FsrsState(val value: Int) {
    NEW(0),
    LEARNING(1),
    REVIEW(2),
    RELEARNING(3);

    companion object {
        fun fromValue(value: Int): FsrsState = entries.firstOrNull { it.value == value } ?: NEW
    }
}

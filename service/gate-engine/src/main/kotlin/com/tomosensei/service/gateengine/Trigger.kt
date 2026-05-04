package com.tomosensei.service.gateengine

enum class Trigger(val key: String) {
    UNLOCK("unlock"),
    APP_LAUNCH("app_launch"),
    APP_SWITCH("app_switch"),
    IDLE("idle"),
    MANUAL("manual"),
}

enum class IntensityLevel(val value: Int) {
    WHISPER(1),
    TAP_TO_PASS(2),
    ANSWER_TO_PASS(3),
    CORRECT_TO_PASS(4),
    TIMEOUT_PUNISHMENT(5),
    HARDCORE_LOCK(6);

    companion object {
        fun fromValue(value: Int): IntensityLevel =
            entries.firstOrNull { it.value == value } ?: ANSWER_TO_PASS
    }
}

data class GateRequest(
    val trigger: Trigger,
    val intensity: IntensityLevel,
    val cardId: String,
    val cardFront: String,
    val cardReading: String,
    val cardMeaning: String,
    val triggeredAt: Long,
) {
    companion object
}

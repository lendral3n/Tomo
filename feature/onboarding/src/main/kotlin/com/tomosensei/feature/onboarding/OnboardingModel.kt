package com.tomosensei.feature.onboarding

import android.os.Build
import java.security.MessageDigest
import java.security.SecureRandom

enum class Preset(val key: String, val title: String, val tagline: String) {
    Casual("casual", "Casual", "\"Aku mau coba dulu\""),
    Serius("serius", "Serius", "\"Aku serius mau bisa\""),
    Hardcore("hardcore", "Hardcore", "\"Paksa aku, please\""),
}

data class OnboardingProgress(
    val step: Int = 0,
    val preset: Preset? = null,
    val pinDigits: String = "",
    val pinConfirm: String = "",
    val dailyGoal: Int = 15,
    val deviceCheckPassed: Boolean = true,
    val pinError: String? = null,
    val deviceCheck: DeviceCheckResult? = null,
    val gatedApps: Set<String> = setOf(
        "com.zhiliaoapp.musically", // TikTok
        "com.instagram.android",
        "com.google.android.youtube",
    ),
)

object PinHasher {
    fun hash(pin: String): String {
        val salt = "tomo-sensei-${Build.MODEL}".toByteArray(Charsets.UTF_8)
        var data = (pin.toByteArray(Charsets.UTF_8) + salt)
        // 5_000 SHA-256 rounds — small enough to not block UI on
        // device-class hardware, large enough that brute-forcing the
        // 4-digit space requires unlocking the device anyway.
        repeat(5_000) {
            data = MessageDigest.getInstance("SHA-256").digest(data)
        }
        return data.joinToString("") { "%02x".format(it) }
    }
}

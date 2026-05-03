package com.tomosensei.core.data.db

import android.content.Context
import android.provider.Settings
import java.security.MessageDigest

/**
 * Derives the SQLCipher passphrase from a device-specific seed (ANDROID_ID)
 * combined with a build-time pepper. Spec §13: PIN never leaves device, DB
 * encrypted at rest. ANDROID_ID stays stable across reinstalls on the same
 * device + user, which is the expected lifetime for this app's local data.
 *
 * Caveat: not survivable across factory reset / device migration. That's
 * intentional for v0 — cloud backup arrives in V1.5.
 */
internal object DatabasePassphrase {
    private const val PEPPER = "tomo-sensei-v0"

    fun derive(context: Context): ByteArray {
        @Suppress("HardwareIds")
        val androidId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID,
        ) ?: "unknown-device"
        val seed = "$androidId:$PEPPER".toByteArray(Charsets.UTF_8)
        return MessageDigest.getInstance("SHA-256").digest(seed)
    }
}

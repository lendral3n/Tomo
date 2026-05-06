package com.tomosensei.service.gateengine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Auto-starts [GateForegroundService] when the device finishes booting,
 * so the gate engine survives reboots without forcing the user to
 * re-open the app. Manifest entry needs RECEIVE_BOOT_COMPLETED.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return
        GateForegroundService.start(context)
    }
}

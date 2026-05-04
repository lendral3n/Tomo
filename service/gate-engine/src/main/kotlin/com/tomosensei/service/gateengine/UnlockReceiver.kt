package com.tomosensei.service.gateengine

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/**
 * Listens for [Intent.ACTION_USER_PRESENT] (phone unlock) and asks the
 * [GateEngine] to spawn a gate. The engine's intent is broadcast as the
 * action [GateEngine.GATE_OVERLAY_ACTION]; the overlay service in
 * :service:overlay listens for it and shows the gate UI.
 *
 * Registered via [GateForegroundService] at runtime (not in manifest) —
 * Android 8+ blocks most implicit broadcasts to manifest receivers, but
 * USER_PRESENT is one of the explicit-only system broadcasts that
 * requires runtime registration anyway.
 */
@AndroidEntryPoint
class UnlockReceiver : BroadcastReceiver() {

    @Inject lateinit var gateEngine: GateEngine

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_USER_PRESENT) return
        scope.launch {
            val showIntent = gateEngine.handleTrigger(Trigger.UNLOCK) ?: return@launch
            // Explicit broadcast within our package; the overlay service
            // registers a runtime listener too.
            context.sendBroadcast(showIntent)
        }
    }
}

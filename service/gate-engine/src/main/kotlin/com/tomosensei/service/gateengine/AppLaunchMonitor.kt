package com.tomosensei.service.gateengine

import android.app.AppOpsManager
import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Process
import com.tomosensei.core.data.db.dao.GatedAppDao
import com.tomosensei.core.data.db.entity.GatedAppEntity
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Polls [UsageStatsManager.queryEvents] for foreground transitions and
 * fires [Trigger.APP_LAUNCH] when a packageName in [GatedAppDao] is the
 * one being entered.
 *
 * Caveats:
 *  - Requires PACKAGE_USAGE_STATS, granted only via Settings (not a
 *    runtime dialog). Caller must guide the user there first.
 *  - One-second polling cadence per spec §4.1; lower would burn CPU,
 *    higher would feel sluggish.
 *  - We track the last MOVE_TO_FOREGROUND we already handled by
 *    timestamp so app re-launches inside the cooldown window don't
 *    double-fire (defense in depth — GateEngine.cooldown also dedupes).
 */
@Singleton
class AppLaunchMonitor @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gateEngine: GateEngine,
    private val gatedAppDao: GatedAppDao,
) {
    private var job: Job? = null
    private var lastHandledTimestamp: Long = 0L

    fun start(scope: CoroutineScope) {
        if (job?.isActive == true) return
        job = scope.launch {
            if (!hasUsageStatsPermission()) return@launch
            val usm = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            // Pull the watchlist once at start; if it changes the user
            // can restart the foreground service.
            val gated: Set<String> = gatedAppDao.observeEnabled().first()
                .map(GatedAppEntity::packageName)
                .toSet()
            if (gated.isEmpty()) return@launch

            // Initialize cursor at "now" so we never replay history.
            lastHandledTimestamp = System.currentTimeMillis()

            while (isActive) {
                delay(POLL_INTERVAL_MS)
                val now = System.currentTimeMillis()
                val events = usm.queryEvents(lastHandledTimestamp, now)
                val ev = UsageEvents.Event()
                while (events.hasNextEvent()) {
                    events.getNextEvent(ev)
                    if (ev.eventType == UsageEvents.Event.MOVE_TO_FOREGROUND &&
                        ev.packageName in gated
                    ) {
                        val intent = gateEngine.handleTrigger(Trigger.APP_LAUNCH)
                        intent?.let { context.sendBroadcast(it) }
                    }
                }
                lastHandledTimestamp = now
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun hasUsageStatsPermission(): Boolean {
        val ops = context.getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        @Suppress("DEPRECATION")
        val mode = ops.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            Process.myUid(),
            context.packageName,
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    companion object {
        const val POLL_INTERVAL_MS = 1_000L
    }
}

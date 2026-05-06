package com.tomosensei.service.gateengine

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Long-lived foreground service that hosts the trigger pipeline. Owns:
 *  - the runtime-registered [UnlockReceiver]
 *  - an [IDLE]-trigger ticker that fires every IDLE_INTERVAL_MILLIS
 *
 * The persistent notification is required by Android for any service that
 * outlives the activity. We keep its weight down — channel low importance,
 * minimum text — so it's a single line in the shade.
 */
@AndroidEntryPoint
class GateForegroundService : Service() {

    @Inject lateinit var gateEngine: GateEngine
    @Inject lateinit var appLaunchMonitor: AppLaunchMonitor

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val unlockReceiver = UnlockReceiver()
    private var receiverRegistered = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, buildNotification())
        registerReceivers()
        startIdleTicker()
        appLaunchMonitor.start(scope)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        if (receiverRegistered) {
            try { unregisterReceiver(unlockReceiver) } catch (_: IllegalArgumentException) {}
            receiverRegistered = false
        }
        appLaunchMonitor.stop()
        scope.cancel()
        super.onDestroy()
    }

    private fun registerReceivers() {
        val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
        // RECEIVER_NOT_EXPORTED on 13+; older platforms ignore the flag.
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Context.RECEIVER_NOT_EXPORTED
        } else {
            0
        }
        registerReceiver(unlockReceiver, filter, flags)
        receiverRegistered = true
    }

    private fun startIdleTicker() {
        scope.launch {
            while (isActive) {
                delay(IDLE_INTERVAL_MILLIS)
                val showIntent = gateEngine.handleTrigger(Trigger.IDLE) ?: continue
                sendBroadcast(showIntent)
            }
        }
    }

    private fun buildNotification(): Notification {
        ensureChannel()
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tomo Sensei sedang aktif")
            .setContentText("Gate sedang menunggu trigger")
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_DEFERRED)
            .build()
    }

    private fun ensureChannel() {
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (nm.getNotificationChannel(CHANNEL_ID) != null) return
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL_ID, "Gate engine", NotificationManager.IMPORTANCE_LOW).apply {
                description = "Indikator pasif bahwa engine gate aktif"
            },
        )
    }

    companion object {
        const val NOTIFICATION_ID = 7401
        const val CHANNEL_ID = "tomo_gate_engine"
        const val IDLE_INTERVAL_MILLIS = 20L * 60 * 1000 // 20 menit (spec §4.1)

        fun start(context: Context) {
            val intent = Intent(context, GateForegroundService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, GateForegroundService::class.java))
        }
    }
}

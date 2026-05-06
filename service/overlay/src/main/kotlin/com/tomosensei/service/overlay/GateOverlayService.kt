package com.tomosensei.service.overlay

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.WindowManager
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.tomosensei.core.common.Clock
import com.tomosensei.core.data.repository.ProgressRepository
import com.tomosensei.core.data.repository.UserStatsRepository
import com.tomosensei.core.designsystem.theme.TomoSenseiTheme
import com.tomosensei.core.srs.FsrsRating
import com.tomosensei.core.srs.FsrsScheduler
import com.tomosensei.service.gateengine.GateEngine
import com.tomosensei.service.gateengine.GateRequest
import com.tomosensei.service.gateengine.fromIntent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * Hosts the gate overlay window. Listens for [GateEngine.GATE_OVERLAY_ACTION]
 * broadcasts and inflates an [AnswerToPassGate] (Level 3) inside a
 * TYPE_APPLICATION_OVERLAY window above whatever app is foregrounded.
 *
 * Compose in an overlay window requires manual ViewTree owners — the
 * service plays the role of LifecycleOwner / ViewModelStoreOwner /
 * SavedStateRegistryOwner so Compose can run.
 */
@AndroidEntryPoint
class GateOverlayService : Service(),
    LifecycleOwner,
    ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject lateinit var progressRepository: ProgressRepository
    @Inject lateinit var statsRepository: UserStatsRepository
    @Inject lateinit var fsrsScheduler: FsrsScheduler
    @Inject lateinit var gateEngine: GateEngine
    @Inject lateinit var clock: Clock

    private val lifecycleRegistry = LifecycleRegistry(this)
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val lifecycle: Lifecycle get() = lifecycleRegistry
    override val viewModelStore: ViewModelStore = ViewModelStore()
    override val savedStateRegistry: SavedStateRegistry get() = savedStateRegistryController.savedStateRegistry

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var overlayView: ComposeView? = null
    private var windowManager: WindowManager? = null

    private val gateRequestState = mutableStateOf<GateRequest?>(null)

    private val showGateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val request = GateRequest.fromIntent(intent) ?: return
            showOverlay(request)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performAttach()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_CREATE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_START)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val filter = IntentFilter(GateEngine.GATE_OVERLAY_ACTION)
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Context.RECEIVER_NOT_EXPORTED
        } else {
            0
        }
        registerReceiver(showGateReceiver, filter, flags)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onDestroy() {
        try { unregisterReceiver(showGateReceiver) } catch (_: IllegalArgumentException) {}
        removeOverlay()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_STOP)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        viewModelStore.clear()
        scope.cancel()
        super.onDestroy()
    }

    private fun showOverlay(request: GateRequest) {
        if (overlayView != null) {
            // Already showing; queue the new request after current dismisses.
            gateRequestState.value = request
            return
        }
        gateRequestState.value = request

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT,
        ).apply { gravity = Gravity.CENTER }

        val view = ComposeView(this).apply {
            setViewTreeLifecycleOwner(this@GateOverlayService)
            setViewTreeViewModelStoreOwner(this@GateOverlayService)
            setViewTreeSavedStateRegistryOwner(this@GateOverlayService)
            setContent {
                TomoSenseiTheme {
                    val current = gateRequestState.value
                    if (current != null) {
                        AnswerToPassGate(
                            request = current,
                            onPassed = { knew -> handleAnswer(current, knew) },
                            onSkip = { handleSkip(current) },
                        )
                    }
                }
            }
        }

        overlayView = view
        runCatching { windowManager?.addView(view, params) }
            .onFailure {
                // SecurityException etc — overlay permission revoked. Bail out.
                overlayView = null
                gateRequestState.value = null
            }
    }

    private fun removeOverlay() {
        val view = overlayView ?: return
        runCatching { windowManager?.removeView(view) }
        overlayView = null
        gateRequestState.value = null
    }

    private fun handleAnswer(request: GateRequest, knew: Boolean) {
        val now = clock.nowMillis()
        scope.launch(Dispatchers.IO) {
            val rating = if (knew) FsrsRating.GOOD else FsrsRating.AGAIN
            val card = progressRepository.fsrsCard(request.cardId, now)
            val updated = fsrsScheduler.review(card, rating, now)
            progressRepository.saveFsrsCard(updated)
            statsRepository.recordReview(passed = knew)
            statsRepository.recordGatePassed()
            gateEngine.logOutcome(
                request = request,
                outcome = if (knew) GateEngine.OUTCOME_PASSED else GateEngine.OUTCOME_FAILED,
                attemptsToPass = 1,
                timeToPassMs = now - request.triggeredAt,
            )
        }
        removeOverlay()
    }

    private fun handleSkip(request: GateRequest) {
        scope.launch(Dispatchers.IO) {
            gateEngine.logOutcome(
                request = request,
                outcome = GateEngine.OUTCOME_SKIPPED,
                attemptsToPass = 0,
                timeToPassMs = clock.nowMillis() - request.triggeredAt,
            )
        }
        removeOverlay()
    }

    companion object {
        fun start(context: Context) {
            context.startService(Intent(context, GateOverlayService::class.java))
        }
    }
}

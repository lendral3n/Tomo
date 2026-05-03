package com.tomosensei.feature.drill

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import java.util.Locale

/**
 * Tiny Compose-friendly wrapper around Android TextToSpeech. We initialize
 * lazily on first composition, set ja-JP locale, and shut down on dispose.
 *
 * The TTS engine is owned by the caller composable; if multiple DrillScreens
 * mount simultaneously they each get their own engine. That's fine — TTS is
 * cheap to spin up and we keep lifecycle simple.
 */
class JapaneseTtsController(context: Context) {
    private var ready: Boolean = false
    private val engine: TextToSpeech = TextToSpeech(context.applicationContext) { status ->
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            engine.language = Locale.JAPAN
        }
    }

    fun speak(text: String) {
        if (!ready || text.isBlank()) return
        engine.stop()
        engine.speak(text, TextToSpeech.QUEUE_FLUSH, null, "tomo-drill-utterance")
    }

    fun shutdown() {
        engine.stop()
        engine.shutdown()
    }
}

@Composable
fun rememberJapaneseTts(): JapaneseTtsController {
    val context = LocalContext.current
    var controller by remember { mutableStateOf<JapaneseTtsController?>(null) }
    DisposableEffect(Unit) {
        val c = JapaneseTtsController(context)
        controller = c
        onDispose { c.shutdown() }
    }
    return controller ?: JapaneseTtsController(context)
}

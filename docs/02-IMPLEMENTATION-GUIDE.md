# Tomo Sensei — Implementation Guide

**Audience:** Claude Code (or future you when developing)
**Companion to:** `01-SPEC.md`

This guide focuses on **how to implement**, not what to build. Read 01-SPEC.md for what.

---

## 1. Prerequisites

### Tools to install
```bash
# Android Studio (Hedgehog or later)
# JDK 17+
# Gradle 8.5+
# Kotlin 2.0+

# Verify
java -version
kotlin -version
```

### Test device requirement
- Android 12+ (API 31+)
- ARM64
- RAM 8GB+ (untuk Gemma 4 E4B)
- Free storage 5GB
- USB debugging enabled
- Developer options on

---

## 2. Initial Project Setup

### Step 1: Buat project Android Studio baru

```
Project name: TomoSensei
Package name: com.tomosensei.app
Language: Kotlin
Minimum SDK: API 31 (Android 12)
Build configuration: Kotlin DSL (build.gradle.kts)
```

### Step 2: Setup Gradle dependencies

`gradle/libs.versions.toml`:
```toml
[versions]
agp = "8.5.0"
kotlin = "2.0.0"
compose-bom = "2024.06.00"
hilt = "2.51"
room = "2.6.1"
lifecycle = "2.8.2"
coroutines = "1.8.1"
litertlm = "1.0.0"  # Verify latest
mediapipe = "0.10.14"

[libraries]
# Compose
androidx-compose-bom = { module = "androidx.compose:compose-bom", version.ref = "compose-bom" }
androidx-compose-ui = { module = "androidx.compose.ui:ui" }
androidx-compose-material3 = { module = "androidx.compose.material3:material3" }
androidx-compose-tooling = { module = "androidx.compose.ui:ui-tooling" }
androidx-activity-compose = "androidx.activity:activity-compose:1.9.0"

# Hilt
hilt-android = { module = "com.google.dagger:hilt-android", version.ref = "hilt" }
hilt-compiler = { module = "com.google.dagger:hilt-android-compiler", version.ref = "hilt" }
androidx-hilt-navigation-compose = "androidx.hilt:hilt-navigation-compose:1.2.0"

# Room
room-runtime = { module = "androidx.room:room-runtime", version.ref = "room" }
room-compiler = { module = "androidx.room:room-compiler", version.ref = "room" }
room-ktx = { module = "androidx.room:room-ktx", version.ref = "room" }

# Coroutines
kotlinx-coroutines-android = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-android", version.ref = "coroutines" }

# Lifecycle
lifecycle-viewmodel-compose = { module = "androidx.lifecycle:lifecycle-viewmodel-compose", version.ref = "lifecycle" }
lifecycle-runtime-compose = { module = "androidx.lifecycle:lifecycle-runtime-compose", version.ref = "lifecycle" }

# AI Edge / LiteRT-LM
google-ai-edge-litertlm = { module = "com.google.ai.edge.litertlm:litertlm", version.ref = "litertlm" }
mediapipe-tasks-genai = { module = "com.google.mediapipe:tasks-genai", version.ref = "mediapipe" }

# DataStore (untuk simple settings)
datastore-preferences = "androidx.datastore:datastore-preferences:1.1.1"

# SQLCipher (encrypted Room)
sqlcipher = "net.zetetic:android-database-sqlcipher:4.5.4"

# Image / Camera
camera-core = "androidx.camera:camera-core:1.3.4"
camera-camera2 = "androidx.camera:camera-camera2:1.3.4"
camera-lifecycle = "androidx.camera:camera-lifecycle:1.3.4"
camera-view = "androidx.camera:camera-view:1.3.4"

# Crash reporting
sentry-android = "io.sentry:sentry-android:7.10.0"
```

### Step 3: Project gradle setup

`app/build.gradle.kts`:
```kotlin
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.hilt)
}

android {
    namespace = "com.tomosensei.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tomosensei.app"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "0.1.0"

        // Untuk Gemma 4
        ndk {
            abiFilters += listOf("arm64-v8a")
        }
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}
```

---

## 3. Implementation Phase Order

**Build dalam urutan ini.** Setiap phase harus working sebelum lanjut ke next.

### Phase 1: Foundation (Week 1-4)

#### 1.1 Setup Compose theme + design system

Lokasi: `core/design-system/`

Tokens:
```kotlin
// Colors (dari prototype v0.2)
val WashiBg = Color(0xFFF5ECD9)
val InkBlack = Color(0xFF1A0F08)
val SenseiRed = Color(0xFFDC4D3A)  // Hanko red
val Sumi = Color(0xFF8B6F47)        // Tinta cokelat
val DarkBg = Color(0xFF0F0A06)

// Typography
val ShipporiMincho = FontFamily(...)  // Untuk Japanese display
val ZenKakuGothic = FontFamily(...)   // Untuk Japanese sans
val Manrope = FontFamily(...)         // Untuk UI Latin
```

Components: WashiCard, HankoStamp, GateBackdrop, SenseiButton.

#### 1.2 Setup Room DB + entities

```kotlin
@Database(
    entities = [
        Card::class,
        CardProgress::class,
        TriggerConfig::class,
        GatedApp::class,
        GateLog::class,
        UserStats::class,
        Settings::class,
        ChatMessage::class
    ],
    version = 1
)
abstract class TomoSenseiDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
    abstract fun progressDao(): ProgressDao
    abstract fun triggerConfigDao(): TriggerConfigDao
    // ... etc
}
```

Use SQLCipher passphrase derived from device-specific seed.

#### 1.3 Setup Hilt DI

`@HiltAndroidApp` di Application class. Modules untuk:
- DatabaseModule (provide Room)
- AIModule (provide GemmaInferenceManager — singleton)
- RepositoryModule (provide repos)

#### 1.4 Implement FSRS algorithm

Pakai Kotlin port dari https://github.com/open-spaced-repetition. Atau implement dari scratch berdasarkan paper FSRS v4.

Test: unit test dengan known input → expected next-due dates.

#### 1.5 Deck loader

Load N5 deck dari `assets/decks/n5.json`. Format:
```json
{
  "version": "1.0",
  "level": "N5",
  "cards": [
    {
      "id": "n5_v_001",
      "type": "vocab",
      "front": "食べる",
      "reading": "たべる",
      "meaning": "makan",
      "examples": [...]
    }
  ]
}
```

Insert ke Room di first launch via `ApplicationStartup` work.

---

### Phase 2: Core Drill (Week 5-6)

#### 2.1 DrillScreen Compose UI

- Vertical card stack with swipe gestures
- Tap = flip
- Swipe up = "tau", swipe down = "lupa"
- TTS button (pakai Android `TextToSpeech` ja-JP)

Reference UI: prototype v0.2 (artifact yang sudah dibuat).

#### 2.2 Connect to FSRS + Room

ViewModel:
```kotlin
@HiltViewModel
class DrillViewModel @Inject constructor(
    private val cardRepo: CardRepository,
    private val progressRepo: ProgressRepository,
    private val srs: SrsScheduler
) : ViewModel() {
    val dueCards = progressRepo.getDueCards().asStateFlow()
    fun onAnswer(card: Card, knew: Boolean) { ... }
}
```

#### 2.3 Stats screen

Streak, mastered count, today count, weekly chart (pakai `Canvas` Compose atau MPAndroidChart).

---

### Phase 3: Gate Engine (Week 7-10)

**Most complex phase.** Banyak Android system integration.

#### 3.1 Foreground service

```kotlin
class GateForegroundService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startForeground(NOTIF_ID, buildNotification())
        // Initialize trigger monitors
        return START_STICKY
    }
}
```

Manifest:
```xml
<service
    android:name=".service.GateForegroundService"
    android:foregroundServiceType="specialUse"
    android:exported="false">
    <property android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
        android:value="user-initiated learning gates" />
</service>
```

#### 3.2 Trigger Monitors

**Unlock Monitor:**
```kotlin
class UnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_USER_PRESENT) {
            GateEngine.handleTrigger(Trigger.UNLOCK)
        }
    }
}
```

**App Launch Monitor:**
```kotlin
// Pakai UsageStatsManager polling (setiap 1 detik di foreground service)
class AppLaunchMonitor @Inject constructor(...) {
    fun startMonitoring() {
        scope.launch {
            while (isActive) {
                val current = getForegroundApp()
                if (current != lastApp && isGatedApp(current)) {
                    GateEngine.handleTrigger(Trigger.APP_LAUNCH, current)
                }
                lastApp = current
                delay(1000)
            }
        }
    }
}
```

**Idle Timer:**
```kotlin
// Track screen-on time, trigger setiap N menit
class IdleTimerMonitor @Inject constructor(...) {
    fun start() {
        scope.launch {
            while (isActive) {
                delay(idleIntervalMillis)
                if (isScreenOn()) GateEngine.handleTrigger(Trigger.IDLE)
            }
        }
    }
}
```

#### 3.3 Gate Engine

```kotlin
@Singleton
class GateEngine @Inject constructor(
    private val srs: SrsRepository,
    private val settings: SettingsRepository,
    private val gateLauncher: GateLauncher,
    private val cooldown: CooldownTracker,
    private val logger: GateLogger
) {
    suspend fun handleTrigger(trigger: Trigger, context: Any? = null) {
        val config = settings.getConfigFor(trigger) ?: return
        if (!config.enabled) return
        if (!cooldown.isPast(trigger)) return
        if (!withinDailyCap(trigger)) return
        
        val card = srs.getNextDue() ?: return
        val gate = Gate(
            card = card,
            intensity = config.intensity,
            trigger = trigger,
            timestamp = System.currentTimeMillis()
        )
        gateLauncher.show(gate)
        cooldown.mark(trigger)
        logger.logTriggered(gate)
    }
}
```

#### 3.4 Gate UI Overlay

```kotlin
class GateOverlayService : Service() {
    private lateinit var windowManager: WindowManager
    private var overlayView: ComposeView? = null
    
    fun showGate(gate: Gate) {
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        
        overlayView = ComposeView(this).apply {
            setContent {
                TomoSenseiTheme {
                    GateScreen(
                        gate = gate,
                        onDismiss = { _, knew -> 
                            removeOverlay()
                            handleAnswer(gate, knew)
                        }
                    )
                }
            }
            setViewTreeLifecycleOwner(...)
            setViewTreeViewModelStoreOwner(...)
            setViewTreeSavedStateRegistryOwner(...)
        }
        
        windowManager.addView(overlayView, params)
    }
}
```

**CRITICAL:** Compose di overlay window butuh manual lifecycle setup. Reference: https://developer.android.com/reference/androidx/lifecycle/setViewTreeLifecycleOwner

#### 3.5 Intensity Levels Implementation

Pisah Composable per level:
- `WhisperGate.kt` (level 1 — bubble di pinggir)
- `TapToPassGate.kt` (level 2)
- `AnswerToPassGate.kt` (level 3)
- `CorrectToPassGate.kt` (level 4)
- `TimeoutGate.kt` (level 5)
- `HardcoreLockGate.kt` (level 6)

Each level: own state machine for retry, timeout, bypass.

---

### Phase 4: AI Integration (Week 11-13)

#### 4.1 Reference Google AI Edge Gallery

**SEBELUM CODING:** Clone dan jalankan Google AI Edge Gallery di device kamu.

```bash
git clone https://github.com/google-ai-edge/gallery.git
cd gallery
# Build & install ke device
./gradlew installDebug
```

Pelajari:
- Cara setup LiteRT-LM
- Model download flow
- Inference dengan multi-modal input
- Streaming response
- Memory management

Banyak code bisa di-port langsung.

#### 4.2 Model download flow

```kotlin
class ModelDownloader @Inject constructor(
    @ApplicationContext private val context: Context,
    private val client: OkHttpClient
) {
    suspend fun downloadGemma4(
        onProgress: (Float) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        val url = "https://huggingface.co/google/gemma-4-e4b-it-litertlm/resolve/main/model.litertlm"
        val targetFile = File(context.filesDir, "gemma-4-e4b-it.litertlm")
        
        // Stream download dengan progress callback
        // Verify checksum
        // Return file path
    }
}
```

Wi-Fi only check via ConnectivityManager.

#### 4.3 GemmaInferenceManager

Lihat 01-SPEC.md §8 untuk full implementation.

Key points:
- Init lazy (load model setelah download done)
- Single instance via Hilt @Singleton
- Streaming response untuk Sensei chat
- Timeout 30s per inference
- Error handling: model corrupted, OOM, etc

#### 4.4 Sensei Chat UI

ChatScreen Compose dengan:
- Message list (bubble user vs sensei)
- Streaming text display
- Image attachment button
- Quick prompts (level-based)

ViewModel handle inference + history persistence.

#### 4.5 Photo Sensei UI

CameraX integration:
```kotlin
@Composable
fun PhotoSenseiScreen() {
    val context = LocalContext.current
    val imageCapture = remember { ImageCapture.Builder().build() }
    
    // Camera preview + capture button
    // On capture → bitmap → sendToGemma()
    // Show extracted text + translation + grammar
}
```

Bitmap → Gemma 4:
```kotlin
suspend fun analyzePhoto(bitmap: Bitmap): PhotoAnalysis {
    val response = gemma.ask(
        prompt = PHOTO_SENSEI_PROMPT.replace("{LEVEL}", userLevel),
        image = bitmap
    )
    return parseJsonResponse(response)
}
```

---

### Phase 5: Onboarding & Polish (Week 14-16)

#### 5.1 Onboarding flow

Multi-step Compose with NavHost:
```
WelcomeScreen → CommitmentScreen → DeviceCheckScreen → 
PresetScreen → PermissionFunnelScreen → PinSetupScreen → 
GatedAppsScreen → DailyGoalScreen → ModelDownloadScreen → 
SimulationScreen → DoneScreen
```

#### 5.2 Settings UI

Per-trigger configurator dengan slider 0-6, sama seperti prototype v0.2.

#### 5.3 First-time UX

- Skip-able onboarding section yang opsional
- Tooltips untuk first 3 gates
- "Jangan tampilkan lagi" untuk tutorial

---

## 4. Code Quality Standards

### Architecture
- MVVM dengan Compose
- Repository pattern (ViewModel → Repository → DataSource)
- Use cases hanya kalau benar-benar reusable
- Single source of truth: Room DB
- Unidirectional data flow

### Testing
- Unit test untuk: SRS, GateEngine logic, AI prompt parsing
- Integration test untuk: Room DAOs, model download
- UI test minimal: critical flows (onboarding, gate dismiss, drill)
- Pakai fakes (in `testing/fakes/`) untuk Hilt test modules

### Style
- Detekt + ktlint
- Conventional commits
- Branch naming: `feature/gate-engine`, `fix/overlay-leak`, etc

---

## 5. Common Gotchas

### Compose in Overlay
Manual lifecycle setup needed. See:
https://developer.android.com/reference/androidx/lifecycle/setViewTreeLifecycleOwner

### Foreground Service in Android 14+
Specific `foregroundServiceType` mandatory. "specialUse" + property declaration.

### UsageStatsManager
Requires user manual permission grant via Settings (no runtime dialog).

### LiteRT-LM Memory
Gemma 4 E4B uses ~5GB RAM saat inference. Release session after each inference.

### Battery Optimizations
Some OEMs (Xiaomi, Oppo, Samsung) aggressively kill foreground services. Add to manifest:
```xml
<uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS" />
```
Guide user to whitelist app in onboarding.

### Overlay Permission
SYSTEM_ALERT_WINDOW butuh manual user grant via Settings (Settings > Apps > Special Access > Display over other apps).

---

## 6. Build & Test

```bash
# Debug build
./gradlew assembleDebug

# Install ke device
./gradlew installDebug

# Run unit tests
./gradlew test

# Run instrumented tests
./gradlew connectedAndroidTest

# Lint
./gradlew detekt ktlintCheck
```

---

## 7. Reference Code Sources

1. **Google AI Edge Gallery**: https://github.com/google-ai-edge/gallery — TEMPLATE PRIMER untuk Gemma 4 + LiteRT-LM
2. **Anki Android**: https://github.com/ankidroid/Anki-Android — reference untuk SRS implementation di Android
3. **OneSec source (jika tersedia)**: untuk pattern overlay-based intervention
4. **Compose samples**: https://github.com/android/compose-samples
5. **Android Architecture Samples**: https://github.com/android/architecture-samples

---

## 8. When stuck

1. Check `01-SPEC.md` untuk intent
2. Check Google AI Edge Gallery code
3. Check Android Developers docs untuk system APIs
4. Sentry breadcrumbs untuk debugging crashes
5. ADB logcat dengan filter: `adb logcat -s TomoSensei`

---

*This guide tumbuh seiring development. Update saat ketemu insight baru.*

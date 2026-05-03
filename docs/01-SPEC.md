# Tomo Sensei — Technical Specification v0.3

**Status:** Final, ready for implementation
**Date:** 2026-05-03
**Approach:** Build-for-self first

---

## 1. Vision

App belajar bahasa Jepang yang bekerja melalui **friction**, bukan engagement. Memanfaatkan kebiasaan HP user (unlock, scroll TikTok, switch app) sebagai trigger untuk gate kecil yang harus di-pass dengan recall bahasa Jepang sebelum bisa lanjut.

**Filosofi:** "Membuat tidak-belajar jadi mustahil." Friction sebagai fitur, bukan bug.

**Pembanding pola:** OneSec, Opal, Forest (friction-based apps yang punya jutaan user). Tomo Sensei pakai pola yang sama, tapi friction = belajar Jepang.

**Diferensiasi vs kompetitor:**

| App | Approach | Result |
|---|---|---|
| Duolingo | Friendly gamification | User skip easily |
| WaniKani | Scheduled SRS | Abandoned when busy |
| Doomersion | Passive immersion | No active recall |
| **Tomo Sensei** | **Forced friction + on-device AI + multi-modal** | **Can't skip without effort** |

---

## 2. Lock Keputusan

| Aspek | Pilihan | Alasan |
|---|---|---|
| Platform | Android only (Kotlin native) | Akses sistem dalam |
| UI | Jetpack Compose | Modern, declarative |
| AI | Gemma 4 E4B-it (3.6GB) | On-device, multi-modal, 32K context |
| AI Runtime | LiteRT-LM (Google AI Edge) | Native Android, optimized |
| DB Lokal | Room (SQLite + SQLCipher) | Standar Android, encrypted |
| DI | Hilt | Less boilerplate |
| Async | Coroutines + Flow | Native Kotlin |
| Backend | Node.js + Fastify (kamu) | Hanya untuk content sync, opsional |
| Backend DB | PostgreSQL via Supabase | Free tier |
| Min Device | Android 12+, RAM 8GB+, flagship-tier | Untuk Gemma 4 E4B |
| Distribution | Self-use → side-load APK → Play Store | Build for one |

---

## 3. Architecture: Gate-First

### Mental Model

```
[System Event] → [Trigger Monitor] → [Gate Engine] → [Gate UI Overlay]
                                            ↓
                                     [SRS Queue + Settings]
                                            ↓
                                     [Pass Condition Met]
                                            ↓
                                     [Log Review + Cooldown]
                                            ↓
                                     [Dismiss → User Resume]
```

### Komponen Inti

**Trigger Monitor** — service yang mendengarkan system events:
- Phone unlock (BroadcastReceiver `ACTION_USER_PRESENT`)
- App launch (UsageStatsManager polling)
- App switch (UsageStatsManager event log)
- Idle timer (foreground service tick)
- Manual (user buka main app)

**Gate Engine** — core logic:
- Cek apakah trigger enabled
- Cek cooldown (default 60 detik antar gate)
- Cek daily cap
- Fetch next due card dari SRS
- Pilih intensity level berdasarkan settings
- Spawn Gate UI

**Gate UI** — overlay dengan 6 level intensitas (lihat §4.2)

**Pass Condition** — apa yang user harus lakukan:
- Tap untuk lewat (level 1-2)
- Jawab tau/lupa (level 3)
- Multiple choice benar (level 4-6)
- Salah X kali = timeout (level 5)
- Salah = layar terkunci (level 6)

**Override Mechanisms (safety):**
- Emergency PIN bypass (level 5-6)
- Skip button (level 1-4)
- Panggilan darurat (always)
- Auto-disable hardcore mode setelah 30 menit
- Reboot bypass

---

## 4. Trigger × Intensity Matrix

### 4.1 Trigger Catalog

| Trigger | Default Frequency | Permission Needed |
|---|---|---|
| Unlock HP | Setiap unlock | `RECEIVE_BOOT_COMPLETED`, screen state |
| Buka App Distraksi (TikTok/IG/YT) | Setiap launch | `PACKAGE_USAGE_STATS` |
| App Switch | Setiap N kali (default 5) | `PACKAGE_USAGE_STATS` |
| Idle Timer | Setiap 20 menit screen-on | `FOREGROUND_SERVICE` |
| Manual | User buka main app | Built-in |

### 4.2 Intensity Levels

| Lv | Nama | Mekanik | Lock? | Risk | Distribusi |
|---|---|---|---|---|---|
| 1 | Whisper | Bubble di pinggir, ignorable | No | Low | Play Store OK |
| 2 | Tap to Pass | Card 3 detik, tap dismiss | No | Low | Play Store OK |
| 3 | Answer to Pass | Harus jawab tau/lupa | No | Low | Play Store OK |
| 4 | Correct to Pass | Multiple choice, salah ulangi (max 3) | Soft | Medium | Play Store OK |
| 5 | Timeout Punishment | Gagal 3x = HP terkunci 5 menit | Hard | High | Side-load |
| 6 | Hardcore Lock | Layar terkunci sampai jawab benar | Hard | High | Side-load |

### 4.3 Presets

| Preset | Trigger Config | Use Case |
|---|---|---|
| **Casual** | Unlock=2, Idle=1 | "Aku mau coba dulu" |
| **Serius** | Unlock=3, TikTok=4, IG=4, Switch=3, Idle=2 | "Aku serius mau bisa" |
| **Hardcore** | Semua trigger=4 (PS) atau 5-6 (side-load) | "Paksa aku, please" |

User bisa custom per-trigger setelah pilih preset.

---

## 5. Multi-Modal Features (Gemma 4 capability)

### 5.1 Foto Sensei (core feature)

User foto teks Jepang (menu ramen, signage, manga panel, screenshot game) → AI extract teks + translate + breakdown grammar.

**Flow:**
1. User tap "Foto Sensei" di main app atau bubble
2. Camera intent → ambil foto / pilih dari galeri
3. Image dikirim ke Gemma 4 E4B (multi-modal input)
4. AI return: extracted text + reading + translation + grammar notes
5. User bisa save vocab baru ke deck personal

**Prompt template:**
```
Kamu adalah Tomo Sensei. User mengirim gambar yang berisi teks Jepang.
Tugasmu:
1. Ekstrak SEMUA teks Jepang yang terlihat
2. Berikan reading (hiragana) untuk kanji
3. Translate ke bahasa Indonesia
4. Identifikasi grammar pattern penting (level: {USER_LEVEL})
5. Highlight 1-3 vocab paling berguna untuk dihafal

Output JSON:
{
  "extracted_text": "...",
  "reading": "...",
  "translation": "...",
  "grammar_notes": ["..."],
  "vocab_highlights": [{"jp":"...", "reading":"...", "meaning":"...", "level":"N5"}]
}
```

### 5.2 Sensei Chat (multi-turn dengan 32K context)

Sebelumnya 8K context membatasi kemampuan multi-turn. Sekarang 32K → bisa simpan:
- 50+ turn conversation history
- Full deck context (kartu yang sedang user pelajari)
- User's mistake history (untuk personalisasi)

### 5.3 Pronunciation Coach (text + audio output)

User tap kanji → Gemma 4 generate breakdown dengan audio (TTS terpisah). Multi-modal input bukan output, jadi audio masih pakai Android TTS native.

---

## 6. Onboarding Flow

### Step 1: Welcome + Komitmen
"App ini mengganggu kamu — itu fiturnya. Mau lanjut?" [Yes / No]

### Step 2: Device Compatibility Check
Auto-detect:
- Android version >= 12
- Total RAM >= 8GB
- Free storage >= 5GB
- ABI = arm64-v8a
- GPU support (untuk inference acceleration)

Jika fail → tampilkan: "Device kamu mungkin terlalu lemah untuk Sensei AI. Tetap lanjut dengan core gate engine (tanpa Sensei chat)?"

### Step 3: Pilih Preset
Casual / Serius / Hardcore. Tampilkan demo simulasi singkat tiap level.

### Step 4: Permission Funnel
Hanya minta permission untuk preset yang dipilih. Justifikasi per permission.

### Step 5: Set Emergency PIN
4-digit PIN untuk bypass level 5-6. Local-only, tidak ke server.

### Step 6: Pick Apps to Gate (jika preset includes app launch trigger)
List installed apps, user pilih (default: TikTok, IG, YouTube, browser).

### Step 7: Daily Goal
5 / 15 / 30 / 50 cards/day.

### Step 8: Download Gemma 4 Model (Wi-Fi only)
- Download 3.6GB di background
- Show progress + estimated time
- App tetap usable (gate engine + core drill) sambil tunggu
- Kalau gagal/skip → mode "tanpa Sensei", masih bisa pakai gate engine

### Step 9: Test Run
3 simulasi gate dengan kartu sample. "Kalau ini terlalu mengganggu, kamu bisa turunkan intensity nanti."

---

## 7. Data Models

```kotlin
@Entity(tableName = "cards")
data class Card(
    @PrimaryKey val id: String,           // "n5_v_001"
    val level: String,                    // "N5", "N4", ...
    val type: String,                     // "vocab" | "kanji" | "grammar"
    val front: String,                    // "食べる"
    val reading: String,                  // "たべる"
    val meaning: String,                  // "makan"
    val examples: String,                 // JSON array of {jp, reading, id_translation}
    val tags: String,                     // CSV: "verb,daily,food"
    val audioUrl: String? = null,
    val mnemonicHint: String? = null
)

@Entity(tableName = "progress")
data class CardProgress(
    @PrimaryKey val cardId: String,
    val stability: Float,                 // FSRS state
    val difficulty: Float,                // FSRS state
    val due: Long,                        // epoch millis
    val lastReview: Long?,
    val reps: Int,
    val lapses: Int,
    val state: Int                        // 0=new, 1=learning, 2=review, 3=relearning
)

@Entity(tableName = "trigger_configs")
data class TriggerConfig(
    @PrimaryKey val triggerType: String,  // "unlock", "tiktok", "switch", "idle"
    val enabled: Boolean,
    val intensity: Int,                    // 1-6
    val cooldownSeconds: Int,
    val dailyCap: Int,
    val frequencyMod: String              // "every", "every_nth:5", "daily_cap:30"
)

@Entity(tableName = "gated_apps")
data class GatedApp(
    @PrimaryKey val packageName: String,
    val appName: String,
    val enabled: Boolean,
    val customIntensity: Int? = null
)

@Entity(tableName = "gate_logs")
data class GateLog(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long,
    val trigger: String,
    val intensity: Int,
    val cardId: String,
    val outcome: String,                   // "passed", "failed", "skipped", "bypassed"
    val attemptsToPass: Int,
    val timeToPassMs: Long
)

@Entity(tableName = "user_stats")
data class UserStats(
    @PrimaryKey val id: Int = 0,
    val streak: Int,
    val lastActiveDate: String,
    val totalReviews: Int,
    val cardsLearned: Int,
    val gatesPassed: Int,
    val targetLevel: String,              // "N5"
    val dailyGoal: Int
)

@Entity(tableName = "settings")
data class Settings(
    @PrimaryKey val id: Int = 0,
    val emergencyPinHash: String,
    val pauseSchedule: String,             // JSON: {start: "09:00", end: "17:00"}
    val modelLoaded: Boolean,
    val soundEnabled: Boolean,
    val hapticEnabled: Boolean
)

@Entity(tableName = "chat_history")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId: String,
    val role: String,                      // "user" | "sensei"
    val content: String,
    val imageUri: String? = null,
    val timestamp: Long
)
```

---

## 8. AI Architecture (Gemma 4 E4B-it on-device)

### 8.1 Setup via LiteRT-LM

Pakai Google AI Edge SDK. Reference implementation: **Google AI Edge Gallery** (GitHub).

```kotlin
class GemmaInferenceManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var llmInference: LlmInference? = null

    suspend fun initialize() = withContext(Dispatchers.IO) {
        val modelPath = "${context.filesDir}/gemma-4-e4b-it.litertlm"
        val options = LlmInferenceOptions.builder()
            .setModelPath(modelPath)
            .setMaxTokens(2048)
            .setMaxNumImages(1)
            .build()
        llmInference = LlmInference.createFromOptions(context, options)
    }

    suspend fun ask(
        prompt: String,
        image: Bitmap? = null,
        onPartialResult: ((String) -> Unit)? = null
    ): String = withContext(Dispatchers.Default) {
        val session = LlmInferenceSession.createFromOptions(
            llmInference,
            LlmInferenceSessionOptions.builder()
                .setTopK(40)
                .setTemperature(0.7f)
                .build()
        )
        session.addQueryChunk(prompt)
        image?.let { session.addImage(BitmapImageBuilder(it).build()) }
        
        val response = StringBuilder()
        session.generateResponseAsync { partial, done ->
            response.append(partial)
            onPartialResult?.invoke(partial)
        }.await()
        session.close()
        response.toString()
    }

    fun release() {
        llmInference?.close()
    }
}
```

### 8.2 Prompt Templates (system prompts)

```kotlin
const val SENSEI_BASE = """
Kamu adalah Tomo Sensei, AI tutor bahasa Jepang untuk pelajar Indonesia.

ATURAN KETAT:
1. Bahasa default: Indonesia. Tulis Jepang hanya saat memberi contoh.
2. MAKSIMAL 3 kalimat per respons. Singkat, padat.
3. Selalu beri contoh konkret (kalimat Jepang + arti).
4. Format kalimat Jepang: 漢字 (ひらがな) = arti.
5. Level user: {LEVEL}. Jangan pakai grammar di atas level user+1.
6. Jangan menggurui. Tanggapi seperti teman yang lebih pintar.
"""

const val PHOTO_SENSEI = """
User mengirim gambar. Ekstrak teks Jepang dan analisis.

Output JSON:
{
  "extracted_text": "teks Jepang asli",
  "reading": "ひらがな untuk kanji",
  "translation": "terjemahan Indonesia",
  "grammar_notes": ["catatan grammar penting"],
  "vocab_highlights": [{"jp":"", "reading":"", "meaning":"", "level":"N5"}]
}
"""

const val CARD_DEEP_DIVE = """
User minta penjelasan untuk kartu:
- Kata: {FRONT}
- Bacaan: {READING}
- Arti: {MEANING}

Berikan:
1. 2 contoh kalimat (level N{LEVEL})
2. Cara cepat hafal (mnemonic atau context)
3. Catatan penting (jika ada)

Output: prose pendek, max 5 kalimat total.
"""
```

### 8.3 Inference Strategy

| Use Case | Strategy |
|---|---|
| Sensei chat | Streaming response, max 400 tokens |
| Photo Sensei | Single inference, max 600 tokens, parsed as JSON |
| Card explain | Cache result per card-id, regenerate on-demand |
| Sentence gen | Generate 5 sentences ahead, cache di Room |

### 8.4 Model Distribution

- Model **TIDAK** dibundel di APK (3.6GB terlalu besar)
- Download saat onboarding step 8, Wi-Fi only by default
- Hosted di Hugging Face atau own CDN
- Versioning: `gemma-4-e4b-it-v1.0.litertlm`
- Checksum verification setelah download
- App tetap usable (gate engine + drill) selagi model belum download

---

## 9. Permission Strategy

### 9.1 Permission Matrix

| Permission | Tujuan | Risk | Required |
|---|---|---|---|
| `POST_NOTIFICATIONS` | Notif gate, smart cards | Low | All |
| `FOREGROUND_SERVICE` | Gate engine, bubble | Low | All |
| `SYSTEM_ALERT_WINDOW` | Overlay gate UI | Medium | Lv 1+ |
| `RECEIVE_BOOT_COMPLETED` | Auto-start setelah reboot | Low | All |
| `PACKAGE_USAGE_STATS` | App launch & switch detection | Medium | Serius/Hardcore |
| `BIND_ACCESSIBILITY_SERVICE` | Touch intercept (Lv 6) | High | Hardcore (side-load) |
| `BIND_DEVICE_ADMIN` | Lock screen (Lv 5 timeout) | High | Hardcore (side-load) |
| `CAMERA` | Foto Sensei | Low | Optional |
| `READ_EXTERNAL_STORAGE` (scoped) | Pilih foto dari galeri | Low | Optional |
| `RECORD_AUDIO` | STT pronunciation (V1.0+) | Low | Optional |
| `INTERNET` | Model download, content sync | Low | First-run only |

### 9.2 Permission Funnel
- **Casual preset**: 4 permissions (semua "low")
- **Serius preset**: + 2 permissions (medium)
- **Hardcore (Play Store)**: + 1 permission (medium)
- **Hardcore (side-load)**: + 2 permissions (high)

User dipandu satu per satu, dengan justifikasi per permission.

---

## 10. SRS Algorithm: FSRS v4

Free Spaced Repetition Scheduler — lebih akurat dari SM-2 klasik Anki.

State per kartu:
- `stability` (S): kekuatan memori (hari)
- `difficulty` (D): kesulitan kartu (1-10)
- `state`: new/learning/review/relearning

```
On review:
  rating = user_input  // 1=lupa, 2=hard, 3=good, 4=easy
  retrievability = exp(-elapsed_days / S)
  
  if rating == 1:
    S' = S * exp(w_lapse * D * (S^-w_decay))
    state = relearning
    next_due = now + 10 minutes
  else:
    S' = S * (1 + exp(w[8]) * (11-D) * S^-w[9] * (exp(w[10]*(1-R)) - 1))
    next_due = now + S' days
  
  D' = clamp(D + w[6] * (rating - 3), 1, 10)
```

Library: port FSRS dari `open-spaced-repetition/fsrs-rs` ke Kotlin (atau pakai existing JVM port jika ada).

### Daily Mix
- Default 10 new cards/hari (configurable)
- Mix new + review (rasio 30/70)
- Daily cap untuk mencegah burnout

### Gate-Specific SRS Priority
- Gate dari unlock pagi → kartu yang due hari ini
- Gate dari TikTok → kartu casual (vocab sehari-hari)
- Gate dari hardcore lockdown → kartu yang sering gagal (paksa konsolidasi)

---

## 11. Module Structure (Kotlin / Gradle)

```
app/                          # Main application module
├── build.gradle.kts
└── src/main/
    ├── AndroidManifest.xml
    └── kotlin/com/tomosensei/
        └── TomoSenseiApp.kt  # Application class

feature/
├── onboarding/               # Welcome, preset, permissions, model download
├── gate-config/              # Per-trigger configurator UI
├── main-app/                 # Stats, manual drill, settings entry
├── chat/                     # Sensei chat UI
├── photo/                    # Foto Sensei UI
└── emergency/                # PIN setup, bypass UI

service/
├── gate-engine/              # Core gate logic (DI: SRS, Settings, Cooldown)
├── trigger-monitor/          # Listen system events
├── overlay/                  # Gate UI overlay window
├── accessibility/            # Touch intercept (hardcore)
├── timeout-lock/             # Phone lockdown after fail
├── bubble/                   # Floating bubble (Lv 1)
├── notif-gate/               # Notification gate (Lv 1-2)
└── audio-companion/          # Background TTS (V1.0+)

core/
├── srs/                      # FSRS algorithm
├── ai/                       # Gemma 4 inference manager
├── ai-prompts/               # Prompt templates
├── content/                  # Deck loader, JSON parser
├── data/                     # Room DB, repositories, daos
├── design-system/            # Compose theme, components, tokens
├── tts-stt/                  # Voice helpers
└── common/                   # Utils, extensions, constants

testing/
├── shared-test-utils/
└── fakes/                    # Fake implementations untuk DI
```

### Gradle Modules
Pakai Gradle version catalogs (`libs.versions.toml`) untuk dependency management.

---

## 12. Backend (minimal, optional for MVP)

Backend hanya dibutuhkan untuk:
1. Deck content distribution (JSON, versioned)
2. Optional cloud backup user progress
3. Anonymous analytics (opt-in)

**Tech:** Node.js + Fastify + Postgres (Supabase).

**Endpoints v1:**
```
POST   /v1/auth/signup
POST   /v1/auth/login
GET    /v1/decks/:level              # Returns deck JSON + version
POST   /v1/sync/progress             # Push local progress (opt-in)
GET    /v1/sync/progress             # Pull (multi-device)
POST   /v1/analytics/event           # Anonymous events (opt-in)
GET    /v1/content/version           # Check for deck updates
```

Untuk MVP self-use: **skip backend sama sekali**. Pakai static JSON file di assets atau `filesDir`.

---

## 13. Privacy & Security

### Prinsip
1. **On-device first.** Semua AI inference dan gate logic lokal.
2. **No telemetry by default.** Analytics opt-in.
3. **Encrypted local DB.** SQLCipher.
4. **Zero PII saat sync.** Hash user ID + event saja.

### Foto Sensei
- Foto **TIDAK PERNAH** dikirim ke server
- Inference 100% on-device (Gemma 4 multi-modal)
- Optional: simpan foto + result di local history (user toggle)

### PIN Storage
- 4-digit PIN di-hash dengan PBKDF2 + device-specific salt
- Local-only, tidak pernah ke cloud
- Lupa PIN = uninstall (intentional, untuk mencegah self-bypass)

### GDPR / UU PDP Indonesia
- Privacy policy bahasa Indonesia
- Right to delete (hapus data dengan satu klik)
- Data export (JSON download)

---

## 14. Distribution Strategy

### Self-Use Phase (bulan 1-6)
- Build & install ke device sendiri via Android Studio
- Iterasi cepat tanpa Play Store review

### Closed Beta (bulan 6-9)
- Side-load APK ke 10-20 friend-of-friend
- Distribute via Google Drive / direct link
- Feedback kumpulan via Discord / Google Form

### Side-load Public (bulan 9+)
- APK di own website (`tomosensei.app` atau sejenis)
- Includes hardcore mode (level 5-6)
- F-Droid submission opsional

### Play Store (bulan 12+)
- Max intensity: Level 4
- Submit dengan video demo + Permission Declaration
- Plan B: kalau ditolak, fokus ke side-load only

---

## 15. Safety & Emergency Bypass

**Kritis untuk hardcore mode.** Tanpa ini, app jadi anti-pattern dan berbahaya.

### Universal Safety Rules
1. **Selalu accessible:** tombol "Panggilan darurat" di setiap gate UI
2. **Gate cap per session:** Lv 5-6 auto-disable setelah 30 menit terus-menerus
3. **Reboot bypass:** setelah reboot HP, hardcore mode tidak auto-aktif lagi
4. **Uninstall always works:** app tidak boleh memblokir uninstall via Settings
5. **PIN bypass tersedia di setiap level 5-6 gate**

### Lupa PIN
- Tombol "Lupa PIN" → guide ke Settings > Apps > Tomo Sensei > Force Stop, lalu uninstall + reinstall
- Trade-off: kehilangan progress (kecuali backup ke cloud aktif)

### Health Monitoring
- Kalau gate di-fail >10x dalam 1 jam → app trigger "Are you ok?" screen dengan opsi cooldown 1 jam
- Counter di lokal, tidak dikirim manapun

---

## 16. MVP Scope

### MVP (target ~3 bulan dev post-JLPT)

**Core wajib ada:**
- ✅ Onboarding lengkap (Step 1-9)
- ✅ Gate engine (Trigger: unlock + idle + 1 app launch)
- ✅ Intensity level 1-4
- ✅ Core drill (manual mode di main app)
- ✅ Sensei chat (Gemma 4 E4B on-device)
- ✅ Foto Sensei (multi-modal)
- ✅ FSRS algorithm
- ✅ Stats dashboard
- ✅ Emergency PIN
- ✅ N5 deck (~800 vocab + 100 kanji + grammar dasar)

**Belum ada di MVP:**
- ❌ App switch trigger (V1.0)
- ❌ Audio companion (V1.0)
- ❌ Floating bubble (V1.0)
- ❌ Hardcore mode level 5-6 (V1.0 side-load)
- ❌ Backend sync (V1.0+)
- ❌ N4+ deck (gradual rollout)

### V1.0 (target ~6 bulan)
- Bubble, app switch trigger, multi-app gating
- Audio companion
- Hardcore mode (side-load only)
- N4 deck

### V1.5 (target ~9 bulan)
- Backend sync multi-device
- N3 deck
- Premium tier

---

## 17. Development Roadmap

> Solo dev, ~15 jam/minggu post-JLPT.

| Phase | Durasi | Deliverable |
|---|---|---|
| **Pre-build (now → JLPT)** | 8 mgg | Spec final, content (deck N5), wireframe, repo setup. **No app coding.** Fokus lulus N5. |
| Phase 1: Foundation | 4 mgg | Android project, Compose UI shell, Room DB, FSRS, deck loader |
| Phase 2: Core Drill | 2 mgg | Drill UI, swipe gestures, TTS, basic stats |
| Phase 3: Gate Engine | 4 mgg | Trigger monitor, cooldown, gate launcher, level 1-4 UI |
| Phase 4: AI Integration | 3 mgg | LiteRT-LM setup, Gemma 4 download flow, chat UI, photo flow |
| Phase 5: Onboarding & Polish | 3 mgg | Preset selector, permission funnel, PIN setup, settings UX |
| Phase 6: Closed Beta | 2 mgg | Side-load to 10-20 testers, feedback, fix |
| Phase 7: V1.0 Features | 8 mgg | Bubble, app switch, audio companion |
| Phase 8: Hardcore Mode | 4 mgg | Level 5-6, accessibility service, side-load distribution |

**Total ke MVP:** ~5-6 bulan post-JLPT.
**Total ke V1.0:** ~9-10 bulan post-JLPT.

---

## 18. Top Risks

| Risk | Probability | Impact | Mitigation |
|---|---|---|---|
| Gemma 4 inference lambat di device | Medium | High | Test early, batasi context, streaming UI |
| Battery drain dari gate engine | Medium | High | Foreground service hemat, batching events |
| User uninstall karena terlalu mengganggu | High | Medium | Onboarding guide expectation, intensity dial mudah turun |
| Burnout solo dev | High | High | Strict scope, ship MVP minimalis, build for self |
| Konten N5 kurang menarik | Medium | High | Curate bukan generate, contoh real |
| Gate engine break di OEM tertentu (Xiaomi, Samsung) | Medium | Medium | Test top 5 OEM, document workaround |
| LiteRT-LM API breaking change | Medium | High | Pin version, monitor release notes |
| User lost PIN, hilang progress | Low | Medium | Optional cloud backup, clear warning |

---

## 19. References

- **Google AI Edge Gallery (REFERENCE IMPLEMENTATION):** https://github.com/google-ai-edge/gallery
- LiteRT-LM docs: https://ai.google.dev/edge/litert
- Gemma models: https://ai.google.dev/gemma
- FSRS algorithm: https://github.com/open-spaced-repetition
- OneSec (friction precedent): https://one-sec.app
- Android AccessibilityService: https://developer.android.com/guide/topics/ui/accessibility/service
- Android Overlay (SYSTEM_ALERT_WINDOW): https://developer.android.com/reference/android/Manifest.permission#SYSTEM_ALERT_WINDOW

---

*v0.3 — On-device + multi-modal. Final lock. Ready for implementation.*

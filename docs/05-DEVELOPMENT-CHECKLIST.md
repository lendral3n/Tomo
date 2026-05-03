# Tomo Sensei — Development Checklist

Checklist phase-by-phase. Centang manual saat selesai, jangan loncat phase.

---

## Phase 0: Pre-build (sekarang → JLPT N5)

**Durasi:** 8 minggu | **Time budget:** ~3 jam/minggu untuk dev (sisanya belajar JLPT)

### Persiapan
- [ ] Lulus JLPT N5 (PRIORITAS #1)
- [ ] Beli/akses HP flagship dengan RAM 8GB+ untuk testing
- [ ] Setup Android Studio Hedgehog atau later
- [ ] Setup GitHub repo private
- [ ] Setup CI minimal (GitHub Actions untuk lint + test)
- [ ] Domain reservation (tomosensei.app atau preferred)
- [ ] Beli Play Console account ($25)

### Konten N5 (paralel)
- [ ] Source vocab N5 dari Anki Core 2K/6K (CC0)
- [ ] Validate dengan official JLPT N5 list
- [ ] Curate 800 vocab final
- [ ] Author/source contoh kalimat per kata
- [ ] Generate JSON deck format
- [ ] Manual review 100 kartu pertama untuk quality

### Riset teknis
- [ ] Clone & jalankan Google AI Edge Gallery di device kamu
- [ ] Test Gemma 4 E4B: latency, RAM, battery di device kamu
- [ ] Catat findings ke `docs/research-notes.md`
- [ ] Test multi-modal (foto teks Jepang) dengan Gemma 4
- [ ] Riset library FSRS untuk Kotlin/JVM

### Deliverables Phase 0
- [ ] JLPT N5 lulus 🎌
- [ ] Repo dengan struktur folder ready (kosong dulu)
- [ ] N5 deck JSON (~800 entries)
- [ ] Notes dari riset Gemma 4 di device

---

## Phase 1: Foundation (4 minggu)

### Setup project
- [ ] Buat Android project baru (Kotlin DSL, Compose)
- [ ] Setup `gradle/libs.versions.toml`
- [ ] Setup multi-module structure
- [ ] Setup Hilt DI
- [ ] Setup detekt + ktlint
- [ ] First commit, push to GitHub

### Core modules
- [ ] Module `core:common` (utils, extensions)
- [ ] Module `core:design-system`
  - [ ] Theme (light + dark)
  - [ ] Typography (Japanese fonts)
  - [ ] WashiCard component
  - [ ] HankoStamp component
  - [ ] JapaneseText component
- [ ] Module `core:data`
  - [ ] Room database
  - [ ] All entities (Card, Progress, TriggerConfig, dll)
  - [ ] All DAOs
  - [ ] Repositories
  - [ ] SQLCipher integration
  - [ ] Migration strategy
- [ ] Module `core:srs`
  - [ ] FSRS algorithm
  - [ ] Unit tests
  - [ ] Edge cases (lapse, new card, mature)
- [ ] Module `core:content`
  - [ ] Deck JSON loader
  - [ ] N5 deck integration
  - [ ] Initial population on first launch

### Smoke test
- [ ] App launches without crash
- [ ] Database initialized
- [ ] N5 cards loaded into DB
- [ ] Theme works in light + dark mode

---

## Phase 2: Core Drill (2 minggu)

### Module `feature:drill`
- [ ] DrillScreen Compose
- [ ] Vertical card stack
- [ ] Tap to flip animation
- [ ] Swipe up = "tau" gesture
- [ ] Swipe down = "lupa" gesture
- [ ] DrillViewModel
- [ ] Connect FSRS algorithm
- [ ] Update Room on review
- [ ] TTS integration (Android native, ja-JP)
- [ ] Empty state ("semua selesai")

### Module `feature:stats`
- [ ] StatsScreen sederhana
- [ ] Streak counter
- [ ] Today count
- [ ] Mastery progress bar
- [ ] Reset all data button (untuk dev/testing)

### App shell
- [ ] MainActivity dengan NavHost
- [ ] Bottom nav (Drill | Stats)
- [ ] Splash screen

### Smoke test
- [ ] Buka app → langsung ke DrillScreen
- [ ] Swipe semua kartu, progress tersimpan
- [ ] Restart app, progress masih ada
- [ ] Esok hari (atau ubah waktu device), kartu muncul lagi sesuai FSRS schedule

---

## Phase 3: Gate Engine (4 minggu)

### Foreground service
- [ ] `GateForegroundService` dengan foregroundServiceType="specialUse"
- [ ] Notification channel "Tomo Sensei active"
- [ ] Auto-restart kalau dimatikan OS

### Trigger monitors
- [ ] `UnlockReceiver` (ACTION_USER_PRESENT)
- [ ] `BootReceiver` (auto-start setelah reboot)
- [ ] `AppLaunchMonitor` (UsageStatsManager polling)
- [ ] `IdleTimerMonitor`

### Gate Engine core
- [ ] `GateEngine` class
- [ ] `CooldownTracker`
- [ ] `DailyCapChecker`
- [ ] `GateLauncher`
- [ ] `GateLogger` (Room insert)

### Gate UI Overlay
- [ ] `GateOverlayService` dengan SYSTEM_ALERT_WINDOW
- [ ] Compose di overlay (dengan ViewTreeLifecycleOwner setup)
- [ ] WhisperGate (Level 1)
- [ ] TapToPassGate (Level 2) dengan countdown
- [ ] AnswerToPassGate (Level 3) dengan tau/lupa buttons
- [ ] CorrectToPassGate (Level 4) dengan multiple choice + retry

### Permission flows
- [ ] Request POST_NOTIFICATIONS
- [ ] Request SYSTEM_ALERT_WINDOW (guide ke Settings)
- [ ] Request PACKAGE_USAGE_STATS (guide ke Settings)
- [ ] Battery optimization whitelist (guide)

### Module `feature:gate-config`
- [ ] Per-trigger configurator UI
- [ ] Slider 0-6 per trigger
- [ ] Preset selector (Casual/Serius/Hardcore)
- [ ] Save to Room

### Smoke test pada device asli
- [ ] Unlock HP → Gate muncul
- [ ] Buka TikTok → Gate muncul
- [ ] Setiap 20 menit screen-on → Gate muncul (idle)
- [ ] Cooldown bekerja (tidak spam)
- [ ] Daily cap bekerja
- [ ] Reboot HP → service auto-restart
- [ ] Tidak crash di Xiaomi/Samsung/Oppo

---

## Phase 4: AI Integration (3 minggu)

### Pre-coding research
- [ ] Re-read Google AI Edge Gallery code
- [ ] Test LiteRT-LM init time di device kamu
- [ ] Benchmark Gemma 4 E4B inference (token/s)

### Module `core:ai`
- [ ] `GemmaInferenceManager` (singleton)
- [ ] Model lifecycle (load on demand, release on background)
- [ ] `ModelDownloader` dengan progress callback
- [ ] Wi-Fi only enforcement
- [ ] Checksum verification
- [ ] `PromptBuilder` dengan templates
- [ ] Streaming response support
- [ ] Multi-modal input (Bitmap → Image)
- [ ] Error handling (OOM, corrupt model, timeout)

### Module `feature:chat`
- [ ] ChatScreen Compose
- [ ] Message bubbles (user vs sensei)
- [ ] Streaming text animation
- [ ] Quick prompts (level-based)
- [ ] ChatViewModel
- [ ] Message history persistence
- [ ] Loading & error states

### Module `feature:photo`
- [ ] CameraX preview
- [ ] Image capture button
- [ ] Pilih dari galeri (alternative)
- [ ] Send to Gemma 4 multi-modal
- [ ] Parse JSON response
- [ ] Display: text + reading + translation + grammar
- [ ] Save vocab highlights to personal deck

### Smoke test
- [ ] Download Gemma 4 berhasil (3.6GB)
- [ ] Sensei chat respon dalam <5 detik untuk pertanyaan pendek
- [ ] Sensei chat tidak halusinasi grammar dasar N5
- [ ] Foto menu Jepang → translate akurat
- [ ] Tidak crash setelah 10x inference berturut-turut
- [ ] Battery drain reasonable (<5% per 10 menit chat)

---

## Phase 5: Onboarding & Polish (3 minggu)

### Module `feature:onboarding`
- [ ] WelcomeScreen
- [ ] CommitmentScreen
- [ ] DeviceCheckScreen (RAM, Android version, storage)
- [ ] PresetScreen (Casual/Serius/Hardcore + demo)
- [ ] PermissionFunnelScreen (one by one)
- [ ] PinSetupScreen (4 digit + confirm)
- [ ] GatedAppsScreen (list installed apps)
- [ ] DailyGoalScreen
- [ ] ModelDownloadScreen (Wi-Fi only, progress)
- [ ] SimulationScreen (3 sample gates)
- [ ] DoneScreen

### Module `feature:settings`
- [ ] SettingsScreen
- [ ] Toggle individual features
- [ ] Reset progress
- [ ] About / version info
- [ ] Privacy policy link
- [ ] Export data (JSON)

### Module `feature:emergency`
- [ ] PIN entry screen
- [ ] PIN reset guide
- [ ] Emergency call button (intent ACTION_DIAL)

### Module `feature:gate-config`
- [ ] Detail config per trigger
- [ ] Custom intensity per app
- [ ] Pause schedule (jam kerja)

### Polish pass
- [ ] Loading states everywhere
- [ ] Error states everywhere
- [ ] Empty states everywhere
- [ ] Accessibility content descriptions
- [ ] Haptic feedback
- [ ] Sound effects (optional, opt-in)
- [ ] Animation polish

### Smoke test
- [ ] First launch → onboarding flow lancar tanpa skip
- [ ] Lupa PIN → guide jelas
- [ ] Restart app post-onboarding → langsung ke main
- [ ] All settings persist after restart

---

## Phase 6: Closed Beta (2 minggu)

### Pre-beta
- [ ] Sign release APK dengan keystore
- [ ] Generate signed APK
- [ ] Setup Sentry (atau crash reporting alternative)
- [ ] Setup feedback channel (Discord / Google Form)

### Distribute
- [ ] Identify 10-20 beta testers
- [ ] Send APK + onboarding instruction
- [ ] Collect feedback secara struktur
- [ ] Triaging issues

### Iteration
- [ ] Top 5 critical bugs fixed
- [ ] Top 3 UX issues addressed
- [ ] Update docs based on learnings

---

## Phase 7: V1.0 Features (8 minggu)

- [ ] FloatingBubbleSensei service
- [ ] AppSwitchTrigger implementation
- [ ] Multi-app gating UI
- [ ] AudioCompanionService
- [ ] N4 deck integration
- [ ] Refined onboarding based on Phase 6 feedback

---

## Phase 8: Hardcore Mode (4 minggu)

- [ ] AccessibilityService implementation
- [ ] TimeoutGate (Level 5)
- [ ] HardcoreLockGate (Level 6)
- [ ] Side-load APK build pipeline
- [ ] Own website untuk distribution
- [ ] Disclaimer & warning halaman

---

## Phase 9: Play Store submission (saat ready)

- [ ] Play Console listing setup
- [ ] Screenshots (phone + tablet)
- [ ] Feature graphic
- [ ] Demo video
- [ ] Permission Declaration form
- [ ] Privacy policy URL
- [ ] Submit untuk review
- [ ] Address review feedback

---

## Self-care reminders

- [ ] Setiap phase, take 1 day rest sebelum lanjut
- [ ] Kalau stuck >2 hari di satu masalah, buka issue di GitHub dan move on
- [ ] Backup database & code regularly (offline juga)
- [ ] Documentation update di setiap commit penting
- [ ] Test di device asli, JANGAN cuma emulator

---

## Definition of Done (per task)

- [ ] Code compiled
- [ ] Unit test (kalau applicable) pass
- [ ] Manual test di device asli pass
- [ ] No new lint errors
- [ ] Committed dengan conventional commit message
- [ ] Documentation updated

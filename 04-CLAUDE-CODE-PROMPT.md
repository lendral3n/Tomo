# Prompt Starter untuk Claude Code

Copy-paste prompt di bawah ke Claude Code sebagai pesan pertama. Sesuaikan bagian `[KAMU PILIH]` dengan kondisi sekarang.

---

## Prompt 1: Initial setup (jalankan PERTAMA KALI)

```
Aku mau bangun app Android bernama Tomo Sensei. Sebelum kamu mulai apapun, baca dulu semua file dokumentasi berikut secara berurutan:

1. docs/00-README.md (overview)
2. docs/01-SPEC.md (technical spec lengkap)
3. docs/02-IMPLEMENTATION-GUIDE.md (panduan implementasi)
4. docs/03-PROJECT-STRUCTURE.md (struktur project)
5. docs/05-DEVELOPMENT-CHECKLIST.md (urutan kerja)
6. docs/06-CONTENT-STRATEGY.md (strategi konten)

Setelah baca, kasih aku ringkasan singkat dalam bahasa Indonesia tentang:
- Apa yang akan kita bangun
- Tech stack yang dipilih
- Phase mana yang harus kita mulai dulu
- Apa pertanyaan yang masih kamu butuhkan dari aku sebelum mulai

JANGAN langsung coding atau setup project. Kita diskusi dulu untuk align.
```

---

## Prompt 2: Setelah align (mulai Phase 1)

```
Oke, kita mulai Phase 1: Foundation.

Tujuan Phase 1: setup Android project skeleton dengan Compose, Hilt, Room, dan FSRS algorithm working. Belum ada UI utama, baru fondasinya.

Tugas konkrit Phase 1 (urutan kerja):

1. Buat Android project baru dengan:
   - Package: com.tomosensei.app
   - Min SDK: 31, Target SDK: 34
   - Kotlin DSL build.gradle
   - Jetpack Compose
   - Multi-module structure sesuai docs/03-PROJECT-STRUCTURE.md

2. Setup gradle/libs.versions.toml dengan dependencies dari docs/02-IMPLEMENTATION-GUIDE.md

3. Buat Application class dengan @HiltAndroidApp

4. Buat module core:design-system dengan:
   - Theme (Compose) menggunakan color tokens dari spec
   - Typography (Shippori Mincho, Zen Kaku Gothic, Manrope)
   - WashiCard component
   - HankoStamp component

5. Buat module core:data dengan:
   - Room database dengan SEMUA entities dari spec §7
   - DAOs untuk setiap entity
   - Repository pattern
   - SQLCipher integration untuk encryption

6. Buat module core:srs dengan:
   - FsrsScheduler implementation (FSRS v4)
   - Unit test untuk verify algorithm

7. Buat module core:content:
   - Deck loader (read JSON dari assets)
   - Initial deck N5 dummy (5 cards saja untuk testing, real content nanti)

Constraint:
- Setiap step, jelaskan apa yang kamu lakukan dan kenapa
- Sebelum lanjut ke step berikutnya, tunggu konfirmasi aku
- Test yang sudah dibuat harus pass sebelum lanjut
- Pakai conventional commits (feat:, fix:, chore:, etc)

Mulai dari step 1.
```

---

## Prompt 3: Phase 2 (Core Drill)

```
Phase 1 done. Sekarang Phase 2: Core Drill.

Tujuan: bisa swipe kartu, jawab tau/lupa, progress tersimpan di Room, FSRS schedule berjalan.

Reference UI: lihat prototype di artifact yang aku punya (akan aku paste kalau perlu). Spirit: minimal, washi paper aesthetic, hanko stamp accent.

Tugas:
1. Buat module feature:drill
2. DrillScreen Compose dengan vertical swipe gesture
3. Card flip on tap (front: kanji, back: reading + meaning)
4. Swipe up = "tau", swipe down = "lupa"
5. TTS button (Android native, locale ja-JP)
6. Connect ke FSRS via DrillViewModel
7. Stats sederhana: streak counter, today count

Deliverable: aku bisa install ke device, swipe kartu, progress tersimpan, restart app dan progress masih ada.

Mulai.
```

---

## Prompt 4: Phase 3 (Gate Engine — paling kompleks)

```
Phase 2 done. Sekarang Phase 3: Gate Engine.

INI PALING KOMPLEKS. Banyak Android system integration. Hati-hati.

Tujuan: ketika user unlock HP atau buka TikTok, gate overlay muncul, user harus jawab kartu untuk dismiss.

Tugas:
1. Foreground service dengan foregroundServiceType="specialUse"
2. UnlockReceiver untuk ACTION_USER_PRESENT
3. AppLaunchMonitor pakai UsageStatsManager polling 1 detik
4. IdleTimerMonitor (foreground service ticker)
5. GateEngine class dengan logic: cooldown, daily cap, intensity selection
6. GateOverlayService: SYSTEM_ALERT_WINDOW + Compose
   - PENTING: setup ViewTreeLifecycleOwner manual untuk Compose di overlay
7. Implementasi level 1-4 sebagai Composable terpisah:
   - WhisperGate, TapToPassGate, AnswerToPassGate, CorrectToPassGate

Constraint:
- Permission flow harus mulus (guide user ke Settings untuk overlay & usage stats)
- Battery optimization whitelist guide
- Test di device real (bukan emulator) — beberapa OEM aggressive
- Logging untuk debugging (Sentry breadcrumbs)

Mulai.
```

---

## Prompt 5: Phase 4 (AI Integration)

```
Phase 3 done. Sekarang Phase 4: AI Integration dengan Gemma 4 E4B.

PRE-REQUISITE: kamu harus sudah baca code dari Google AI Edge Gallery (https://github.com/google-ai-edge/gallery). Kalau belum, fetch dan analisis dulu sebelum mulai.

Tujuan: Sensei chat dengan Gemma 4 on-device, plus Foto Sensei (multi-modal).

Tugas:
1. Module core:ai dengan:
   - GemmaInferenceManager (singleton via Hilt)
   - ModelDownloader (3.6GB, Wi-Fi only, dengan progress)
   - PromptBuilder dengan templates dari spec §8.2
2. Module feature:chat:
   - ChatScreen Compose dengan streaming response
   - Message history persistence (Room ChatMessage entity)
   - Quick prompts based on user level
3. Module feature:photo:
   - CameraX integration
   - Photo capture → Gemma 4 multi-modal input
   - Display: extracted text + reading + translation + grammar
   - Save vocab to personal deck

Constraint:
- Inference timeout 30s
- Release LlmInferenceSession setelah setiap inference
- Graceful degradation kalau model gagal load
- Memory monitoring (Gemma 4 E4B = ~5GB RAM saat inference)

Mulai dengan module core:ai.
```

---

## Prompt 6: Phase 5 (Onboarding & Polish)

```
Phase 4 done. Phase 5: Onboarding & Polish.

Tujuan: user pertama kali install bisa setup app dengan smooth, paham apa yang bakal terjadi.

Tugas:
1. Module feature:onboarding dengan flow:
   - Welcome
   - Commitment ("App ini akan ganggu kamu — fiturnya")
   - Device check (RAM, Android version)
   - Preset selector (Casual/Serius/Hardcore)
   - Permission funnel (one by one dengan justifikasi)
   - PIN setup (4 digit, hashed)
   - Gated apps picker (kalau preset includes app launch)
   - Daily goal slider
   - Model download (3.6GB, progress bar, Wi-Fi only)
   - 3 simulasi gate (test run)
   - Done screen
2. Settings screen dengan per-trigger configurator (slider 0-6)
3. Error states yang bagus
4. Empty states yang bagus
5. Loading states yang bagus

Mulai.
```

---

## Tips umum saat bekerja dengan Claude Code di project ini

1. **Selalu reference spec.** Kalau Claude Code menyimpang dari spec, tunjukkan section spec yang relevan.

2. **One phase at a time.** Jangan biarkan dia loncat-loncat phase. Selesaikan dan test sebelum lanjut.

3. **Test di device asli sering.** Banyak fitur (overlay, foreground service, accessibility) tidak bekerja di emulator atau bekerja beda dari device asli.

4. **Commit sering.** Conventional commits per logical unit. Mudah revert kalau ada masalah.

5. **Jangan biarkan dia meng-skip safety features.** Emergency PIN, panggilan darurat, auto-disable hardcore — wajib selalu ada.

6. **Verify Gemma 4 dependency versions.** LiteRT-LM masih relatif baru, version mungkin berubah cepat.

7. **Kalau stuck di system integration, reference Google AI Edge Gallery code.** Banyak pattern yang bisa di-copy.

---

## Format follow-up untuk Claude Code

```
Status update Phase X:
- [x] Done: [list]
- [ ] In progress: [list]
- [ ] Blocked: [reason]
- Issues: [list bugs/questions]

Next: [what to do next]
```

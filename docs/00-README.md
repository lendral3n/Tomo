# Tomo Sensei — Project Documentation

**Status:** Pre-development. Spec v0.3 final.
**Author:** [Your Name]
**Last updated:** 2026-05-03

---

## Daftar dokumen

| File | Tujuan | Audience |
|---|---|---|
| `01-SPEC.md` | Technical specification lengkap | Developer (kamu, Claude Code) |
| `02-IMPLEMENTATION-GUIDE.md` | Panduan implementasi step-by-step Android Kotlin | Claude Code |
| `03-PROJECT-STRUCTURE.md` | Struktur folder & module Android | Claude Code |
| `04-CLAUDE-CODE-PROMPT.md` | Prompt starter untuk Claude Code | Kamu (copy-paste ke Claude Code) |
| `05-DEVELOPMENT-CHECKLIST.md` | Checklist phase-by-phase | Kamu (tracking progress) |
| `06-CONTENT-STRATEGY.md` | Cara author/curate deck N5 | Kamu (paralel saat dev) |

---

## Cara pakai paket ini di Claude Code

1. Buat folder project baru: `mkdir tomo-sensei && cd tomo-sensei`
2. Copy semua file ini ke folder tersebut
3. Buka Claude Code di folder tersebut
4. Copy-paste isi `04-CLAUDE-CODE-PROMPT.md` sebagai prompt pertama
5. Claude Code akan membaca semua dokumen dan mulai bekerja

---

## Lock keputusan final (v0.3)

- **Bahasa:** Kotlin native + Jetpack Compose
- **AI:** Gemma 4 E4B-it (3.6GB) via LiteRT-LM, on-device only
- **Context:** 32K tokens, multi-modal (text + image)
- **Min device:** RAM 8GB+, Android 12+, Snapdragon 8 series / setara
- **Architecture:** Gate-first (gate engine sebagai pusat, main app sekunder)
- **Distribution:** Self-use first, side-load APK untuk kohort, Play Store nanti
- **Build philosophy:** Build for one (kamu), validate, baru scale

---

## Phase 0 priorities (sebelum coding)

1. Lulus JLPT N5 (target utama 2 bulan)
2. Setup Android Studio + project skeleton
3. Curate deck N5 (paralel, ~3 jam/minggu)
4. Test Google AI Edge Gallery untuk paham LiteRT-LM integration

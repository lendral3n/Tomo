# Tomo Sensei — Content Strategy

Strategi membuat dan curate konten N5. **Bisa dikerjakan paralel dengan belajar JLPT N5** — ini lebih dari sekedar admin work, ini juga belajar.

---

## 1. Sumber Konten

### Vocab N5

**Source primer:**
- **Anki Core 2K/6K** (CC0 license) — https://github.com/Kuuuube/japanese-immersion-anki-decks
- **JLPT N5 Official Word List** (publik, banyak mirror)
- **Tango N5** (commercial, tapi mirror banyak di Quizlet)

**Validation:**
- Cross-check dengan setidaknya 2 source berbeda
- Filter yang **tidak** masuk N5 (anime words, slang)
- Target: 800 vocab (di atas standar 700)

### Kanji N5

**Source primer:**
- **WaniKani Level 1-5** (paid, tapi list public)
- **Kanji Damage** (free, irreverent style)
- **Remembering the Kanji** Heisig (paid, list public)

Target: 100 kanji yang cocok N5 (resmi: ~80, kita beri buffer).

### Grammar N5

**Source primer:**
- **Genki I textbook** (commercial, content widely referenced)
- **Tae Kim's Guide** (CC license, free) — http://guidetojapanese.org
- **Bunpro N5 list** (paid, list public)

Target: 50 grammar pattern N5.

---

## 2. Format Setiap Kartu

### Vocab card

```json
{
  "id": "n5_v_001",
  "type": "vocab",
  "level": "N5",
  "front": "食べる",
  "reading": "たべる",
  "meaning": "makan",
  "examples": [
    {
      "jp": "私はりんごを食べます。",
      "reading": "わたしはりんごをたべます。",
      "id": "Saya makan apel."
    },
    {
      "jp": "何を食べたいですか？",
      "reading": "なにをたべたいですか？",
      "id": "Mau makan apa?"
    }
  ],
  "tags": ["verb", "ichidan", "daily", "food"],
  "mnemonic": null,
  "audio_url": null
}
```

### Kanji card

```json
{
  "id": "n5_k_001",
  "type": "kanji",
  "level": "N5",
  "front": "食",
  "reading": "ショク・ジキ・た(べる)・く(う)",
  "meaning": "makan, makanan",
  "components": ["人", "良"],
  "examples": [
    {"jp": "食べる", "reading": "たべる", "id": "makan"},
    {"jp": "食事", "reading": "しょくじ", "id": "makanan/jamuan"},
    {"jp": "朝食", "reading": "ちょうしょく", "id": "sarapan"}
  ],
  "tags": ["food", "daily", "common"],
  "stroke_count": 9,
  "mnemonic": "Orang (人) yang baik (良) butuh makan (食)"
}
```

### Grammar card

```json
{
  "id": "n5_g_001",
  "type": "grammar",
  "level": "N5",
  "front": "〜は〜です",
  "reading": null,
  "meaning": "X adalah Y (basic copula)",
  "explanation": "Pola dasar untuk menyatakan A adalah B. は (wa) menunjukkan topik, です (desu) adalah kata kerja kopula sopan.",
  "examples": [
    {"jp": "私は学生です。", "reading": "わたしはがくせいです。", "id": "Saya adalah pelajar."},
    {"jp": "これは本です。", "reading": "これはほんです。", "id": "Ini adalah buku."}
  ],
  "tags": ["copula", "basic", "structure"]
}
```

---

## 3. Workflow Curation (~3 jam/minggu)

### Week 1-2: Bulk import
- Download Anki Core deck
- Convert ke JSON dengan script Python (akan kita buat)
- Filter level N5 only
- ~800 entries raw

### Week 3-4: Quality review
- Review 100 kartu/hari (target 800 selesai dalam ~8 hari)
- Per kartu, cek:
  - Reading akurat?
  - Arti Indonesia natural? (bukan terjemahan kaku)
  - 2 contoh kalimat realistic?
  - Tags benar?

### Week 5-6: Add example sentences
- Banyak vocab dari Anki core tidak punya contoh kalimat bagus
- Source dari:
  - Tatoeba (https://tatoeba.org) — sentences with translations
  - Native speaker channel (Reddit r/translator atau HiNative)
  - Generate dengan AI (verify dengan native, jangan trust 100%)

### Week 7-8: Audio (optional MVP)
- Forvo (https://forvo.com) untuk pronunciation per word (CC license check needed)
- Atau pakai TTS Android native saja (cukup decent untuk N5)
- Bundle audio nanti, tidak wajib di MVP

---

## 4. AI-Assisted Curation

Pakai Claude/Gemini untuk speed up, BUKAN replace human review.

### Prompt untuk generate contoh kalimat
```
Saya butuh 3 contoh kalimat Jepang menggunakan kata "{WORD}" ({READING}) yang berarti "{MEANING}".

Constraint:
- Level: JLPT N5 (gunakan grammar dan vocab N5 saja)
- Konteks: kehidupan sehari-hari natural
- Variasi: 1 kalimat positif, 1 negatif, 1 pertanyaan
- Format JSON:
[
  {"jp": "...", "reading": "...", "id": "..."},
  ...
]

Tulis dalam JSON saja, tanpa preamble.
```

### Prompt untuk validate
```
Saya punya kartu vocab N5 berikut:
{CARD_JSON}

Tolong cek:
1. Apakah reading benar?
2. Apakah arti Indonesia natural dan akurat?
3. Apakah contoh kalimat menggunakan grammar N5 saja (tidak ada grammar N4+)?
4. Apakah tags relevan?

Jawab dengan: PASS atau FAIL [reason]. Jangan bertele-tele.
```

### Verify dengan native speaker
Untuk kartu yang ragu (terutama nuance arti, contoh kalimat), tanya:
- HiNative
- Reddit r/translator
- Jepang native speaker friend (kalau ada)
- Italki tutor (paid, $5-10 per session)

---

## 5. Anti-Pattern: yang HARUS dihindari

### ❌ Direct translation tanpa context
**Bad:** 食べる = "eat" (terlalu kering)
**Good:** 食べる = "makan" + contoh kalimat dimana kata ini dipakai

### ❌ Romaji di front
**Bad:** Front: "tabemono", reading: "たべもの"
**Good:** Front: "食べ物", reading: "たべもの" (selalu Japanese script)

### ❌ Contoh terlalu kompleks
**Bad:** "このレストランは混んでいるけれども、料理がとても美味しいので、よく食べに行きます。" (terlalu N3 grammar)
**Good:** "このレストランで食べました。" (simple, N5)

### ❌ Overly literal Indonesian
**Bad:** 行ってきます = "Saya akan pergi dan kembali"
**Good:** 行ってきます = "Aku berangkat dulu" (atau "Pergi dulu, ya!")

### ❌ Mnemonic yang nggak nyambung
Mnemonic harus benar-benar membantu hafal, bukan dipaksakan. Lebih baik kosong daripada bad mnemonic.

---

## 6. Quality Bar

Sebelum kartu masuk deck final:
- [ ] Reading verified dari minimum 2 source
- [ ] Arti natural dalam bahasa Indonesia
- [ ] Minimum 1 contoh kalimat (idealnya 2-3)
- [ ] Contoh kalimat menggunakan grammar level user atau lebih rendah
- [ ] Tags konsisten dengan taxonomy
- [ ] Tested di device sendiri (kamu pakai sendiri, harus terasa enak)

---

## 7. Tag Taxonomy

### Part of speech
- `noun`, `verb`, `adjective`, `adverb`, `particle`, `conjunction`, `interjection`, `pronoun`

### Verb subtype
- `ichidan` (る verbs), `godan` (う verbs), `irregular`

### Adjective subtype
- `i-adj` (い adjectives), `na-adj` (な adjectives)

### Theme
- `food`, `family`, `travel`, `time`, `weather`, `body`, `daily`, `school`, `work`, `nature`, `numbers`

### Frequency
- `top-100`, `top-500`, `top-1000` (untuk prioritas review)

### Difficulty
- `easy`, `medium`, `hard` (subjective, untuk daily mix balance)

---

## 8. Deck Versioning

```json
{
  "version": "1.0.0",
  "level": "N5",
  "language": "ja",
  "interface_lang": "id",
  "card_count": 800,
  "checksum": "sha256:...",
  "created_at": "2026-04-01",
  "updated_at": "2026-05-15",
  "cards": [...]
}
```

Deck updates di-handle oleh app:
- Check version on app launch (kalau backend aktif)
- Download diff (atau full deck)
- Migrate progress mapping by ID

---

## 9. Backup Plan: Kalau Curation Lambat

Kalau JLPT mendekat dan deck belum siap:
- **MVP minimum: 200 kartu** (top vocab N5 + 30 kanji + 20 grammar)
- Cukup untuk validate gate engine + AI
- Sisanya tambah post-MVP

Jangan tunda development karena content. Content bisa di-update via app update terpisah.

---

## 10. Long-term Content Roadmap

| Phase | Content Goal |
|---|---|
| MVP | N5 (800 vocab + 100 kanji + 50 grammar) |
| V1.0 | + N4 (1500 vocab + 300 kanji + 80 grammar) |
| V1.5 | + N3 (3700 vocab + 650 kanji + 120 grammar) |
| V2.0 | + N2 (6000 vocab + 1000 kanji + 150 grammar) |
| V2.5+ | + N1 + custom decks |

N5 → N4 → N3 jadi paid tier eventual (premium subscription).
N2 + N1 untuk power users, dengan harga lebih tinggi.

---

## 11. Tools

### Conversion script (Python)

Akan kita buat di Phase 0:

```python
# scripts/convert_anki_to_json.py
# Convert Anki .apkg deck to Tomo Sensei JSON format

import sqlite3
import zipfile
import json

def convert_anki(apkg_path, output_path):
    # Extract .apkg (zip)
    # Read collection.anki2 (sqlite)
    # Map fields ke schema
    # Output JSON
    pass
```

### Validation script

```python
# scripts/validate_deck.py
# Validate deck JSON structure + content rules

import json
from pathlib import Path

def validate(deck_path):
    deck = json.loads(Path(deck_path).read_text())
    issues = []
    for card in deck['cards']:
        if not card.get('front'): issues.append(f"Missing front: {card['id']}")
        if not card.get('reading'): issues.append(f"Missing reading: {card['id']}")
        # ... more checks
    return issues
```

---

*Konten adalah moat product ini. Spend lebih banyak waktu di sini daripada yang kamu kira nyaman.*

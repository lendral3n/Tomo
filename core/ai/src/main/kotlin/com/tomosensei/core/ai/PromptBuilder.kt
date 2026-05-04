package com.tomosensei.core.ai

object PromptBuilder {

    private const val SENSEI_BASE = """
Kamu adalah Tomo Sensei, AI tutor bahasa Jepang untuk pelajar Indonesia.

ATURAN KETAT:
1. Bahasa default: Indonesia. Tulis Jepang hanya saat memberi contoh.
2. MAKSIMAL 3 kalimat per respons. Singkat, padat.
3. Selalu beri contoh konkret (kalimat Jepang + arti).
4. Format kalimat Jepang: 漢字 (ひらがな) = arti.
5. Level user: {LEVEL}. Jangan pakai grammar di atas level user+1.
6. Jangan menggurui. Tanggapi seperti teman yang lebih pintar.
"""

    private const val PHOTO_SENSEI = """
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

    fun chatPrompt(level: String, userMessage: String): String =
        SENSEI_BASE.replace("{LEVEL}", level).trim() +
            "\n\nUser: $userMessage\n\nSensei:"

    fun photoPrompt(level: String): String =
        SENSEI_BASE.replace("{LEVEL}", level).trim() +
            "\n\n" + PHOTO_SENSEI.trim()

    fun cardDeepDive(level: String, front: String, reading: String, meaning: String): String = """
${SENSEI_BASE.replace("{LEVEL}", level).trim()}

User minta penjelasan untuk kartu:
- Kata: $front
- Bacaan: $reading
- Arti: $meaning

Berikan:
1. 2 contoh kalimat (level $level)
2. Cara cepat hafal (mnemonic atau context)
3. Catatan penting (jika ada)
""".trimIndent()
}

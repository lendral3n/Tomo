
// ─── Tomo Sensei — Extra Screens (Settings, Stats, States, Notif) ───────────

const Te = window.TomoTokens;
const {
  WashiBg, HankoStamp, Overline, SectionHeader, SenseiButton,
  VerticalAccent, BottomNav,
} = window;

// ─── SETTINGS ───────────────────────────────────────────────────────────────
function SettingsScreen() {
  const Row = ({ label, value, sub, danger }) => (
    <div style={{
      display: 'flex', justifyContent: 'space-between', alignItems: 'center',
      padding: '14px 0', borderBottom: `1px solid ${Te.sumiLight}1a`,
    }}>
      <div>
        <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, fontWeight: 500, color: danger ? Te.hankoRed : Te.sumiBlack }}>{label}</div>
        {sub && <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: Te.sumiLight, marginTop: 2 }}>{sub}</div>}
      </div>
      {value && <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 12, color: Te.sumiMid }}>{value}</span>}
    </div>
  );
  return (
    <div className="tomo-screen" style={{ position: 'relative', overflowY: 'auto' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1 }}>
        <div style={{ padding: '52px 24px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 18, color: Te.sumiMid, cursor: 'pointer' }}>←</span>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 16, color: Te.sumiBlack }}>Settings</span>
          <span style={{ width: 18 }} />
        </div>
        <div style={{ padding: '0 24px 100px' }}>
          <div style={{ marginTop: 20 }}>
            <SectionHeader label="Preset & Trigger" />
            <Row label="Preset" value="SERIUS →" sub="Real friction mode" />
            <Row label="Unlock HP" value="Lv 3 →" />
            <Row label="TikTok" value="Lv 4 →" />
            <Row label="Instagram" value="Lv 4 →" />
            <Row label="App switch" value="Lv 2 →" />
            <Row label="Idle 30 min" value="Lv 1 →" />
          </div>
          <div style={{ marginTop: 28 }}>
            <SectionHeader label="Daily Goal" />
            <Row label="Target kartu / hari" value="20 →" />
            <Row label="JLPT level" value="N5 →" />
            <Row label="Reminder time" value="08:00 →" />
          </div>
          <div style={{ marginTop: 28 }}>
            <SectionHeader label="Privacy & Account" />
            <Row label="Emergency PIN" value="Ubah →" sub="Backup code tersedia" />
            <Row label="Sensei AI model" value="On-device" sub="3.6 GB · offline" />
            <Row label="Export data" value="→" sub="JSON / Anki deck" />
          </div>
          <div style={{ marginTop: 28 }}>
            <SectionHeader label="Danger Zone" />
            <Row label="Reset progress" sub="Hapus streak & history" danger />
            <Row label="Uninstall app" sub="Butuh PIN konfirmasi" danger />
          </div>
        </div>
      </div>
      <BottomNav active="settings" />
    </div>
  );
}

// ─── STATS / PROGRESS ───────────────────────────────────────────────────────
function StatsScreen() {
  const heatmap = Array.from({ length: 7 * 8 }, (_, i) => Math.random());
  return (
    <div className="tomo-screen" style={{ position: 'relative', overflowY: 'auto' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1 }}>
        <div style={{ padding: '52px 24px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 16, color: Te.sumiBlack }}>Progress</span>
          <span style={{ fontSize: 16, color: Te.sumiLight, cursor: 'pointer' }}>⋯</span>
        </div>

        <div style={{ padding: '0 24px 100px' }}>
          {/* Hero streak */}
          <div className="tomo-card-base tomo-grain" style={{ padding: 24, marginTop: 12, marginBottom: 20, position: 'relative' }}>
            <Overline color={Te.sumiLight}>Streak</Overline>
            <div style={{ display: 'flex', alignItems: 'baseline', gap: 8, marginTop: 8 }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 64, color: Te.sumiBlack, lineHeight: 1, letterSpacing: '-0.04em' }}>47</span>
              <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Te.sumiMid }}>hari</span>
              <span style={{ marginLeft: 'auto', fontSize: 32 }}>🔥</span>
            </div>
            <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 13, color: Te.sumiMid, margin: '12px 0 0' }}>
              "Catatan terbaikmu: 52 hari"
            </p>
          </div>

          {/* Stats grid */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 12, marginBottom: 20 }}>
            {[
              ['Total kartu', '1,247', 'dipelajari'],
              ['Akurasi', '87%', 'rata-rata'],
              ['Gate dilewati', '3,412', '× di-trigger'],
              ['Waktu fokus', '34h', 'bulan ini'],
            ].map(([l, v, s]) => (
              <div key={l} className="tomo-card-base tomo-grain" style={{ padding: 16 }}>
                <Overline color={Te.sumiLight}>{l}</Overline>
                <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 26, color: Te.sumiBlack, margin: '6px 0 2px' }}>{v}</div>
                <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: Te.sumiLight }}>{s}</div>
              </div>
            ))}
          </div>

          {/* Heatmap */}
          <SectionHeader label="8 Minggu Terakhir" />
          <div className="tomo-card-base tomo-grain" style={{ padding: 18, marginTop: 12, marginBottom: 20 }}>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(8, 1fr)', gap: 4 }}>
              {heatmap.map((v, i) => (
                <div key={i} style={{
                  aspectRatio: 1, borderRadius: 3,
                  background: v < 0.2 ? `${Te.sumiLight}22` : v < 0.5 ? `${Te.hankoRed}55` : v < 0.8 ? `${Te.hankoRed}aa` : Te.hankoRed,
                }} />
              ))}
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: Te.sumiLight, marginTop: 10, letterSpacing: '0.06em' }}>
              <span>SEP</span><span>OKT</span>
            </div>
          </div>

          {/* Weak words */}
          <SectionHeader label="Vocab Sering Lupa" />
          <div style={{ marginTop: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[
              ['難しい', 'むずしい', 'sulit', 67],
              ['約束', 'やくそく', 'janji', 58],
              ['頑張る', 'がんばる', 'berusaha', 52],
            ].map(([k, r, m, miss]) => (
              <div key={k} className="tomo-card-base tomo-grain" style={{ padding: '14px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 18, color: Te.sumiBlack, marginRight: 8 }}>{k}</span>
                  <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 11, color: Te.sumiLight }}>{r}</span>
                  <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: Te.sumiMid, marginTop: 2 }}>{m}</div>
                </div>
                <div style={{ textAlign: 'right' }}>
                  <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: Te.hankoRed, fontWeight: 600 }}>{miss}% miss</span>
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>
      <BottomNav active="stats" />
    </div>
  );
}

// ─── NOTIFICATION (Interactive Flashcard in Notif Panel) ────────────────────
function NotificationScreen() {
  return (
    <div className="tomo-screen" style={{ position: 'relative', background: '#000' }}>
      {/* Lock screen wallpaper */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'linear-gradient(180deg, #1a1a2e 0%, #0f1424 100%)',
      }}>
        {/* Time + date */}
        <div style={{ textAlign: 'center', paddingTop: 70 }}>
          <div style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 200, fontSize: 72, color: '#fff', letterSpacing: '-0.03em', lineHeight: 1 }}>09:42</div>
          <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: '#ffffff99', marginTop: 6, letterSpacing: '0.04em' }}>Senin, 12 Okt</div>
        </div>
      </div>

      {/* Notification stack */}
      <div style={{ position: 'absolute', top: 240, left: 12, right: 12, display: 'flex', flexDirection: 'column', gap: 10 }}>
        {/* Sensei flashcard notif */}
        <div className="tomo-card-base tomo-grain" style={{
          padding: '14px 16px', borderRadius: 18,
          boxShadow: '0 8px 32px rgba(0,0,0,0.4)',
          background: 'rgba(245, 236, 217, 0.96)',
          backdropFilter: 'blur(20px)',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
            <div style={{ width: 18, height: 18, borderRadius: 4, background: Te.hankoRed, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 9, color: '#fff' }}>智</span>
            </div>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, fontWeight: 600, color: Te.sumiDark, letterSpacing: '0.04em' }}>TOMO SENSEI</span>
            <span style={{ marginLeft: 'auto', fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Te.sumiLight }}>now</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14 }}>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 26, color: Te.sumiBlack, marginBottom: 2, lineHeight: 1 }}>食べる</div>
              <div style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 12, color: Te.sumiMid, marginBottom: 4 }}>たべる</div>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: Te.sumiMid }}>Apa artinya?</div>
            </div>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ display: 'flex', gap: 8, marginTop: 12 }}>
            <button style={{ flex: 1, padding: '8px 0', borderRadius: 999, background: Te.successMoss, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 600 }}>✓ Tau</button>
            <button style={{ flex: 1, padding: '8px 0', borderRadius: 999, background: `${Te.hankoRed}cc`, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 600 }}>✗ Lupa</button>
            <button style={{ padding: '8px 12px', borderRadius: 999, background: 'rgba(0,0,0,0.06)', color: Te.sumiMid, border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500 }}>Buka</button>
          </div>
        </div>

        {/* Other notifs (dimmed) */}
        <div style={{ padding: '10px 14px', borderRadius: 16, background: 'rgba(255,255,255,0.12)', backdropFilter: 'blur(20px)' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 4 }}>
            <div style={{ width: 14, height: 14, borderRadius: 3, background: '#1DB954' }} />
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 10, fontWeight: 600, color: '#ffffffcc', letterSpacing: '0.04em' }}>SPOTIFY</span>
            <span style={{ marginLeft: 'auto', fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: '#ffffff77' }}>2m ago</span>
          </div>
          <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: '#ffffffdd' }}>Daily mix is ready</div>
        </div>
      </div>
    </div>
  );
}

// ─── EMPTY STATE: No cards due ──────────────────────────────────────────────
function EmptyStateScreen() {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '52px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 16, color: Te.sumiBlack, letterSpacing: '0.04em' }}>智センセイ</span>
          <div style={{ display: 'flex', gap: 16 }}>
            <span style={{ fontFamily: 'JetBrains Mono, monospace', fontWeight: 600, fontSize: 13, color: Te.sumiDark }}>🔥 47</span>
          </div>
        </div>

        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 32px', textAlign: 'center' }}>
          <div style={{ marginBottom: 20 }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 110, color: `${Te.sumiBlack}15`, lineHeight: 1 }}>休</span>
          </div>
          <Overline color={Te.hankoRed} style={{ marginBottom: 12 }}>Selesai untuk hari ini</Overline>
          <h2 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 26, color: Te.sumiBlack, margin: '0 0 14px', lineHeight: 1.3 }}>
            Istirahat dulu.<br/>Sensei juga capek.
          </h2>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: Te.sumiMid, lineHeight: 1.6, marginBottom: 32, maxWidth: 280 }}>
            20 / 20 kartu hari ini sudah selesai. Kartu baru tersedia besok jam 06:00.
          </p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, width: '100%' }}>
            <SenseiButton variant="ghost" style={{ width: '100%', textAlign: 'center' }}>Latihan tambahan (+5 kartu)</SenseiButton>
            <SenseiButton variant="ghost" small style={{ width: '100%', textAlign: 'center' }}>Review weak words</SenseiButton>
          </div>
        </div>

        <BottomNav active="drill" />
      </div>
    </div>
  );
}

// ─── ERROR STATE: Permission Denied ─────────────────────────────────────────
function ErrorStateScreen() {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', padding: '52px 28px 32px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 18, color: Te.sumiMid, cursor: 'pointer' }}>←</span>
        </div>

        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 14 }}>
            <div style={{ width: 24, height: 1, background: Te.hankoRed }} />
            <Overline color={Te.hankoRed}>Izin Ditolak</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 30, color: Te.sumiBlack, margin: '0 0 18px', lineHeight: 1.2 }}>
            Tanpa izin ini,<br/>Sensei lemah.
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: Te.sumiMid, lineHeight: 1.65, marginBottom: 16 }}>
            Accessibility Service belum aktif. Tanpa ini, gate Lv 4–6 tidak bisa berjalan — hanya notifikasi yang masih bekerja.
          </p>
          <div style={{
            background: `${Te.hankoRed}10`, border: `1px solid ${Te.hankoRed}33`,
            borderRadius: 12, padding: '14px 16px', marginBottom: 24,
          }}>
            <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: Te.hankoRed, lineHeight: 1.55, margin: 0 }}>
              <strong>Cara aktifkan:</strong><br/>
              Settings → Accessibility → Tomo Sensei → Toggle ON
            </p>
          </div>
        </div>

        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <SenseiButton style={{ width: '100%', textAlign: 'center' }}>Buka Settings →</SenseiButton>
          <SenseiButton variant="ghost" small style={{ width: '100%', textAlign: 'center' }}>Lanjut tanpa izin (mode lemah)</SenseiButton>
        </div>
      </div>
    </div>
  );
}

// ─── ACHIEVEMENT UNLOCK ─────────────────────────────────────────────────────
function AchievementScreen() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: `radial-gradient(ellipse at 50% 35%, ${Te.kinGold}15 0%, ${Te.sumiBlack} 70%)`,
      }} />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        {/* Decorative kanji + hanko */}
        <div style={{ position: 'relative', marginBottom: 36 }}>
          <span style={{
            fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 140,
            color: Te.kinGold, lineHeight: 1, letterSpacing: '-0.04em',
            filter: `drop-shadow(0 0 30px ${Te.kinGold}66)`,
          }}>祝</span>
          <div style={{ position: 'absolute', top: 30, right: -16 }}>
            <HankoStamp text="50日" size="md" rotation={-8} />
          </div>
        </div>

        <Overline color={`${Te.kinGold}cc`} style={{ marginBottom: 12, letterSpacing: '0.3em' }}>Achievement Unlocked</Overline>

        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 32, color: Te.washiCreamLight, margin: '0 0 14px', lineHeight: 1.2, textAlign: 'center' }}>
          50 hari berturut-turut.
        </h1>

        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: `${Te.washiCream}aa`, lineHeight: 1.65, marginBottom: 28, maxWidth: 280, textAlign: 'center' }}>
          Sensei mulai percaya kamu serius. Level naik dari N5 ke N4 mode.
        </p>

        {/* Stats badges */}
        <div style={{ display: 'flex', gap: 12, marginBottom: 36 }}>
          {[
            ['1,247', 'kartu'],
            ['87%', 'akurat'],
            ['34h', 'fokus'],
          ].map(([v, l]) => (
            <div key={l} style={{
              padding: '10px 14px', borderRadius: 10,
              background: `${Te.washiCream}0a`, border: `1px solid ${Te.washiCream}1a`,
              textAlign: 'center', minWidth: 70,
            }}>
              <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 18, color: Te.kinGold }}>{v}</div>
              <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: `${Te.washiCream}77`, letterSpacing: '0.08em', textTransform: 'uppercase', marginTop: 2 }}>{l}</div>
            </div>
          ))}
        </div>

        <SenseiButton style={{ width: '100%', textAlign: 'center', marginBottom: 12 }}>Naik ke N4 →</SenseiButton>
        <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: `${Te.washiCream}55`, background: 'none', border: 'none', cursor: 'pointer' }}>Tetap di N5</button>
      </div>
    </div>
  );
}

// ─── DARK MODE DRILL ────────────────────────────────────────────────────────
function DrillDarkScreen() {
  return (
    <div className="tomo-screen" style={{ position: 'relative', background: Te.sumiBlack }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: `radial-gradient(ellipse at 30% 0%, ${Te.sumiDark} 0%, ${Te.sumiBlack} 60%)`,
      }} />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '52px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 16, color: Te.washiCream, letterSpacing: '0.04em' }}>智センセイ</span>
          <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
            <span style={{ fontFamily: 'JetBrains Mono, monospace', fontWeight: 600, fontSize: 13, color: Te.washiCream }}>🔥 47</span>
            <span style={{ fontFamily: 'JetBrains Mono, monospace', fontWeight: 600, fontSize: 13, color: Te.washiCream }}>🔓 23</span>
          </div>
        </div>

        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px 24px 0' }}>
          <div className="tomo-card-base tomo-grain" style={{
            width: '100%',
            background: `linear-gradient(180deg, ${Te.sumiDark} 0%, #2a1f15 100%)`,
            border: `1px solid ${Te.washiCream}15`,
            boxShadow: `0 12px 48px rgba(0,0,0,0.6), inset 0 1px 0 ${Te.washiCream}10`,
            padding: '28px',
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 20 }}>
              <Overline color={`${Te.washiCream}55`}>Vocab</Overline>
              <HankoStamp text="N5" size="sm" />
            </div>
            <div style={{ textAlign: 'center', padding: '24px 0 20px' }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 80, color: Te.washiCreamLight, letterSpacing: '-0.02em' }}>食べる</span>
            </div>
            <div style={{ textAlign: 'center', marginBottom: 24 }}>
              <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500, color: `${Te.washiCream}aa`, background: 'none', border: 'none', cursor: 'pointer' }}>🔊 dengar</button>
            </div>
            <div style={{ borderTop: `1px solid ${Te.washiCream}15`, paddingTop: 14, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Overline color={`${Te.washiCream}55`}>tap untuk arti</Overline>
              <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: `${Te.washiCream}66` }}>03 / 12</span>
            </div>
          </div>
        </div>

        <div style={{ textAlign: 'center', padding: '14px 0 8px', display: 'flex', justifyContent: 'center', gap: 28 }}>
          <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: '#7FB069', fontWeight: 600 }}>↑ tau</span>
          <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: Te.hankoRed, fontWeight: 600 }}>↓ lupa</span>
        </div>

        {/* Bottom nav dark */}
        <div style={{
          padding: '14px 28px 28px', borderTop: `1px solid ${Te.washiCream}10`,
          display: 'flex', justifyContent: 'space-around', alignItems: 'center',
          background: `${Te.sumiBlack}cc`,
        }}>
          {[['Drill', '⌒', true], ['Stats', '◇', false], ['Sensei', '✦', false], ['Settings', '⚙', false]].map(([l, i, a]) => (
            <div key={l} style={{ textAlign: 'center', opacity: a ? 1 : 0.45 }}>
              <div style={{ fontFamily: 'Shippori Mincho, serif', fontSize: 18, color: a ? Te.hankoRed : Te.washiCream, marginBottom: 2 }}>{i}</div>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 9, color: a ? Te.hankoRed : Te.washiCream, letterSpacing: '0.1em', textTransform: 'uppercase' }}>{l}</div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  SettingsScreen, StatsScreen, NotificationScreen,
  EmptyStateScreen, ErrorStateScreen, AchievementScreen,
  DrillDarkScreen,
});

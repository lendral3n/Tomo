
// ─── Tomo Sensei — Streak Screens (Duolingo + GitHub style) ─────────────────

const Ts = window.TomoTokens;
const {
  WashiBg, HankoStamp, Overline, SectionHeader, SenseiButton,
  BottomNav,
} = window;

// Generate a fake year of streak data (deterministic)
function genYear() {
  const days = [];
  let seed = 42;
  const rand = () => { seed = (seed * 9301 + 49297) % 233280; return seed / 233280; };
  for (let i = 0; i < 365; i++) {
    const r = rand();
    let level = 0;
    if (i < 30) level = 0; // 30 days ago - empty
    else if (i < 50) level = r < 0.3 ? 0 : Math.ceil(r * 4);
    else if (i > 320) level = Math.ceil(r * 4); // recent — heavy
    else level = r < 0.15 ? 0 : Math.ceil(r * 4);
    days.push(Math.min(4, level));
  }
  return days;
}

// ─── 1. STREAK HERO (Duolingo-style celebration) ────────────────────────────
function StreakHeroScreen() {
  const ringPct = 0.86;
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '52px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 18, color: Ts.sumiMid, cursor: 'pointer' }}>←</span>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 16, color: Ts.sumiBlack }}>Streak</span>
          <span style={{ fontSize: 16, color: Ts.sumiLight, cursor: 'pointer' }}>⋯</span>
        </div>

        <div style={{ flex: 1, padding: '20px 28px', overflowY: 'auto' }}>
          {/* Hero — Big flame + ring */}
          <div style={{ textAlign: 'center', marginBottom: 28, position: 'relative' }}>
            {/* Decorative kanji bg */}
            <span style={{
              position: 'absolute', top: -10, left: '50%', transform: 'translateX(-50%)',
              fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 200,
              color: `${Ts.hankoRed}08`, lineHeight: 1, pointerEvents: 'none',
            }}>火</span>

            {/* SVG ring */}
            <div style={{ position: 'relative', display: 'inline-block', marginBottom: 8 }}>
              <svg width="180" height="180" viewBox="0 0 180 180" style={{ transform: 'rotate(-90deg)' }}>
                <circle cx="90" cy="90" r="78" fill="none" stroke={`${Ts.sumiLight}25`} strokeWidth="6" />
                <circle cx="90" cy="90" r="78" fill="none" stroke={Ts.hankoRed} strokeWidth="6" strokeLinecap="round" strokeDasharray={`${2 * Math.PI * 78}`} strokeDashoffset={`${2 * Math.PI * 78 * (1 - ringPct)}`} />
              </svg>
              <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
                <span style={{ fontSize: 48, lineHeight: 1, marginBottom: 4, filter: `drop-shadow(0 0 12px ${Ts.hankoRed}66)` }}>🔥</span>
                <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 44, color: Ts.sumiBlack, lineHeight: 1, letterSpacing: '-0.04em' }}>47</span>
              </div>
            </div>
            <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: Ts.sumiLight, letterSpacing: '0.2em', textTransform: 'uppercase', marginTop: 4 }}>Hari Berturut-turut</div>
            <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 14, color: Ts.sumiMid, marginTop: 10 }}>
              "Jangan biarkan padam."
            </p>
          </div>

          {/* Week strip */}
          <SectionHeader label="Minggu Ini" />
          <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 14, marginBottom: 24 }}>
            {['S','S','R','K','J','S','M'].map((d, i) => {
              const isToday = i === 4;
              const done = i <= 4;
              return (
                <div key={i} style={{ textAlign: 'center', flex: 1 }}>
                  <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Ts.sumiLight, marginBottom: 6 }}>{d}</div>
                  <div style={{
                    width: 36, height: 36, borderRadius: '50%',
                    background: done ? (isToday ? Ts.hankoRed : `${Ts.hankoRed}33`) : 'transparent',
                    border: !done ? `1.5px dashed ${Ts.sumiLight}55` : (isToday ? `2px solid ${Ts.hankoRed}` : 'none'),
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    margin: '0 auto',
                    boxShadow: isToday ? `0 0 16px ${Ts.hankoRed}55` : 'none',
                  }}>
                    {done && <span style={{ fontSize: 14 }}>{isToday ? '🔥' : '✓'}</span>}
                  </div>
                </div>
              );
            })}
          </div>

          {/* Stats row */}
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr 1fr', gap: 10, marginBottom: 24 }}>
            {[
              ['47', 'sekarang'],
              ['52', 'terbaik'],
              ['273', 'total hari'],
            ].map(([v, l]) => (
              <div key={l} style={{ textAlign: 'center', padding: '14px 8px', background: `${Ts.washiCreamDark}88`, borderRadius: 12 }}>
                <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 22, color: Ts.sumiBlack, lineHeight: 1 }}>{v}</div>
                <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: Ts.sumiLight, letterSpacing: '0.08em', textTransform: 'uppercase', marginTop: 4 }}>{l}</div>
              </div>
            ))}
          </div>

          {/* Streak freeze */}
          <div className="tomo-card-base tomo-grain" style={{ padding: '14px 16px', display: 'flex', alignItems: 'center', gap: 14, marginBottom: 20 }}>
            <div style={{ width: 40, height: 40, borderRadius: 10, background: '#A8C5E5', display: 'flex', alignItems: 'center', justifyContent: 'center', fontSize: 20, flexShrink: 0 }}>❄</div>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, fontWeight: 600, color: Ts.sumiBlack }}>Streak Freeze · 2 tersedia</div>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: Ts.sumiMid, marginTop: 2 }}>Lindungi streak saat libur. Auto-aktif.</div>
            </div>
          </div>
        </div>

        <BottomNav active="stats" />
      </div>
    </div>
  );
}

// ─── 2. GITHUB-STYLE HEATMAP (Full year contribution graph) ─────────────────
function StreakHeatmapScreen() {
  const days = genYear();
  // Group into weeks (53 columns × 7 rows). Pad start to align Sunday.
  const padStart = 3; // arbitrary day-of-week alignment
  const grid = [...Array(padStart).fill(-1), ...days];
  const cols = Math.ceil(grid.length / 7);
  const months = ['JAN', 'FEB', 'MAR', 'APR', 'MEI', 'JUN', 'JUL', 'AGS', 'SEP', 'OKT', 'NOV', 'DES'];
  const colorAt = (lvl) => {
    if (lvl <= 0) return `${Ts.sumiLight}1a`;
    if (lvl === 1) return `${Ts.hankoRed}33`;
    if (lvl === 2) return `${Ts.hankoRed}66`;
    if (lvl === 3) return `${Ts.hankoRed}aa`;
    return Ts.hankoRed;
  };

  return (
    <div className="tomo-screen" style={{ position: 'relative', overflowY: 'auto' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1 }}>
        <div style={{ padding: '52px 24px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 18, color: Ts.sumiMid, cursor: 'pointer' }}>←</span>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 16, color: Ts.sumiBlack }}>2025 · Setahun</span>
          <span style={{ fontSize: 16, color: Ts.sumiLight, cursor: 'pointer' }}>⋯</span>
        </div>

        <div style={{ padding: '0 20px 100px' }}>
          {/* Hero stat */}
          <div style={{ marginTop: 12, marginBottom: 20 }}>
            <div style={{ display: 'flex', alignItems: 'baseline', gap: 8 }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 56, color: Ts.sumiBlack, letterSpacing: '-0.04em', lineHeight: 1 }}>1,247</span>
              <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Ts.sumiMid }}>kartu di 2025</span>
            </div>
            <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: Ts.sumiLight, marginTop: 4 }}>
              <span style={{ color: Ts.hankoRed, fontWeight: 600 }}>↑ 273</span> hari aktif · streak terpanjang 52 hari
            </div>
          </div>

          {/* Heatmap card */}
          <div className="tomo-card-base tomo-grain" style={{ padding: 16, marginBottom: 16, overflow: 'hidden' }}>
            {/* Months row */}
            <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: Ts.sumiLight, letterSpacing: '0.06em', marginBottom: 8, paddingLeft: 14 }}>
              {months.map(m => <span key={m} style={{ flex: 1 }}>{m}</span>)}
            </div>
            {/* Grid */}
            <div style={{ display: 'flex', gap: 6 }}>
              {/* Day labels */}
              <div style={{ display: 'flex', flexDirection: 'column', gap: 3, fontFamily: 'JetBrains Mono, monospace', fontSize: 8, color: Ts.sumiLight, paddingTop: 1 }}>
                {['M', '', 'R', '', 'J', '', 'M'].map((d, i) => (
                  <div key={i} style={{ height: 8, lineHeight: '8px' }}>{d}</div>
                ))}
              </div>
              {/* Squares */}
              <div style={{ display: 'flex', gap: 3, flex: 1 }}>
                {Array.from({ length: cols }, (_, c) => (
                  <div key={c} style={{ display: 'flex', flexDirection: 'column', gap: 3, flex: 1 }}>
                    {Array.from({ length: 7 }, (_, r) => {
                      const idx = c * 7 + r;
                      const lvl = grid[idx];
                      if (lvl === undefined || lvl === -1) {
                        return <div key={r} style={{ aspectRatio: 1, background: 'transparent' }} />;
                      }
                      return <div key={r} style={{ aspectRatio: 1, background: colorAt(lvl), borderRadius: 1.5 }} />;
                    })}
                  </div>
                ))}
              </div>
            </div>
            {/* Legend */}
            <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 4, marginTop: 14, fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: Ts.sumiLight }}>
              <span>kurang</span>
              {[0,1,2,3,4].map(l => <div key={l} style={{ width: 10, height: 10, background: colorAt(l), borderRadius: 1.5 }} />)}
              <span>banyak</span>
            </div>
          </div>

          {/* Monthly bars */}
          <SectionHeader label="Per Bulan" />
          <div className="tomo-card-base tomo-grain" style={{ padding: 18, marginTop: 12, marginBottom: 16 }}>
            {[
              ['Okt 2025', 287, 100],
              ['Sep 2025', 245, 85],
              ['Ags 2025', 192, 67],
              ['Jul 2025', 156, 54],
              ['Jun 2025', 134, 47],
            ].map(([m, n, pct]) => (
              <div key={m} style={{ display: 'flex', alignItems: 'center', gap: 10, padding: '7px 0' }}>
                <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: Ts.sumiMid, width: 70 }}>{m}</span>
                <div style={{ flex: 1, height: 8, background: `${Ts.sumiLight}22`, borderRadius: 4, overflow: 'hidden' }}>
                  <div style={{ width: `${pct}%`, height: '100%', background: Ts.hankoRed, borderRadius: 4 }} />
                </div>
                <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: Ts.sumiBlack, fontWeight: 600, width: 36, textAlign: 'right' }}>{n}</span>
              </div>
            ))}
          </div>

          {/* Streak history */}
          <SectionHeader label="Riwayat Streak" />
          <div style={{ marginTop: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
            {[
              ['25 Aug — sekarang', '47 hari', 'aktif', true],
              ['12 Jun — 4 Aug', '52 hari', 'broken', false],
              ['1 Apr — 22 Mei', '34 hari', 'broken', false],
              ['15 Jan — 27 Mar', '24 hari', 'broken', false],
            ].map(([range, len, status, active]) => (
              <div key={range} className="tomo-card-base tomo-grain" style={{ padding: '12px 14px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <div>
                  <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 600, color: Ts.sumiBlack }}>{len}</div>
                  <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Ts.sumiLight, marginTop: 2 }}>{range}</div>
                </div>
                <span style={{
                  fontFamily: 'Manrope, sans-serif', fontSize: 10, fontWeight: 600,
                  letterSpacing: '0.1em', textTransform: 'uppercase',
                  color: active ? Ts.hankoRed : Ts.sumiLight,
                }}>{status}</span>
              </div>
            ))}
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── 3. STREAK SAVED (Recovery moment) ──────────────────────────────────────
function StreakSavedScreen() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: `radial-gradient(ellipse at 50% 35%, #A8C5E522 0%, ${Ts.sumiBlack} 70%)`,
      }} />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        {/* Snowflake icon */}
        <div style={{
          width: 96, height: 96, borderRadius: 24,
          background: 'linear-gradient(135deg, #A8C5E5 0%, #6B95C4 100%)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          marginBottom: 24, fontSize: 48,
          boxShadow: '0 0 40px rgba(168,197,229,0.4)',
        }}>❄</div>

        <Overline color="#A8C5E5cc" style={{ marginBottom: 10, letterSpacing: '0.3em' }}>STREAK FREEZE TERPAKAI</Overline>

        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 28, color: Ts.washiCreamLight, textAlign: 'center', margin: '0 0 14px', lineHeight: 1.25 }}>
          Streak-mu<br/>tetap aman.
        </h1>

        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: `${Ts.washiCream}aa`, lineHeight: 1.65, textAlign: 'center', marginBottom: 28, maxWidth: 280 }}>
          Kemarin kamu lupa belajar, tapi 1 freeze otomatis melindungi 47 hari streak-mu.
        </p>

        {/* Counter */}
        <div style={{
          padding: '16px 24px', borderRadius: 14,
          background: `${Ts.washiCream}0a`, border: `1px solid ${Ts.washiCream}1a`,
          marginBottom: 36, display: 'flex', alignItems: 'center', gap: 14,
        }}>
          <div style={{ textAlign: 'center' }}>
            <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 28, color: Ts.washiCreamLight, lineHeight: 1 }}>1</div>
            <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: `${Ts.washiCream}77`, letterSpacing: '0.08em', textTransform: 'uppercase', marginTop: 4 }}>tersisa</div>
          </div>
          <div style={{ width: 1, height: 30, background: `${Ts.washiCream}22` }} />
          <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: `${Ts.washiCream}aa`, lineHeight: 1.5 }}>
            Freeze pulih<br/>tiap 30 hari belajar
          </div>
        </div>

        <SenseiButton style={{ width: '100%', textAlign: 'center', marginBottom: 12 }}>Lanjut belajar →</SenseiButton>
      </div>
    </div>
  );
}

// ─── 4. STREAK BROKEN ───────────────────────────────────────────────────────
function StreakBrokenScreen() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: `radial-gradient(ellipse at 50% 30%, ${Ts.hankoRed}1a 0%, #050200 80%)`,
      }} />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        {/* Broken flame */}
        <div style={{ position: 'relative', marginBottom: 24 }}>
          <span style={{ fontSize: 90, filter: 'grayscale(0.7) opacity(0.5)', lineHeight: 1 }}>🔥</span>
          {/* Crack line */}
          <div style={{
            position: 'absolute', top: '50%', left: -8, right: -8,
            height: 2, background: `${Ts.hankoRed}aa`,
            transform: 'rotate(-12deg)',
            boxShadow: `0 0 8px ${Ts.hankoRed}66`,
          }} />
        </div>

        <Overline color={`${Ts.hankoRed}cc`} style={{ marginBottom: 10, letterSpacing: '0.3em' }}>Streak putus</Overline>

        <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 84, color: `${Ts.washiCream}33`, lineHeight: 1, letterSpacing: '-0.04em', marginBottom: 4, textAlign: 'center' }}>
          47 → 0
        </div>

        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: `${Ts.washiCream}99`, lineHeight: 1.65, textAlign: 'center', maxWidth: 280, margin: '20px 0 12px' }}>
          Sensei kecewa. Tapi belajar bukan tentang sempurna — tentang kembali.
        </p>

        <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 14, color: `${Ts.kinGold}cc`, textAlign: 'center', marginBottom: 36 }}>
          "七転び八起き"<br/>
          <span style={{ fontFamily: 'Manrope, sans-serif', fontStyle: 'normal', fontSize: 11, color: `${Ts.washiCream}66`, letterSpacing: '0.1em' }}>Jatuh 7 kali, bangkit 8.</span>
        </p>

        <SenseiButton style={{ width: '100%', textAlign: 'center', marginBottom: 12 }}>Mulai dari awal →</SenseiButton>
        <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: `${Ts.washiCream}55`, background: 'none', border: 'none', cursor: 'pointer' }}>
          Lihat catatan terbaik
        </button>
      </div>
    </div>
  );
}

Object.assign(window, {
  StreakHeroScreen, StreakHeatmapScreen, StreakSavedScreen, StreakBrokenScreen,
});

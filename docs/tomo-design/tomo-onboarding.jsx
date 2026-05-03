
// ─── Tomo Sensei — Onboarding Screens (11) ──────────────────────────────────

const { useState: useStateOb } = React;
const T = window.TomoTokens;
const { WashiBg, SenseiButton, VerticalAccent, Overline, SectionHeader, HankoStamp } = window;

// Shared wrapper
function OBScreen({ children, hideBack = false, progress = null }) {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        <div style={{ padding: '52px 24px 0', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          {!hideBack ? <span style={{ fontSize: 18, color: T.sumiMid, cursor: 'pointer' }}>←</span> : <span />}
          {progress && <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: T.sumiLight, letterSpacing: '0.1em' }}>{progress}</span>}
        </div>
        {children}
      </div>
    </div>
  );
}

// 1. Welcome (already exists — skip; we use the existing one)

// 2. Commitment
function OBCommitment() {
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '40px 28px 32px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>App ini akan</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 44, lineHeight: 1.1, color: T.sumiBlack, margin: '0 0 28px', letterSpacing: '-0.02em' }}>
            Mengganggu<br/>kamu.
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 15, lineHeight: 1.65, color: T.sumiMid, marginBottom: 18 }}>
            Itu fiturnya, bukan bug.
          </p>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.7, color: T.sumiMid, marginBottom: 12 }}>
            Setiap kali kamu unlock HP, buka TikTok, atau idle terlalu lama, akan muncul tembok kecil. Kamu harus jawab kartu Jepang untuk lewat.
          </p>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.7, color: T.sumiDark, fontWeight: 600 }}>
            Kamu pilih sendiri seberapa keras.
          </p>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          <SenseiButton style={{ width: '100%', textAlign: 'center' }}>Aku siap →</SenseiButton>
          <SenseiButton variant="ghost" style={{ width: '100%', textAlign: 'center' }}>Aku belum siap</SenseiButton>
        </div>
      </div>
    </OBScreen>
  );
}

// 3. Device Check
function OBDeviceCheck() {
  const items = [
    ['Android 14', 'OK', true],
    ['RAM 12GB', 'OK', true],
    ['Storage 45GB free', 'OK', true],
    ['Snapdragon 8 Gen 3', 'OK', true],
  ];
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '40px 28px 32px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>Cek Device</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 30, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 32px' }}>
            Mengecek HP-mu...
          </h1>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 14, marginBottom: 24 }}>
            {items.map(([label, status]) => (
              <div key={label} style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                <span style={{ color: T.successMoss, fontSize: 16, fontWeight: 700 }}>✓</span>
                <span style={{ flex: 1, fontFamily: 'Manrope, sans-serif', fontSize: 14, color: T.sumiDark }}>{label}</span>
                <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: T.successMoss, letterSpacing: '0.1em' }}>{status}</span>
              </div>
            ))}
          </div>
          <div style={{ height: 1, background: `${T.sumiLight}33`, margin: '20px 0' }} />
          <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
            <span style={{ color: T.successMoss, fontSize: 18 }}>✓</span>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 15, fontWeight: 600, color: T.sumiBlack }}>Siap untuk Sensei AI</span>
          </div>
        </div>
        <SenseiButton style={{ width: '100%', textAlign: 'center' }}>Lanjut →</SenseiButton>
      </div>
    </OBScreen>
  );
}

// 4. Preset Picker
function OBPresetPicker() {
  const presets = [
    { kanji: '緩', name: 'CASUAL', tone: 'Gentle nudges', items: ['Unlock: Lv 2', 'Idle: Lv 1'], quote: '"Aku coba dulu"', color: T.successMoss, recommended: false },
    { kanji: '真', name: 'SERIUS', tone: 'Real friction', items: ['Unlock: Lv 3', 'TikTok: Lv 4', 'App switch: Lv 3'], quote: '"Aku serius mau bisa"', color: T.kinGold, recommended: true },
    { kanji: '激', name: 'HARDCORE', tone: 'No escape', items: ['Semua trigger Lv 4-6'], quote: '"Paksa aku, please"', color: T.hankoRed, recommended: false, warning: '⚠ Side-load only' },
  ];
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '32px 24px 24px', overflowY: 'auto' }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
          <div style={{ width: 24, height: 1, background: T.hankoRed }} />
          <Overline color={T.hankoRed}>Pilih Gaya</Overline>
        </div>
        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 24px' }}>
          Seberapa keras<br/>kamu mau?
        </h1>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 12 }}>
          {presets.map(p => (
            <div key={p.name} className="tomo-card-base tomo-grain"
              style={{ padding: '16px 18px', border: p.recommended ? `2px solid ${T.hankoRed}` : `1px solid ${T.sumiLight}33`, boxShadow: p.recommended ? `0 8px 24px ${T.hankoRed}22` : '0 4px 12px rgba(26,15,8,0.06)' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 8 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
                  <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 22, color: p.color }}>{p.kanji}</span>
                  <span style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 700, fontSize: 13, letterSpacing: '0.18em', color: T.sumiBlack }}>{p.name}</span>
                </div>
                {p.recommended && <HankoStamp text="PILIHAN" size="sm" />}
              </div>
              <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: T.sumiMid, margin: '0 0 8px' }}>{p.tone}</p>
              <ul style={{ listStyle: 'none', padding: 0, margin: '0 0 8px' }}>
                {p.items.map(i => (
                  <li key={i} style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: T.sumiMid, padding: '2px 0' }}>• {i}</li>
                ))}
              </ul>
              <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 13, color: T.sumiDark, margin: '4px 0 0' }}>{p.quote}</p>
              {p.warning && <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: T.hankoRed, margin: '6px 0 0' }}>{p.warning}</p>}
            </div>
          ))}
        </div>
        <div style={{ textAlign: 'center', marginTop: 16 }}>
          <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: T.sumiLight, background: 'none', border: 'none', cursor: 'pointer' }}>Custom dari awal →</button>
        </div>
      </div>
    </OBScreen>
  );
}

// 5. Permission Funnel
function OBPermission() {
  return (
    <OBScreen progress="1 / 4">
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>Izin: Notifikasi</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 16px' }}>
            Kenapa Sensei<br/>butuh ini?
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.65, color: T.sumiMid, marginBottom: 24 }}>
            Untuk mengirim flashcard mini via notifikasi yang interaktif. Kamu bisa jawab langsung dari panel notif tanpa buka app.
          </p>
          {/* Mini illustration */}
          <div style={{ background: `${T.washiCreamDark}aa`, borderRadius: 14, padding: 14 }}>
            <Overline color={T.sumiLight} style={{ display: 'block', marginBottom: 10 }}>Contoh</Overline>
            <div className="tomo-card-base" style={{ padding: '12px 14px', boxShadow: '0 4px 12px rgba(26,15,8,0.12)' }}>
              <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 22, color: T.sumiBlack, marginBottom: 4 }}>食べる</div>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: T.sumiMid, marginBottom: 10 }}>Apa artinya?</div>
              <div style={{ display: 'flex', gap: 8 }}>
                <button style={{ flex: 1, padding: '7px 0', borderRadius: 999, background: T.successMoss, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 600 }}>Tau</button>
                <button style={{ flex: 1, padding: '7px 0', borderRadius: 999, background: `${T.hankoRed}cc`, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 600 }}>Lupa</button>
              </div>
            </div>
          </div>
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          <SenseiButton style={{ width: '100%', textAlign: 'center' }}>Beri izin</SenseiButton>
          <SenseiButton variant="ghost" small style={{ width: '100%', textAlign: 'center' }}>Skip permission ini</SenseiButton>
        </div>
      </div>
    </OBScreen>
  );
}

// 6. PIN Setup
function OBPinSetup() {
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
          <div style={{ width: 24, height: 1, background: T.hankoRed }} />
          <Overline color={T.hankoRed}>Emergency PIN</Overline>
        </div>
        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 12px' }}>Buat PIN 4 angka</h1>
        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, lineHeight: 1.6, color: T.sumiMid, marginBottom: 28 }}>
          PIN ini untuk bypass darurat saat kamu gate level 5–6. Disimpan local, tidak pernah ke server.
        </p>
        {/* Dots */}
        <div style={{ display: 'flex', justifyContent: 'center', gap: 18, marginBottom: 32 }}>
          {[true, true, true, false].map((filled, i) => (
            <div key={i} style={{ width: 14, height: 14, borderRadius: '50%', background: filled ? T.sumiBlack : 'transparent', border: `1.5px solid ${filled ? T.sumiBlack : T.sumiLight}` }} />
          ))}
        </div>
        {/* Numpad */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 10, marginBottom: 18 }}>
          {[1,2,3,4,5,6,7,8,9,'',0,'⌫'].map((n, i) => (
            <button key={i} disabled={n === ''} style={{
              padding: '16px 0', borderRadius: 14,
              background: n === '' ? 'transparent' : `${T.washiCreamDark}cc`,
              border: n === '' ? 'none' : `1px solid ${T.sumiLight}25`,
              fontFamily: 'Shippori Mincho, serif', fontSize: 22, fontWeight: 500,
              color: T.sumiBlack, cursor: n === '' ? 'default' : 'pointer',
            }}>{n}</button>
          ))}
        </div>
        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: T.hankoRed, textAlign: 'center' }}>
          ⚠ Lupa PIN = uninstall app
        </p>
      </div>
    </OBScreen>
  );
}

// 7. Gated Apps Picker
function OBGatedApps() {
  const apps = [
    { name: 'TikTok', icon: '🎵', selected: true, color: '#000' },
    { name: 'Instagram', icon: '📷', selected: true, color: '#E1306C' },
    { name: 'YouTube', icon: '▶', selected: true, color: '#FF0000' },
    { name: 'Twitter / X', icon: '𝕏', selected: false, color: '#000' },
    { name: 'Reddit', icon: '🤖', selected: false, color: '#FF4500' },
    { name: 'Facebook', icon: 'f', selected: false, color: '#1877F2' },
  ];
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column' }}>
        <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
          <div style={{ width: 24, height: 1, background: T.hankoRed }} />
          <Overline color={T.hankoRed}>Apps yang di-gate</Overline>
        </div>
        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 26, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 24px' }}>
          App mana yang<br/>perlu tembok?
        </h1>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8, flex: 1 }}>
          {apps.map(a => (
            <div key={a.name} style={{
              display: 'flex', alignItems: 'center', gap: 12,
              padding: '12px 14px',
              background: a.selected ? `${T.hankoRed}10` : `${T.washiCreamDark}66`,
              border: `1.5px solid ${a.selected ? T.hankoRed : T.sumiLight + '22'}`,
              borderRadius: 12,
            }}>
              <div style={{ width: 36, height: 36, borderRadius: 9, background: a.color, display: 'flex', alignItems: 'center', justifyContent: 'center', color: '#fff', fontSize: 16, fontWeight: 700 }}>{a.icon}</div>
              <span style={{ flex: 1, fontFamily: 'Manrope, sans-serif', fontSize: 14, fontWeight: 600, color: T.sumiBlack }}>{a.name}</span>
              <div style={{
                width: 22, height: 22, borderRadius: '50%',
                background: a.selected ? T.hankoRed : 'transparent',
                border: `1.5px solid ${a.selected ? T.hankoRed : T.sumiLight}`,
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                color: '#fff', fontSize: 12,
              }}>{a.selected && '✓'}</div>
            </div>
          ))}
        </div>
        <SenseiButton style={{ width: '100%', textAlign: 'center', marginTop: 16 }}>Lanjut · 3 dipilih</SenseiButton>
      </div>
    </OBScreen>
  );
}

// 8. Daily Goal
function OBDailyGoal() {
  const value = 20;
  const pct = ((value - 5) / (50 - 5)) * 100;
  return (
    <OBScreen>
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>Daily Goal</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 12px' }}>
            Berapa kartu<br/>per hari?
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: T.sumiMid, marginBottom: 36 }}>
            Bisa diubah kapan saja di Settings.
          </p>
          {/* Big number */}
          <div style={{ textAlign: 'center', marginBottom: 32 }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 96, color: T.sumiBlack, lineHeight: 1, letterSpacing: '-0.04em' }}>{value}</span>
            <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: T.sumiLight, letterSpacing: '0.18em', textTransform: 'uppercase', marginTop: 4 }}>kartu / hari</div>
          </div>
          {/* Slider */}
          <div style={{ marginBottom: 8, position: 'relative' }}>
            <div style={{ height: 6, background: `${T.sumiLight}33`, borderRadius: 3, position: 'relative' }}>
              <div style={{ position: 'absolute', left: 0, top: 0, bottom: 0, width: `${pct}%`, background: T.hankoRed, borderRadius: 3 }} />
              <div style={{ position: 'absolute', left: `${pct}%`, top: '50%', transform: 'translate(-50%, -50%)', width: 22, height: 22, borderRadius: '50%', background: '#fff', boxShadow: '0 2px 8px rgba(0,0,0,0.2)', border: `2px solid ${T.hankoRed}` }} />
            </div>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginTop: 12, fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: T.sumiLight }}>
              <span>5</span>
              <span>50</span>
            </div>
          </div>
          <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 14, color: T.sumiMid, textAlign: 'center', marginTop: 24 }}>
            "Sedikit-sedikit, lama-lama jadi bukit."
          </p>
        </div>
        <SenseiButton style={{ width: '100%', textAlign: 'center' }}>Set goal →</SenseiButton>
      </div>
    </OBScreen>
  );
}

// 9. Model Download
function OBModelDownload() {
  const pct = 33;
  return (
    <OBScreen hideBack>
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>Sensei AI Setup</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 12px' }}>
            Mengunduh otak<br/>Sensei...
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, lineHeight: 1.65, color: T.sumiMid, marginBottom: 36 }}>
            Model AI 3.6GB. Akan dijalankan offline di HP-mu — tidak ada data ke server.
          </p>
          {/* Big kanji */}
          <div style={{ textAlign: 'center', marginBottom: 24 }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 110, color: `${T.sumiBlack}15`, lineHeight: 1, letterSpacing: '-0.04em' }}>智</span>
          </div>
          {/* Progress */}
          <div style={{ marginBottom: 14 }}>
            <div style={{ height: 4, background: `${T.sumiLight}25`, borderRadius: 2, overflow: 'hidden' }}>
              <div style={{ width: `${pct}%`, height: '100%', background: T.hankoRed, borderRadius: 2 }} />
            </div>
          </div>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontFamily: 'JetBrains Mono, monospace', fontSize: 12, color: T.sumiMid }}>
            <span>1.2 GB / 3.6 GB</span>
            <span>2 min remaining</span>
          </div>
        </div>
        <SenseiButton variant="ghost" style={{ width: '100%', textAlign: 'center' }}>Download nanti</SenseiButton>
      </div>
    </OBScreen>
  );
}

// 10. Simulation
function OBSimulation() {
  return (
    <OBScreen progress="2 / 3">
      <div style={{ flex: 1, padding: '32px 28px 28px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between' }}>
        <div>
          <div style={{ display: 'flex', gap: 12, alignItems: 'center', marginBottom: 10 }}>
            <div style={{ width: 24, height: 1, background: T.hankoRed }} />
            <Overline color={T.hankoRed}>Latihan Gate</Overline>
          </div>
          <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 16px' }}>
            Coba dulu,<br/>biar nggak kaget.
          </h1>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, lineHeight: 1.65, color: T.sumiMid, marginBottom: 24 }}>
            3 gate sample — tanpa konsekuensi.
          </p>
          {/* Mock card preview */}
          <div className="tomo-card-base tomo-grain tomo-card-shadow" style={{ padding: '20px 22px', marginBottom: 14 }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              <Overline color={T.sumiLight}>Sample 2 / 3</Overline>
              <HankoStamp text="N5" size="sm" />
            </div>
            <div style={{ textAlign: 'center', padding: '12px 0' }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 56, color: T.sumiBlack }}>水</span>
            </div>
            <div style={{ textAlign: 'center', marginBottom: 14 }}>
              <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 14, color: T.sumiMid, letterSpacing: '0.06em' }}>みず</span>
            </div>
          </div>
          <div style={{ display: 'flex', gap: 10 }}>
            <button style={{ flex: 1, padding: '12px 0', borderRadius: 999, background: T.successMoss, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 13, fontWeight: 600 }}>✓ Tau</button>
            <button style={{ flex: 1, padding: '12px 0', borderRadius: 999, background: `${T.hankoRed}cc`, color: '#fff', border: 'none', fontFamily: 'Manrope, sans-serif', fontSize: 13, fontWeight: 600 }}>✗ Lupa</button>
          </div>
        </div>
        <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: T.sumiLight, background: 'none', border: 'none', cursor: 'pointer' }}>Lewati simulasi</button>
      </div>
    </OBScreen>
  );
}

// 11. Onboarding Complete
function OBComplete() {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', padding: '0 28px', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
        {/* Decorative kanji */}
        <div style={{ position: 'relative', marginBottom: 32 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 140, color: `${T.sumiBlack}10`, lineHeight: 1, letterSpacing: '-0.04em' }}>始</span>
          {/* Hanko stamp overlay */}
          <div style={{ position: 'absolute', top: 30, right: -10 }}>
            <HankoStamp text="始まり" size="md" rotation={8} />
          </div>
        </div>
        <Overline color={T.hankoRed} style={{ marginBottom: 12, letterSpacing: '0.3em' }}>Mulai sekarang</Overline>
        <h1 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 30, lineHeight: 1.2, color: T.sumiBlack, margin: '0 0 18px' }}>
          Sensei siap.<br/>Kamu siap?
        </h1>
        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.7, color: T.sumiMid, maxWidth: 280, marginBottom: 40 }}>
          Mulai sekarang, setiap kali kamu unlock HP, akan ada kartu Jepang menunggu.
        </p>
        <SenseiButton style={{ width: '100%', textAlign: 'center', marginBottom: 12 }}>Masuk app →</SenseiButton>
      </div>
    </div>
  );
}

Object.assign(window, {
  OBCommitment, OBDeviceCheck, OBPresetPicker, OBPermission,
  OBPinSetup, OBGatedApps, OBDailyGoal, OBModelDownload,
  OBSimulation, OBComplete,
});

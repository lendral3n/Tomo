
// ─── Tomo Sensei — Component Library Gallery ────────────────────────────────

const Tk = window.TomoTokens;
const {
  WashiCard, HankoStamp, VerticalAccent,
  Overline, SectionHeader, SenseiButton,
  TypingDots, EmergencyButton,
} = window;

// Wrapper for a labeled component spec
function ComponentSpec({ name, code, children, dark = false }) {
  return (
    <div style={{ borderRadius: 14, overflow: 'hidden', boxShadow: '0 4px 16px rgba(26,15,8,0.08)', border: `1px solid ${Tk.sumiLight}22` }}>
      {/* Demo area */}
      <div style={{
        padding: '32px 24px',
        background: dark ? Tk.sumiBlack : Tk.washiCream,
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        minHeight: 120,
        position: 'relative',
      }}>
        {children}
      </div>
      {/* Spec strip */}
      <div style={{
        padding: '10px 16px',
        background: '#1a1410',
        display: 'flex', justifyContent: 'space-between', alignItems: 'center',
      }}>
        <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: '#F5ECD9aa' }}>{name}</span>
        {code && <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: '#8B6F4788' }}>{code}</span>}
      </div>
    </div>
  );
}

// Color swatch
function Swatch({ name, hex, code }) {
  return (
    <div>
      <div style={{
        width: '100%', aspectRatio: '1.5', borderRadius: 10,
        background: hex,
        boxShadow: '0 2px 12px rgba(0,0,0,0.08), inset 0 1px 0 rgba(255,255,255,0.1)',
        marginBottom: 8,
      }} />
      <div style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 600, fontSize: 12, color: Tk.sumiBlack, marginBottom: 2 }}>{name}</div>
      <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Tk.sumiLight, letterSpacing: '0.04em' }}>{hex.toUpperCase()}</div>
      {code && <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 9, color: Tk.sumiLight + '99', marginTop: 1 }}>{code}</div>}
    </div>
  );
}

// ─── COLOR PALETTE GALLERY ──────────────────────────────────────────────────
function ColorPaletteGallery() {
  const colors = [
    { group: 'Washi', items: [
      ['Cream Light', Tk.washiCreamLight, 'washi-50'],
      ['Cream', Tk.washiCream, 'washi-100'],
      ['Cream Dark', Tk.washiCreamDark, 'washi-200'],
    ]},
    { group: 'Sumi', items: [
      ['Sumi Light', Tk.sumiLight, 'sumi-300'],
      ['Sumi Mid', Tk.sumiMid, 'sumi-500'],
      ['Sumi Dark', Tk.sumiDark, 'sumi-700'],
      ['Sumi Black', Tk.sumiBlack, 'sumi-900'],
    ]},
    { group: 'Accents', items: [
      ['Hanko Red', Tk.hankoRed, 'red-600'],
      ['Kin Gold', Tk.kinGold, 'gold-500'],
      ['Moss Green', Tk.successMoss, 'moss-500'],
    ]},
  ];
  return (
    <div style={{ padding: 32, background: Tk.washiCreamLight, minHeight: '100%' }}>
      <h2 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 24, color: Tk.sumiBlack, margin: '0 0 6px' }}>Color Palette</h2>
      <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Tk.sumiMid, margin: '0 0 28px' }}>10 tokens · paper, ink, hanko</p>
      {colors.map(g => (
        <div key={g.group} style={{ marginBottom: 28 }}>
          <SectionHeader label={g.group} />
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 16, marginTop: 14 }}>
            {g.items.map(([n, h, c]) => <Swatch key={n} name={n} hex={h} code={c} />)}
          </div>
        </div>
      ))}
    </div>
  );
}

// ─── TYPOGRAPHY GALLERY ─────────────────────────────────────────────────────
function TypographyGallery() {
  const samples = [
    { label: 'Display · Shippori Mincho 700', font: 'Shippori Mincho, serif', weight: 700, size: 44, text: '智センセイ' },
    { label: 'H1 · Shippori Mincho 700', font: 'Shippori Mincho, serif', weight: 700, size: 32, text: 'Belajar Jepang' },
    { label: 'H2 · Shippori Mincho 600', font: 'Shippori Mincho, serif', weight: 600, size: 24, text: 'Daily Goal' },
    { label: 'Kanji Hero · Shippori Mincho 600', font: 'Shippori Mincho, serif', weight: 600, size: 64, text: '食べる' },
    { label: 'Body · Manrope 500', font: 'Manrope, sans-serif', weight: 500, size: 16, text: 'Aku sensei galak yang akan bikin tembok di setiap pintu HP-mu.' },
    { label: 'Small · Manrope 500', font: 'Manrope, sans-serif', weight: 500, size: 13, text: 'Tap untuk arti' },
    { label: 'Reading · Zen Kaku Gothic 400', font: 'Zen Kaku Gothic Antique, sans-serif', weight: 400, size: 16, text: 'たべる', spacing: '0.06em' },
    { label: 'Mono · JetBrains Mono 600', font: 'JetBrains Mono, monospace', weight: 600, size: 14, text: '03 / 12 · 4:32' },
    { label: 'Overline · Manrope 600', font: 'Manrope, sans-serif', weight: 600, size: 11, text: 'TIKTOK BLOCKED', spacing: '0.18em' },
  ];
  return (
    <div style={{ padding: 32, background: Tk.washiCreamLight, minHeight: '100%' }}>
      <h2 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 24, color: Tk.sumiBlack, margin: '0 0 6px' }}>Typography</h2>
      <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Tk.sumiMid, margin: '0 0 28px' }}>4 font families · sumi ink hierarchy</p>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 22 }}>
        {samples.map((s, i) => (
          <div key={i} style={{ paddingBottom: 18, borderBottom: `1px solid ${Tk.sumiLight}22` }}>
            <div style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Tk.sumiLight, letterSpacing: '0.06em', marginBottom: 10 }}>{s.label}</div>
            <div style={{ fontFamily: s.font, fontWeight: s.weight, fontSize: s.size, color: Tk.sumiBlack, lineHeight: 1.3, letterSpacing: s.spacing || 'normal' }}>
              {s.text}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

// ─── COMPONENTS GALLERY ─────────────────────────────────────────────────────
function ComponentsGallery() {
  return (
    <div style={{ padding: 32, background: Tk.washiCreamLight, minHeight: '100%' }}>
      <h2 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 24, color: Tk.sumiBlack, margin: '0 0 6px' }}>Components</h2>
      <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Tk.sumiMid, margin: '0 0 28px' }}>10 atoms · ready to compose</p>

      <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
        {/* Buttons */}
        <ComponentSpec name="SenseiButton · primary">
          <SenseiButton>Mulai →</SenseiButton>
        </ComponentSpec>
        <ComponentSpec name="SenseiButton · secondary">
          <SenseiButton variant="secondary">Foto baru</SenseiButton>
        </ComponentSpec>
        <ComponentSpec name="SenseiButton · ghost">
          <SenseiButton variant="ghost">Skip onboarding</SenseiButton>
        </ComponentSpec>
        <ComponentSpec name="SenseiButton · ghost dark" dark>
          <SenseiButton variant="ghostDark">Skip</SenseiButton>
        </ComponentSpec>
        <ComponentSpec name="EmergencyButton" dark>
          <EmergencyButton />
        </ComponentSpec>

        {/* Hanko stamps */}
        <ComponentSpec name="HankoStamp · sm / md">
          <div style={{ display: 'flex', gap: 24, alignItems: 'center' }}>
            <HankoStamp text="N5" size="sm" />
            <HankoStamp text="N3" size="sm" />
            <HankoStamp text="始まり" size="md" rotation={8} />
          </div>
        </ComponentSpec>

        {/* Vertical accent */}
        <ComponentSpec name="VerticalAccent · 1px line">
          <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
            <VerticalAccent height={48} color={Tk.hankoRed} />
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Tk.sumiMid, lineHeight: 1.5 }}>
              Sensei galak<br/>tapi sayang.
            </span>
          </div>
        </ComponentSpec>

        {/* Overline */}
        <ComponentSpec name="Overline · 11px / 0.18em">
          <Overline color={Tk.hankoRed}>TIKTOK BLOCKED</Overline>
        </ComponentSpec>

        {/* Section header */}
        <ComponentSpec name="SectionHeader">
          <SectionHeader label="Vocab untuk dihafal" />
        </ComponentSpec>

        {/* Typing dots */}
        <ComponentSpec name="TypingDots">
          <div style={{ background: Tk.washiCreamLight, borderRadius: '0 16px 16px 16px', boxShadow: '0 2px 8px rgba(26,15,8,0.08)' }}>
            <TypingDots />
          </div>
        </ComponentSpec>

        {/* Card */}
        <ComponentSpec name="WashiCard · with grain">
          <div className="tomo-card-base tomo-grain tomo-card-shadow" style={{ width: '100%', padding: '20px 22px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
              <Overline color={Tk.sumiLight}>Vocab</Overline>
              <HankoStamp text="N5" size="sm" />
            </div>
            <div style={{ textAlign: 'center', padding: '12px 0' }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 56, color: Tk.sumiBlack }}>水</span>
            </div>
          </div>
        </ComponentSpec>
      </div>
    </div>
  );
}

// ─── BRAND PAGE ─────────────────────────────────────────────────────────────
function BrandGallery() {
  return (
    <div style={{ padding: 32, background: Tk.washiCreamLight, minHeight: '100%' }}>
      <h2 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 24, color: Tk.sumiBlack, margin: '0 0 6px' }}>Brand</h2>
      <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: Tk.sumiMid, margin: '0 0 28px' }}>Tomo Sensei · 智センセイ</p>

      {/* Logo lockup */}
      <div style={{ background: Tk.washiCream, borderRadius: 14, padding: 36, textAlign: 'center', marginBottom: 20, boxShadow: '0 4px 16px rgba(26,15,8,0.08)' }}>
        <div style={{ marginBottom: 8 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 64, color: Tk.sumiBlack }}>智</span>
        </div>
        <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 18, color: Tk.sumiBlack, marginBottom: 4, letterSpacing: '0.06em' }}>智センセイ</div>
        <div style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 500, fontSize: 11, color: Tk.sumiLight, letterSpacing: '0.18em', textTransform: 'uppercase' }}>Tomo Sensei</div>
      </div>

      {/* Logo on dark */}
      <div style={{ background: Tk.sumiBlack, borderRadius: 14, padding: 36, textAlign: 'center', marginBottom: 20 }}>
        <div style={{ marginBottom: 8 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 64, color: Tk.washiCreamLight }}>智</span>
        </div>
        <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 18, color: Tk.washiCreamLight, marginBottom: 4, letterSpacing: '0.06em' }}>智センセイ</div>
        <div style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 500, fontSize: 11, color: `${Tk.washiCream}aa`, letterSpacing: '0.18em', textTransform: 'uppercase' }}>Tomo Sensei</div>
      </div>

      {/* Hanko mark */}
      <div style={{ background: Tk.washiCream, borderRadius: 14, padding: 36, textAlign: 'center', marginBottom: 20, boxShadow: '0 4px 16px rgba(26,15,8,0.08)' }}>
        <div style={{ display: 'inline-block', transform: 'scale(1.5)' }}>
          <HankoStamp text="智" size="lg" rotation={-4} />
        </div>
      </div>

      {/* Voice */}
      <SectionHeader label="Voice & Tone" />
      <div style={{ marginTop: 16, padding: '20px 22px', background: Tk.washiCream, borderRadius: 14, marginBottom: 20 }}>
        <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 16, color: Tk.sumiDark, lineHeight: 1.6, margin: 0 }}>
          "Galak tapi sayang. Tegas tapi sabar.<br/>Aku sensei kamu di Asakusa."
        </p>
      </div>

      <SectionHeader label="Tagline" />
      <div style={{ marginTop: 16, padding: '20px 22px', background: Tk.washiCream, borderRadius: 14 }}>
        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 18, fontWeight: 700, color: Tk.sumiBlack, lineHeight: 1.3, margin: 0 }}>
          Belajar Jepang yang nggak bisa kamu hindari.
        </p>
      </div>
    </div>
  );
}

Object.assign(window, {
  ColorPaletteGallery, TypographyGallery, ComponentsGallery, BrandGallery,
});

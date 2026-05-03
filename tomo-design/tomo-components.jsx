
// ─── Tomo Sensei — Shared Design System Components ───────────────────────────
// Tokens, typography, and primitive UI components
// Exports to window.Tomo for cross-script access

// ── TOKENS ───────────────────────────────────────────────────────────────────
const TomoTokens = {
  // Colors
  washiCream:       '#F5ECD9',
  washiCreamLight:  '#FAF4E5',
  washiCreamDark:   '#EAE0CB',
  washiAged:        '#E8DBC0',
  sumiBlack:        '#1A0F08',
  sumiDark:         '#2A1810',
  sumiMid:          '#5C4A37',
  sumiLight:        '#8B6F47',
  hankoRed:         '#DC4D3A',
  hankoRedDeep:     '#C0392B',
  hankoRedFaded:    '#E07060',
  yamiDeep:         '#0F0A06',
  yamiMid:          '#1F1810',
  yamiSoft:         '#2D2419',
  kinGold:          '#D4A857',
  successMoss:      '#7DA876',
  levelEasy:        '#7DA876',
  levelModerate:    '#D4A857',
  levelHard:        '#DC4D3A',
};

// ── GOOGLE FONT INJECTION ────────────────────────────────────────────────────
const fontLink = document.createElement('link');
fontLink.rel = 'stylesheet';
fontLink.href = 'https://fonts.googleapis.com/css2?family=Shippori+Mincho:wght@400;500;600;700;800&family=Manrope:wght@300;400;500;600;700;800&family=Zen+Kaku+Gothic+Antique:wght@400;500;700;900&family=JetBrains+Mono:wght@400;600&display=swap';
document.head.appendChild(fontLink);

// ── GRAIN TEXTURE (CSS) ───────────────────────────────────────────────────────
const grainStyle = document.createElement('style');
grainStyle.textContent = `
  @keyframes grain-shift {
    0%, 100% { transform: translate(0,0); }
    10% { transform: translate(-1px,-1px); }
    30% { transform: translate(1px,1px); }
    50% { transform: translate(-1px,1px); }
    70% { transform: translate(1px,-1px); }
    90% { transform: translate(-1px,0); }
  }
  .tomo-grain::after {
    content: '';
    position: absolute; inset: 0;
    background-image: url("data:image/svg+xml,%3Csvg viewBox='0 0 256 256' xmlns='http://www.w3.org/2000/svg'%3E%3Cfilter id='noise'%3E%3CfeTurbulence type='fractalNoise' baseFrequency='0.9' numOctaves='4' stitchTiles='stitch'/%3E%3C/filter%3E%3Crect width='100%25' height='100%25' filter='url(%23noise)' opacity='0.08'/%3E%3C/svg%3E");
    background-size: 180px;
    opacity: 0.45;
    pointer-events: none;
    border-radius: inherit;
    mix-blend-mode: multiply;
    animation: grain-shift 0.8s steps(1) infinite;
  }
  .tomo-card-shadow {
    box-shadow: 0 30px 80px -20px rgba(26,15,8,0.55), 0 2px 8px rgba(26,15,8,0.12), inset 0 1px 0 rgba(255,255,255,0.45);
  }
  .tomo-card-base {
    background: ${TomoTokens.washiCreamLight};
    border-radius: 24px;
    position: relative;
    overflow: hidden;
  }
  .tomo-screen {
    width: 393px; height: 852px;
    background: ${TomoTokens.washiCream};
    overflow: hidden; position: relative;
    font-family: 'Manrope', sans-serif;
    color: ${TomoTokens.sumiBlack};
  }
  .tomo-screen-dark {
    background: ${TomoTokens.yamiDeep};
    color: ${TomoTokens.washiCream};
  }
  .font-shippori { font-family: 'Shippori Mincho', serif; }
  .font-zen { font-family: 'Zen Kaku Gothic Antique', sans-serif; }
  .font-manrope { font-family: 'Manrope', sans-serif; }
  .font-mono { font-family: 'JetBrains Mono', monospace; }
`;
document.head.appendChild(grainStyle);

// ── PRIMITIVE COMPONENTS ─────────────────────────────────────────────────────

const { useState, useEffect, useRef } = React;

// Washi background gradient overlay
function WashiBg({ dark = false, style = {}, children }) {
  const bg = dark
    ? { background: `radial-gradient(ellipse at 20% 30%, rgba(220,77,58,0.08) 0%, transparent 60%), radial-gradient(ellipse at 80% 70%, rgba(180,135,86,0.06) 0%, transparent 60%), ${TomoTokens.yamiDeep}` }
    : { background: `radial-gradient(ellipse at 20% 30%, rgba(255,234,208,0.5) 0%, transparent 60%), radial-gradient(ellipse at 80% 70%, rgba(180,135,86,0.12) 0%, transparent 60%), ${TomoTokens.washiCream}` };
  return (
    <div style={{ ...bg, width: '100%', height: '100%', position: 'absolute', inset: 0, ...style }}>
      {children}
    </div>
  );
}

// WashiCard container
function WashiCard({ children, style = {}, elevated = true, dark = false }) {
  return (
    <div className={`tomo-card-base tomo-grain ${elevated ? 'tomo-card-shadow' : ''}`}
      style={{
        background: dark ? TomoTokens.yamiMid : TomoTokens.washiCreamLight,
        ...style
      }}>
      {children}
    </div>
  );
}

// HankoStamp
function HankoStamp({ text, size = 'md', color = TomoTokens.hankoRed, rotation = -8 }) {
  const sizes = {
    sm: { fontSize: 10, px: 8, py: 4, border: 1.5 },
    md: { fontSize: 13, px: 11, py: 7, border: 2 },
    lg: { fontSize: 17, px: 14, py: 10, border: 2 },
  };
  const s = sizes[size];
  return (
    <span style={{
      display: 'inline-block',
      fontFamily: 'Shippori Mincho, serif',
      fontWeight: 800,
      fontSize: s.fontSize,
      color: TomoTokens.washiCreamLight,
      background: color,
      border: `${s.border}px solid rgba(245,236,217,0.35)`,
      borderRadius: 3,
      padding: `${s.py}px ${s.px}px`,
      transform: `rotate(${rotation}deg)`,
      letterSpacing: '0.12em',
      boxShadow: '0 2px 6px rgba(0,0,0,0.25)',
      whiteSpace: 'nowrap',
      lineHeight: 1,
    }}>
      {text}
    </span>
  );
}

// Vertical accent line
function VerticalAccent({ height = 80, color = TomoTokens.sumiLight, style = {} }) {
  return (
    <div style={{
      width: 2,
      height,
      background: `linear-gradient(to bottom, transparent, ${color}55, transparent)`,
      ...style,
    }} />
  );
}

// Section overline label
function Overline({ children, color = TomoTokens.sumiMid, style = {} }) {
  return (
    <span style={{
      fontFamily: 'Manrope, sans-serif',
      fontWeight: 700,
      fontSize: 10,
      letterSpacing: '0.22em',
      textTransform: 'uppercase',
      color,
      ...style,
    }}>{children}</span>
  );
}

// Section header w/ accent
function SectionHeader({ label, dark = false }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 0 }}>
      <div style={{ width: 2, height: 18, background: TomoTokens.hankoRed, borderRadius: 1 }} />
      <Overline color={dark ? `${TomoTokens.washiCream}88` : TomoTokens.sumiMid}>{label}</Overline>
    </div>
  );
}

// SenseiButton
function SenseiButton({ children, variant = 'primary', onClick, style = {}, small = false }) {
  const variants = {
    primary: { bg: TomoTokens.hankoRed, color: '#fff', border: 'none' },
    secondary: { bg: TomoTokens.washiCreamDark, color: TomoTokens.sumiDark, border: 'none' },
    ghost: { bg: 'transparent', color: TomoTokens.sumiMid, border: `1.5px solid ${TomoTokens.sumiLight}55` },
    destructive: { bg: TomoTokens.hankoRedDeep, color: '#fff', border: 'none' },
    ghostDark: { bg: 'transparent', color: `${TomoTokens.washiCream}99`, border: `1.5px solid ${TomoTokens.washiCream}33` },
    ghostRed: { bg: 'transparent', color: TomoTokens.hankoRed, border: `1.5px solid ${TomoTokens.hankoRed}55` },
  };
  const v = variants[variant] || variants.primary;
  return (
    <button onClick={onClick} style={{
      fontFamily: 'Manrope, sans-serif',
      fontWeight: 600,
      fontSize: small ? 13 : 15,
      padding: small ? '9px 20px' : '13px 32px',
      borderRadius: 999,
      background: v.bg,
      color: v.color,
      border: v.border || 'none',
      cursor: 'pointer',
      letterSpacing: '0.01em',
      outline: 'none',
      ...style,
    }}>{children}</button>
  );
}

// Bottom Nav (kanji labels)
function BottomNav({ active = 'drill', dark = false }) {
  const items = [
    { id: 'drill', kanji: '練', label: 'Drill' },
    { id: 'chat',  kanji: '会', label: 'Chat' },
    { id: 'photo', kanji: '撮', label: 'Foto' },
    { id: 'stats', kanji: '塾', label: 'Stats' },
    { id: 'settings', kanji: '設', label: 'Atur' },
  ];
  const bg = dark ? TomoTokens.yamiDeep : TomoTokens.washiCream;
  const inactive = dark ? `${TomoTokens.washiCream}40` : `${TomoTokens.sumiLight}88`;
  return (
    <div style={{
      position: 'absolute', bottom: 0, left: 0, right: 0,
      background: `linear-gradient(to bottom, ${bg}00, ${bg}f5, ${bg})`,
      paddingTop: 24, paddingBottom: 20,
      display: 'flex', justifyContent: 'space-around',
    }}>
      {items.map(item => {
        const isActive = item.id === active;
        const color = isActive ? TomoTokens.hankoRed : inactive;
        return (
          <div key={item.id} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 3 }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontSize: 18, color, fontWeight: isActive ? 700 : 400 }}>
              {item.kanji}
            </span>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 9, fontWeight: 600, letterSpacing: '0.18em', textTransform: 'uppercase', color }}>
              {item.label}
            </span>
          </div>
        );
      })}
    </div>
  );
}

// Typing dots
function TypingDots() {
  return (
    <div style={{ display: 'flex', gap: 5, alignItems: 'center', padding: '12px 16px' }}>
      {[0,1,2].map(i => (
        <div key={i} style={{
          width: 7, height: 7, borderRadius: '50%',
          background: TomoTokens.sumiMid,
          animation: `typingDot 1.2s ease-in-out ${i * 0.2}s infinite`,
        }} />
      ))}
      <style>{`
        @keyframes typingDot {
          0%,60%,100%{opacity:0.3;transform:scale(1)}
          30%{opacity:1;transform:scale(1.15)}
        }
      `}</style>
    </div>
  );
}

// Emergency call button (always visible in gates)
function EmergencyButton() {
  return (
    <div style={{
      display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 7,
      color: `${TomoTokens.washiCream}66`,
      fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500,
      cursor: 'pointer', padding: '8px 0',
    }}>
      <span>📞</span>
      <span>Panggilan darurat</span>
    </div>
  );
}

// Ghost cards (behind main card for depth)
function CardStackGhosts({ dark = false }) {
  return (
    <>
      <div style={{
        position: 'absolute',
        top: 8, left: '50%', transform: 'translateX(-50%)',
        width: 'calc(100% - 16px)',
        height: '100%',
        background: dark ? TomoTokens.yamiSoft : TomoTokens.washiCreamDark,
        borderRadius: 24,
        opacity: 0.5,
        zIndex: 0,
      }} />
      <div style={{
        position: 'absolute',
        top: 16, left: '50%', transform: 'translateX(-50%)',
        width: 'calc(100% - 32px)',
        height: '100%',
        background: dark ? TomoTokens.yamiMid : TomoTokens.washiAged,
        borderRadius: 24,
        opacity: 0.28,
        zIndex: 0,
      }} />
    </>
  );
}

// Export everything
Object.assign(window, {
  TomoTokens,
  WashiBg, WashiCard, HankoStamp, VerticalAccent,
  Overline, SectionHeader, SenseiButton,
  BottomNav, TypingDots, EmergencyButton, CardStackGhosts,
});


// ─── Tomo Sensei — Screen Components ─────────────────────────────────────────
// All screen renders. Exports to window for design-canvas consumption.

const { useState, useEffect, useRef } = React;
const {
  TomoTokens, WashiBg, WashiCard, HankoStamp, VerticalAccent,
  Overline, SectionHeader, SenseiButton, BottomNav, TypingDots,
  EmergencyButton, CardStackGhosts,
} = window;

// ─── SCREEN 1: WELCOME ────────────────────────────────────────────────────────
function WelcomeScreen() {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, padding: '0 28px', height: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        {/* Decorative kanji */}
        <div style={{ marginBottom: 8, lineHeight: 1 }}>
          <span style={{
            fontFamily: 'Shippori Mincho, serif', fontWeight: 800,
            fontSize: 120, color: `${TomoTokens.sumiBlack}12`,
            letterSpacing: '-0.04em', lineHeight: 1,
            display: 'block', marginLeft: -8,
          }}>友</span>
        </div>

        {/* Brand */}
        <div style={{ marginBottom: 32, display: 'flex', alignItems: 'center', gap: 10 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 15, color: TomoTokens.sumiMid, letterSpacing: '0.06em' }}>智センセイ</span>
          <div style={{ width: 1, height: 14, background: `${TomoTokens.sumiLight}55` }} />
          <span style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 500, fontSize: 11, color: TomoTokens.sumiLight, letterSpacing: '0.14em', textTransform: 'uppercase' }}>Tomo Sensei</span>
        </div>

        {/* Headline */}
        <h1 style={{
          fontFamily: 'Shippori Mincho, serif', fontWeight: 700,
          fontSize: 32, lineHeight: 1.25, color: TomoTokens.sumiBlack,
          margin: '0 0 20px', letterSpacing: '-0.01em',
        }}>
          Belajar Jepang<br/>yang nggak bisa<br/>kamu hindari
        </h1>

        {/* Vertical accent + body */}
        <div style={{ display: 'flex', gap: 14, marginBottom: 48 }}>
          <VerticalAccent height={70} color={TomoTokens.hankoRed} style={{ flexShrink: 0, marginTop: 3 }} />
          <p style={{
            fontFamily: 'Manrope, sans-serif', fontSize: 14,
            lineHeight: 1.65, color: TomoTokens.sumiMid, margin: 0,
          }}>
            Aku sensei galak yang akan bikin tembok di setiap pintu HP-mu. Jawab kartu Jepang, atau tidak lewat.
          </p>
        </div>

        {/* CTA */}
        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'flex-start', gap: 16 }}>
          <SenseiButton style={{ alignSelf: 'stretch', textAlign: 'center' }}>
            Mulai →
          </SenseiButton>
          <button style={{
            fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500,
            color: `${TomoTokens.sumiLight}cc`, background: 'none', border: 'none',
            cursor: 'pointer', letterSpacing: '0.06em', alignSelf: 'center',
          }}>
            Skip onboarding
          </button>
        </div>
      </div>
    </div>
  );
}

// ─── SCREEN 2: HOME / DRILL ──────────────────────────────────────────────────
function DrillScreen({ flipped = false }) {
  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        {/* Top bar */}
        <div style={{
          display: 'flex', justifyContent: 'space-between', alignItems: 'center',
          padding: '52px 24px 0',
        }}>
          <span style={{
            fontFamily: 'Shippori Mincho, serif', fontWeight: 700,
            fontSize: 16, color: TomoTokens.sumiBlack, letterSpacing: '0.04em',
          }}>智センセイ</span>
          <div style={{ display: 'flex', gap: 16, alignItems: 'center' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
              <span style={{ fontSize: 15 }}>🔥</span>
              <span style={{ fontFamily: 'JetBrains Mono, monospace', fontWeight: 600, fontSize: 13, color: TomoTokens.sumiDark }}>5</span>
            </div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
              <span style={{ fontSize: 15 }}>🔓</span>
              <span style={{ fontFamily: 'JetBrains Mono, monospace', fontWeight: 600, fontSize: 13, color: TomoTokens.sumiDark }}>23</span>
            </div>
          </div>
        </div>

        {/* Card area */}
        <div style={{ flex: 1, display: 'flex', alignItems: 'center', justifyContent: 'center', padding: '20px 24px 0' }}>
          <div style={{ position: 'relative', width: '100%' }}>
            <CardStackGhosts />
            {/* Main card */}
            <div className="tomo-card-base tomo-grain tomo-card-shadow" style={{ position: 'relative', zIndex: 1 }}>
              {!flipped ? (
                /* Card FRONT */
                <div style={{ padding: '28px 28px 24px', minHeight: 340 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 20 }}>
                    <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
                    <HankoStamp text="N5" size="sm" />
                  </div>

                  {/* Kanji hero */}
                  <div style={{ textAlign: 'center', padding: '24px 0 20px', position: 'relative' }}>
                    {/* Decorative vertical accent */}
                    <div style={{
                      position: 'absolute', right: 0, top: 0, bottom: 0,
                      width: 1,
                      background: `linear-gradient(to bottom, transparent, ${TomoTokens.sumiLight}30, transparent)`,
                    }} />
                    <span style={{
                      fontFamily: 'Shippori Mincho, serif', fontWeight: 600,
                      fontSize: 80, color: TomoTokens.sumiBlack,
                      letterSpacing: '-0.02em', lineHeight: 1,
                    }}>食べる</span>
                  </div>

                  {/* Listen button */}
                  <div style={{ textAlign: 'center', marginBottom: 24 }}>
                    <button style={{
                      fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500,
                      color: TomoTokens.sumiMid, background: 'none', border: 'none',
                      cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 5,
                    }}>
                      <span>🔊</span> dengar
                    </button>
                  </div>

                  {/* Hint */}
                  <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 14, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Overline color={`${TomoTokens.sumiLight}aa`}>tap untuk arti</Overline>
                    <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: `${TomoTokens.sumiLight}88` }}>03 / 12</span>
                  </div>
                </div>
              ) : (
                /* Card BACK (flipped) */
                <div style={{ padding: '28px 28px 24px', minHeight: 340 }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', marginBottom: 16 }}>
                    <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
                    <HankoStamp text="N5" size="sm" />
                  </div>

                  {/* Kanji smaller */}
                  <div style={{ marginBottom: 6 }}>
                    <span style={{
                      fontFamily: 'Shippori Mincho, serif', fontWeight: 600,
                      fontSize: 44, color: TomoTokens.sumiBlack, letterSpacing: '-0.01em',
                    }}>食べる</span>
                  </div>
                  {/* Reading */}
                  <div style={{ marginBottom: 14 }}>
                    <span style={{
                      fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontWeight: 400,
                      fontSize: 20, color: TomoTokens.sumiMid, letterSpacing: '0.06em',
                    }}>たべる</span>
                  </div>

                  {/* Hanko red divider */}
                  <div style={{ width: 40, height: 2, background: TomoTokens.hankoRed, marginBottom: 16, borderRadius: 1 }} />

                  {/* Meaning */}
                  <div style={{ marginBottom: 20 }}>
                    <span style={{
                      fontFamily: 'Manrope, sans-serif', fontWeight: 500,
                      fontSize: 22, color: TomoTokens.sumiDark,
                    }}>makan</span>
                  </div>

                  {/* Grammar note */}
                  <div style={{
                    background: `${TomoTokens.washiCreamDark}88`, borderRadius: 10,
                    padding: '10px 14px', marginBottom: 20,
                    display: 'flex', alignItems: 'flex-start', gap: 8,
                  }}>
                    <div style={{ width: 2, height: 16, background: TomoTokens.hankoRed, borderRadius: 1, flexShrink: 0, marginTop: 2 }} />
                    <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.sumiMid, lineHeight: 1.5 }}>
                      Ichidan verb (る-verb). Conjugates: 食べます / 食べない
                    </span>
                  </div>

                  {/* Swipe hints + counter */}
                  <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 14, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <div style={{ display: 'flex', gap: 12 }}>
                      <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.successMoss, fontWeight: 600 }}>↑ tau</span>
                      <span style={{ color: `${TomoTokens.sumiLight}44` }}>·</span>
                      <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.hankoRed, fontWeight: 600 }}>↓ lupa</span>
                    </div>
                    <span style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 11, color: `${TomoTokens.sumiLight}88` }}>03 / 12</span>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>

        {/* Swipe hints (card front only) */}
        {!flipped && (
          <div style={{ textAlign: 'center', padding: '14px 0 8px', display: 'flex', justifyContent: 'center', gap: 28 }}>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.successMoss, fontWeight: 600 }}>↑ tau</span>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.hankoRed, fontWeight: 600 }}>↓ lupa</span>
          </div>
        )}

        {/* FAB Sensei */}
        <div style={{ position: 'absolute', bottom: 88, right: 24, zIndex: 10 }}>
          <div style={{
            width: 50, height: 50, borderRadius: '50%',
            background: TomoTokens.sumiDark,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 4px 20px rgba(26,15,8,0.35)',
            cursor: 'pointer',
          }}>
            <span style={{ color: TomoTokens.washiCream, fontSize: 18, fontFamily: 'Shippori Mincho, serif' }}>✦</span>
          </div>
        </div>

        <BottomNav active="drill" />
      </div>
    </div>
  );
}

// ─── SCREENS 3a–3f: GATE OVERLAYS ────────────────────────────────────────────

// Shared Gate card
function GateCard({ children, style = {} }) {
  return (
    <div className="tomo-card-base tomo-grain tomo-card-shadow"
      style={{ width: '100%', padding: '24px', ...style }}>
      {children}
    </div>
  );
}

// Multiple choice options
function MultiChoice({ options, selected = null, wrong = null }) {
  const letters = ['A', 'B', 'C', 'D'];
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 9 }}>
      {options.map((opt, i) => {
        const isCorrect = i === 1; // B is correct
        const isWrong = wrong === i;
        const isSelected = selected === i;
        let bg = `${TomoTokens.washiCream}18`;
        let border = `1px solid ${TomoTokens.washiCream}28`;
        let textColor = TomoTokens.washiCreamLight;
        if (isSelected && !isCorrect) { bg = `${TomoTokens.hankoRed}33`; border = `1px solid ${TomoTokens.hankoRed}88`; }
        return (
          <div key={i} style={{
            display: 'flex', alignItems: 'center', gap: 12,
            background: bg, border, borderRadius: 12,
            padding: '12px 16px', cursor: 'pointer',
          }}>
            <span style={{
              fontFamily: 'JetBrains Mono, monospace', fontWeight: 600,
              fontSize: 12, color: TomoTokens.hankoRed, minWidth: 16,
            }}>{letters[i]}.</span>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 15, color: textColor, fontWeight: 500 }}>{opt}</span>
          </div>
        );
      })}
    </div>
  );
}

// Lv 1: Whisper
function GateLv1() {
  return (
    <div className="tomo-screen" style={{ position: 'relative', background: '#000' }}>
      {/* Fake blurred TikTok bg */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'linear-gradient(160deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        gap: 8,
      }}>
        <div style={{ width: 48, height: 48, borderRadius: '50%', background: '#ffffff22' }} />
        <div style={{ width: 120, height: 10, borderRadius: 5, background: '#ffffff18' }} />
        <div style={{ width: 80, height: 8, borderRadius: 5, background: '#ffffff12' }} />
      </div>

      {/* Whisper bubble — bottom right */}
      <div style={{
        position: 'absolute', bottom: 80, right: 16,
        width: 160, zIndex: 10,
      }}>
        <div className="tomo-card-base tomo-grain tomo-card-shadow"
          style={{ padding: '16px 16px 14px' }}>
          <div style={{ marginBottom: 8 }}>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 28, color: TomoTokens.sumiBlack, marginBottom: 2, lineHeight: 1 }}>食べる</div>
          <div style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 12, color: TomoTokens.sumiMid, marginBottom: 8, letterSpacing: '0.04em' }}>たべる</div>
          <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, fontWeight: 500, color: TomoTokens.sumiDark, marginBottom: 12 }}>makan</div>
          <button style={{
            fontFamily: 'Manrope, sans-serif', fontSize: 11, fontWeight: 600,
            color: TomoTokens.sumiLight, background: 'none', border: 'none',
            cursor: 'pointer', letterSpacing: '0.06em',
          }}>dismiss →</button>
        </div>
      </div>
    </div>
  );
}

// Lv 2: Tap to Pass
function GateLv2() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(ellipse at 50% 40%, rgba(26,15,8,0.82) 0%, rgba(10,6,2,0.95) 100%)',
        zIndex: 0,
      }} />
      <div style={{
        position: 'relative', zIndex: 1, height: '100%',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 28px',
      }}>
        {/* Trigger label */}
        <div style={{ marginBottom: 8, textAlign: 'center' }}>
          <Overline color={`${TomoTokens.washiCream}50`}>Unlock HP Gate</Overline>
        </div>
        <div style={{ marginBottom: 28, textAlign: 'center' }}>
          <span style={{
            fontFamily: 'Shippori Mincho, serif', fontWeight: 500,
            fontSize: 13, color: `${TomoTokens.washiCream}60`, letterSpacing: '0.05em',
          }}>Level 2 · Tap to Pass</span>
        </div>

        {/* Card */}
        <GateCard style={{ marginBottom: 24 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
            <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ textAlign: 'center', padding: '16px 0' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 64, color: TomoTokens.sumiBlack, lineHeight: 1 }}>食べる</span>
          </div>
          <div style={{ textAlign: 'center', marginBottom: 16 }}>
            <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500, color: TomoTokens.sumiMid, background: 'none', border: 'none', cursor: 'pointer', display: 'inline-flex', alignItems: 'center', gap: 4 }}>
              <span>🔊</span> dengar
            </button>
          </div>
          <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 12, textAlign: 'center' }}>
            <button style={{
              fontFamily: 'Manrope, sans-serif', fontSize: 13, fontWeight: 600,
              color: TomoTokens.sumiMid, background: 'none',
              border: `1px solid ${TomoTokens.sumiLight}40`, borderRadius: 999,
              padding: '8px 20px', cursor: 'pointer',
            }}>Lihat arti</button>
          </div>
        </GateCard>

        {/* Primary action */}
        <SenseiButton style={{ width: '100%', textAlign: 'center', marginBottom: 14 }}>
          Tap untuk lewat (3s...)
        </SenseiButton>
        <SenseiButton variant="ghostDark" style={{ width: '100%', textAlign: 'center', marginBottom: 24 }}>
          Skip
        </SenseiButton>
        <EmergencyButton />
      </div>
    </div>
  );
}

// Lv 3: Answer to Pass
function GateLv3() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(ellipse at 50% 40%, rgba(26,15,8,0.88) 0%, rgba(10,6,2,0.97) 100%)',
      }} />
      <div style={{
        position: 'relative', zIndex: 1, height: '100%',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 28px',
      }}>
        <div style={{ marginBottom: 6, textAlign: 'center' }}>
          <Overline color={`${TomoTokens.washiCream}45`}>TikTok Blocked</Overline>
        </div>
        <div style={{ marginBottom: 24, textAlign: 'center' }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 500, fontSize: 13, color: `${TomoTokens.washiCream}55`, letterSpacing: '0.05em' }}>Level 3 · Answer to Pass</span>
        </div>

        <GateCard style={{ marginBottom: 20 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
            <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ textAlign: 'center', padding: '12px 0 8px' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 64, color: TomoTokens.sumiBlack, lineHeight: 1 }}>食べる</span>
          </div>
          <div style={{ textAlign: 'center', paddingBottom: 14 }}>
            <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 18, color: TomoTokens.sumiMid, letterSpacing: '0.06em' }}>たべる</span>
          </div>
          <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 14, display: 'flex', gap: 12 }}>
            <button style={{
              flex: 1, fontFamily: 'Manrope, sans-serif', fontWeight: 600, fontSize: 14,
              color: '#fff', background: TomoTokens.successMoss, border: 'none',
              borderRadius: 999, padding: '11px 0', cursor: 'pointer',
            }}>✓ Tau</button>
            <button style={{
              flex: 1, fontFamily: 'Manrope, sans-serif', fontWeight: 600, fontSize: 14,
              color: '#fff', background: `${TomoTokens.hankoRed}cc`, border: 'none',
              borderRadius: 999, padding: '11px 0', cursor: 'pointer',
            }}>✗ Lupa</button>
          </div>
        </GateCard>

        <SenseiButton variant="ghostDark" style={{ width: '100%', textAlign: 'center', marginBottom: 20 }}>Skip</SenseiButton>
        <EmergencyButton />
      </div>
    </div>
  );
}

// Lv 4: Correct to Pass
function GateLv4() {
  return (
    <div className="tomo-screen tomo-screen-dark" style={{ position: 'relative' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(ellipse at 50% 35%, rgba(15,10,6,0.9) 0%, rgba(5,3,1,0.98) 100%)',
      }} />
      <div style={{
        position: 'relative', zIndex: 1, height: '100%',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 24px',
      }}>
        <div style={{ marginBottom: 5, textAlign: 'center' }}>
          <Overline color={`${TomoTokens.washiCream}40`}>TikTok Blocked</Overline>
        </div>
        <div style={{ marginBottom: 20 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 500, fontSize: 13, color: `${TomoTokens.washiCream}50`, letterSpacing: '0.05em' }}>Level 4 · Correct to Pass</span>
        </div>

        {/* Card */}
        <GateCard style={{ marginBottom: 18 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
            <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ textAlign: 'center', padding: '10px 0 6px' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 52, color: TomoTokens.sumiBlack, lineHeight: 1 }}>食べる</span>
          </div>
          <div style={{ textAlign: 'center', marginBottom: 10 }}>
            <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 16, color: TomoTokens.sumiMid, letterSpacing: '0.06em' }}>たべる</span>
          </div>
          <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 10, textAlign: 'center' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 28, color: `${TomoTokens.sumiLight}88` }}>?</span>
          </div>
        </GateCard>

        {/* Prompt */}
        <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: `${TomoTokens.washiCream}70`, marginBottom: 14, textAlign: 'center', letterSpacing: '0.02em' }}>
          Pilih arti yang benar:
        </p>

        {/* Multi choice */}
        <MultiChoice options={['minum', 'makan', 'melihat', 'pergi']} selected={0} />

        {/* Retry counter */}
        <div style={{ marginTop: 14, marginBottom: 14 }}>
          <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.hankoRed, fontWeight: 600 }}>Salah · 1/3 percobaan</span>
        </div>

        <button style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, fontWeight: 500, color: `${TomoTokens.washiCream}40`, background: 'none', border: 'none', cursor: 'pointer', marginBottom: 16 }}>
          Skip (counted as fail)
        </button>
        <EmergencyButton />
      </div>
    </div>
  );
}

// Lv 5: Timeout Punishment
function GateLv5() {
  return (
    <div className="tomo-screen" style={{ position: 'relative', background: TomoTokens.yamiDeep }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(ellipse at 50% 40%, rgba(180,30,10,0.22) 0%, rgba(10,4,2,0.99) 70%)',
      }} />
      <div style={{
        position: 'relative', zIndex: 1, height: '100%',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 32px',
      }}>
        {/* Lock icon with glow */}
        <div style={{
          width: 72, height: 72, borderRadius: '50%',
          background: `radial-gradient(circle, ${TomoTokens.hankoRed}33, transparent 70%)`,
          display: 'flex', alignItems: 'center', justifyContent: 'center',
          marginBottom: 20, boxShadow: `0 0 40px ${TomoTokens.hankoRed}44`,
          animation: 'pulseGlow 2s ease-in-out infinite',
        }}>
          <span style={{ fontSize: 36 }}>🔒</span>
        </div>
        <style>{`@keyframes pulseGlow { 0%,100%{box-shadow:0 0 30px ${TomoTokens.hankoRed}44} 50%{box-shadow:0 0 60px ${TomoTokens.hankoRed}66} }`}</style>

        <Overline color={`${TomoTokens.hankoRed}cc`} style={{ marginBottom: 12, letterSpacing: '0.2em' }}>Timeout Punishment</Overline>

        {/* Countdown */}
        <div style={{
          fontFamily: 'JetBrains Mono, monospace', fontWeight: 600,
          fontSize: 64, color: TomoTokens.washiCream, letterSpacing: '-0.02em',
          lineHeight: 1, marginBottom: 24,
          textShadow: `0 0 30px ${TomoTokens.hankoRed}88`,
        }}>4:32</div>

        <p style={{
          fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.6,
          color: `${TomoTokens.washiCream}70`, textAlign: 'center', marginBottom: 36,
        }}>
          Kamu gagal 3x. HP terkunci<br/>untuk istirahat 5 menit.
        </p>

        <SenseiButton variant="ghostRed" style={{ width: '100%', textAlign: 'center', marginBottom: 20 }}>
          Emergency PIN bypass
        </SenseiButton>
        <EmergencyButton />
      </div>
    </div>
  );
}

// Lv 6: Hardcore Lock
function GateLv6() {
  return (
    <div className="tomo-screen" style={{ position: 'relative', background: '#0A0500' }}>
      <div style={{
        position: 'absolute', inset: 0,
        background: 'radial-gradient(ellipse at 50% 30%, rgba(100,15,5,0.18) 0%, #050200 80%)',
      }} />
      <div style={{
        position: 'relative', zIndex: 1, height: '100%',
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 24px',
      }}>
        {/* Header */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 20 }}>
          <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 13, color: `${TomoTokens.hankoRed}dd`, letterSpacing: '0.1em' }}>HARDCORE LOCK</span>
          <span style={{ fontSize: 16 }}>🔒</span>
        </div>

        {/* Card */}
        <GateCard style={{ marginBottom: 16 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
            <Overline color={TomoTokens.sumiLight}>Vocab</Overline>
            <HankoStamp text="N5" size="sm" />
          </div>
          <div style={{ textAlign: 'center', padding: '10px 0 6px' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 52, color: TomoTokens.sumiBlack, lineHeight: 1 }}>食べる</span>
          </div>
          <div style={{ textAlign: 'center', marginBottom: 8 }}>
            <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 16, color: TomoTokens.sumiMid }}>たべる</span>
          </div>
          <div style={{ borderTop: `1px solid ${TomoTokens.sumiLight}22`, paddingTop: 10, textAlign: 'center' }}>
            <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 28, color: `${TomoTokens.sumiLight}88` }}>?</span>
          </div>
        </GateCard>

        <MultiChoice options={['minum', 'makan', 'melihat', 'pergi']} />

        {/* Warning text */}
        <div style={{
          marginTop: 16, padding: '12px 16px',
          border: `1px solid ${TomoTokens.hankoRed}44`,
          borderRadius: 10,
          background: `${TomoTokens.hankoRed}0d`,
          width: '100%', marginBottom: 14,
        }}>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: `${TomoTokens.hankoRed}dd`, margin: 0, lineHeight: 1.55, textAlign: 'center' }}>
            ⚠ NO SKIP. Jawab benar atau gunakan PIN bypass.
          </p>
        </div>

        <SenseiButton variant="ghostRed" small style={{ width: '100%', textAlign: 'center', marginBottom: 14 }}>
          🚨 Emergency PIN bypass
        </SenseiButton>
        <EmergencyButton />
      </div>
    </div>
  );
}

// ─── SCREEN 4: SENSEI CHAT ────────────────────────────────────────────────────
function SenseiChatScreen({ active = false }) {
  const sumiMsg = {
    fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.6,
    color: TomoTokens.sumiDark,
  };
  const washiMsg = {
    fontFamily: 'Manrope, sans-serif', fontSize: 14, lineHeight: 1.6,
    color: TomoTokens.washiCreamLight,
  };

  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column' }}>
        {/* Top bar */}
        <div style={{
          padding: '48px 20px 14px',
          borderBottom: `1px solid ${TomoTokens.sumiLight}22`,
          background: `${TomoTokens.washiCreamLight}ee`,
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            {/* Sensei avatar */}
            <div style={{
              width: 38, height: 38, borderRadius: '50%',
              background: TomoTokens.hankoRed,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              flexShrink: 0,
              boxShadow: `0 2px 8px ${TomoTokens.hankoRed}44`,
            }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 16, color: TomoTokens.washiCreamLight }}>智</span>
            </div>
            <div style={{ flex: 1 }}>
              <div style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 700, fontSize: 15, color: TomoTokens.sumiBlack }}>Tomo Sensei</div>
              <div style={{ display: 'flex', alignItems: 'center', gap: 5 }}>
                <div style={{ width: 6, height: 6, borderRadius: '50%', background: TomoTokens.successMoss }} />
                <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 11, color: TomoTokens.sumiMid }}>Online · On-device</span>
              </div>
            </div>
            <span style={{ fontSize: 16, color: TomoTokens.sumiLight, cursor: 'pointer' }}>✕</span>
          </div>
        </div>

        {/* Context bar */}
        <div style={{
          padding: '9px 20px',
          background: `${TomoTokens.washiCreamDark}88`,
          borderBottom: `1px solid ${TomoTokens.sumiLight}18`,
          display: 'flex', alignItems: 'center', gap: 10,
        }}>
          <div style={{ width: 2, height: 16, background: TomoTokens.hankoRed, borderRadius: 1, flexShrink: 0 }} />
          <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 12, color: TomoTokens.sumiMid }}>
            Konteks: <strong style={{ fontFamily: 'Shippori Mincho, serif', color: TomoTokens.sumiDark }}>食べる</strong>
            <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 11, margin: '0 4px' }}>(たべる)</span>
            — makan
          </span>
        </div>

        {/* Chat messages */}
        <div style={{ flex: 1, overflowY: 'auto', padding: '20px 20px 16px', display: 'flex', flexDirection: 'column', gap: 14 }}>
          {/* Sensei message */}
          <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
            <div style={{ width: 28, height: 28, borderRadius: '50%', background: TomoTokens.hankoRed, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 11, color: '#fff' }}>智</span>
            </div>
            <div style={{
              background: TomoTokens.washiCreamLight,
              borderRadius: '0 16px 16px 16px',
              padding: '12px 15px', maxWidth: '80%',
              boxShadow: '0 2px 8px rgba(26,15,8,0.08)',
            }}>
              <p style={{ ...sumiMsg, margin: 0 }}>
                こんにちは! Tanya apa saja tentang <strong style={{ fontFamily: 'Shippori Mincho, serif' }}>食べる</strong> atau bahasa Jepang lainnya.
              </p>
            </div>
          </div>

          {active && (
            <>
              {/* User message */}
              <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                <div style={{
                  background: TomoTokens.hankoRed,
                  borderRadius: '16px 0 16px 16px',
                  padding: '12px 15px', maxWidth: '75%',
                }}>
                  <p style={{ ...washiMsg, margin: 0 }}>Contoh kalimat 食べる?</p>
                </div>
              </div>

              {/* Sensei reply */}
              <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                <div style={{ width: 28, height: 28, borderRadius: '50%', background: TomoTokens.hankoRed, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 11, color: '#fff' }}>智</span>
                </div>
                <div style={{
                  background: TomoTokens.washiCreamLight,
                  borderRadius: '0 16px 16px 16px',
                  padding: '12px 15px', maxWidth: '82%',
                  boxShadow: '0 2px 8px rgba(26,15,8,0.08)',
                }}>
                  <p style={{ ...sumiMsg, margin: '0 0 6px', fontFamily: 'Shippori Mincho, serif', fontWeight: 500, fontSize: 15 }}>私はパンを食べる。</p>
                  <p style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 11, color: TomoTokens.sumiLight, margin: '0 0 4px' }}>watashi wa pan o taberu</p>
                  <p style={{ ...sumiMsg, margin: 0, color: TomoTokens.sumiMid }}>Saya makan roti.</p>
                </div>
              </div>

              {/* Typing indicator */}
              <div style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                <div style={{ width: 28, height: 28, borderRadius: '50%', background: TomoTokens.hankoRed, display: 'flex', alignItems: 'center', justifyContent: 'center', flexShrink: 0 }}>
                  <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 800, fontSize: 11, color: '#fff' }}>智</span>
                </div>
                <div style={{
                  background: TomoTokens.washiCreamLight,
                  borderRadius: '0 16px 16px 16px',
                  boxShadow: '0 2px 8px rgba(26,15,8,0.08)',
                }}>
                  <TypingDots />
                </div>
              </div>
            </>
          )}
        </div>

        {/* Quick prompts */}
        <div style={{ padding: '0 20px 10px', display: 'flex', gap: 8, overflowX: 'auto' }}>
          {['Contoh kalimat', 'Cara hafal', 'Grammar note', 'Kata terkait'].map(p => (
            <button key={p} style={{
              fontFamily: 'Manrope, sans-serif', fontSize: 12, fontWeight: 500,
              color: TomoTokens.sumiMid, background: `${TomoTokens.washiCreamDark}cc`,
              border: `1px solid ${TomoTokens.sumiLight}33`,
              borderRadius: 999, padding: '7px 14px',
              whiteSpace: 'nowrap', cursor: 'pointer',
              flexShrink: 0,
            }}>{p}</button>
          ))}
        </div>

        {/* Input row */}
        <div style={{
          padding: '0 20px 28px',
          display: 'flex', gap: 10, alignItems: 'center',
        }}>
          <div style={{
            flex: 1, display: 'flex', alignItems: 'center',
            background: `${TomoTokens.washiCreamDark}cc`,
            border: `1px solid ${TomoTokens.sumiLight}30`,
            borderRadius: 999, padding: '12px 18px',
          }}>
            <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: `${TomoTokens.sumiLight}88`, flex: 1 }}>Tanya Sensei...</span>
            <span style={{ fontSize: 16, color: TomoTokens.sumiLight }}>🎤</span>
          </div>
          <div style={{
            width: 44, height: 44, borderRadius: '50%',
            background: TomoTokens.sumiDark,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            cursor: 'pointer', flexShrink: 0,
            boxShadow: '0 3px 12px rgba(26,15,8,0.3)',
          }}>
            <span style={{ color: TomoTokens.washiCream, fontSize: 16 }}>✦</span>
          </div>
        </div>
      </div>
    </div>
  );
}

// ─── SCREEN 5: FOTO SENSEI ────────────────────────────────────────────────────
function FotoSenseiScreen({ state = 'results' }) {
  if (state === 'camera') {
    return (
      <div className="tomo-screen" style={{ position: 'relative', background: '#0d0d0d' }}>
        {/* Camera viewfinder */}
        <div style={{ position: 'absolute', inset: 0, background: 'linear-gradient(160deg, #1a1a1a 0%, #0d0d0d 100%)' }}>
          {/* Frame corners */}
          {[{t:60,l:20},{t:60,r:20},{b:120,l:20},{b:120,r:20}].map((pos,i) => (
            <div key={i} style={{
              position:'absolute',width:24,height:24,...pos,
              borderTop: (pos.t !== undefined) ? `2px solid ${TomoTokens.washiCream}cc` : 'none',
              borderBottom: (pos.b !== undefined) ? `2px solid ${TomoTokens.washiCream}cc` : 'none',
              borderLeft: (pos.l !== undefined) ? `2px solid ${TomoTokens.washiCream}cc` : 'none',
              borderRight: (pos.r !== undefined) ? `2px solid ${TomoTokens.washiCream}cc` : 'none',
            }} />
          ))}
          <div style={{ position:'absolute',top:'50%',left:'50%',transform:'translate(-50%,-50%)',textAlign:'center' }}>
            <p style={{ fontFamily:'Manrope,sans-serif',fontSize:13,color:`${TomoTokens.washiCream}55`,margin:0 }}>Arahkan ke teks Jepang</p>
          </div>
        </div>
        {/* Top bar */}
        <div style={{ position:'absolute',top:0,left:0,right:0,padding:'52px 20px 16px',display:'flex',justifyContent:'space-between',alignItems:'center',background:'linear-gradient(to bottom,rgba(0,0,0,0.5),transparent)' }}>
          <span style={{fontSize:20,color:'#fff',cursor:'pointer'}}>←</span>
          <span style={{fontFamily:'Manrope,sans-serif',fontWeight:600,fontSize:15,color:'#fff'}}>Foto Sensei</span>
          <span style={{fontSize:18,color:'#fff',cursor:'pointer'}}>✕</span>
        </div>
        {/* Bottom controls */}
        <div style={{ position:'absolute',bottom:50,left:0,right:0,display:'flex',justifyContent:'space-around',alignItems:'center',padding:'0 40px' }}>
          <button style={{fontFamily:'Manrope,sans-serif',fontSize:12,color:`${TomoTokens.washiCream}99`,background:'none',border:'none',cursor:'pointer'}}>galeri</button>
          <div style={{width:64,height:64,borderRadius:'50%',background:`${TomoTokens.washiCream}ee`,border:`3px solid ${TomoTokens.washiCream}`,cursor:'pointer',boxShadow:'0 0 20px rgba(255,255,255,0.2)'}} />
          <button style={{fontFamily:'Manrope,sans-serif',fontSize:12,color:`${TomoTokens.washiCream}99`,background:'none',border:'none',cursor:'pointer'}}>⚡ flash</button>
        </div>
      </div>
    );
  }

  // Results state
  return (
    <div className="tomo-screen" style={{ position: 'relative', overflowY: 'auto' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1 }}>
        {/* Top bar */}
        <div style={{ padding: '48px 20px 14px', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <span style={{ fontSize: 18, color: TomoTokens.sumiMid, cursor: 'pointer' }}>←</span>
          <span style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 600, fontSize: 15, color: TomoTokens.sumiDark }}>Hasil Analisis</span>
          <span style={{ fontSize: 16, color: TomoTokens.sumiLight, cursor: 'pointer' }}>✕</span>
        </div>

        {/* Thumbnail */}
        <div style={{ padding: '0 20px 20px' }}>
          <div style={{
            width: 72, height: 72, borderRadius: 12,
            background: `linear-gradient(135deg, ${TomoTokens.sumiMid} 0%, ${TomoTokens.sumiDark} 100%)`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 28,
          }}>🍜</div>
        </div>

        {/* Results sections */}
        <div style={{ padding: '0 20px', display: 'flex', flexDirection: 'column', gap: 24, paddingBottom: 120 }}>
          {/* Section: Text found */}
          <div>
            <SectionHeader label="Teks yang ditemukan" />
            <div style={{ marginTop: 12 }}>
              <div style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 28, color: TomoTokens.sumiBlack, marginBottom: 4 }}>ラーメン特売中</div>
              <div style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 14, color: TomoTokens.sumiMid, letterSpacing: '0.04em' }}>らーめんとくばいちゅう</div>
            </div>
          </div>

          {/* Section: Translation */}
          <div>
            <SectionHeader label="Terjemahan" />
            <div style={{ marginTop: 12 }}>
              <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 16, color: TomoTokens.sumiDark, margin: 0, lineHeight: 1.6 }}>
                Promo ramen sedang berlangsung
              </p>
            </div>
          </div>

          {/* Section: Grammar notes */}
          <div>
            <SectionHeader label="Grammar Notes" />
            <div style={{ marginTop: 12, display: 'flex', flexDirection: 'column', gap: 8 }}>
              {[
                ['中', '(ちゅう/じゅう)', 'sedang berlangsung — suffix untuk status aktif'],
                ['特売', '(とくばい)', 'promo / penjualan khusus'],
              ].map(([k, r, d]) => (
                <div key={k} style={{ display: 'flex', gap: 10, alignItems: 'flex-start' }}>
                  <div style={{ width: 2, height: 16, background: TomoTokens.sumiLight, borderRadius: 1, flexShrink: 0, marginTop: 4 }} />
                  <span>
                    <strong style={{ fontFamily: 'Shippori Mincho, serif', fontSize: 15, color: TomoTokens.sumiBlack }}>{k}</strong>
                    <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 11, color: TomoTokens.sumiLight, margin: '0 5px' }}>{r}</span>
                    <span style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: TomoTokens.sumiMid }}>{d}</span>
                  </span>
                </div>
              ))}
            </div>
          </div>

          {/* Section: Vocab to memorize */}
          <div>
            <SectionHeader label="Vocab untuk dihafal" />
            <div style={{ marginTop: 12, display: 'flex', flexDirection: 'column', gap: 10 }}>
              {[
                ['特売', 'とくばい', 'promo / discount sale'],
                ['中', 'ちゅう', 'sedang / in progress'],
              ].map(([k, r, m]) => (
                <div key={k} className="tomo-card-base tomo-grain" style={{
                  padding: '14px 16px', display: 'flex', justifyContent: 'space-between', alignItems: 'center',
                  boxShadow: '0 2px 12px rgba(26,15,8,0.1)',
                }}>
                  <div>
                    <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 18, color: TomoTokens.sumiBlack, marginRight: 8 }}>{k}</span>
                    <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 12, color: TomoTokens.sumiLight }}>{r}</span>
                    <div style={{ fontFamily: 'Manrope, sans-serif', fontSize: 13, color: TomoTokens.sumiMid, marginTop: 2 }}>{m}</div>
                  </div>
                  <button style={{
                    width: 32, height: 32, borderRadius: '50%',
                    background: TomoTokens.hankoRed, border: 'none',
                    color: '#fff', fontSize: 18, cursor: 'pointer',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    flexShrink: 0, boxShadow: `0 2px 8px ${TomoTokens.hankoRed}44`,
                  }}>+</button>
                </div>
              ))}
            </div>
          </div>

          <SenseiButton style={{ width: '100%', textAlign: 'center' }} variant="secondary">Foto baru</SenseiButton>
        </div>
      </div>
    </div>
  );
}

// Export all screens
Object.assign(window, {
  WelcomeScreen,
  DrillScreen,
  GateLv1, GateLv2, GateLv3, GateLv4, GateLv5, GateLv6,
  SenseiChatScreen,
  FotoSenseiScreen,
});

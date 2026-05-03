
// ─── Tomo Sensei — Animations Showcase ──────────────────────────────────────

const { useState: useStateAn, useEffect: useEffectAn } = React;
const Ta = window.TomoTokens;
const { WashiBg, HankoStamp, Overline } = window;

// ─── 1. CARD FLIP ANIMATION ─────────────────────────────────────────────────
function CardFlipAnim() {
  const [flipped, setFlipped] = useStateAn(false);

  useEffectAn(() => {
    const id = setInterval(() => setFlipped(f => !f), 2400);
    return () => clearInterval(id);
  }, []);

  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        <Overline color={Ta.hankoRed} style={{ marginBottom: 20 }}>Card Flip · 600ms</Overline>
        {/* Flip container */}
        <div style={{ width: '100%', perspective: 1200 }}>
          <div style={{
            position: 'relative', width: '100%', minHeight: 320,
            transformStyle: 'preserve-3d',
            transition: 'transform 600ms cubic-bezier(0.4, 0.0, 0.2, 1)',
            transform: flipped ? 'rotateY(180deg)' : 'rotateY(0deg)',
          }}>
            {/* Front */}
            <div className="tomo-card-base tomo-grain tomo-card-shadow"
              style={{ position: 'absolute', inset: 0, backfaceVisibility: 'hidden', padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 16 }}>
                <Overline color={Ta.sumiLight}>Vocab</Overline>
                <HankoStamp text="N5" size="sm" />
              </div>
              <div style={{ textAlign: 'center', padding: '40px 0 32px' }}>
                <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 72, color: Ta.sumiBlack }}>食べる</span>
              </div>
              <div style={{ borderTop: `1px solid ${Ta.sumiLight}22`, paddingTop: 14, textAlign: 'center' }}>
                <Overline color={`${Ta.sumiLight}aa`}>tap untuk arti</Overline>
              </div>
            </div>
            {/* Back */}
            <div className="tomo-card-base tomo-grain tomo-card-shadow"
              style={{ position: 'absolute', inset: 0, backfaceVisibility: 'hidden', transform: 'rotateY(180deg)', padding: '24px' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 12 }}>
                <Overline color={Ta.sumiLight}>Vocab</Overline>
                <HankoStamp text="N5" size="sm" />
              </div>
              <div style={{ marginBottom: 4 }}>
                <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 38, color: Ta.sumiBlack }}>食べる</span>
              </div>
              <div style={{ marginBottom: 12 }}>
                <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 18, color: Ta.sumiMid, letterSpacing: '0.06em' }}>たべる</span>
              </div>
              <div style={{ width: 36, height: 2, background: Ta.hankoRed, marginBottom: 14, borderRadius: 1 }} />
              <div style={{ marginBottom: 18 }}>
                <span style={{ fontFamily: 'Manrope, sans-serif', fontWeight: 500, fontSize: 22, color: Ta.sumiDark }}>makan</span>
              </div>
              <div style={{ background: `${Ta.washiCreamDark}88`, borderRadius: 10, padding: '10px 12px', fontFamily: 'Manrope, sans-serif', fontSize: 11, color: Ta.sumiMid }}>
                Ichidan verb. 食べます / 食べない
              </div>
            </div>
          </div>
        </div>
        <p style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Ta.sumiLight, marginTop: 32, letterSpacing: '0.1em' }}>cubic-bezier(0.4, 0.0, 0.2, 1)</p>
      </div>
    </div>
  );
}

// ─── 2. HANKO STAMP DROP ────────────────────────────────────────────────────
function HankoStampAnim() {
  const [tick, setTick] = useStateAn(0);

  useEffectAn(() => {
    const id = setInterval(() => setTick(t => t + 1), 2200);
    return () => clearInterval(id);
  }, []);

  // Keyframes injected once
  useEffectAn(() => {
    if (document.getElementById('hanko-anim-keys')) return;
    const s = document.createElement('style');
    s.id = 'hanko-anim-keys';
    s.textContent = `
      @keyframes hankoDrop {
        0% { transform: scale(2.5) rotate(-20deg); opacity: 0; }
        45% { transform: scale(0.85) rotate(-2deg); opacity: 1; }
        60% { transform: scale(1.05) rotate(-4deg); opacity: 1; }
        100% { transform: scale(1) rotate(-4deg); opacity: 1; }
      }
      @keyframes hankoBleed {
        0%, 100% { box-shadow: 0 0 0 0 rgba(220, 77, 58, 0); }
        50% { box-shadow: 0 0 0 4px rgba(220, 77, 58, 0.15); }
      }
    `;
    document.head.appendChild(s);
  }, []);

  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        <Overline color={Ta.hankoRed} style={{ marginBottom: 28 }}>Hanko Stamp Drop · 450ms</Overline>

        {/* Document mockup */}
        <div className="tomo-card-base tomo-grain tomo-card-shadow" style={{ width: '100%', padding: 28, marginBottom: 32, position: 'relative' }}>
          <div style={{ marginBottom: 16 }}>
            <Overline color={Ta.sumiLight}>Hari ke 47</Overline>
          </div>
          <h3 style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 22, color: Ta.sumiBlack, margin: '0 0 8px' }}>
            Streak hari ini
          </h3>
          <p style={{ fontFamily: 'Manrope, sans-serif', fontSize: 14, color: Ta.sumiMid, margin: 0 }}>
            20 / 20 kartu · 100% akurat
          </p>
          {/* Stamp lands here */}
          <div key={tick} style={{
            position: 'absolute', bottom: 16, right: 16,
            animation: 'hankoDrop 450ms cubic-bezier(0.34, 1.56, 0.64, 1) forwards',
            transformOrigin: 'center',
          }}>
            <HankoStamp text="✓" size="md" rotation={0} />
          </div>
        </div>

        <p style={{ fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: Ta.sumiLight, letterSpacing: '0.1em' }}>cubic-bezier(0.34, 1.56, 0.64, 1) · spring</p>
      </div>
    </div>
  );
}

// ─── 3. GATE APPEARANCE ─────────────────────────────────────────────────────
function GateAppearAnim() {
  const [tick, setTick] = useStateAn(0);

  useEffectAn(() => {
    const id = setInterval(() => setTick(t => t + 1), 2800);
    return () => clearInterval(id);
  }, []);

  useEffectAn(() => {
    if (document.getElementById('gate-anim-keys')) return;
    const s = document.createElement('style');
    s.id = 'gate-anim-keys';
    s.textContent = `
      @keyframes gateOverlay {
        0% { opacity: 0; backdrop-filter: blur(0px); }
        100% { opacity: 1; backdrop-filter: blur(8px); }
      }
      @keyframes gateCardRise {
        0% { transform: translateY(40px) scale(0.94); opacity: 0; }
        60% { opacity: 1; }
        100% { transform: translateY(0) scale(1); opacity: 1; }
      }
      @keyframes gateLabel {
        0%, 30% { opacity: 0; transform: translateY(-8px); }
        100% { opacity: 1; transform: translateY(0); }
      }
    `;
    document.head.appendChild(s);
  }, []);

  return (
    <div className="tomo-screen" style={{ position: 'relative', background: '#1a1a2e', overflow: 'hidden' }}>
      {/* Fake home screen behind */}
      <div style={{
        position: 'absolute', inset: 0,
        background: 'linear-gradient(160deg, #1a1a2e 0%, #16213e 50%, #0f3460 100%)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        flexDirection: 'column', gap: 14,
      }}>
        <div style={{ width: 64, height: 64, borderRadius: '50%', background: '#ffffff22' }} />
        <div style={{ width: 140, height: 12, borderRadius: 5, background: '#ffffff18' }} />
        <div style={{ width: 100, height: 10, borderRadius: 5, background: '#ffffff12' }} />
      </div>

      {/* Gate overlay */}
      <div key={tick} style={{
        position: 'absolute', inset: 0,
        background: 'rgba(15, 10, 6, 0.85)',
        animation: 'gateOverlay 350ms ease-out forwards',
        zIndex: 5,
      }} />

      <div key={`c-${tick}`} style={{
        position: 'absolute', inset: 0, zIndex: 6,
        display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
        padding: '0 28px',
      }}>
        <div style={{
          marginBottom: 12, opacity: 0,
          animation: 'gateLabel 400ms ease-out 200ms forwards',
        }}>
          <Overline color={`${Ta.washiCream}55`}>TikTok Blocked</Overline>
        </div>

        <div style={{
          width: '100%', opacity: 0,
          animation: 'gateCardRise 500ms cubic-bezier(0.0, 0.0, 0.2, 1) 100ms forwards',
        }}>
          <div className="tomo-card-base tomo-grain tomo-card-shadow" style={{ padding: '24px' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: 14 }}>
              <Overline color={Ta.sumiLight}>Vocab</Overline>
              <HankoStamp text="N5" size="sm" />
            </div>
            <div style={{ textAlign: 'center', padding: '20px 0' }}>
              <span style={{ fontFamily: 'Shippori Mincho, serif', fontWeight: 600, fontSize: 64, color: Ta.sumiBlack }}>食べる</span>
            </div>
            <div style={{ textAlign: 'center', marginBottom: 14 }}>
              <span style={{ fontFamily: 'Zen Kaku Gothic Antique, sans-serif', fontSize: 16, color: Ta.sumiMid, letterSpacing: '0.06em' }}>たべる</span>
            </div>
          </div>
        </div>

        <div style={{
          marginTop: 28, position: 'absolute', bottom: 60,
          fontFamily: 'JetBrains Mono, monospace', fontSize: 10, color: `${Ta.washiCream}55`, letterSpacing: '0.1em',
        }}>
          overlay 350ms · card 500ms · label 400ms
        </div>
      </div>
    </div>
  );
}

// ─── 4. STREAK FIRE PULSE ───────────────────────────────────────────────────
function StreakPulseAnim() {
  useEffectAn(() => {
    if (document.getElementById('streak-anim-keys')) return;
    const s = document.createElement('style');
    s.id = 'streak-anim-keys';
    s.textContent = `
      @keyframes flamePulse {
        0%, 100% { transform: scale(1); filter: drop-shadow(0 0 8px rgba(220, 77, 58, 0.4)); }
        50% { transform: scale(1.08); filter: drop-shadow(0 0 16px rgba(220, 77, 58, 0.7)); }
      }
      @keyframes flameRotate {
        0%, 100% { transform: rotate(-2deg); }
        50% { transform: rotate(2deg); }
      }
      @keyframes counterUp {
        0%, 100% { transform: translateY(0); }
        50% { transform: translateY(-2px); }
      }
    `;
    document.head.appendChild(s);
  }, []);

  return (
    <div className="tomo-screen" style={{ position: 'relative' }}>
      <WashiBg />
      <div style={{ position: 'relative', zIndex: 1, height: '100%', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: '0 28px' }}>
        <Overline color={Ta.hankoRed} style={{ marginBottom: 32 }}>Streak Pulse · 1.6s loop</Overline>

        {/* Big flame */}
        <div style={{
          fontSize: 100,
          animation: 'flamePulse 1.6s ease-in-out infinite, flameRotate 3.2s ease-in-out infinite',
          marginBottom: 20,
        }}>🔥</div>

        {/* Counter */}
        <div style={{ textAlign: 'center', marginBottom: 8 }}>
          <span style={{
            fontFamily: 'Shippori Mincho, serif', fontWeight: 700, fontSize: 72,
            color: Ta.sumiBlack, lineHeight: 1, letterSpacing: '-0.04em',
            display: 'inline-block',
            animation: 'counterUp 1.6s ease-in-out infinite',
          }}>47</span>
        </div>

        <Overline color={Ta.sumiMid} style={{ marginBottom: 24, letterSpacing: '0.2em' }}>HARI BERTURUT-TURUT</Overline>

        <p style={{ fontFamily: 'Shippori Mincho, serif', fontStyle: 'italic', fontSize: 15, color: Ta.sumiMid, textAlign: 'center', maxWidth: 240 }}>
          "Jangan biarkan padam."
        </p>
      </div>
    </div>
  );
}

Object.assign(window, {
  CardFlipAnim, HankoStampAnim, GateAppearAnim, StreakPulseAnim,
});

import { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence, useInView } from "framer-motion";
import {
  ArrowRight, Shuffle, HelpCircle, Heart, Trophy,
  Clock, ChevronDown, PenLine, Code2, HandHeart,
} from "lucide-react";
import { useTranslation } from "react-i18next";
import AuthModal from "@/components/auth/AuthModal";

// ─────────────────────────────────────────────────────────────────────────────
// DATA
// ─────────────────────────────────────────────────────────────────────────────

const LIVE_FEED = [
  { user: "axelr",    val: "940 pts",    mode: "Blitz",    ago: "2m" },
  { user: "sophiabe", val: "rank #12",   mode: "Classic",  ago: "5m" },
  { user: "mrquiz",   val: "1 240 pts",  mode: "Rush",     ago: "8m" },
  { user: "lena_k",   val: "streak 7d",  mode: "",         ago: "12m" },
  { user: "devhub",   val: "780 pts",    mode: "Blitz",    ago: "15m" },
  { user: "camille7", val: "rank #3",    mode: "Classic",  ago: "18m" },
  { user: "noxvoid",  val: "1 080 pts",  mode: "Survival", ago: "22m" },
  { user: "julek",    val: "860 pts",    mode: "Rush",     ago: "27m" },
];

// ─────────────────────────────────────────────────────────────────────────────
// INTERACTIVE QCM DEMO CARD
// ─────────────────────────────────────────────────────────────────────────────

function GameCard({ question, answers }) {
  const [picked, setPicked] = useState(null);
  const CORRECT = 1;
  const revealed = picked !== null;

  return (
    <div className="relative w-full max-w-[520px] select-none">
      <div className="absolute -inset-4 rounded-3xl bg-primary/15 blur-2xl pointer-events-none" />
      <div className="relative rounded-2xl border border-white/[0.09] bg-[#111]/90 backdrop-blur-sm overflow-hidden shadow-2xl shadow-black/70">

        <div className="flex items-center justify-between px-5 py-4 border-b border-white/[0.06] bg-black/50">
          <div className="flex gap-0.5">
            {[0, 1, 2].map(i => (
              <Heart key={i} className="h-4 w-4 fill-red-500 text-red-500" />
            ))}
          </div>
          <div className="flex items-center gap-1.5 font-mono text-sm text-white/40">
            <Clock className="h-3.5 w-3.5" />
            <span className="font-black text-white">2:14</span>
          </div>
          <div className="flex items-center gap-1.5 bg-primary/15 border border-primary/20 rounded-full px-3 py-1">
            <Trophy className="h-3.5 w-3.5 text-primary" />
            <span className="text-sm font-black text-primary">360</span>
          </div>
        </div>

        <div className="px-6 pt-5 pb-3">
          <div className="flex items-center gap-2 mb-3">
            <span className="text-[10px] font-black tracking-[0.2em] text-white/25">Q05</span>
            <span className="text-[10px] font-black tracking-wider text-yellow-400/70 border border-yellow-400/20 bg-yellow-400/10 rounded px-2 py-0.5">
              MEDIUM
            </span>
          </div>
          <p className="text-base font-semibold leading-snug text-white/90">
            {question}
          </p>
        </div>

        <div className="px-5 pb-5 space-y-2">
          {answers.map((a, i) => {
            const sel = picked === i;
            const cor = i === CORRECT;
            let card = "border-white/[0.06] bg-white/[0.02] hover:border-white/20";
            let badge = "bg-white/[0.07] text-white/40";
            if (revealed) {
              if (cor)      { card = "border-emerald-500/40 bg-emerald-500/10";  badge = "bg-emerald-500/25 text-emerald-300"; }
              else if (sel) { card = "border-red-500/30 bg-red-500/[0.07]";      badge = "bg-red-500/20 text-red-300"; }
            } else if (sel) { card = "border-primary/40 bg-primary/10";          badge = "bg-primary/20 text-primary"; }
            return (
              <button
                key={i}
                onClick={() => !revealed && setPicked(i)}
                className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl border text-left transition-all duration-200 ${card}`}
              >
                <span className={`w-7 h-7 shrink-0 rounded-lg text-xs font-black flex items-center justify-center ${badge}`}>
                  {a.l}
                </span>
                <span className="text-sm font-medium text-white/75">{a.text}</span>
              </button>
            );
          })}
        </div>
      </div>

      <AnimatePresence>
        {picked === CORRECT && (
          <motion.div
            initial={{ opacity: 0, scale: 0.6, y: 0 }}
            animate={{ opacity: [0, 1, 1, 0], scale: [0.6, 1.1, 1, 0.9], y: [0, -20, -50, -80] }}
            transition={{ duration: 1.8 }}
            className="absolute -top-2 right-4 bg-emerald-500 text-white text-[11px] font-black px-3 py-1 rounded-full shadow-lg pointer-events-none"
          >
            +20 pts
          </motion.div>
        )}
      </AnimatePresence>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// LIVE ACTIVITY BAR
// ─────────────────────────────────────────────────────────────────────────────

function LiveBar() {
  return (
    <div className="relative overflow-hidden bg-[#050505] border-y border-white/[0.05] py-3">
      <div className="absolute left-0 inset-y-0 z-10 flex items-center pl-4 pr-14 bg-gradient-to-r from-[#050505] via-[#050505] to-transparent pointer-events-none">
        <span className="flex items-center gap-2 text-[10px] font-black tracking-[0.25em] uppercase text-red-400">
          <span className="h-1.5 w-1.5 rounded-full bg-red-400 animate-pulse" />
          LIVE
        </span>
      </div>
      <div className="absolute right-0 inset-y-0 z-10 w-20 bg-gradient-to-l from-[#050505] to-transparent pointer-events-none" />
      <motion.div
        animate={{ x: ["0%", "-50%"] }}
        transition={{ duration: 44, repeat: Infinity, ease: "linear" }}
        className="flex gap-12 whitespace-nowrap pl-28 font-mono"
      >
        {[...LIVE_FEED, ...LIVE_FEED].map((f, i) => (
          <span key={i} className="flex items-center gap-3 text-[11px] shrink-0">
            <span className="text-white/25">{f.ago}</span>
            <span className="text-white/70 font-semibold">{f.user}</span>
            <span className="text-primary font-black">{f.val}</span>
            {f.mode && (
              <span className="text-[10px] text-white/25 border border-white/[0.07] rounded px-1.5 py-0.5">
                {f.mode}
              </span>
            )}
          </span>
        ))}
      </motion.div>
    </div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// FAQ ITEM
// ─────────────────────────────────────────────────────────────────────────────

function FaqItem({ q, a, index }) {
  const [open, setOpen] = useState(false);
  const ref = useRef(null);
  const inView = useInView(ref, { once: true });

  return (
    <motion.div
      ref={ref}
      initial={{ opacity: 0 }}
      animate={inView ? { opacity: 1 } : {}}
      transition={{ delay: index * 0.06, duration: 0.45 }}
      className="border-b border-white/[0.07] last:border-0"
    >
      <button
        onClick={() => setOpen(v => !v)}
        className="w-full flex items-start gap-6 py-6 text-left group"
      >
        <span className="text-[11px] font-black tabular-nums text-white/20 w-6 shrink-0 pt-0.5 group-hover:text-primary transition-colors">
          {String(index + 1).padStart(2, "0")}
        </span>
        <span className="flex-1 text-sm font-semibold text-white/65 group-hover:text-white/90 transition-colors leading-relaxed">
          {q}
        </span>
        <motion.span
          animate={{ rotate: open ? 45 : 0 }}
          transition={{ duration: 0.2 }}
          className="shrink-0 mt-0.5"
        >
          <ChevronDown className={`h-4 w-4 transition-colors ${open ? "text-primary" : "text-white/20"}`} />
        </motion.span>
      </button>
      <AnimatePresence initial={false}>
        {open && (
          <motion.div
            initial={{ height: 0, opacity: 0 }}
            animate={{ height: "auto", opacity: 1 }}
            exit={{ height: 0, opacity: 0 }}
            transition={{ duration: 0.28, ease: [0.22, 1, 0.36, 1] }}
            className="overflow-hidden"
          >
            <p className="pl-12 pb-6 text-sm text-white/38 leading-relaxed">{a}</p>
          </motion.div>
        )}
      </AnimatePresence>
    </motion.div>
  );
}

// ─────────────────────────────────────────────────────────────────────────────
// PAGE
// ─────────────────────────────────────────────────────────────────────────────

const EASE_OUT_EXPO = [0.16, 1, 0.3, 1];

export default function Home() {
  const { t } = useTranslation("home");
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState("signup");
  const heroRef = useRef(null);

  const open = (mode) => { setAuthMode(mode); setAuthOpen(true); };

  const demoAnswers = [
    { l: "A", text: t("home.demo.answerA") },
    { l: "B", text: t("home.demo.answerB") },
    { l: "C", text: t("home.demo.answerC") },
    { l: "D", text: t("home.demo.answerD") },
  ];

  const faqItems = [
    { question: t("home.faq.q1.question"), answer: t("home.faq.q1.answer") },
    { question: t("home.faq.q2.question"), answer: t("home.faq.q2.answer") },
    { question: t("home.faq.q3.question"), answer: t("home.faq.q3.answer") },
    { question: t("home.faq.q4.question"), answer: t("home.faq.q4.answer") },
    { question: t("home.faq.q5.question"), answer: t("home.faq.q5.answer") },
  ];

  const contributeItems = [
    { Icon: PenLine, title: t("home.contribute.contentTitle"), body: t("home.contribute.contentBody") },
    { Icon: Code2,   title: t("home.contribute.codeTitle"),    body: t("home.contribute.codeBody") },
    { Icon: HandHeart, title: t("home.contribute.supportTitle"), body: t("home.contribute.supportBody") },
  ];

  // Cursor-tracking spotlight — CSS vars only, zero re-renders
  useEffect(() => {
    const el = heroRef.current;
    if (!el) return;
    const fn = (e) => {
      const r = el.getBoundingClientRect();
      el.style.setProperty("--cx", `${e.clientX - r.left}px`);
      el.style.setProperty("--cy", `${e.clientY - r.top}px`);
    };
    el.addEventListener("mousemove", fn);
    return () => el.removeEventListener("mousemove", fn);
  }, []);

  return (
    <div className="bg-[#080808] text-white overflow-x-hidden">

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  HERO                                                              */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <section
        ref={heroRef}
        className="relative min-h-screen flex flex-col"
        style={{
          background:
            "radial-gradient(700px circle at var(--cx,35%) var(--cy,40%), oklch(0.62 0.19 260/.08) 0%, transparent 60%), #080808",
        }}
      >
        {/* Dot matrix */}
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            backgroundImage: "radial-gradient(rgba(255,255,255,.055) 1px,transparent 1px)",
            backgroundSize: "36px 36px",
          }}
        />
        {/* Bottom mist */}
        <div className="pointer-events-none absolute bottom-0 inset-x-0 h-64 bg-gradient-to-t from-[#080808] to-transparent" />

        <div className="relative flex-1 flex flex-col justify-center px-6 sm:px-10 lg:px-20 pt-24 pb-16 lg:pt-28 lg:pb-24 max-w-[1400px] mx-auto w-full">
          <div className="max-w-3xl xl:max-w-[52%]">

            {/* Live indicator */}
            <motion.p
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ duration: 0.4 }}
              className="mb-4 flex items-center gap-2 text-[10px] font-mono tracking-[0.18em] text-white/30 uppercase"
            >
              <span className="h-1.5 w-1.5 rounded-full bg-emerald-400 animate-pulse shrink-0" />
              {t("home.live", { count: "2 845" })}
            </motion.p>

            {/* Headline */}
            <h1 className="text-[clamp(2rem,5.5vw,5rem)] font-black leading-[0.9] tracking-[-0.03em]">
              {[
                { key: "home.hero.line1", accent: false },
                { key: "home.hero.line2", accent: false },
                { key: "home.hero.line3", accent: true },
              ].map(({ key, accent }, i) => (
                <div key={key} className="overflow-hidden">
                  <motion.div
                    initial={{ y: "110%" }}
                    animate={{ y: "0%" }}
                    transition={{ delay: 0.15 + i * 0.12, duration: 0.7, ease: EASE_OUT_EXPO }}
                    className={accent ? "text-primary" : ""}
                  >
                    {t(key)}
                  </motion.div>
                </div>
              ))}
            </h1>

            {/* Subtext */}
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.82, duration: 0.5 }}
              className="mt-8 max-w-[520px] space-y-2"
            >
              <p className="text-[15px] font-semibold text-white/70 leading-snug">
                {t("home.hero.tagline")}
              </p>
              <p className="text-sm text-white/38 leading-relaxed">
                {t("home.hero.description")}
              </p>
            </motion.div>

            {/* CTAs */}
            <motion.div
              initial={{ opacity: 0, y: 10 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.97, duration: 0.45 }}
              className="mt-10 flex flex-wrap gap-3"
            >
              <button
                onClick={() => open("signup")}
                className="group inline-flex items-center gap-2 bg-primary rounded-full px-8 py-4 text-sm font-bold text-white shadow-xl shadow-primary/20 hover:brightness-110 hover:shadow-primary/35 transition-all"
              >
                {t("home.hero.ctaPrimary")}
                <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
              </button>
              <button
                onClick={() => open("login")}
                className="inline-flex items-center px-8 py-4 rounded-full text-sm font-semibold text-white/40 border border-white/[0.09] hover:border-white/25 hover:text-white/70 transition-all"
              >
                {t("home.hero.ctaSecondary")}
              </button>
            </motion.div>

            {/* Stats strip */}
            <motion.div
              initial={{ opacity: 0 }}
              animate={{ opacity: 1 }}
              transition={{ delay: 1.2, duration: 0.7 }}
              className="mt-12 pt-8 border-t border-white/[0.07] grid grid-cols-2 sm:grid-cols-4 gap-6 sm:gap-0 sm:divide-x sm:divide-white/[0.07]"
            >
              {[
                { val: "2.8K+", labelKey: "home.hero.statPlayers" },
                { val: "50K+",  labelKey: "home.hero.statQuestions" },
                { val: "1.2M",  labelKey: "home.hero.statGames" },
                { val: "30+",   labelKey: "home.hero.statCountries" },
              ].map(s => (
                <div key={s.labelKey} className="sm:px-6 first:pl-0 last:pr-0">
                  <p className="text-2xl font-black tabular-nums">{s.val}</p>
                  <p className="text-[10px] uppercase tracking-[0.22em] text-white/25 mt-1">{t(s.labelKey)}</p>
                </div>
              ))}
            </motion.div>
          </div>

          {/* Floating game card — xl+ screens */}
          <div className="absolute right-12 top-1/2 -translate-y-1/2 hidden xl:block">
            <motion.div
              initial={{ opacity: 0, x: 60 }}
              animate={{ opacity: 1, x: 0 }}
              transition={{ delay: 0.65, duration: 0.9, ease: EASE_OUT_EXPO }}
              style={{
                rotateY: -12,
                rotateX: 5,
                transformPerspective: 1200,
                filter: "drop-shadow(0px 40px 80px rgba(0,0,0,0.85))",
              }}
            >
              <GameCard question={t("home.demo.question")} answers={demoAnswers} />
            </motion.div>
          </div>
        </div>
      </section>

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  LIVE BAR                                                          */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <LiveBar />

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  GAMES BENTO                                                       */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <section className="py-28 px-6 sm:px-10 lg:px-20 max-w-[1400px] mx-auto">

        <motion.div
          initial={{ opacity: 0, y: 22 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.55 }}
          className="mb-14"
        >
          <p className="text-[11px] font-black uppercase tracking-[0.28em] text-white/25 mb-4">
            {t("home.games.sectionLabel")}
          </p>
          <h2 className="text-[clamp(2.5rem,6vw,6rem)] font-black leading-[0.9] tracking-tight">
            {t("home.games.headline1")}<br />
            <span className="text-white/25">{t("home.games.headline2")}</span>
          </h2>
        </motion.div>

        <div className="grid grid-cols-1 lg:grid-cols-3 gap-4">

          {/* ── QCM (col-span-2) ─────────────────────────────────────────── */}
          <motion.div
            initial={{ opacity: 0, y: 28 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ duration: 0.55, ease: EASE_OUT_EXPO }}
            onClick={() => open("signup")}
            className="lg:col-span-2 group relative rounded-3xl border border-white/[0.07] bg-white/[0.015] p-8 lg:p-10 cursor-pointer overflow-hidden hover:border-primary/30 transition-all duration-500"
          >
            <div className="absolute inset-0 bg-gradient-to-br from-primary/[0.05] via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />

            <div className="relative flex flex-col sm:flex-row gap-10">
              <div className="flex-1 min-w-0">
                <div className="flex items-center gap-3 mb-6">
                  <div className="h-12 w-12 rounded-2xl bg-primary/10 border border-primary/20 flex items-center justify-center shrink-0">
                    <HelpCircle className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h3 className="text-4xl font-black leading-none tracking-tight">QCM</h3>
                    <p className="text-[10px] text-white/25 uppercase tracking-widest mt-0.5">
                      {t("home.games.qcm.tagline")}
                    </p>
                  </div>
                </div>

                <p className="text-sm text-white/45 leading-relaxed mb-8 max-w-xs">
                  {t("home.games.qcm.description")}
                </p>

                <div className="space-y-2">
                  {[
                    { name: "Blitz",   time: "2 min",  pts: "+15 pts/q", noteKey: "home.games.qcm.blitzNote" },
                    { name: "Rush",    time: "5 min",  pts: "+10 pts/q", noteKey: "home.games.qcm.rushNote" },
                    { name: "Classic", time: "10 min", pts: "+8 pts/q",  noteKey: "home.games.qcm.classicNote" },
                  ].map(m => (
                    <div
                      key={m.name}
                      className="flex items-center gap-4 px-4 py-3 rounded-xl bg-white/[0.03] border border-white/[0.04] hover:border-white/10 transition-colors"
                    >
                      <span className="text-xs font-black w-14 text-white">{m.name}</span>
                      <span className="text-xs text-white/30 flex-1">{t(m.noteKey)}</span>
                      <span className="text-xs text-white/35 font-mono">{m.time}</span>
                      <span className="text-xs text-primary font-black">{m.pts}</span>
                    </div>
                  ))}
                </div>
              </div>

              <div className="sm:w-[260px] shrink-0 flex items-center justify-center py-2">
                <GameCard question={t("home.demo.question")} answers={demoAnswers} />
              </div>
            </div>

            <div className="relative mt-8 flex items-center gap-2 text-xs text-white/25 group-hover:text-white/60 transition-colors">
              {t("home.games.qcm.playNow")}
              <ArrowRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-1" />
            </div>
          </motion.div>

          {/* ── SMATCH (col-span-1) ───────────────────────────────────────── */}
          <motion.div
            initial={{ opacity: 0, y: 28 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.1, duration: 0.55, ease: EASE_OUT_EXPO }}
            onClick={() => open("signup")}
            className="group relative rounded-3xl border border-white/[0.07] bg-white/[0.015] p-8 lg:p-10 cursor-pointer overflow-hidden hover:border-white/20 transition-all duration-500 flex flex-col"
          >
            <div className="absolute inset-0 bg-gradient-to-b from-violet-500/[0.05] to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />

            <div className="relative flex-1">
              <div className="flex items-start justify-between mb-8">
                <div className="h-12 w-12 rounded-2xl bg-white/[0.04] border border-white/[0.08] flex items-center justify-center">
                  <Shuffle className="h-5 w-5 text-white/55" />
                </div>
                <span className="text-[10px] font-black uppercase tracking-wider text-primary border border-primary/20 bg-primary/10 rounded-full px-2.5 py-1">
                  NEW
                </span>
              </div>

              <h3 className="text-4xl font-black leading-none tracking-tight mb-1">SMATCH</h3>
              <p className="text-[10px] text-white/25 uppercase tracking-widest mb-6">{t("home.games.smatch.tagline")}</p>

              <p className="text-sm text-white/45 leading-relaxed mb-10">
                {t("home.games.smatch.description")}
              </p>

              <div className="divide-y divide-white/[0.05]">
                {[
                  { name: "Time Attack", time: "90s",  pts: "+10 pts" },
                  { name: "Zen",         time: "∞",    pts: "+5 pts"  },
                  { name: "Survival",    time: "120s", pts: "+15 pts" },
                ].map(m => (
                  <div key={m.name} className="flex items-center justify-between py-3.5">
                    <span className="text-xs font-semibold text-white/65">{m.name}</span>
                    <div className="flex items-center gap-3">
                      <span className="text-xs font-mono text-white/30">{m.time}</span>
                      <span className="text-xs font-black text-white/45">{m.pts}</span>
                    </div>
                  </div>
                ))}
              </div>
            </div>

            <div className="relative mt-8 flex items-center gap-2 text-xs text-white/25 group-hover:text-white/60 transition-colors">
              {t("home.games.smatch.discover")}
              <ArrowRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-1" />
            </div>
          </motion.div>
        </div>
      </section>

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  MANIFESTO / CTA                                                   */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <section className="relative py-36 px-6 sm:px-10 lg:px-20 overflow-hidden border-y border-white/[0.06]">
        <div
          className="pointer-events-none absolute inset-0"
          style={{
            backgroundImage: "radial-gradient(rgba(255,255,255,.016) 1px,transparent 1px)",
            backgroundSize: "24px 24px",
          }}
        />
        <div className="pointer-events-none absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[800px] h-[400px] rounded-full bg-primary/[0.06] blur-[100px]" />

        <div className="relative max-w-4xl mx-auto">
          <motion.p
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ duration: 0.5 }}
            className="text-[11px] font-black uppercase tracking-[0.28em] text-white/20 mb-10"
          >
            {t("home.manifesto.label")}
          </motion.p>

          <div className="overflow-hidden">
            <motion.h2
              initial={{ y: "80%", opacity: 0 }}
              whileInView={{ y: "0%", opacity: 1 }}
              viewport={{ once: true }}
              transition={{ duration: 0.85, ease: EASE_OUT_EXPO }}
              className="text-[clamp(1.85rem,4.5vw,4rem)] font-black leading-[1.07] tracking-[-0.02em]"
            >
              {t("home.manifesto.quote")}{" "}
              <span className="text-white/30">{t("home.manifesto.quoteLight")}</span>
            </motion.h2>
          </div>

          <motion.p
            initial={{ opacity: 0 }}
            whileInView={{ opacity: 1 }}
            viewport={{ once: true }}
            transition={{ delay: 0.28, duration: 0.6 }}
            className="mt-8 text-white/40 text-[15px] leading-relaxed max-w-md"
          >
            {t("home.manifesto.description")}
          </motion.p>

          <motion.div
            initial={{ opacity: 0, y: 14 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            transition={{ delay: 0.42, duration: 0.5 }}
            className="mt-12 flex flex-wrap gap-4"
          >
            <button
              onClick={() => open("signup")}
              className="group inline-flex items-center gap-2 bg-white text-black rounded-full px-10 py-4 text-sm font-black hover:bg-white/90 transition-all shadow-2xl shadow-white/10"
            >
              {t("home.manifesto.ctaPrimary")}
              <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
            </button>
            <button
              onClick={() => open("login")}
              className="inline-flex items-center gap-2 px-8 py-4 rounded-full text-sm font-semibold text-white/40 border border-white/[0.1] hover:text-white/70 hover:border-white/25 transition-all"
            >
              {t("home.manifesto.ctaSecondary")}
            </button>
          </motion.div>
        </div>
      </section>

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  CONTRIBUTE                                                        */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <section className="py-28 px-6 sm:px-10 lg:px-20 max-w-[1400px] mx-auto">

        <motion.div
          initial={{ opacity: 0, y: 22 }}
          whileInView={{ opacity: 1, y: 0 }}
          viewport={{ once: true }}
          transition={{ duration: 0.55 }}
          className="mb-16"
        >
          <p className="text-[11px] font-black uppercase tracking-[0.28em] text-white/25 mb-4">
            {t("home.contribute.sectionLabel")}
          </p>
          <h2 className="text-[clamp(2.5rem,6vw,6rem)] font-black leading-[0.9] tracking-tight">
            {t("home.contribute.headline1")}<br />
            <span className="text-white/25">{t("home.contribute.headline2")}</span>
          </h2>
        </motion.div>

        {/* 3 contribution spaces */}
        <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 mb-14">
          {contributeItems.map(({ Icon, title, body }, i) => (
            <motion.div
              key={i}
              initial={{ opacity: 0, y: 24 }}
              whileInView={{ opacity: 1, y: 0 }}
              viewport={{ once: true }}
              transition={{ delay: i * 0.1, duration: 0.5, ease: EASE_OUT_EXPO }}
              className="rounded-3xl border border-white/[0.07] bg-white/[0.015] p-8"
            >
              <div className="h-10 w-10 rounded-2xl bg-white/[0.04] border border-white/[0.08] flex items-center justify-center mb-6">
                <Icon className="h-5 w-5 text-white/50" />
              </div>
              <p className="text-lg font-black text-white mb-3">{title}</p>
              <p className="text-sm text-white/40 leading-relaxed">{body}</p>
            </motion.div>
          ))}
        </div>

        {/* Inline CTA */}
        <motion.div
          initial={{ opacity: 0 }}
          whileInView={{ opacity: 1 }}
          viewport={{ once: true }}
          transition={{ delay: 0.3 }}
          className="flex flex-col sm:flex-row items-start sm:items-center justify-between gap-6 rounded-3xl border border-white/[0.07] bg-white/[0.015] px-8 py-7"
        >
          <div>
            <p className="text-base font-bold text-white mb-1">
              {t("home.contribute.bannerTitle")}
            </p>
            <p className="text-sm text-white/40">
              {t("home.contribute.bannerSub")}
            </p>
          </div>
          <button
            onClick={() => open("signup")}
            className="group shrink-0 inline-flex items-center gap-2 bg-white text-black rounded-full px-7 py-3.5 text-sm font-black hover:bg-white/90 transition-all whitespace-nowrap"
          >
            {t("home.contribute.ctaButton")}
            <ArrowRight className="h-4 w-4 transition-transform group-hover:translate-x-1" />
          </button>
        </motion.div>
      </section>

      {/* ═══════════════════════════════════════════════════════════════════ */}
      {/*  FAQ                                                               */}
      {/* ═══════════════════════════════════════════════════════════════════ */}
      <section className="py-28 px-6 sm:px-10 lg:px-20">
        <div className="max-w-3xl mx-auto">
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true }}
            className="mb-16"
          >
            <p className="text-[11px] font-black uppercase tracking-[0.28em] text-white/20 mb-4">
              {t("home.faq.sectionLabel")}
            </p>
            <h2 className="text-[clamp(3rem,8vw,7rem)] font-black leading-none tracking-tight">
              FAQ
            </h2>
          </motion.div>

          <div>
            {faqItems.map((item, i) => (
              <FaqItem key={i} q={item.question} a={item.answer} index={i} />
            ))}
          </div>
        </div>
      </section>

      <AuthModal isOpen={authOpen} onClose={() => setAuthOpen(false)} initialMode={authMode} />
    </div>
  );
}

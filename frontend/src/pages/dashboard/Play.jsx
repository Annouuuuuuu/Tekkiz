import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { HelpCircle, Shuffle, ArrowRight, Clock, Heart, Zap, Timer } from "lucide-react";

const QCM_MODES = [
  { name: "Blitz",   time: "2 min",  pts: "+15 pts/q", note: "Pression max" },
  { name: "Rush",    time: "5 min",  pts: "+10 pts/q", note: "Rythme soutenu" },
  { name: "Classic", time: "10 min", pts: "+8 pts/q",  note: "Réflexion profonde" },
];

const SMATCH_MODES = [
  { name: "Time Attack", time: "90s",  pts: "+10 pts" },
  { name: "Zen",         time: "∞",    pts: "+5 pts" },
  { name: "Survival",    time: "120s", pts: "+15 pts" },
];

const EASE_OUT_EXPO = [0.16, 1, 0.3, 1];

export default function PlayPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen px-6 py-10">
      <div className="max-w-5xl mx-auto space-y-10">

        {/* Section header */}
        <motion.div
          initial={{ opacity: 0, y: 16 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 0.45 }}
        >
          <p className="text-[11px] font-black uppercase tracking-[0.28em] text-white/25 mb-3">
            Les arènes
          </p>
          <h1 className="text-[clamp(2rem,5vw,3.5rem)] font-black leading-[0.92] tracking-tight text-white">
            Choisis.<br />
            <span className="text-white/25">Joue. Grimpe.</span>
          </h1>
        </motion.div>

        {/* Cards grid */}
        <div className="grid gap-4 md:grid-cols-2">

          {/* QCM */}
          <motion.div
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, ease: EASE_OUT_EXPO }}
            onClick={() => navigate("/dashboard/play/qcm/config")}
            className="group relative overflow-hidden rounded-3xl border border-white/[0.07] bg-white/[0.015] cursor-pointer select-none hover:border-primary/30 transition-all duration-500"
          >
            {/* Hover gradient overlay */}
            <div className="absolute inset-0 bg-gradient-to-br from-primary/[0.05] via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />
            {/* Ambient glow */}
            <div className="pointer-events-none absolute -top-16 -right-16 h-48 w-48 rounded-full bg-primary/10 blur-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-700" />

            <div className="relative p-8 flex flex-col gap-7">
              {/* Icon + title */}
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-4">
                  <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-primary/10 border border-primary/20">
                    <HelpCircle className="h-5 w-5 text-primary" />
                  </div>
                  <div>
                    <h2 className="text-3xl font-black leading-none tracking-tight text-white">QCM</h2>
                    <p className="text-[10px] text-white/25 uppercase tracking-widest mt-0.5">Quiz à choix multiples</p>
                  </div>
                </div>
                <ArrowRight className="h-5 w-5 text-white/25 group-hover:text-white/70 group-hover:translate-x-1 transition-all" />
              </div>

              {/* Description */}
              <p className="text-sm text-white/45 leading-relaxed">
                Timer global, 3 vies, difficulté progressive EASY → EXPERT.
                Chaque partie compte dans ton classement global.
              </p>

              {/* Rules badges */}
              <div className="flex items-center gap-3 flex-wrap">
                <span className="flex items-center gap-1.5 text-xs text-white/35">
                  <Clock className="h-3.5 w-3.5" /> Chrono global
                </span>
                <span className="flex items-center gap-1.5 text-xs text-white/35">
                  <Heart className="h-3.5 w-3.5 fill-red-500 text-red-500" /> 3 vies
                </span>
                <span className="flex items-center gap-1.5 text-xs text-white/35">
                  <Zap className="h-3.5 w-3.5 text-yellow-400" /> Score XP
                </span>
              </div>

              {/* Mode list */}
              <div className="space-y-2">
                {QCM_MODES.map((m) => (
                  <div
                    key={m.name}
                    className="flex items-center gap-4 px-4 py-3 rounded-xl bg-white/[0.03] border border-white/[0.04] hover:border-white/10 transition-colors"
                  >
                    <span className="text-xs font-black w-14 text-white/80">{m.name}</span>
                    <span className="text-xs text-white/30 flex-1">{m.note}</span>
                    <span className="text-xs text-white/35 font-mono">{m.time}</span>
                    <span className="text-xs text-primary font-black">{m.pts}</span>
                  </div>
                ))}
              </div>

              <div className="flex items-center gap-2 text-xs text-white/25 group-hover:text-white/60 transition-colors">
                Jouer maintenant <ArrowRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-1" />
              </div>
            </div>
          </motion.div>

          {/* Smatch */}
          <motion.div
            initial={{ opacity: 0, y: 24 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.5, delay: 0.08, ease: EASE_OUT_EXPO }}
            onClick={() => navigate("/dashboard/play/smatch/config")}
            className="group relative overflow-hidden rounded-3xl border border-white/[0.07] bg-white/[0.015] cursor-pointer select-none hover:border-white/20 transition-all duration-500 flex flex-col"
          >
            {/* Hover gradient overlay */}
            <div className="absolute inset-0 bg-gradient-to-b from-orange-500/[0.05] to-transparent opacity-0 group-hover:opacity-100 transition-opacity duration-700 pointer-events-none" />
            <div className="pointer-events-none absolute -top-16 -right-16 h-48 w-48 rounded-full bg-orange-500/10 blur-3xl opacity-0 group-hover:opacity-100 transition-opacity duration-700" />

            <div className="relative flex-1 p-8 flex flex-col gap-7">
              {/* Icon + NEW badge */}
              <div className="flex items-start justify-between">
                <div className="h-12 w-12 rounded-2xl bg-white/[0.04] border border-white/[0.08] flex items-center justify-center">
                  <Shuffle className="h-5 w-5 text-white/55" />
                </div>
                <span className="text-[10px] font-black uppercase tracking-wider text-orange-400 border border-orange-500/20 bg-orange-500/10 rounded-full px-2.5 py-1">
                  NEW
                </span>
              </div>

              {/* Title */}
              <div>
                <h2 className="text-3xl font-black leading-none tracking-tight text-white">Smatch</h2>
                <p className="text-[10px] text-white/25 uppercase tracking-widest mt-0.5">Speed matching</p>
              </div>

              {/* Description */}
              <p className="text-sm text-white/45 leading-relaxed">
                Associe termes et définitions avant la fin du timer.
                Réflexes, mémoire et rapidité — tout est en jeu.
              </p>

              {/* Rules */}
              <div className="flex items-center gap-3 flex-wrap">
                <span className="flex items-center gap-1.5 text-xs text-white/35">
                  <Timer className="h-3.5 w-3.5" /> Compte à rebours
                </span>
                <span className="flex items-center gap-1.5 text-xs text-white/35">
                  <Heart className="h-3.5 w-3.5 fill-red-500 text-red-500" /> 3 vies
                </span>
              </div>

              {/* Mode list */}
              <div className="divide-y divide-white/[0.05]">
                {SMATCH_MODES.map((m) => (
                  <div key={m.name} className="flex items-center justify-between py-3.5">
                    <span className="text-xs font-semibold text-white/65">{m.name}</span>
                    <div className="flex items-center gap-3">
                      <span className="text-xs font-mono text-white/30">{m.time}</span>
                      <span className="text-xs font-black text-white/45">{m.pts}</span>
                    </div>
                  </div>
                ))}
              </div>

              <div className="flex items-center gap-2 text-xs text-white/25 group-hover:text-white/60 transition-colors mt-auto">
                Découvrir <ArrowRight className="h-3.5 w-3.5 transition-transform group-hover:translate-x-1" />
              </div>
            </div>
          </motion.div>

        </div>
      </div>
    </div>
  );
}

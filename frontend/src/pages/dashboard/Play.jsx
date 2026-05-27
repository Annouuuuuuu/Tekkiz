import { useNavigate } from "react-router-dom";
import { motion } from "framer-motion";
import { HelpCircle, Shuffle, ArrowRight, Clock, Heart, Star, Timer } from "lucide-react";

const QCM_MODES = [
  { name: "Blitz",   sub: "2 min"  },
  { name: "Rush",    sub: "5 min"  },
  { name: "Classic", sub: "10 min" },
];

const SMATCH_MODES = [
  { name: "Time Attack", sub: "90s"        },
  { name: "Zen",         sub: "Sans chrono" },
  { name: "Survival",    sub: "120s"        },
];

export default function PlayPage() {
  const navigate = useNavigate();

  return (
    <div className="min-h-screen bg-background px-6 py-10">
      <div className="max-w-5xl mx-auto space-y-10">
        <div>
          <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Choisissez votre jeu</p>
          <h1 className="mt-1 text-4xl font-black tracking-tight">Choisissez votre mode.</h1>
        </div>

        <div className="grid gap-5 md:grid-cols-2">
          {/* QCM */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3 }}
            onClick={() => navigate("/dashboard/play/qcm/config")}
            className="group relative overflow-hidden rounded-2xl border border-border bg-card cursor-pointer select-none"
          >
            {/* Top gradient strip */}
            <div className="h-1.5 w-full bg-linear-to-r from-primary to-violet-500" />

            {/* Background glow */}
            <div className="pointer-events-none absolute -top-20 -right-20 h-56 w-56 rounded-full bg-primary/10 blur-3xl" />
            <div className="pointer-events-none absolute inset-0 bg-linear-to-br from-primary/5 via-transparent to-transparent" />

            <div className="relative p-8 flex flex-col gap-6">
              {/* Icon + title */}
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-4">
                  <div className="flex h-14 w-14 items-center justify-center rounded-2xl border border-primary/30 bg-primary/15">
                    <HelpCircle className="h-7 w-7 text-primary" />
                  </div>
                  <div>
                    <h2 className="text-2xl font-black leading-none">QCM</h2>
                    <p className="text-xs text-muted-foreground mt-1">Choix multiple · classé</p>
                  </div>
                </div>
                <ArrowRight className="h-5 w-5 text-muted-foreground group-hover:text-primary group-hover:translate-x-1 transition-all" />
              </div>

              {/* Description */}
              <p className="text-sm text-muted-foreground leading-relaxed">
                Répondez à des questions en rafale contre la montre. Chaque bonne réponse rapporte des XP.
                Trop d'erreurs — vous perdez une vie.
              </p>

              {/* Rules */}
              <div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
                <span className="flex items-center gap-1.5"><Clock className="h-3.5 w-3.5" /> Chrono global</span>
                <span className="flex items-center gap-1.5"><Heart className="h-3.5 w-3.5 fill-red-500 text-red-500" /> 3 vies</span>
                <span className="flex items-center gap-1.5"><Star className="h-3.5 w-3.5 text-yellow-400" /> Score XP</span>
              </div>

              {/* Modes */}
              <div>
                <p className="text-[10px] font-semibold uppercase tracking-widest text-muted-foreground mb-2">Modes</p>
                <div className="flex flex-wrap gap-2">
                  {QCM_MODES.map((m) => (
                    <div key={m.name} className="flex items-center gap-1.5 rounded-full border border-border bg-muted/40 px-3 py-1">
                      <span className="text-xs font-semibold text-foreground">{m.name}</span>
                      <span className="text-xs text-muted-foreground">·</span>
                      <span className="text-xs text-muted-foreground">{m.sub}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* CTA */}
              <button className="mt-2 flex items-center gap-2 text-sm font-black text-primary group-hover:gap-3 transition-all">
                Jouer au QCM <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          </motion.div>

          {/* Smatch */}
          <motion.div
            initial={{ opacity: 0, y: 20 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.3, delay: 0.1 }}
            onClick={() => navigate("/dashboard/play/smatch/config")}
            className="group relative overflow-hidden rounded-2xl border border-border bg-card cursor-pointer select-none"
          >
            {/* Top gradient strip */}
            <div className="h-1.5 w-full bg-linear-to-r from-orange-500 to-red-500" />

            {/* Background glow */}
            <div className="pointer-events-none absolute -top-20 -right-20 h-56 w-56 rounded-full bg-orange-500/10 blur-3xl" />
            <div className="pointer-events-none absolute inset-0 bg-linear-to-br from-orange-500/5 via-transparent to-transparent" />

            <div className="relative p-8 flex flex-col gap-6">
              {/* Icon + title */}
              <div className="flex items-start justify-between">
                <div className="flex items-center gap-4">
                  <div className="flex h-14 w-14 items-center justify-center rounded-2xl border border-orange-500/30 bg-orange-500/15">
                    <Shuffle className="h-7 w-7 text-orange-400" />
                  </div>
                  <div>
                    <div className="flex items-center gap-2">
                      <h2 className="text-2xl font-black leading-none">Smatch</h2>
                      <span className="rounded-full bg-orange-500/20 px-2 py-0.5 text-[10px] font-black uppercase tracking-wider text-orange-400">NEW</span>
                    </div>
                    <p className="text-xs text-muted-foreground mt-1">Association rapide · réflexes</p>
                  </div>
                </div>
                <ArrowRight className="h-5 w-5 text-muted-foreground group-hover:text-orange-400 group-hover:translate-x-1 transition-all" />
              </div>

              {/* Description */}
              <p className="text-sm text-muted-foreground leading-relaxed">
                Associez chaque terme à sa définition avant la fin du chrono.
                Réflexes, mémoire et rapidité — tout est en jeu.
              </p>

              {/* Rules */}
              <div className="flex flex-wrap gap-4 text-xs text-muted-foreground">
                <span className="flex items-center gap-1.5"><Timer className="h-3.5 w-3.5" /> Compte à rebours</span>
                <span className="flex items-center gap-1.5"><Heart className="h-3.5 w-3.5 fill-red-500 text-red-500" /> 3 vies</span>
                <span className="flex items-center gap-1.5"><Shuffle className="h-3.5 w-3.5 text-orange-400" /> Bonus combo</span>
              </div>

              {/* Modes */}
              <div>
                <p className="text-[10px] font-semibold uppercase tracking-widest text-muted-foreground mb-2">Modes</p>
                <div className="flex flex-wrap gap-2">
                  {SMATCH_MODES.map((m) => (
                    <div key={m.name} className="flex items-center gap-1.5 rounded-full border border-border bg-muted/40 px-3 py-1">
                      <span className="text-xs font-semibold text-foreground">{m.name}</span>
                      <span className="text-xs text-muted-foreground">·</span>
                      <span className="text-xs text-muted-foreground">{m.sub}</span>
                    </div>
                  ))}
                </div>
              </div>

              {/* CTA */}
              <button className="mt-2 flex items-center gap-2 text-sm font-black text-orange-400 group-hover:gap-3 transition-all">
                Jouer à Smatch <ArrowRight className="h-4 w-4" />
              </button>
            </div>
          </motion.div>
        </div>
      </div>
    </div>
  );
}

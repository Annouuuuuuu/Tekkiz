import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Trophy, HelpCircle, Shuffle, Loader2, Medal } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import qcmGameService from "@/services/qcmGame.service";

const getInitials = (u) => u?.slice(0, 2).toUpperCase() || "??";

const PODIUM = [
  { place: 1, color: "#F59E0B", size: "h-28", offset: "", label: "1st" },
  { place: 2, color: "#94A3B8", size: "h-20", offset: "mt-8", label: "2nd" },
  { place: 3, color: "#B45309", size: "h-14", offset: "mt-14", label: "3rd" },
];

const QCM_MODE_PILLS = [
  { key: "ALL", label: "Tous" },
  { key: "BLITZ", label: "Blitz" },
  { key: "RUSH", label: "Rush" },
  { key: "CLASSIC", label: "Classic" },
];

function PodiumSlot({ entry, cfg }) {
  const isFirst = cfg.place === 1;
  return (
    <motion.div
      initial={{ opacity: 0, y: 30 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ delay: (3 - cfg.place) * 0.12 }}
      className={`flex flex-col items-center gap-2 ${cfg.offset}`}
    >
      {isFirst && <Trophy className="h-7 w-7 mb-1" style={{ color: cfg.color }} />}
      <Avatar
        className={`${isFirst ? "h-16 w-16" : "h-11 w-11"} ring-2 shadow-2xl`}
        style={{ ringColor: cfg.color }}
      >
        <AvatarImage src={entry.avatarUrl} />
        <AvatarFallback className="text-xs font-black">{getInitials(entry.username)}</AvatarFallback>
      </Avatar>
      <div className="text-center">
        <p className="text-xs font-bold max-w-[72px] truncate">{entry.username}</p>
        <p className="text-sm font-black tabular-nums" style={{ color: cfg.color }}>
          {Math.round(entry.averageScore || 0)}
          <span className="text-[10px] font-normal text-muted-foreground ml-0.5">pts</span>
        </p>
      </div>
      <div
        className={`w-20 rounded-t-xl flex items-end justify-center pb-2 font-black text-xl ${cfg.size}`}
        style={{ background: `${cfg.color}18`, border: `1px solid ${cfg.color}30`, color: cfg.color }}
      >
        {cfg.place}
      </div>
    </motion.div>
  );
}

function QcmLeaderboard() {
  const [entries, setEntries] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [gameMode, setGameMode] = useState("ALL");

  useEffect(() => {
    setLoading(true);
    setError(null);
    qcmGameService
      .getLeaderboard({ page: 0, size: 50, gameMode })
      .then((r) => setEntries(r.data?.entries || r.entries || []))
      .catch((e) => setError(e.message || "Failed to load"))
      .finally(() => setLoading(false));
  }, [gameMode]);

  const top3 = entries.slice(0, 3);
  const rest = entries.slice(3);
  const medalColors = ["text-yellow-400", "text-slate-400", "text-amber-600"];

  return (
    <div className="space-y-6">
      {/* Mode filter pills */}
      <div className="flex flex-wrap gap-2">
        {QCM_MODE_PILLS.map(({ key, label }) => (
          <button
            key={key}
            onClick={() => setGameMode(key)}
            className={`px-4 py-1.5 rounded-full border text-xs font-semibold transition-all ${
              gameMode === key
                ? "bg-card border-border text-foreground shadow-sm"
                : "border-transparent text-muted-foreground hover:text-foreground"
            }`}
          >
            {label}
          </button>
        ))}
      </div>

      {loading && (
        <div className="flex items-center justify-center py-24">
          <Loader2 className="h-8 w-8 animate-spin text-primary" />
        </div>
      )}

      {error && !loading && (
        <p className="text-center py-20 text-destructive">{error}</p>
      )}

      {!loading && !error && entries.length === 0 && (
        <div className="flex flex-col items-center py-24 gap-3 text-muted-foreground">
          <Trophy className="h-12 w-12 opacity-20" />
          <p className="text-sm">Aucun joueur pour ce mode — soyez le premier.</p>
        </div>
      )}

      {!loading && !error && entries.length > 0 && (
        <div className="space-y-10">
          {/* Podium */}
          {top3.length >= 2 && (
            <div className="relative rounded-2xl overflow-hidden border border-border bg-card p-8 pt-10">
              <div className="pointer-events-none absolute inset-0 bg-linear-to-b from-primary/5 to-transparent" />
              <div className="relative flex items-end justify-center gap-6">
                {[top3[1], top3[0], top3[2]].filter(Boolean).map((entry) => {
                  const cfg = PODIUM.find((c) => c.place === entry.rank);
                  return cfg ? <PodiumSlot key={entry.userId} entry={entry} cfg={cfg} /> : null;
                })}
              </div>
            </div>
          )}

          {/* Rank list */}
          <div className="rounded-2xl border border-border bg-card overflow-hidden">
            <div className="divide-y divide-border">
              {entries.map((entry) => {
                const isTop3 = entry.rank <= 3;
                return (
                  <motion.div
                    key={entry.userId || entry.rank}
                    initial={{ opacity: 0 }}
                    animate={{ opacity: 1 }}
                    transition={{ delay: Math.min(entry.rank * 0.02, 0.4) }}
                    className={`flex items-center gap-4 px-5 py-3.5 ${
                      isTop3 ? "bg-primary/3" : "hover:bg-muted/30"
                    } transition-colors`}
                  >
                    <div className="w-8 text-center shrink-0">
                      {isTop3 ? (
                        <Medal className={`h-5 w-5 mx-auto ${medalColors[entry.rank - 1]}`} />
                      ) : (
                        <span className="text-sm font-mono text-muted-foreground">{entry.rank}</span>
                      )}
                    </div>
                    <Avatar className="h-8 w-8 shrink-0">
                      <AvatarImage src={entry.avatarUrl} />
                      <AvatarFallback className="text-xs">{getInitials(entry.username)}</AvatarFallback>
                    </Avatar>
                    <span className={`flex-1 text-sm ${isTop3 ? "font-bold" : "font-medium"} truncate`}>
                      {entry.username}
                    </span>
                    <div className="text-right shrink-0">
                      <span className="text-base font-black text-primary tabular-nums">
                        {Math.round(entry.averageScore || 0)}
                      </span>
                      <span className="text-xs text-muted-foreground ml-1">pts</span>
                    </div>
                    <div className="hidden sm:block text-right shrink-0 w-14">
                      <span
                        className={`text-sm font-semibold tabular-nums ${
                          entry.accuracy >= 70
                            ? "text-emerald-400"
                            : entry.accuracy >= 50
                            ? "text-yellow-400"
                            : "text-muted-foreground"
                        }`}
                      >
                        {(entry.accuracy || 0).toFixed(0)}%
                      </span>
                    </div>
                    <div className="hidden md:block text-right shrink-0 w-16 text-xs text-muted-foreground tabular-nums">
                      {entry.gamesPlayed} games
                    </div>
                  </motion.div>
                );
              })}
            </div>
          </div>

          <p className="text-xs text-muted-foreground text-right">{entries.length} joueurs classés</p>
        </div>
      )}
    </div>
  );
}

function SmatchLeaderboard() {
  return (
    <div className="flex flex-col items-center py-24 gap-4 text-center">
      <div className="flex h-16 w-16 items-center justify-center rounded-2xl border border-orange-500/20 bg-orange-500/10">
        <Shuffle className="h-8 w-8 text-orange-400" />
      </div>
      <div>
        <h3 className="text-lg font-black">Smatch leaderboard coming soon</h3>
        <p className="text-sm text-muted-foreground mt-1">Play Smatch to be first on the board.</p>
      </div>
    </div>
  );
}

export default function LeaderboardPage() {
  const [tab, setTab] = useState("qcm");

  return (
    <div className="px-6 py-10 max-w-4xl mx-auto space-y-8">
      <div>
        <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Rankings</p>
        <h1 className="mt-1 text-4xl font-black tracking-tight">Leaderboard.</h1>
      </div>

      {/* Tab switcher */}
      <div className="flex gap-1 p-1 rounded-xl bg-muted/40 border border-border w-fit">
        {[
          { key: "qcm", icon: HelpCircle, label: "QCM" },
          { key: "smatch", icon: Shuffle, label: "Smatch" },
        ].map(({ key, icon: Icon, label }) => (
          <button
            key={key}
            onClick={() => setTab(key)}
            className={`flex items-center gap-2 px-5 py-2 rounded-lg text-sm font-semibold transition-all ${
              tab === key
                ? "bg-card shadow text-foreground"
                : "text-muted-foreground hover:text-foreground"
            }`}
          >
            <Icon className="h-4 w-4" />
            {label}
          </button>
        ))}
      </div>

      {tab === "qcm" ? <QcmLeaderboard /> : <SmatchLeaderboard />}
    </div>
  );
}

import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  TrendingUp, Trophy, Target, Brain, Zap, Loader2, Flame,
  BarChart3, Star, CheckCircle2, XCircle,
} from "lucide-react";
import GlobalScoreChart from "@/components/dashboard/Performance/GlobalScoreChart";
import qcmGameService from "@/services/qcmGame.service";

const difficultyColors = {
  EASY:   { badge: "bg-emerald-500/10 text-emerald-400 border-emerald-500/20", bar: "bg-emerald-500" },
  MEDIUM: { badge: "bg-yellow-500/10 text-yellow-400 border-yellow-500/20",   bar: "bg-yellow-400" },
  HARD:   { badge: "bg-orange-500/10 text-orange-400 border-orange-500/20",   bar: "bg-orange-400" },
  EXPERT: { badge: "bg-red-500/10 text-red-400 border-red-500/20",            bar: "bg-red-500" },
};

const gameModeColors = {
  BLITZ:   "border-violet-500/20 bg-violet-500/8 text-violet-400",
  RUSH:    "border-blue-500/20 bg-blue-500/8 text-blue-400",
  CLASSIC: "border-emerald-500/20 bg-emerald-500/8 text-emerald-400",
};

function AccuracyRing({ value }) {
  const size = 88;
  const stroke = 9;
  const r = (size - stroke) / 2;
  const circ = 2 * Math.PI * r;
  const offset = circ - (value / 100) * circ;
  const color = value >= 70 ? "#22c55e" : value >= 50 ? "#eab308" : "#ef4444";
  return (
    <div className="relative inline-flex items-center justify-center">
      <svg width={size} height={size} className="-rotate-90">
        <circle cx={size / 2} cy={size / 2} r={r} fill="none" stroke="rgba(255,255,255,0.07)" strokeWidth={stroke} />
        <circle cx={size / 2} cy={size / 2} r={r} fill="none" stroke={color} strokeWidth={stroke}
          strokeDasharray={circ} strokeDashoffset={offset} strokeLinecap="round"
          style={{ transition: "stroke-dashoffset 0.9s ease" }} />
      </svg>
      <span className="absolute text-base font-black" style={{ color }}>{Math.round(value)}%</span>
    </div>
  );
}

function StatTile({ icon: Icon, label, value, sub, accent }) {
  return (
    <div className={`rounded-2xl border p-5 flex flex-col gap-1.5 ${accent ? "border-primary/30 bg-primary/8" : "border-border bg-card"}`}>
      <div className="flex items-center gap-2 text-muted-foreground">
        <Icon className={`h-4 w-4 ${accent ? "text-primary" : ""}`} />
        <span className="text-[11px] font-semibold uppercase tracking-wider">{label}</span>
      </div>
      <p className={`text-3xl font-black tabular-nums ${accent ? "text-primary" : ""}`}>{value}</p>
      {sub && <p className="text-xs text-muted-foreground">{sub}</p>}
    </div>
  );
}

export default function PerformancePage() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    qcmGameService.getUserStats()
      .then((r) => setStats(r.data || r))
      .catch((e) => setError(e.message || "Failed to load"))
      .finally(() => setLoading(false));
  }, []);

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error) {
    return <p className="text-center py-20 text-destructive">{error}</p>;
  }

  if (!stats || stats.totalGamesPlayed === 0) {
    return (
      <div className="px-6 py-10 max-w-4xl mx-auto">
        <div>
          <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Stats</p>
          <h1 className="mt-1 text-4xl font-black tracking-tight">Performance.</h1>
        </div>
        <div className="flex flex-col items-center py-24 gap-4 text-center">
          <Target className="h-12 w-12 opacity-20" />
          <h3 className="text-lg font-black">No data yet</h3>
          <p className="text-sm text-muted-foreground">Play a few games to see your stats here.</p>
        </div>
      </div>
    );
  }

  return (
    <div className="px-6 py-10 max-w-4xl mx-auto space-y-8">
      <div>
        <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Stats · QCM</p>
        <h1 className="mt-1 text-4xl font-black tracking-tight">Performance.</h1>
      </div>

      <motion.div initial={{ opacity: 0, y: 12 }} animate={{ opacity: 1, y: 0 }} className="space-y-6">

        {/* Hero banner */}
        <div className="rounded-2xl border border-primary/20 bg-primary/8 p-7">
          <div className="flex flex-wrap items-center gap-8">
            <div>
              <p className="text-[11px] uppercase tracking-widest text-muted-foreground mb-1">Avg score</p>
              <p className="text-6xl font-black text-primary tabular-nums leading-none">
                {Math.round(stats.averageScore || 0)}
              </p>
              <p className="text-xs text-muted-foreground mt-1">pts / game</p>
            </div>
            <div className="h-16 w-px bg-border hidden sm:block" />
            <div className="flex flex-col items-center gap-1">
              <AccuracyRing value={stats.overallAccuracy || 0} />
              <p className="text-[11px] text-muted-foreground uppercase tracking-wider">Accuracy</p>
              {stats.recentAccuracy > stats.overallAccuracy && (
                <p className="text-[11px] text-emerald-400 font-semibold">
                  +{(stats.recentAccuracy - stats.overallAccuracy).toFixed(0)}% recently
                </p>
              )}
            </div>
            <div className="ml-auto text-right hidden md:block">
              <p className="text-[11px] uppercase tracking-widest text-muted-foreground mb-1">Rank</p>
              <p className="text-4xl font-black tabular-nums">#{stats.leaderboardPosition || "–"}</p>
              <p className="text-xs text-muted-foreground">of {stats.totalPlayers || 0} players</p>
            </div>
          </div>
        </div>

        {/* Stats grid */}
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          <StatTile icon={Trophy} label="Best score" value={stats.bestScore || 0} accent />
          <StatTile icon={Brain} label="Games" value={stats.totalGamesPlayed} />
          <StatTile icon={Flame} label="Streak" value={`${stats.currentStreak || 0}d`} />
          <StatTile icon={Zap} label="Total pts" value={(stats.totalPointsEarned || 0).toLocaleString()} />
        </div>

        {/* Correct / Wrong */}
        <div className="grid grid-cols-2 gap-3">
          <div className="rounded-2xl border border-emerald-500/20 bg-emerald-500/6 p-5 flex items-center gap-4">
            <CheckCircle2 className="h-9 w-9 text-emerald-400 shrink-0" />
            <div>
              <p className="text-3xl font-black text-emerald-400 tabular-nums">{stats.totalCorrectAnswers || 0}</p>
              <p className="text-xs text-muted-foreground mt-0.5">Correct answers</p>
            </div>
          </div>
          <div className="rounded-2xl border border-red-500/20 bg-red-500/6 p-5 flex items-center gap-4">
            <XCircle className="h-9 w-9 text-red-400 shrink-0" />
            <div>
              <p className="text-3xl font-black text-red-400 tabular-nums">{stats.totalWrongAnswers || 0}</p>
              <p className="text-xs text-muted-foreground mt-0.5">Wrong answers</p>
            </div>
          </div>
        </div>

        {/* Best level */}
        {stats.bestPerformingLevel && stats.bestPerformingLevel !== "N/A" && (
          <div className="rounded-2xl border border-border bg-card p-5 flex items-center gap-4">
            <Star className="h-8 w-8 text-yellow-400 shrink-0" />
            <div>
              <p className="text-sm text-muted-foreground">Best performing level</p>
              <span className={`inline-block mt-1 text-sm font-black border rounded-lg px-3 py-1 ${difficultyColors[stats.bestPerformingLevel]?.badge || ""}`}>
                {stats.bestPerformingLevel}
              </span>
            </div>
          </div>
        )}

        {/* Score chart */}
        <div className="rounded-2xl border border-border bg-card p-6">
          <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground mb-4">Score history</p>
          <GlobalScoreChart recentGames={stats.recentGames || []} averageScore={stats.averageScore || 0} />
        </div>

        {/* Category breakdown */}
        {stats.categoryStats?.length > 0 && (
          <div className="rounded-2xl border border-border bg-card p-6 space-y-4">
            <div className="flex items-center gap-2">
              <BarChart3 className="h-4 w-4 text-primary" />
              <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">By category</p>
            </div>
            <div className="space-y-4">
              {stats.categoryStats.map((cat) => (
                <div key={cat.categoryId}>
                  <div className="flex justify-between items-center mb-1.5">
                    <span className="text-sm font-semibold">{cat.categoryName}</span>
                    <span className="text-xs text-muted-foreground tabular-nums">
                      {cat.gamesPlayed} games · {Math.round(cat.averageScore || 0)} avg pts
                    </span>
                  </div>
                  <div className="flex items-center gap-3">
                    <div className="flex-1 h-2 bg-muted rounded-full overflow-hidden">
                      <div className="h-full bg-primary rounded-full transition-all duration-700"
                        style={{ width: `${cat.accuracy || 0}%` }} />
                    </div>
                    <span className="text-xs font-bold text-primary w-9 text-right tabular-nums">
                      {(cat.accuracy || 0).toFixed(0)}%
                    </span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Game mode breakdown */}
        {stats.gameModeStats?.length > 0 && (
          <div className="rounded-2xl border border-border bg-card p-6 space-y-4">
            <div className="flex items-center gap-2">
              <TrendingUp className="h-4 w-4 text-primary" />
              <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">By game mode</p>
            </div>
            <div className="grid grid-cols-1 sm:grid-cols-3 gap-3">
              {stats.gameModeStats.map((mode) => (
                <div key={mode.gameMode} className={`rounded-xl border p-4 space-y-2 ${gameModeColors[mode.gameMode] || "border-border bg-muted/30"}`}>
                  <div className="flex items-center justify-between">
                    <span className="text-xs font-black uppercase tracking-wider">{mode.gameMode}</span>
                    <span className="text-xs text-muted-foreground">{mode.gamesPlayed} games</span>
                  </div>
                  {[
                    { label: "Avg score", val: Math.round(mode.averageScore) },
                    { label: "Best", val: mode.bestScore },
                    { label: "Accuracy", val: `${(mode.accuracy || 0).toFixed(0)}%` },
                  ].map(({ label, val }) => (
                    <div key={label} className="flex justify-between text-xs">
                      <span className="text-muted-foreground">{label}</span>
                      <span className="font-bold">{val}</span>
                    </div>
                  ))}
                </div>
              ))}
            </div>
          </div>
        )}

        {/* Recent games */}
        {stats.recentGames?.length > 0 && (
          <div className="rounded-2xl border border-border bg-card overflow-hidden">
            <div className="px-6 py-4 border-b border-border">
              <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Recent games</p>
            </div>
            <div className="divide-y divide-border">
              {stats.recentGames.map((game) => (
                <div key={game.sessionId} className="flex items-center gap-4 px-6 py-3.5 hover:bg-muted/20 transition-colors">
                  <div className="text-center w-14 shrink-0">
                    <p className="text-2xl font-black text-primary tabular-nums">{game.score}</p>
                    <p className="text-[10px] text-muted-foreground">pts</p>
                  </div>
                  <div className="flex-1 min-w-0">
                    <p className="text-sm font-semibold truncate">{game.categoryName}</p>
                    <p className="text-xs text-muted-foreground">
                      {game.correctAnswers}/{game.totalQuestions} correct · {(game.accuracy || 0).toFixed(0)}%
                    </p>
                  </div>
                  <div className="text-right shrink-0">
                    <span className={`text-xs font-bold border rounded-lg px-2 py-0.5 ${difficultyColors[game.difficultyReached]?.badge || "text-muted-foreground border-border"}`}>
                      {game.difficultyReached}
                    </span>
                    <p className="text-xs text-muted-foreground mt-1">
                      {new Date(game.completedAt).toLocaleDateString()}
                    </p>
                  </div>
                </div>
              ))}
            </div>
          </div>
        )}

      </motion.div>
    </div>
  );
}

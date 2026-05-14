import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { Users, FileQuestion, Layers, Play, TrendingUp, Activity } from "lucide-react";
import adminService from "@/services/admin.service";

function StatCard({ icon: Icon, label, value, sub, color, to }) {
  const card = (
    <div className={`rounded-xl border border-border bg-card p-5 flex items-start gap-4 hover:border-primary/50 transition-colors ${to ? "cursor-pointer" : ""}`}>
      <div className={`h-10 w-10 rounded-lg flex items-center justify-center ${color}`}>
        <Icon className="h-5 w-5" />
      </div>
      <div className="flex-1 min-w-0">
        <p className="text-xs text-muted-foreground">{label}</p>
        <p className="text-2xl font-bold mt-0.5">{value ?? "—"}</p>
        {sub && <p className="text-xs text-muted-foreground mt-0.5">{sub}</p>}
      </div>
    </div>
  );
  return to ? <Link to={to}>{card}</Link> : card;
}

export default function AdminDashboard() {
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    adminService.getStats()
      .then(r => setStats(r.data))
      .catch(() => {})
      .finally(() => setLoading(false));
  }, []);

  const s = stats;

  return (
    <div className="space-y-8">
      <div>
        <h2 className="text-xl font-bold">Platform Overview</h2>
        <p className="text-sm text-muted-foreground mt-1">Real-time platform statistics</p>
      </div>

      {loading ? (
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
          {Array.from({ length: 6 }).map((_, i) => (
            <div key={i} className="h-28 rounded-xl bg-muted animate-pulse" />
          ))}
        </div>
      ) : (
        <>
          {/* Platform */}
          <section>
            <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-3">Platform</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <StatCard
                icon={Users} label="Total Users" value={s?.totalUsers}
                color="bg-blue-500/10 text-blue-500" to="/admin/users"
              />
            </div>
          </section>

          {/* QCM */}
          <section>
            <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-3">QCM Game</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <StatCard
                icon={FileQuestion} label="Questions"
                value={s?.totalQcmQuestions}
                sub={`${s?.activeQcmQuestions ?? 0} active`}
                color="bg-violet-500/10 text-violet-500"
                to="/admin/qcm/questions"
              />
              <StatCard
                icon={TrendingUp} label="Categories"
                value={s?.totalQcmCategories}
                color="bg-emerald-500/10 text-emerald-500"
                to="/admin/qcm/categories"
              />
              <StatCard
                icon={Activity} label="QCM Sessions"
                value={s?.totalQcmSessions}
                sub={`${s?.activeQcmSessions ?? 0} completed`}
                color="bg-orange-500/10 text-orange-500"
                to="/admin/qcm/sessions"
              />
            </div>
          </section>

          {/* Smatch */}
          <section>
            <h3 className="text-sm font-semibold text-muted-foreground uppercase tracking-wider mb-3">Smatch Game</h3>
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
              <StatCard
                icon={Layers} label="Decks"
                value={s?.totalSmatchDecks}
                sub={`${s?.activeSmatchDecks ?? 0} active · ${s?.totalSmatchPairs ?? 0} pairs`}
                color="bg-pink-500/10 text-pink-500"
                to="/admin/smatch/decks"
              />
              <StatCard
                icon={Play} label="Smatch Sessions"
                value={s?.totalSmatchSessions}
                sub={`${s?.activeSmatchSessions ?? 0} in progress`}
                color="bg-cyan-500/10 text-cyan-500"
                to="/admin/smatch/sessions"
              />
            </div>
          </section>
        </>
      )}
    </div>
  );
}

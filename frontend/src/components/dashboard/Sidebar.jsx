import { NavLink } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/contexts/AuthContext";
import { Play, BarChart3, Trophy, Settings, Info, LogOut, X, Shield, PenLine } from "lucide-react";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";

const NAV_KEYS = [
  { to: "/dashboard/play",        icon: Play,     key: "play" },
  { to: "/dashboard/performance", icon: BarChart3, key: "performance" },
  { to: "/dashboard/leaderboard", icon: Trophy,    key: "leaderboard" },
  { to: "/dashboard/contribute",  icon: PenLine,  key: "contribute" },
];

const NAV_SECONDARY_KEYS = [
  { to: "/dashboard/settings", icon: Settings, key: "settings" },
  { to: "/dashboard/about",    icon: Info,     key: "about" },
];

const link = (active) =>
  `flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold transition-colors ${
    active
      ? "bg-primary/10 text-primary"
      : "text-muted-foreground hover:text-foreground hover:bg-muted/50"
  }`;

const Sidebar = ({ open, onClose }) => {
  const { t } = useTranslation("common");
  const { user, logout } = useAuth();

  const initials = () => {
    if (user?.firstName && user?.lastName)
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    return user?.username?.slice(0, 2).toUpperCase() || "U";
  };

  const handleLogout = async () => {
    await logout();
    window.location.href = "/";
  };

  return (
    <aside
      className={`fixed left-0 top-0 z-40 h-screen w-[240px] flex flex-col bg-card border-r border-border transition-transform duration-300 ease-in-out
        ${open ? "translate-x-0" : "-translate-x-full"} lg:translate-x-0`}
    >
      {/* Logo + mobile close */}
      <div className="flex items-center justify-between h-16 px-5 border-b border-border shrink-0">
        <span className="text-xl font-black tracking-tighter">
          Tekizz<span className="text-primary">.</span>
        </span>
        <button
          onClick={onClose}
          className="lg:hidden flex items-center justify-center w-8 h-8 rounded-lg text-muted-foreground hover:text-foreground hover:bg-muted transition-colors"
        >
          <X className="h-4 w-4" />
        </button>
      </div>

      {/* Main nav */}
      <nav className="flex-1 py-4 px-3 space-y-1 overflow-y-auto">
        {NAV_KEYS.map(({ to, icon: Icon, key }) => (
          <NavLink key={to} to={to} onClick={onClose}
            className={({ isActive }) => link(isActive)}>
            <Icon className="h-4 w-4 shrink-0" />
            {t(`sidebar.${key}`)}
          </NavLink>
        ))}
      </nav>

      {/* Secondary nav */}
      <div className="border-t border-border px-3 py-4 space-y-1">
        {NAV_SECONDARY_KEYS.map(({ to, icon: Icon, key }) => (
          <NavLink key={to} to={to} onClick={onClose}
            className={({ isActive }) => link(isActive)}>
            <Icon className="h-4 w-4 shrink-0" />
            {t(`sidebar.${key}`)}
          </NavLink>
        ))}
        {user?.roleName === "ADMIN" && (
          <a href="/admin" onClick={onClose}
            className="flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-primary hover:bg-primary/10 transition-colors">
            <Shield className="h-4 w-4 shrink-0" />
            Admin panel
          </a>
        )}
      </div>

      {/* User */}
      <div className="border-t border-border px-3 py-4 space-y-2 shrink-0">
        <div className="flex items-center gap-3 px-2">
          <Avatar className="h-8 w-8 shrink-0">
            <AvatarImage src={user?.avatar} />
            <AvatarFallback className="text-xs font-black">{initials()}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <p className="text-sm font-semibold truncate">{user?.username || "User"}</p>
            <p className="text-xs text-muted-foreground truncate">{user?.email || ""}</p>
          </div>
        </div>
        <button onClick={handleLogout}
          className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-semibold text-muted-foreground hover:text-foreground hover:bg-muted/50 transition-colors">
          <LogOut className="h-4 w-4 shrink-0" />
          {t("sidebar.logout")}
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;

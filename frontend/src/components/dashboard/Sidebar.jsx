import { NavLink } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/contexts/AuthContext";
import {
  Play,
  BarChart3,
  Trophy,
  Settings,
  Info,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Button } from "@/components/ui/button";

const Sidebar = () => {
  const { t } = useTranslation("common");
  const { user, logout } = useAuth();

  const mainNavItems = [
    {
      to: "/dashboard/play",
      icon: Play,
      label: t("sidebar.play") || "Play",
    },
    {
      to: "/dashboard/performance",
      icon: BarChart3,
      label: t("sidebar.performance") || "Performance",
    },
    {
      to: "/dashboard/leaderboard",
      icon: Trophy,
      label: t("sidebar.leaderboard") || "Leaderboard",
    },
  ];

  const secondaryNavItems = [
    {
      to: "/dashboard/settings",
      icon: Settings,
      label: t("sidebar.settings") || "Settings",
    },
    {
      to: "/dashboard/about",
      icon: Info,
      label: t("sidebar.about") || "About",
    },
  ];

  const handleLogout = async () => {
    await logout();
    window.location.href = "/";
  };

  const getInitials = (firstName, lastName, username) => {
    if (firstName && lastName) {
      return `${firstName[0]}${lastName[0]}`.toUpperCase();
    }
    if (username) {
      return username.slice(0, 2).toUpperCase();
    }
    return "U";
  };

  return (
    <aside className="fixed left-0 top-0 z-40 h-screen w-[240px] flex flex-col bg-card border-r">
      {/* Header - Tekizz simple text */}
      <div className="flex items-center justify-center h-14 border-b border-border/50">
        <span className="text-base font-semibold tracking-tight">Tekizz</span>
      </div>

      {/* Main Navigation */}
      <div className="flex-1 py-4">
        <nav className="space-y-1 px-3">
          {mainNavItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              end={item.to === "/dashboard/performance"}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-lg text-base font-medium transition-colors",
                  isActive
                    ? "bg-primary/10 text-primary"
                    : "text-muted-foreground hover:text-foreground hover:bg-muted"
                )
              }
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* Secondary Navigation */}
      <div className="border-t border-border/50 py-4">
        <nav className="space-y-1 px-3">
          {secondaryNavItems.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) =>
                cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-lg text-base font-medium transition-colors",
                  isActive
                    ? "bg-primary/10 text-primary"
                    : "text-muted-foreground hover:text-foreground hover:bg-muted"
                )
              }
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* User Section - Compact */}
      <div className="border-t border-border/50 px-3 py-3">
        <div className="flex items-center gap-3">
          <Avatar className="h-8 w-8">
            <AvatarImage src={user?.avatar} />
            <AvatarFallback>{getInitials(user?.firstName, user?.lastName, user?.username)}</AvatarFallback>
          </Avatar>
          <div className="flex-1 min-w-0">
            <p className="text-base font-medium truncate">{user?.username || "User"}</p>
          </div>
        </div>
        <Button
          variant="ghost"
          size="sm"
          onClick={handleLogout}
          className="w-full mt-2 justify-start text-base text-muted-foreground hover:text-foreground"
        >
          <LogOut className="w-4 h-4 mr-2" />
          {t("sidebar.logout") || "Sign out"}
        </Button>
      </div>
    </aside>
  );
};

export default Sidebar;

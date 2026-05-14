import { useLocation } from "react-router-dom";
import { useAuth } from "@/contexts/AuthContext";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { Menu } from "lucide-react";

const PAGE_TITLES = {
  "/dashboard/play":        "Play",
  "/dashboard/performance": "Performance",
  "/dashboard/leaderboard": "Leaderboard",
  "/dashboard/settings":    "Paramètres",
  "/dashboard/about":       "À propos",
};

const Topbar = ({ onMenuClick }) => {
  const { pathname } = useLocation();
  const { user } = useAuth();

  const title = PAGE_TITLES[pathname]
    ?? (pathname.includes("/smatch/") ? "Smatch"
      : pathname.includes("/qcm/") ? "QCM"
      : "Dashboard");

  const initials = () => {
    if (user?.firstName && user?.lastName)
      return `${user.firstName[0]}${user.lastName[0]}`.toUpperCase();
    return user?.username?.slice(0, 2).toUpperCase() || "U";
  };

  return (
    <header className="fixed top-0 left-0 right-0 lg:left-[240px] z-20 h-16 px-5 bg-background/90 backdrop-blur-sm border-b border-border flex items-center justify-between">
      <div className="flex items-center gap-3">
        {/* Hamburger — mobile only */}
        <button
          onClick={onMenuClick}
          className="lg:hidden flex items-center justify-center w-9 h-9 rounded-xl text-muted-foreground hover:text-foreground hover:bg-muted transition-colors"
        >
          <Menu className="h-5 w-5" />
        </button>
        <h1 className="text-lg font-black tracking-tight">{title}</h1>
      </div>

      <Avatar className="h-8 w-8 cursor-pointer hover:opacity-80 transition-opacity">
        <AvatarImage src={user?.avatar} />
        <AvatarFallback className="text-xs font-black">{initials()}</AvatarFallback>
      </Avatar>
    </header>
  );
};

export default Topbar;

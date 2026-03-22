import { useLocation } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/contexts/AuthContext";
import { Avatar, AvatarFallback, AvatarImage } from "@/components/ui/avatar";
import { cn } from "@/lib/utils";
import ThemeLangBox from "@/components/ThemeLangBox";

const pageTitles = {
  "/dashboard/play": "Play",
  "/dashboard/performance": "Performance",
  "/dashboard/leaderboard": "Leaderboard",
  "/dashboard/settings": "Settings",
  "/dashboard/about": "About",
};

const Topbar = () => {
  const location = useLocation();
  const { t } = useTranslation("common");
  const { user } = useAuth();

  const currentTitle = pageTitles[location.pathname] || "Dashboard";

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
    <header className="fixed top-0 right-0 z-30 h-16 px-6 bg-background/80 backdrop-blur-sm border-b flex items-center justify-between ml-[240px] w-[calc(100%-240px)]">
      {/* Titre de la section */}
      <div>
        <h1 className="text-xl font-semibold">{t(`sidebar.${location.pathname.split('/').pop()}`) || currentTitle}</h1>
      </div>

      {/* Zone droite - Theme/Language et Avatar */}
      <div className="flex items-center gap-4">
        {/* Theme & Language Switcher */}
        <ThemeLangBox />

        {/* Avatar cliquable */}
        <button className="flex items-center gap-2 hover:opacity-80 transition-opacity">
          <Avatar className="h-9 w-9">
            <AvatarImage src={user?.avatar} />
            <AvatarFallback>
              {getInitials(user?.firstName, user?.lastName, user?.username)}
            </AvatarFallback>
          </Avatar>
        </button>
      </div>
    </header>
  );
};

export default Topbar;

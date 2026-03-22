import { useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { HelpCircle, Zap, Heart, Star, Clock } from "lucide-react";
import GameCard from "@/components/dashboard/Play/GameCard";

const PlayPage = () => {
  const { t } = useTranslation("common");
  const navigate = useNavigate();

  const handleStartQCM = () => {
    navigate("/dashboard/play/qcm/config");
  };

  const handleStartSMatch = () => {
    console.log("SMatch coming soon...");
  };

  const qcmRules = [
    { icon: <Clock className="w-4 h-4" />, label: t("play.qcm.globalTimer") },
    { icon: <Heart className="w-4 h-4" />, label: t("play.qcm.lives") },
    { icon: <Star className="w-4 h-4" />, label: t("play.qcm.points") },
  ];

  const sMatchRules = [
    { icon: <Clock className="w-4 h-4" />, label: t("play.smatch.timer") },
    { icon: <Heart className="w-4 h-4" />, label: t("play.smatch.lives") },
    { icon: <Star className="w-4 h-4" />, label: t("play.smatch.combo") },
  ];

  return (
    <div className="p-6 space-y-6 max-w-6xl mx-auto">
      {/* Zone Jeux */}
      <div className="space-y-4">
        <h2 className="text-xl font-semibold">{t("play.title")}</h2>
        
        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {/* QCM - Active */}
          <GameCard
            title={t("play.qcm.title")}
            description={t("play.qcm.description")}
            icon={HelpCircle}
            rules={qcmRules}
            isActive={true}
            onPlay={handleStartQCM}
          />

          {/* SMATCH - Coming Soon */}
          <GameCard
            title={t("play.smatch.title")}
            description={t("play.smatch.description")}
            icon={Zap}
            rules={sMatchRules}
            isActive={false}
            onPlay={handleStartSMatch}
            comingSoon={true}
          />
        </div>
      </div>
    </div>
  );
};

export default PlayPage;

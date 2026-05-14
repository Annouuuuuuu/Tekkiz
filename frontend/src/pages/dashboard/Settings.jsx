import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/contexts/AuthContext";
import { userService } from "@/services";
import qcmGameService from "@/services/qcmGame.service";
import { User, Lock, Trash2, RefreshCw, ExternalLink, Loader2, Globe } from "lucide-react";
import LanguageSwitcher from "@/components/LanguageSwitcher";

const SettingsPage = () => {
  const { t } = useTranslation("common");
  const { user, updateUser, logout } = useAuth();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [isResetting, setIsResetting] = useState(false);

  useEffect(() => {
    if (user) {
      setFirstName(user.firstName || "");
      setLastName(user.lastName || "");
      setUsername(user.username || "");
      setEmail(user.email || "");
    }
  }, [user]);

  const isOAuthUser = user?.provider && user.provider !== "LOCAL" && user.provider !== "local";

  const handleSaveAccount = async (e) => {
    e.preventDefault();

    if (!user?.id) return;

    setIsSaving(true);
    try {
      const result = await userService.updateProfile(user.id, {
        firstName,
        lastName,
        username,
      });

      if (result.success) {
        updateUser(result.data);
      }
    } finally {
      setIsSaving(false);
    }
  };

  const handleResetStats = async () => {
    if (!window.confirm("Reset all your QCM statistics? This cannot be undone.")) return;
    setIsResetting(true);
    try {
      await qcmGameService.resetUserStats();
    } finally {
      setIsResetting(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!user?.id) return;

    if (window.confirm(t("settings.deleteConfirm"))) {
      setIsDeleting(true);
      try {
        const result = await userService.deleteAccount(user.id);

        if (result.success) {
          await logout();
        }
      } finally {
        setIsDeleting(false);
      }
    }
  };

  const getProviderLabel = (provider) => {
    switch (provider?.toUpperCase()) {
      case "GOOGLE":
        return "Google";
      case "GITHUB":
        return "GitHub";
      default:
        return provider;
    }
  };

  const inputClass =
    "w-full px-3 py-2.5 text-sm rounded-xl border border-border bg-background focus:outline-none focus:ring-2 focus:ring-primary/30";
  const labelClass =
    "block text-xs font-semibold uppercase tracking-wider text-muted-foreground mb-1.5";

  return (
    <div className="max-w-2xl mx-auto px-6 py-8 space-y-4">
      {/* Page header */}
      <div>
        <p className="text-xs font-semibold uppercase tracking-widest text-muted-foreground">Compte</p>
        <h1 className="mt-1 text-3xl font-black tracking-tight">{t("settings.account")}</h1>
      </div>

      {/* Account section */}
      <div className="rounded-2xl border border-border bg-card p-6 space-y-5">
        <div className="flex items-center gap-2">
          <User className="w-4 h-4 text-primary" />
          <span className="text-sm font-bold">{t("settings.account")}</span>
        </div>
        <p className="text-xs text-muted-foreground -mt-3">{t("settings.accountDescription")}</p>

        <form onSubmit={handleSaveAccount} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass} htmlFor="firstName">
                {t("settings.firstName")}
              </label>
              <input
                id="firstName"
                className={inputClass}
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                placeholder={t("settings.firstNamePlaceholder")}
              />
            </div>
            <div>
              <label className={labelClass} htmlFor="lastName">
                {t("settings.lastName")}
              </label>
              <input
                id="lastName"
                className={inputClass}
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                placeholder={t("settings.lastNamePlaceholder")}
              />
            </div>
          </div>

          <div>
            <label className={labelClass} htmlFor="username">
              {t("settings.username")}
            </label>
            <input
              id="username"
              className={inputClass}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder={t("settings.usernamePlaceholder")}
            />
          </div>

          <div>
            <label className={labelClass} htmlFor="email">
              {t("settings.email")}
            </label>
            <input
              id="email"
              type="email"
              className={`${inputClass} ${isOAuthUser ? "opacity-60 cursor-not-allowed" : ""}`}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder={t("settings.emailPlaceholder")}
              disabled={isOAuthUser}
            />
            {isOAuthUser && (
              <p className="text-xs text-muted-foreground mt-1.5 flex items-center gap-1">
                <ExternalLink className="w-3 h-3" />
                {t("settings.emailManagedBy", { provider: getProviderLabel(user.provider) })}
              </p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSaving}
            className="px-5 py-2.5 rounded-xl bg-primary text-primary-foreground text-sm font-bold hover:bg-primary/90 transition-colors disabled:opacity-40 flex items-center gap-2"
          >
            {isSaving && <Loader2 className="w-4 h-4 animate-spin" />}
            {t("settings.saveChanges")}
          </button>
        </form>
      </div>

      {/* OAuth security section */}
      {isOAuthUser && (
        <div className="rounded-2xl border border-border bg-card p-6 space-y-4">
          <div className="flex items-center gap-2">
            <Lock className="w-4 h-4 text-primary" />
            <span className="text-sm font-bold">{t("settings.security")}</span>
          </div>
          <p className="text-xs text-muted-foreground -mt-2">{t("settings.securityDescription")}</p>

          <div className="rounded-xl border border-border bg-background p-4 flex items-start gap-3">
            <ExternalLink className="w-4 h-4 text-muted-foreground mt-0.5 shrink-0" />
            <div>
              <p className="text-sm font-semibold">
                {t("settings.signedInWith", { provider: getProviderLabel(user.provider) })}
              </p>
              <p className="text-xs text-muted-foreground mt-0.5">
                {t("settings.passwordManagedBy", { provider: getProviderLabel(user.provider) })}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Preferences section */}
      <div className="rounded-2xl border border-border bg-card p-6 space-y-4">
        <div className="flex items-center gap-2">
          <Globe className="w-4 h-4 text-primary" />
          <span className="text-sm font-bold">{t("settings.preferences")}</span>
        </div>
        <p className="text-xs text-muted-foreground -mt-2">{t("settings.preferencesDescription")}</p>

        <div className="flex items-center justify-between gap-4 rounded-xl border border-border bg-background p-4">
          <div>
            <p className="text-sm font-semibold">{t("settings.language")}</p>
            <p className="text-xs text-muted-foreground mt-0.5">{t("settings.languageDescription")}</p>
          </div>
          <LanguageSwitcher variant="light" />
        </div>
      </div>

      {/* Danger zone */}
      <div className="rounded-2xl border border-red-500/20 bg-red-500/5 p-6 space-y-5">
        <div className="flex items-center gap-2">
          <Trash2 className="w-4 h-4 text-red-400" />
          <span className="text-sm font-bold text-red-400">{t("settings.data")}</span>
        </div>
        <p className="text-xs text-muted-foreground -mt-3">{t("settings.dataDescription")}</p>

        {/* Reset stats */}
        <div className="flex items-center justify-between gap-4 rounded-xl border border-border bg-background p-4">
          <div>
            <p className="text-sm font-semibold">{t("settings.resetStatistics")}</p>
            <p className="text-xs text-muted-foreground mt-0.5">
              {t("settings.resetStatisticsDescription")}
            </p>
          </div>
          <button
            onClick={handleResetStats}
            disabled={isResetting}
            className="px-4 py-2 rounded-xl border border-border bg-card text-sm font-bold hover:bg-muted/30 transition-colors flex items-center gap-2 shrink-0 disabled:opacity-50"
          >
            {isResetting ? <Loader2 className="w-4 h-4 animate-spin" /> : <RefreshCw className="w-4 h-4" />}
            {t("settings.reset")}
          </button>
        </div>

        {/* Delete account */}
        <div className="flex items-center justify-between gap-4 rounded-xl border border-red-500/20 bg-background p-4">
          <div>
            <p className="text-sm font-semibold text-red-400">{t("settings.deleteAccount")}</p>
            <p className="text-xs text-muted-foreground mt-0.5">
              {t("settings.deleteAccountDescription")}
            </p>
          </div>
          <button
            onClick={handleDeleteAccount}
            disabled={isDeleting}
            className="px-4 py-2 rounded-xl border border-red-500/30 bg-red-500/10 text-red-400 text-sm font-bold hover:bg-red-500/20 transition-colors flex items-center gap-2 shrink-0 disabled:opacity-50"
          >
            {isDeleting ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : (
              <Trash2 className="w-4 h-4" />
            )}
            {t("settings.delete")}
          </button>
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;

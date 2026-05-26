import { useState, useEffect } from "react";
import { useAuth } from "@/contexts/AuthContext";
import { userService } from "@/services";
import qcmGameService from "@/services/qcmGame.service";
import { User, Lock, Trash2, RefreshCw, ExternalLink, Loader2 } from "lucide-react";

const SettingsPage = () => {
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
    if (!window.confirm("Réinitialiser toutes vos statistiques QCM ? Cette action est irréversible.")) return;
    setIsResetting(true);
    try {
      await qcmGameService.resetUserStats();
    } finally {
      setIsResetting(false);
    }
  };

  const handleDeleteAccount = async () => {
    if (!user?.id) return;

    if (window.confirm("Êtes-vous sûr de vouloir supprimer votre compte ? Cette action est irréversible.")) {
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
        <h1 className="mt-1 text-3xl font-black tracking-tight">Compte</h1>
      </div>

      {/* Account section */}
      <div className="rounded-2xl border border-border bg-card p-6 space-y-5">
        <div className="flex items-center gap-2">
          <User className="w-4 h-4 text-primary" />
          <span className="text-sm font-bold">Compte</span>
        </div>
        <p className="text-xs text-muted-foreground -mt-3">Gérez les informations de votre compte</p>

        <form onSubmit={handleSaveAccount} className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div>
              <label className={labelClass} htmlFor="firstName">
                Prénom
              </label>
              <input
                id="firstName"
                className={inputClass}
                value={firstName}
                onChange={(e) => setFirstName(e.target.value)}
                placeholder="Entrez votre prénom"
              />
            </div>
            <div>
              <label className={labelClass} htmlFor="lastName">
                Nom
              </label>
              <input
                id="lastName"
                className={inputClass}
                value={lastName}
                onChange={(e) => setLastName(e.target.value)}
                placeholder="Entrez votre nom"
              />
            </div>
          </div>

          <div>
            <label className={labelClass} htmlFor="username">
              Nom d'utilisateur
            </label>
            <input
              id="username"
              className={inputClass}
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="Entrez votre nom d'utilisateur"
            />
          </div>

          <div>
            <label className={labelClass} htmlFor="email">
              Email
            </label>
            <input
              id="email"
              type="email"
              className={`${inputClass} ${isOAuthUser ? "opacity-60 cursor-not-allowed" : ""}`}
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Entrez votre email"
              disabled={isOAuthUser}
            />
            {isOAuthUser && (
              <p className="text-xs text-muted-foreground mt-1.5 flex items-center gap-1">
                <ExternalLink className="w-3 h-3" />
                {`Email géré par ${getProviderLabel(user.provider)}`}
              </p>
            )}
          </div>

          <button
            type="submit"
            disabled={isSaving}
            className="px-5 py-2.5 rounded-xl bg-primary text-primary-foreground text-sm font-bold hover:bg-primary/90 transition-colors disabled:opacity-40 flex items-center gap-2"
          >
            {isSaving && <Loader2 className="w-4 h-4 animate-spin" />}
            Enregistrer
          </button>
        </form>
      </div>

      {/* OAuth security section */}
      {isOAuthUser && (
        <div className="rounded-2xl border border-border bg-card p-6 space-y-4">
          <div className="flex items-center gap-2">
            <Lock className="w-4 h-4 text-primary" />
            <span className="text-sm font-bold">Sécurité</span>
          </div>
          <p className="text-xs text-muted-foreground -mt-2">Compte OAuth</p>

          <div className="rounded-xl border border-border bg-background p-4 flex items-start gap-3">
            <ExternalLink className="w-4 h-4 text-muted-foreground mt-0.5 shrink-0" />
            <div>
              <p className="text-sm font-semibold">
                {`Connecté avec ${getProviderLabel(user.provider)}`}
              </p>
              <p className="text-xs text-muted-foreground mt-0.5">
                {`Votre mot de passe est géré par ${getProviderLabel(user.provider)}. Modifiez-le depuis votre compte ${getProviderLabel(user.provider)}.`}
              </p>
            </div>
          </div>
        </div>
      )}

      {/* Danger zone */}
      <div className="rounded-2xl border border-red-500/20 bg-red-500/5 p-6 space-y-5">
        <div className="flex items-center gap-2">
          <Trash2 className="w-4 h-4 text-red-400" />
          <span className="text-sm font-bold text-red-400">Données</span>
        </div>
        <p className="text-xs text-muted-foreground -mt-3">Gérez les données de votre compte</p>

        {/* Reset stats */}
        <div className="flex items-center justify-between gap-4 rounded-xl border border-border bg-background p-4">
          <div>
            <p className="text-sm font-semibold">Réinitialiser les statistiques</p>
            <p className="text-xs text-muted-foreground mt-0.5">
              Remettre toutes vos statistiques de jeu à zéro
            </p>
          </div>
          <button
            onClick={handleResetStats}
            disabled={isResetting}
            className="px-4 py-2 rounded-xl border border-border bg-card text-sm font-bold hover:bg-muted/30 transition-colors flex items-center gap-2 shrink-0 disabled:opacity-50"
          >
            {isResetting ? <Loader2 className="w-4 h-4 animate-spin" /> : <RefreshCw className="w-4 h-4" />}
            Réinitialiser
          </button>
        </div>

        {/* Delete account */}
        <div className="flex items-center justify-between gap-4 rounded-xl border border-red-500/20 bg-background p-4">
          <div>
            <p className="text-sm font-semibold text-red-400">Supprimer le compte</p>
            <p className="text-xs text-muted-foreground mt-0.5">
              Supprimer définitivement votre compte et toutes vos données
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
            Supprimer
          </button>
        </div>
      </div>
    </div>
  );
};

export default SettingsPage;

import { useState, useEffect } from "react";
import { useTranslation } from "react-i18next";
import { useAuth } from "@/contexts/AuthContext";
import { userService } from "@/services";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Separator } from "@/components/ui/separator";
import { User, Lock, Trash2, RefreshCw, ExternalLink, Loader2 } from "lucide-react";

const SettingsPage = () => {
  const { t } = useTranslation("common");
  const { user, updateUser, logout } = useAuth();
  const [firstName, setFirstName] = useState("");
  const [lastName, setLastName] = useState("");
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [isSaving, setIsSaving] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);

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

  const handleResetStats = () => {
    if (window.confirm(t("settings.resetConfirm"))) {
      console.log("Resetting statistics...");
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

  return (
    <div className="p-6 space-y-6 max-w-2xl mx-auto">
      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <User className="w-5 h-5 text-primary" />
            <CardTitle>{t("settings.account")}</CardTitle>
          </div>
          <CardDescription>{t("settings.accountDescription")}</CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={handleSaveAccount} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">{t("settings.firstName")}</Label>
                <Input
                  id="firstName"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                  placeholder={t("settings.firstNamePlaceholder")}
                />
              </div>
              <div className="space-y-2">
                <Label htmlFor="lastName">{t("settings.lastName")}</Label>
                <Input
                  id="lastName"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                  placeholder={t("settings.lastNamePlaceholder")}
                />
              </div>
            </div>
            <div className="space-y-2">
              <Label htmlFor="username">{t("settings.username")}</Label>
              <Input
                id="username"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder={t("settings.usernamePlaceholder")}
              />
            </div>
            <div className="space-y-2">
              <Label htmlFor="email">{t("settings.email")}</Label>
              <Input
                id="email"
                type="email"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder={t("settings.emailPlaceholder")}
                disabled={isOAuthUser}
                className={isOAuthUser ? "bg-muted" : ""}
              />
              {isOAuthUser && (
                <p className="text-xs text-muted-foreground flex items-center gap-1">
                  <ExternalLink className="w-3 h-3" />
                  {t("settings.emailManagedBy", { provider: getProviderLabel(user.provider) })}
                </p>
              )}
            </div>
            <Button type="submit" disabled={isSaving}>
              {isSaving && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
              {t("settings.saveChanges")}
            </Button>
          </form>
        </CardContent>
      </Card>

      {isOAuthUser && (
        <>
          <Separator />

          <Card>
            <CardHeader>
              <div className="flex items-center gap-2">
                <Lock className="w-5 h-5 text-primary" />
                <CardTitle>{t("settings.security")}</CardTitle>
              </div>
              <CardDescription>{t("settings.securityDescription")}</CardDescription>
            </CardHeader>
            <CardContent>
              <div className="p-4 rounded-lg border bg-muted/50">
                <div className="flex items-center gap-2">
                  <ExternalLink className="w-4 w-4" />
                  <span className="font-medium">{t("settings.signedInWith", { provider: getProviderLabel(user.provider) })}</span>
                </div>
                <p className="text-sm text-muted-foreground mt-1">
                  {t("settings.passwordManagedBy", { provider: getProviderLabel(user.provider) })}
                </p>
              </div>
            </CardContent>
          </Card>
        </>
      )}

      <Separator />

      <Card>
        <CardHeader>
          <div className="flex items-center gap-2">
            <Trash2 className="w-5 h-5 text-primary" />
            <CardTitle>{t("settings.data")}</CardTitle>
          </div>
          <CardDescription>{t("settings.dataDescription")}</CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="flex items-center justify-between p-3 rounded-lg border">
            <div>
              <p className="font-medium">{t("settings.resetStatistics")}</p>
              <p className="text-sm text-muted-foreground">
                {t("settings.resetStatisticsDescription")}
              </p>
            </div>
            <Button variant="outline" onClick={handleResetStats}>
              <RefreshCw className="w-4 h-4 mr-2" />
              {t("settings.reset")}
            </Button>
          </div>

          <div className="flex items-center justify-between p-3 rounded-lg border border-red-200 bg-red-50">
            <div>
              <p className="font-medium text-red-600">{t("settings.deleteAccount")}</p>
              <p className="text-sm text-muted-foreground">
                {t("settings.deleteAccountDescription")}
              </p>
            </div>
            <Button variant="destructive" onClick={handleDeleteAccount} disabled={isDeleting}>
              {isDeleting && <Loader2 className="w-4 h-4 mr-2 animate-spin" />}
              <Trash2 className="w-4 h-4 mr-2" />
              {t("settings.delete")}
            </Button>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

export default SettingsPage;

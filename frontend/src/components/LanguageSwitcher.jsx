import { useTranslation } from "react-i18next";

const LANGS = ["fr", "en"];

const LanguageSwitcher = ({ variant = "dark" }) => {
  const { i18n } = useTranslation();
  const current = i18n.language?.startsWith("fr") ? "fr" : "en";

  const base =
    variant === "dark"
      ? "px-2.5 py-1 text-[11px] font-bold uppercase tracking-widest rounded-md transition-colors"
      : "px-3 py-1.5 text-xs font-bold uppercase tracking-widest rounded-lg transition-colors";

  const active =
    variant === "dark"
      ? "bg-white/10 text-white"
      : "bg-primary/10 text-primary";

  const inactive =
    variant === "dark"
      ? "text-white/30 hover:text-white/60"
      : "text-muted-foreground hover:text-foreground";

  return (
    <div className="flex items-center gap-1">
      {LANGS.map((lang) => (
        <button
          key={lang}
          onClick={() => i18n.changeLanguage(lang)}
          className={`${base} ${current === lang ? active : inactive}`}
        >
          {lang}
        </button>
      ))}
    </div>
  );
};

export default LanguageSwitcher;

import { Github, Linkedin } from "lucide-react";
import ThemeLangBox from "../ThemeLangBox";
import { useTranslation } from "react-i18next";

const Footer = () => {
  const { t } = useTranslation("common");

  return (
    <footer className="relative mt-20 border-t border-border/40 bg-background/50 backdrop-blur-sm">
      <div className="max-w-7xl mx-auto px-6 py-12 flex flex-col md:flex-row items-start md:items-center justify-between gap-6">

        <div className="flex flex-col gap-3">
          <div className="flex items-center gap-2 font-black text-lg tracking-tighter text-foreground">
            <span>Tekizz</span>
          </div>
          <p className="text-sm text-muted-foreground max-w-xs">
            {t("footer.tagline")}
          </p>
        </div>

        <div className="flex flex-col items-start md:items-end gap-4 mt-4 md:mt-0">
          <div className="flex gap-5 items-center">
            <a href="#" className="text-muted-foreground hover:text-primary transition-colors">
              <Github size={18} />
            </a>
            <a href="#" className="text-muted-foreground hover:text-primary transition-colors">
              <Linkedin size={18} />
            </a>
          </div>
          <span className="text-[10px] font-medium tracking-widest uppercase text-muted-foreground/50">
            © {new Date().getFullYear()} Tekizz. {t("footer.rights")}
          </span>
        </div>

        <div className="absolute bottom-4 right-6">
          <ThemeLangBox />
        </div>

      </div>
    </footer>
  );
};

export default Footer;

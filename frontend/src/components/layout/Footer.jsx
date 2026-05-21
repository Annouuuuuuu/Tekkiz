import { Github, Linkedin } from "lucide-react";
import { useTranslation } from "react-i18next";
import LanguageSwitcher from "../LanguageSwitcher";

const Footer = () => {
  const { t } = useTranslation("common");

  return (
    <footer className="bg-[#080808] border-t border-white/[0.06]">
      <div className="max-w-[1400px] mx-auto px-6 sm:px-10 lg:px-20 py-10 flex flex-col sm:flex-row items-center justify-between gap-6">
        
        <span className="text-xl font-black tracking-tighter text-white">
          Tekizz<span className="text-primary">.</span>
        </span>

        <div className="flex flex-col items-center gap-2">
          <span className="text-[11px] font-mono tracking-[0.18em] uppercase text-white/20 text-center">
            © {new Date().getFullYear()} Tekizz. {t("footer.rights")}
          </span>

          <a
            href="/privacy"
            className="text-xs text-white/40 hover:text-primary transition-colors"
          >
            Privacy Policy
          </a>
        </div>

        <div className="flex gap-4 items-center">
          <LanguageSwitcher variant="dark" />

          <a
            href="https://github.com/LesCracks-OS/Tekkiz"
            target="_blank"
            rel="noopener noreferrer"
            className="text-white/25 hover:text-primary transition-colors"
          >
            <Github size={17} />
          </a>

          <a
            href="https://www.linkedin.com/company/lescracks/"
            target="_blank"
            rel="noopener noreferrer"
            className="text-white/25 hover:text-primary transition-colors"
          >
            <Linkedin size={17} />
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
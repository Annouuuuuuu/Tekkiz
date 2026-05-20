import { Github, Linkedin } from "lucide-react";
import { useTranslation } from "react-i18next";
import LanguageSwitcher from "../LanguageSwitcher";

const Footer = () => {
  const { t } = useTranslation("common");

  return (
    <footer className="bg-[#080808] border-t border-white/[0.06]">
      <div className="max-w-[1400px] mx-auto px-6 sm:px-10 lg:px-20 py-10 flex flex-col sm:flex-row items-center justify-between gap-4">
        <span className="text-xl font-black tracking-tighter text-white">
          Tekizz<span className="text-primary">.</span>
        </span>
        <span className="text-[11px] font-mono tracking-[0.18em] uppercase text-white/20">
          © {new Date().getFullYear()} Tekizz. {t("footer.rights")}
        </span>
        <div className="flex gap-4 items-center">
          <LanguageSwitcher variant="dark" />
          <a href="https://github.com/LesCracks-OS/Tekkiz" className="text-white/25 hover:text-primary transition-colors">
            <Github size={17} />
          </a>
          <a href="https://www.linkedin.com/company/lescracks/" className="text-white/25 hover:text-primary transition-colors">
            <Linkedin size={17} />
          </a>
        </div>
      </div>
    </footer>
  );
};

export default Footer;

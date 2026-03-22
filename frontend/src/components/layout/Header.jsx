import { useState, useEffect } from "react";
import { Link, useNavigate } from "react-router-dom";
import { useTranslation } from "react-i18next";
import { motion } from "framer-motion";
import { Button } from "@/components/ui/button";
import NavBar from "../NavBar";
import AuthModal from "../auth/AuthModal";

const Header = () => {
  const { t } = useTranslation("common");
  const [isScrolled, setIsScrolled] = useState(false);
  const [isAuthModalOpen, setIsAuthModalOpen] = useState(false);


  useEffect(() => {
    const handleScroll = () => setIsScrolled(window.scrollY > 20);
    window.addEventListener("scroll", handleScroll);
    return () => window.removeEventListener("scroll", handleScroll);
  }, []);


  return (
    <>
      <header className="fixed top-0 left-0 right-0 z-50 flex justify-center p-4 transition-all duration-500">
        <motion.div 
          className={`
            flex items-center justify-between transition-all duration-500 ease-in-out
            ${isScrolled 
              ? "w-full max-w-5xl bg-transparent backdrop-blur-xl border-y border-white/10 px-6 py-3 rounded-xl shadow-xl" 
              : "w-full max-w-7xl bg-transparent border-transparent px-2 py-4 shadow-none"
            }
          `}
        >
          <div className="flex items-center gap-12">
            <Link to="/" className="text-2xl font-black tracking-tighter">
              Tekizz<span className="text-primary">.</span>
            </Link>

            <NavBar />
          </div>

          <div className="flex items-center gap-4">
              <Button 
                onClick={() => setIsAuthModalOpen(true)}
                className="rounded-full px-6 bg-primary text-primary-foreground hover:opacity-90 shadow-lg shadow-primary/20"
              >
                {t("header.getStarted")}
              </Button>
          </div>
        </motion.div>
      </header>

      <AuthModal 
        isOpen={isAuthModalOpen} 
        onClose={() => setIsAuthModalOpen(false)} 
        initialMode="signup"
      />
    </>
  );
};

export default Header;

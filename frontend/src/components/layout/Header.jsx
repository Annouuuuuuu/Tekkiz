import { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import AuthModal from "../auth/AuthModal";

const Header = () => {
  const [scrolled, setScrolled] = useState(false);
  const [authOpen, setAuthOpen] = useState(false);
  const [authMode, setAuthMode] = useState("signup");

  useEffect(() => {
    const fn = () => setScrolled(window.scrollY > 24);
    window.addEventListener("scroll", fn);
    return () => window.removeEventListener("scroll", fn);
  }, []);

  const open = (mode) => { setAuthMode(mode); setAuthOpen(true); };

  return (
    <>
      <header className="fixed top-0 left-0 right-0 z-50 flex justify-center px-4 pt-4 transition-all duration-300">
        <div
          className={`flex items-center justify-between w-full transition-all duration-300 ${
            scrolled
              ? "max-w-5xl bg-[#0d0d0d]/90 backdrop-blur-xl border border-white/[0.08] px-6 py-3 rounded-2xl shadow-2xl shadow-black/50"
              : "max-w-7xl px-2 py-3"
          }`}
        >
          <Link to="/" className="text-xl font-black tracking-tighter text-white">
            Tekizz<span className="text-primary">.</span>
          </Link>

          <div className="flex items-center gap-2">
            <button
              onClick={() => open("login")}
              className="hidden sm:inline-flex items-center px-5 py-2.5 rounded-full text-sm font-semibold text-white/45 hover:text-white/80 transition-colors"
            >
              Connexion
            </button>
            <button
              onClick={() => open("signup")}
              className="inline-flex items-center rounded-full bg-primary px-6 py-2.5 text-sm font-bold text-white shadow-lg shadow-primary/20 hover:brightness-110 hover:shadow-primary/35 transition-all"
            >
              Commencer
            </button>
          </div>
        </div>
      </header>

      <AuthModal isOpen={authOpen} onClose={() => setAuthOpen(false)} initialMode={authMode} />
    </>
  );
};

export default Header;

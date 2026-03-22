import React from 'react';
import { Link, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

const NavBar = () => {
  const { t } = useTranslation('common');
  const location = useLocation();

  const scrollToSection = (sectionId) => {
    if (location.pathname !== '/') {
      window.location.href = '/' + sectionId;
      return;
    }
    const element = document.getElementById(sectionId);
    if (element) {
      element.scrollIntoView({ behavior: 'smooth' });
    }
  };

  return (
    <nav className="hidden md:flex items-center gap-8 text-sm font-medium">
      <button 
        onClick={() => scrollToSection('features')}
        className="text-muted-foreground hover:text-foreground transition-colors cursor-pointer"
      >
        {t("header.features")}
      </button>
      <button 
        onClick={() => scrollToSection('faq')}
        className="text-muted-foreground hover:text-foreground transition-colors cursor-pointer"
      >
        {t("header.faq")}
      </button>
    </nav>
  );
};

export default NavBar;

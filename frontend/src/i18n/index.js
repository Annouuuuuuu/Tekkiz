import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

import enCommon from './locales/en/common.json';
import enHome from './locales/en/home.json';
import frCommon from './locales/fr/common.json';
import frHome from './locales/fr/home.json';

const resources = {
  en: { common: enCommon, home: enHome },
  fr: { common: frCommon, home: frHome },
};

const savedLang = localStorage.getItem('tekizz-lang') || 'fr';

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: savedLang,
    fallbackLng: 'fr',
    interpolation: { escapeValue: false },
  });

i18n.on('languageChanged', (lng) => {
  localStorage.setItem('tekizz-lang', lng);
});

export default i18n;

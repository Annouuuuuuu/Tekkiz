import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';

// Import translation files
import enCommon from './locales/en/common.json';
import enHome from './locales/en/home.json';
import frCommon from './locales/fr/common.json';
import frHome from './locales/fr/home.json';

const resources = {
  en: {
    common: enCommon,
    home: enHome,
  },
  fr: {
    common: frCommon,
    home: frHome,
  },
};

i18n
  .use(initReactI18next)
  .init({
    resources,
    lng: 'en', // default language
    fallbackLng: 'en',
    interpolation: {
      escapeValue: false, // React already escapes
    },
  });

export default i18n;
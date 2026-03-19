import i18n from 'i18next';
import { initReactI18next } from 'react-i18next';
import enCommon from './locales/en-US/common.json';
import zhCommon from './locales/zh-CN/common.json';

const LANG_KEY = 'devflow.lang';
const fallbackLng = 'en-US';

const detectLanguage = (): string => {
  const stored = localStorage.getItem(LANG_KEY);
  if (stored === 'en-US' || stored === 'zh-CN') {
    return stored;
  }
  return navigator.language.startsWith('zh') ? 'zh-CN' : fallbackLng;
};

i18n.use(initReactI18next).init({
  resources: {
    'en-US': { translation: enCommon },
    'zh-CN': { translation: zhCommon }
  },
  lng: detectLanguage(),
  fallbackLng,
  interpolation: {
    escapeValue: false
  }
});

i18n.on('languageChanged', (language) => {
  localStorage.setItem(LANG_KEY, language);
});

export default i18n;

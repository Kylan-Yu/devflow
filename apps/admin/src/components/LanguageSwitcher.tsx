import { useTranslation } from 'react-i18next';

const LANG_KEY = 'devflow.lang';

export default function LanguageSwitcher() {
  const { i18n, t } = useTranslation();

  const onChange = (value: string) => {
    i18n.changeLanguage(value);
    localStorage.setItem(LANG_KEY, value);
  };

  return (
    <div className="lang-switcher">
      <label htmlFor="lang-select">{t('common.language')}</label>
      <select id="lang-select" value={i18n.language} onChange={(event) => onChange(event.target.value)}>
        <option value="en-US">English</option>
        <option value="zh-CN">中文</option>
      </select>
    </div>
  );
}

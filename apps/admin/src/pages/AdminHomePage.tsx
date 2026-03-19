import { useTranslation } from 'react-i18next';
import { Link } from 'react-router-dom';
import LanguageSwitcher from '../components/LanguageSwitcher';

export default function AdminHomePage() {
  const { t } = useTranslation();
  const adminName = localStorage.getItem('devflow.admin.displayName');

  return (
    <main className="page-shell">
      <header className="top-row">
        <h1>{t('app.title')}</h1>
        <LanguageSwitcher />
      </header>

      <p>{t('admin.subtitle')}</p>
      <p className="hint-text">
        {adminName ? `${t('admin.current_admin')}: ${adminName}` : t('admin.no_session')}
      </p>

      <Link to="/login" className="btn btn-secondary">
        {t('admin.back_to_login')}
      </Link>
    </main>
  );
}

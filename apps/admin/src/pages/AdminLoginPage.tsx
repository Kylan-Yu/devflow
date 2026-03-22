import { FormEvent, useState } from 'react';
import { Navigate, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { adminLogin } from '../api/adminAuth';
import LanguageSwitcher from '../components/LanguageSwitcher';
import { useAdminAccessToken } from '../hooks/useAdminAccessToken';
import { saveAdminSession } from '../utils/adminSession';

export default function AdminLoginPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const accessToken = useAdminAccessToken();

  const [username, setUsername] = useState('admin');
  const [password, setPassword] = useState('Admin@123456');
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage(null);

    try {
      const session = await adminLogin({ username, password });
      saveAdminSession(session.accessToken, session.adminId, session.displayName);
      setMessage(t('messages.admin.login_success'));
      navigate('/dashboard');
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setSubmitting(false);
    }
  };

  if (accessToken) {
    return <Navigate to="/dashboard" replace />;
  }

  return (
    <main className="page-shell auth-shell">
      <header className="top-row">
        <h1>{t('admin.login_title')}</h1>
        <LanguageSwitcher />
      </header>

      <form className="auth-form" onSubmit={submit}>
        <label>
          {t('admin.username')}
          <input value={username} onChange={(event) => setUsername(event.target.value)} required />
        </label>

        <label>
          {t('admin.password')}
          <input
            type="password"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
          />
        </label>

        <button type="submit" className="btn btn-primary" disabled={submitting}>
          {submitting ? t('common.loading') : t('admin.login')}
        </button>
      </form>

      {message ? <p className="hint-text">{message}</p> : null}
    </main>
  );
}

import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { login } from '../api/auth';
import LanguageSwitcher from '../components/LanguageSwitcher';
import { saveSession } from '../utils/authStorage';

export default function LoginPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();

  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage(null);

    try {
      const session = await login({ email, password });
      saveSession(session.tokens.accessToken, session.tokens.refreshToken, session.user.id);
      if (session.user.preferredLanguage !== i18n.language) {
        await i18n.changeLanguage(session.user.preferredLanguage);
      }
      setMessage(t('messages.auth.login_success'));
      navigate('/');
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <main className="page-shell auth-shell">
      <header className="top-row">
        <h1>{t('auth.login_title')}</h1>
        <LanguageSwitcher />
      </header>

      <form className="auth-form" onSubmit={submit}>
        <label>
          {t('auth.email')}
          <input
            type="email"
            required
            value={email}
            onChange={(event) => setEmail(event.target.value)}
          />
        </label>

        <label>
          {t('auth.password')}
          <input
            type="password"
            required
            minLength={8}
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </label>

        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? t('common.loading') : t('auth.login')}
        </button>
      </form>

      {message ? <p className="hint-text">{message}</p> : null}

      <p className="hint-text">
        {t('auth.no_account')}{' '}
        <Link to="/register">{t('auth.register')}</Link>
      </p>
    </main>
  );
}

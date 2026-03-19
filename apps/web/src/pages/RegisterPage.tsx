import { FormEvent, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { register, type LanguageValue } from '../api/auth';
import LanguageSwitcher from '../components/LanguageSwitcher';
import { saveSession } from '../utils/authStorage';

export default function RegisterPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();

  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [password, setPassword] = useState('');
  const [preferredLanguage, setPreferredLanguage] = useState<LanguageValue>(
    i18n.language === 'zh-CN' ? 'zh-CN' : 'en-US'
  );
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage(null);

    try {
      const session = await register({
        username,
        email,
        displayName,
        password,
        preferredLanguage
      });
      saveSession(session.tokens.accessToken, session.tokens.refreshToken, session.user.id);
      setMessage(t('messages.auth.register_success'));
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
        <h1>{t('auth.register_title')}</h1>
        <LanguageSwitcher />
      </header>

      <form className="auth-form" onSubmit={submit}>
        <label>
          {t('auth.username')}
          <input value={username} required onChange={(event) => setUsername(event.target.value)} />
        </label>

        <label>
          {t('auth.display_name')}
          <input value={displayName} required onChange={(event) => setDisplayName(event.target.value)} />
        </label>

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
            minLength={8}
            required
            value={password}
            onChange={(event) => setPassword(event.target.value)}
          />
        </label>

        <label>
          {t('auth.preferred_language')}
          <select
            value={preferredLanguage}
            onChange={(event) => setPreferredLanguage(event.target.value as LanguageValue)}
          >
            <option value="en-US">English</option>
            <option value="zh-CN">中文</option>
          </select>
        </label>

        <button className="btn btn-primary" type="submit" disabled={submitting}>
          {submitting ? t('common.loading') : t('auth.register')}
        </button>
      </form>

      {message ? <p className="hint-text">{message}</p> : null}

      <p className="hint-text">
        {t('auth.has_account')}{' '}
        <Link to="/login">{t('auth.login')}</Link>
      </p>
    </main>
  );
}

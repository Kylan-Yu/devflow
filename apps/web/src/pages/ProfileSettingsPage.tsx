import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { uploadAvatarImage } from '../api/media';
import {
  getCurrentUserProfile,
  updateCurrentUserProfile,
  type UpdateCurrentUserProfilePayload
} from '../api/users';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import BackButton from '../components/BackButton';

export default function ProfileSettingsPage() {
  const { i18n, t } = useTranslation();
  const currentUserId = useCurrentUserId();

  const [displayName, setDisplayName] = useState('');
  const [bio, setBio] = useState('');
  const [avatarUrl, setAvatarUrl] = useState('');
  const [preferredLanguage, setPreferredLanguage] = useState<'zh-CN' | 'en-US'>('en-US');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [createdAt, setCreatedAt] = useState('');
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [uploadingAvatar, setUploadingAvatar] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!currentUserId) {
      setLoading(false);
      return;
    }

    const loadProfile = async () => {
      setLoading(true);
      setMessage(null);
      try {
        const profile = await getCurrentUserProfile();
        setDisplayName(profile.displayName);
        setBio(profile.bio ?? '');
        setAvatarUrl(profile.avatarUrl ?? '');
        setPreferredLanguage(profile.preferredLanguage);
        setUsername(profile.username);
        setEmail(profile.email);
        setCreatedAt(profile.createdAt);
      } catch (error) {
        const text = error instanceof Error ? error.message : 'common.request_failed';
        setMessage(t(`messages.${text}`, { defaultValue: text }));
      } finally {
        setLoading(false);
      }
    };

    loadProfile().catch(() => undefined);
  }, [currentUserId, t]);

  const memberSince = useMemo(() => {
    if (!createdAt) {
      return '-';
    }

    const date = new Date(createdAt);
    if (Number.isNaN(date.getTime())) {
      return createdAt;
    }

    return date.toLocaleDateString(i18n.language === 'zh-CN' ? 'zh-CN' : 'en-US');
  }, [createdAt, i18n.language]);

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSaving(true);
    setMessage(null);

    const payload: UpdateCurrentUserProfilePayload = {
      displayName: displayName.trim(),
      bio,
      avatarUrl,
      preferredLanguage
    };

    try {
      const profile = await updateCurrentUserProfile(payload);
      setDisplayName(profile.displayName);
      setBio(profile.bio ?? '');
      setAvatarUrl(profile.avatarUrl ?? '');
      setPreferredLanguage(profile.preferredLanguage);

      if (profile.preferredLanguage !== i18n.language) {
        await i18n.changeLanguage(profile.preferredLanguage);
      }

      setMessage(t('messages.profile.update_success'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setSaving(false);
    }
  };

  const onAvatarSelected = async (file: File | null) => {
    if (!file) {
      return;
    }

    setUploadingAvatar(true);
    setMessage(null);
    try {
      const uploaded = await uploadAvatarImage(file);
      setAvatarUrl(uploaded.url);
      setMessage(t('messages.media.avatar_upload_success'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setUploadingAvatar(false);
    }
  };

  if (!currentUserId) {
    return (
      <main className="page-shell">
        <h1>{t('profile.settings_title')}</h1>
        <p className="hint-text">{t('profile.login_required')}</p>
        <Link className="btn btn-primary" to="/login">
          {t('auth.login')}
        </Link>
      </main>
    );
  }

  if (loading) {
    return (
      <main className="page-shell auth-shell-wide">
        <h1>{t('profile.settings_title')}</h1>
        <p className="hint-text">{t('common.loading')}</p>
      </main>
    );
  }

  return (
    <main className="page-shell auth-shell-wide">
      <header className="top-row">
        <div className="header-left">
          <BackButton fallback="/" />
          <div>
            <h1>{t('profile.settings_title')}</h1>
            <p className="hint-text">{t('profile.settings_description')}</p>
          </div>
        </div>

        <div className="action-row compact-row">
          <Link className="btn btn-secondary" to={`/users/${currentUserId}`}>
            {t('profile.view_public_profile')}
          </Link>
        </div>
      </header>

      <div className="settings-layout">
        <section className="profile-card settings-card">
          <div className="avatar-block">
            {avatarUrl ? (
              <img className="avatar-preview" src={avatarUrl} alt={displayName} />
            ) : (
              <div className="avatar-placeholder">
                {(displayName || username || 'D').slice(0, 1).toUpperCase()}
              </div>
            )}
          </div>

          <h2>{t('profile.account_overview')}</h2>

          <div className="readonly-field">
            <span className="readonly-label">{t('profile.username_label')}</span>
            <strong>@{username}</strong>
          </div>

          <div className="readonly-field">
            <span className="readonly-label">{t('profile.email_label')}</span>
            <strong>{email}</strong>
          </div>

          <div className="readonly-field">
            <span className="readonly-label">{t('profile.member_since')}</span>
            <strong>{memberSince}</strong>
          </div>

          <div className="readonly-field is-last">
            <span className="readonly-label">{t('profile.bio_label')}</span>
            <p className="hint-text">{bio.trim() ? bio : t('profile.no_bio')}</p>
          </div>
        </section>

        <form className="auth-form settings-form" onSubmit={submit}>
          <label>
            {t('profile.avatar_label')}
            <input
              type="file"
              accept="image/*"
              onChange={(event) => {
                void onAvatarSelected(event.target.files?.[0] ?? null);
                event.currentTarget.value = '';
              }}
              disabled={uploadingAvatar || saving}
            />
          </label>
          <p className="form-help">
            {uploadingAvatar ? t('common.loading') : t('profile.avatar_upload_hint')}
          </p>

          <label>
            {t('auth.display_name')}
            <input
              value={displayName}
              onChange={(event) => setDisplayName(event.target.value)}
              required
              minLength={2}
              maxLength={64}
            />
          </label>

          <label>
            {t('profile.bio_label')}
            <textarea
              value={bio}
              onChange={(event) => setBio(event.target.value)}
              rows={6}
              maxLength={255}
            />
          </label>
          <p className="form-help">
            {t('profile.bio_hint')} {bio.length}/255
          </p>

          <label>
            {t('auth.preferred_language')}
            <select
              value={preferredLanguage}
              onChange={(event) => setPreferredLanguage(event.target.value as 'zh-CN' | 'en-US')}
            >
              <option value="en-US">English</option>
              <option value="zh-CN">{'\u4e2d\u6587'}</option>
            </select>
          </label>

          <div className="action-row">
            <button className="btn btn-primary" type="submit" disabled={saving || uploadingAvatar}>
              {saving ? t('common.loading') : t('profile.save_changes')}
            </button>
          </div>
        </form>
      </div>

      {message ? <p className="hint-text">{message}</p> : null}
    </main>
  );
}

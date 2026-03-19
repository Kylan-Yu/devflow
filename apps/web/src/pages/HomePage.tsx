import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import LanguageSwitcher from '../components/LanguageSwitcher';
import { getCurrentUserId } from '../utils/authStorage';
import { fetchUnreadCount } from '../api/notifications';
import { useNotificationSocket } from '../hooks/useNotificationSocket';
import type { NotificationPushMessage } from '../types/notification';

export default function HomePage() {
  const { t } = useTranslation();
  const currentUserId = getCurrentUserId();
  const [unreadCount, setUnreadCount] = useState(0);

  const loadUnread = useCallback(async () => {
    if (!currentUserId) {
      setUnreadCount(0);
      return;
    }
    try {
      setUnreadCount(await fetchUnreadCount());
    } catch {
      setUnreadCount(0);
    }
  }, [currentUserId]);

  useEffect(() => {
    loadUnread().catch(() => undefined);
  }, [loadUnread]);

  const handleSocketMessage = useCallback((payload: NotificationPushMessage) => {
    setUnreadCount(payload.unreadCount);
  }, []);

  useNotificationSocket({
    enabled: Boolean(currentUserId),
    userId: currentUserId,
    onMessage: handleSocketMessage
  });

  return (
    <main className="page-shell">
      <header className="top-row">
        <h1>{t('app.title')}</h1>
        <LanguageSwitcher />
      </header>

      <p>{t('home.subtitle')}</p>

      <div className="action-row">
        <Link to="/login" className="btn btn-primary">
          {t('auth.login')}
        </Link>
        <Link to="/register" className="btn btn-secondary">
          {t('auth.register')}
        </Link>
      </div>

      <div className="action-row">
        <Link to="/feed/latest" className="btn btn-secondary">
          {t('feed.latest_title')}
        </Link>
        <Link to="/feed/hot" className="btn btn-secondary">
          {t('feed.hot_title')}
        </Link>
        <Link to="/posts/new" className="btn btn-secondary">
          {t('post.create_title')}
        </Link>
        {currentUserId ? (
          <Link to="/notifications" className="btn btn-secondary notification-link">
            {t('notification.title')}
            {unreadCount > 0 ? <span className="badge">{unreadCount}</span> : null}
          </Link>
        ) : null}
        {currentUserId ? (
          <Link to={`/users/${currentUserId}`} className="btn btn-secondary">
            {t('profile.title')}
          </Link>
        ) : null}
      </div>
    </main>
  );
}

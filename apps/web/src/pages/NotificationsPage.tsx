import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  fetchUnreadCount,
  listNotifications,
  markAllNotificationsRead,
  markNotificationRead
} from '../api/notifications';
import type {
  NotificationItem,
  NotificationPushMessage,
  NotificationTargetType
} from '../types/notification';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import { useNotificationSocket } from '../hooks/useNotificationSocket';

function formatTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
}

function targetLink(item: NotificationItem): string {
  if (item.targetType === 'POST') {
    return `/posts/${item.targetId}`;
  }
  if (item.targetType === 'COMMENT') {
    return `/posts/${item.targetId}`;
  }
  return `/users/${item.actorId}`;
}

function targetText(targetType: NotificationTargetType): string {
  switch (targetType) {
    case 'POST':
      return 'notification.target.post';
    case 'COMMENT':
      return 'notification.target.comment';
    case 'USER':
      return 'notification.target.user';
    default:
      return 'notification.target.unknown';
  }
}

export default function NotificationsPage() {
  const { t } = useTranslation();
  const userId = useCurrentUserId();

  const [items, setItems] = useState<NotificationItem[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const refresh = useCallback(async () => {
    if (!userId) {
      return;
    }
    setLoading(true);
    setMessage(null);
    try {
      const [list, unread] = await Promise.all([listNotifications(30), fetchUnreadCount()]);
      setItems(list);
      setUnreadCount(unread);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  }, [t, userId]);

  useEffect(() => {
    refresh().catch(() => undefined);
  }, [refresh]);

  const handleSocketMessage = useCallback(
    (payload: NotificationPushMessage) => {
      setUnreadCount(payload.unreadCount);
      refresh().catch(() => undefined);
    },
    [refresh]
  );

  useNotificationSocket({
    enabled: Boolean(userId),
    userId,
    onMessage: handleSocketMessage
  });

  const onMarkRead = useCallback(
    async (id: number) => {
      try {
        await markNotificationRead(id);
        setItems((prev) => prev.map((item) => (item.id === id ? { ...item, read: true } : item)));
        setUnreadCount((prev) => Math.max(0, prev - 1));
      } catch (error) {
        const text = error instanceof Error ? error.message : 'common.request_failed';
        setMessage(t(`messages.${text}`, { defaultValue: text }));
      }
    },
    [t]
  );

  const onMarkAllRead = useCallback(async () => {
    try {
      await markAllNotificationsRead();
      setItems((prev) => prev.map((item) => ({ ...item, read: true })));
      setUnreadCount(0);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    }
  }, [t]);

  const unreadLabel = useMemo(
    () => t('notification.unread_count', { count: unreadCount }),
    [t, unreadCount]
  );

  if (!userId) {
    return (
      <main className="page-shell">
        <h1>{t('notification.title')}</h1>
        <p className="hint-text">{t('notification.login_required')}</p>
        <Link className="btn btn-primary" to="/login">
          {t('auth.login')}
        </Link>
      </main>
    );
  }

  return (
    <main className="page-shell">
      <header className="top-row">
        <h1>{t('notification.title')}</h1>
        <div className="action-row compact-row">
          <button type="button" className="btn btn-secondary" onClick={() => refresh()} disabled={loading}>
            {t('notification.refresh')}
          </button>
          <button type="button" className="btn btn-secondary" onClick={onMarkAllRead} disabled={loading}>
            {t('notification.mark_all_read')}
          </button>
        </div>
      </header>

      <p className="hint-text">{unreadLabel}</p>
      {message ? <p className="hint-text">{message}</p> : null}

      <section className="notification-list">
        {items.length === 0 && !loading ? <p className="hint-text">{t('notification.empty')}</p> : null}

        {items.map((item) => (
          <article key={item.id} className={`notification-item ${item.read ? 'is-read' : 'is-unread'}`}>
            <div className="notification-main">
              <p className="notification-message">
                {t(`messages.${item.messageCode}`, {
                  actor: item.actorDisplayName,
                  preview: item.preview,
                  defaultValue: item.messageCode
                })}
              </p>
              <p className="hint-text">
                {t(targetText(item.targetType))} | {formatTime(item.createdAt)}
              </p>
            </div>

            <div className="action-row compact-row">
              <Link className="btn btn-secondary" to={targetLink(item)}>
                {t('notification.open_target')}
              </Link>
              {!item.read ? (
                <button
                  type="button"
                  className="btn btn-secondary"
                  onClick={() => onMarkRead(item.id)}
                  disabled={loading}
                >
                  {t('notification.mark_read')}
                </button>
              ) : null}
            </div>
          </article>
        ))}
      </section>

      {loading ? <p className="hint-text">{t('common.loading')}</p> : null}
    </main>
  );
}

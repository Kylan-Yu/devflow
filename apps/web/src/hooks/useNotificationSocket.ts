import { useEffect } from 'react';
import type { NotificationPushMessage } from '../types/notification';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

function buildWebSocketUrl(userId: number): string {
  const base = API_BASE_URL.replace(/^http/, 'ws');
  return `${base}/ws/notifications?userId=${userId}`;
}

interface NotificationSocketOptions {
  enabled: boolean;
  userId: number | null;
  onMessage: (message: NotificationPushMessage) => void;
}

export function useNotificationSocket(options: NotificationSocketOptions): void {
  const { enabled, userId, onMessage } = options;

  useEffect(() => {
    if (!enabled || !userId) {
      return;
    }

    const socket = new WebSocket(buildWebSocketUrl(userId));
    socket.onmessage = (event) => {
      try {
        const payload = JSON.parse(event.data) as NotificationPushMessage;
        onMessage(payload);
      } catch {
        // Ignore invalid payload from socket.
      }
    };

    return () => {
      socket.close();
    };
  }, [enabled, onMessage, userId]);
}

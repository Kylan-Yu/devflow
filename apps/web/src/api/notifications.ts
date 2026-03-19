import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { NotificationItem, UnreadCountPayload } from '../types/notification';
import { getAccessToken } from '../utils/authStorage';

function currentToken(): string | undefined {
  return getAccessToken() ?? undefined;
}

export async function listNotifications(size = 20): Promise<NotificationItem[]> {
  const response = await requestJson<ApiResponse<NotificationItem[]>>(
    `/api/v1/notifications?size=${size}`,
    {
      accessToken: currentToken()
    }
  );
  return response.data;
}

export async function fetchUnreadCount(): Promise<number> {
  const response = await requestJson<ApiResponse<UnreadCountPayload>>(
    '/api/v1/notifications/unread-count',
    {
      accessToken: currentToken()
    }
  );
  return response.data.unreadCount;
}

export async function markNotificationRead(id: number): Promise<void> {
  await requestJson<ApiResponse<void>>(`/api/v1/notifications/${id}/read`, {
    method: 'PATCH',
    accessToken: currentToken()
  });
}

export async function markAllNotificationsRead(): Promise<number> {
  const response = await requestJson<ApiResponse<{ updatedCount: number }>>(
    '/api/v1/notifications/read-all',
    {
      method: 'PATCH',
      accessToken: currentToken()
    }
  );
  return response.data.updatedCount;
}

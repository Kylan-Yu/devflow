import { clearAdminSession, getAdminAccessToken } from '../utils/adminSession';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  accessToken?: string;
}

export async function requestJson<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, accessToken = getAdminAccessToken() ?? undefined } = options;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {})
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  const payload = await response.json().catch(() => null);
  if (!response.ok) {
    if (response.status === 401) {
      clearAdminSession();
    }
    throw new Error(payload?.message ?? 'common.request_failed');
  }

  return payload as T;
}

import { clearSession, getRefreshToken, saveSession } from '../utils/authStorage';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

interface RequestOptions {
  method?: 'GET' | 'POST' | 'PUT' | 'PATCH' | 'DELETE';
  body?: unknown;
  accessToken?: string;
  skipAuthRefresh?: boolean;
}

interface MultipartRequestOptions {
  accessToken?: string;
  skipAuthRefresh?: boolean;
}

interface ApiEnvelope<T> {
  code: string;
  message: string;
  data: T;
  traceId?: string;
}

interface RefreshedSession {
  tokens: {
    accessToken: string;
    refreshToken: string;
  };
  user: {
    id: number;
  };
}

let refreshPromise: Promise<string | null> | null = null;

async function refreshAccessToken(): Promise<string | null> {
  if (refreshPromise) {
    return refreshPromise;
  }

  refreshPromise = (async () => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      clearSession();
      return null;
    }

    const response = await fetch(`${API_BASE_URL}/api/v1/auth/refresh`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ refreshToken })
    });

    const payload = (await response.json().catch(() => null)) as ApiEnvelope<RefreshedSession> | null;
    if (!response.ok || !payload?.data) {
      clearSession();
      return null;
    }

    saveSession(
      payload.data.tokens.accessToken,
      payload.data.tokens.refreshToken,
      payload.data.user.id
    );
    return payload.data.tokens.accessToken;
  })();

  try {
    return await refreshPromise;
  } finally {
    refreshPromise = null;
  }
}

export async function requestJson<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const { method = 'GET', body, accessToken, skipAuthRefresh = false } = options;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {})
    },
    body: body === undefined ? undefined : JSON.stringify(body)
  });

  const payload = await response.json().catch(() => null);
  if (response.status === 401 && accessToken && !skipAuthRefresh) {
    const refreshedAccessToken = await refreshAccessToken();
    if (refreshedAccessToken) {
      return requestJson<T>(path, {
        ...options,
        accessToken: refreshedAccessToken,
        skipAuthRefresh: true
      });
    }
    throw new Error('auth.unauthorized');
  }

  if (!response.ok) {
    throw new Error(payload?.message ?? 'common.request_failed');
  }

  return payload as T;
}

export async function requestMultipart<T>(
  path: string,
  formData: FormData,
  options: MultipartRequestOptions = {}
): Promise<T> {
  const { accessToken, skipAuthRefresh = false } = options;

  const response = await fetch(`${API_BASE_URL}${path}`, {
    method: 'POST',
    headers: {
      ...(accessToken ? { Authorization: `Bearer ${accessToken}` } : {})
    },
    body: formData
  });

  const payload = await response.json().catch(() => null);
  if (response.status === 401 && accessToken && !skipAuthRefresh) {
    const refreshedAccessToken = await refreshAccessToken();
    if (refreshedAccessToken) {
      return requestMultipart<T>(path, formData, {
        accessToken: refreshedAccessToken,
        skipAuthRefresh: true
      });
    }
    throw new Error('auth.unauthorized');
  }

  if (!response.ok) {
    throw new Error(payload?.message ?? 'common.request_failed');
  }

  return payload as T;
}

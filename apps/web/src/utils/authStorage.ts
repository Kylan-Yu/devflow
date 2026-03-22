const ACCESS_TOKEN_KEY = 'devflow.web.accessToken';
const REFRESH_TOKEN_KEY = 'devflow.web.refreshToken';
const USER_ID_KEY = 'devflow.web.userId';
export const SESSION_CHANGE_EVENT = 'devflow:web:session-changed';

function notifySessionChanged(): void {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event(SESSION_CHANGE_EVENT));
  }
}

export function saveSession(accessToken: string, refreshToken: string, userId: number): void {
  localStorage.setItem(ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, refreshToken);
  localStorage.setItem(USER_ID_KEY, String(userId));
  notifySessionChanged();
}

export function clearSession(): void {
  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_ID_KEY);
  notifySessionChanged();
}

export function getAccessToken(): string | null {
  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function getCurrentUserId(): number | null {
  const value = localStorage.getItem(USER_ID_KEY);
  if (!value) {
    return null;
  }
  const numeric = Number(value);
  return Number.isFinite(numeric) ? numeric : null;
}

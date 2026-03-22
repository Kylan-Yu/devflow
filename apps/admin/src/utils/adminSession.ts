const ADMIN_ACCESS_TOKEN_KEY = 'devflow.admin.accessToken';
const ADMIN_DISPLAY_NAME_KEY = 'devflow.admin.displayName';
const ADMIN_ID_KEY = 'devflow.admin.adminId';

export const ADMIN_SESSION_CHANGE_EVENT = 'devflow:admin:session-changed';

function notifyAdminSessionChanged(): void {
  if (typeof window !== 'undefined') {
    window.dispatchEvent(new Event(ADMIN_SESSION_CHANGE_EVENT));
  }
}

export function saveAdminSession(accessToken: string, adminId: number, displayName: string): void {
  localStorage.setItem(ADMIN_ACCESS_TOKEN_KEY, accessToken);
  localStorage.setItem(ADMIN_ID_KEY, String(adminId));
  localStorage.setItem(ADMIN_DISPLAY_NAME_KEY, displayName);
  notifyAdminSessionChanged();
}

export function clearAdminSession(): void {
  localStorage.removeItem(ADMIN_ACCESS_TOKEN_KEY);
  localStorage.removeItem(ADMIN_ID_KEY);
  localStorage.removeItem(ADMIN_DISPLAY_NAME_KEY);
  notifyAdminSessionChanged();
}

export function getAdminAccessToken(): string | null {
  return localStorage.getItem(ADMIN_ACCESS_TOKEN_KEY);
}

export function getAdminDisplayName(): string | null {
  return localStorage.getItem(ADMIN_DISPLAY_NAME_KEY);
}

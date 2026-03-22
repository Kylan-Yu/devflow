import { useEffect, useState } from 'react';
import { ADMIN_SESSION_CHANGE_EVENT, getAdminAccessToken } from '../utils/adminSession';

export function useAdminAccessToken(): string | null {
  const [accessToken, setAccessToken] = useState<string | null>(() => getAdminAccessToken());

  useEffect(() => {
    const sync = () => {
      setAccessToken(getAdminAccessToken());
    };

    window.addEventListener(ADMIN_SESSION_CHANGE_EVENT, sync);
    window.addEventListener('storage', sync);

    return () => {
      window.removeEventListener(ADMIN_SESSION_CHANGE_EVENT, sync);
      window.removeEventListener('storage', sync);
    };
  }, []);

  return accessToken;
}

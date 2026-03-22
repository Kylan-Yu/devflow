import { useEffect, useState } from 'react';
import { SESSION_CHANGE_EVENT, getCurrentUserId } from '../utils/authStorage';

export function useCurrentUserId(): number | null {
  const [userId, setUserId] = useState<number | null>(() => getCurrentUserId());

  useEffect(() => {
    const sync = () => {
      setUserId(getCurrentUserId());
    };

    window.addEventListener(SESSION_CHANGE_EVENT, sync);
    window.addEventListener('storage', sync);

    return () => {
      window.removeEventListener(SESSION_CHANGE_EVENT, sync);
      window.removeEventListener('storage', sync);
    };
  }, []);

  return userId;
}

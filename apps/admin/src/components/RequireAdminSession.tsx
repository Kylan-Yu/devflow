import type { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAdminAccessToken } from '../hooks/useAdminAccessToken';

interface RequireAdminSessionProps {
  children: ReactNode;
}

export default function RequireAdminSession({ children }: RequireAdminSessionProps) {
  const accessToken = useAdminAccessToken();

  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

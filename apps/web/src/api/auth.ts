import { requestJson } from './client';
import type { ApiResponse } from '../types/api';

export type LanguageValue = 'zh-CN' | 'en-US';

export interface UserProfile {
  id: number;
  username: string;
  email: string;
  displayName: string;
  bio: string | null;
  preferredLanguage: LanguageValue;
  role: 'USER' | 'ADMIN';
  status: 'ACTIVE' | 'DISABLED';
  createdAt: string;
}

export interface AuthSession {
  tokens: {
    accessToken: string;
    accessTokenExpiresAt: string;
    refreshToken: string;
    refreshTokenExpiresAt: string;
  };
  user: UserProfile;
}

export interface LoginPayload {
  email: string;
  password: string;
}

export interface RegisterPayload {
  username: string;
  email: string;
  password: string;
  displayName: string;
  preferredLanguage: LanguageValue;
}

export async function login(payload: LoginPayload): Promise<AuthSession> {
  const response = await requestJson<ApiResponse<AuthSession>>('/api/v1/auth/login', {
    method: 'POST',
    body: payload
  });
  return response.data;
}

export async function register(payload: RegisterPayload): Promise<AuthSession> {
  const response = await requestJson<ApiResponse<AuthSession>>('/api/v1/auth/register', {
    method: 'POST',
    body: payload
  });
  return response.data;
}

export async function logout(refreshToken: string): Promise<void> {
  await requestJson<ApiResponse<void>>('/api/v1/auth/logout', {
    method: 'POST',
    body: { refreshToken }
  });
}

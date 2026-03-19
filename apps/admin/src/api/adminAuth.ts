import { requestJson } from './client';
import type { ApiResponse } from '../types/api';

export interface AdminLoginPayload {
  username: string;
  password: string;
}

export interface AdminLoginSession {
  accessToken: string;
  accessTokenExpiresAt: string;
  adminId: number;
  username: string;
  displayName: string;
}

export async function adminLogin(payload: AdminLoginPayload): Promise<AdminLoginSession> {
  const response = await requestJson<ApiResponse<AdminLoginSession>>('/api/v1/admin/auth/login', {
    method: 'POST',
    body: payload
  });
  return response.data;
}

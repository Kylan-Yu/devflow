import { requestJson } from './client';
import type { ApiResponse } from '../types/api';

export interface PublicUserProfile {
  id: number;
  username: string;
  email: string;
  displayName: string;
  bio: string | null;
  preferredLanguage: 'zh-CN' | 'en-US';
  role: 'USER' | 'ADMIN';
  status: 'ACTIVE' | 'DISABLED';
  createdAt: string;
}

export async function getUserProfile(userId: number): Promise<PublicUserProfile> {
  const response = await requestJson<ApiResponse<PublicUserProfile>>(`/api/v1/users/${userId}`);
  return response.data;
}

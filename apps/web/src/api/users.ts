import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { LanguageValue, UserProfile } from './auth';
import { getAccessToken } from '../utils/authStorage';

export interface UpdateCurrentUserProfilePayload {
  displayName: string;
  bio: string;
  avatarUrl: string;
  preferredLanguage: LanguageValue;
}

export type PublicUserProfile = UserProfile;

function currentToken(): string | undefined {
  return getAccessToken() ?? undefined;
}

export async function getUserProfile(userId: number): Promise<PublicUserProfile> {
  const response = await requestJson<ApiResponse<PublicUserProfile>>(`/api/v1/users/${userId}`);
  return response.data;
}

export async function getCurrentUserProfile(): Promise<UserProfile> {
  const response = await requestJson<ApiResponse<UserProfile>>('/api/v1/users/me', {
    accessToken: currentToken()
  });
  return response.data;
}

export async function updateCurrentUserProfile(
  payload: UpdateCurrentUserProfilePayload
): Promise<UserProfile> {
  const response = await requestJson<ApiResponse<UserProfile>>('/api/v1/users/me', {
    method: 'PUT',
    body: payload,
    accessToken: currentToken()
  });
  return response.data;
}

import { requestMultipart } from './client';
import type { ApiResponse } from '../types/api';
import { getAccessToken } from '../utils/authStorage';

export interface UploadedMedia {
  url: string;
  objectKey: string;
  contentType: string;
  size: number;
}

function currentToken(): string | undefined {
  return getAccessToken() ?? undefined;
}

async function uploadImage(path: string, file: File): Promise<UploadedMedia> {
  const formData = new FormData();
  formData.append('file', file);

  const response = await requestMultipart<ApiResponse<UploadedMedia>>(path, formData, {
    accessToken: currentToken()
  });
  return response.data;
}

export async function uploadAvatarImage(file: File): Promise<UploadedMedia> {
  return uploadImage('/api/v1/media/avatar', file);
}

export async function uploadPostCoverImage(file: File): Promise<UploadedMedia> {
  return uploadImage('/api/v1/media/post-cover', file);
}

import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { CursorPage, PostDetail, PostSummary } from '../types/post';
import { getAccessToken } from '../utils/authStorage';

interface PostPayload {
  title: string;
  content: string;
  contentType: 'MARKDOWN' | 'RICH_TEXT';
  coverImageUrl?: string | null;
  categoryId: number;
  tagIds: number[];
}

export async function createPost(payload: PostPayload): Promise<PostDetail> {
  const response = await requestJson<ApiResponse<PostDetail>>('/api/v1/posts', {
    method: 'POST',
    body: payload,
    accessToken: getAccessToken() ?? undefined
  });
  return response.data;
}

export async function updatePost(postId: number, payload: PostPayload): Promise<PostDetail> {
  const response = await requestJson<ApiResponse<PostDetail>>(`/api/v1/posts/${postId}`, {
    method: 'PUT',
    body: payload,
    accessToken: getAccessToken() ?? undefined
  });
  return response.data;
}

export async function deletePost(postId: number): Promise<void> {
  await requestJson<ApiResponse<void>>(`/api/v1/posts/${postId}`, {
    method: 'DELETE',
    accessToken: getAccessToken() ?? undefined
  });
}

export async function getPostDetail(postId: number): Promise<PostDetail> {
  const response = await requestJson<ApiResponse<PostDetail>>(`/api/v1/posts/${postId}`);
  return response.data;
}

export async function listUserPosts(
  userId: number,
  cursor?: string | null,
  size = 10
): Promise<CursorPage<PostSummary>> {
  const query = new URLSearchParams();
  query.set('size', String(size));
  if (cursor) {
    query.set('cursor', cursor);
  }
  const response = await requestJson<ApiResponse<CursorPage<PostSummary>>>(
    `/api/v1/users/${userId}/posts?${query.toString()}`
  );
  return response.data;
}

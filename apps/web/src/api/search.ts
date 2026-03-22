import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { CursorPage, PostSummary } from '../types/post';

interface SearchPostsOptions {
  keyword?: string | null;
  categoryId?: number | null;
  cursor?: string | null;
  size?: number;
}

export async function searchPosts(options: SearchPostsOptions): Promise<CursorPage<PostSummary>> {
  const params = new URLSearchParams();
  if (options.keyword && options.keyword.trim().length > 0) {
    params.set('keyword', options.keyword.trim());
  }
  if (options.categoryId) {
    params.set('categoryId', String(options.categoryId));
  }
  if (options.cursor) {
    params.set('cursor', options.cursor);
  }
  params.set('size', String(options.size ?? 10));

  const response = await requestJson<ApiResponse<CursorPage<PostSummary>>>(`/api/v1/search/posts?${params.toString()}`);
  return response.data;
}

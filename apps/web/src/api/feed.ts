import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { CursorPage, PostSummary } from '../types/post';

interface FeedParams {
  cursor?: string | null;
  size?: number;
  categoryId?: number | null;
}

function buildFeedQuery(params: FeedParams): string {
  const query = new URLSearchParams();
  if (params.cursor) {
    query.set('cursor', params.cursor);
  }
  if (params.categoryId) {
    query.set('categoryId', String(params.categoryId));
  }
  query.set('size', String(params.size ?? 10));
  const queryText = query.toString();
  return queryText ? `?${queryText}` : '';
}

export async function latestFeed(params: FeedParams = {}): Promise<CursorPage<PostSummary>> {
  const response = await requestJson<ApiResponse<CursorPage<PostSummary>>>(
    `/api/v1/feed/latest${buildFeedQuery(params)}`
  );
  return response.data;
}

export async function hotFeed(params: FeedParams = {}): Promise<CursorPage<PostSummary>> {
  const response = await requestJson<ApiResponse<CursorPage<PostSummary>>>(
    `/api/v1/feed/hot${buildFeedQuery(params)}`
  );
  return response.data;
}

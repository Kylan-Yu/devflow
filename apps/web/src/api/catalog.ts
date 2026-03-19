import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { CategoryItem, TagItem } from '../types/post';

export async function listCategories(): Promise<CategoryItem[]> {
  const response = await requestJson<ApiResponse<CategoryItem[]>>('/api/v1/categories');
  return response.data;
}

export async function listTags(keyword?: string): Promise<TagItem[]> {
  const params = new URLSearchParams();
  if (keyword && keyword.trim().length > 0) {
    params.set('keyword', keyword.trim());
  }
  params.set('size', '30');
  const query = params.toString();
  const response = await requestJson<ApiResponse<TagItem[]>>(`/api/v1/tags${query ? `?${query}` : ''}`);
  return response.data;
}

import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import { getAccessToken } from '../utils/authStorage';

export type ReportTargetType = 'POST' | 'USER';
export type ReportReason = 'SPAM' | 'HARASSMENT' | 'INAPPROPRIATE' | 'MISLEADING' | 'OTHER';
export type ReportStatus = 'PENDING' | 'RESOLVED' | 'DISMISSED';
export type ReportResolutionAction = 'NONE' | 'HIDE_POST' | 'DISABLE_USER';

export interface ReportItem {
  id: number;
  targetType: ReportTargetType;
  targetId: number;
  targetLabel: string;
  targetStatus: string;
  reason: ReportReason;
  detail: string | null;
  status: ReportStatus;
  resolutionAction: ReportResolutionAction;
  resolutionNote: string | null;
  createdAt: string;
  reviewedAt: string | null;
}

export interface CreateReportPayload {
  reason: ReportReason;
  detail?: string | null;
}

export async function reportPost(postId: number, payload: CreateReportPayload): Promise<ReportItem> {
  const response = await requestJson<ApiResponse<ReportItem>>(`/api/v1/posts/${postId}/reports`, {
    method: 'POST',
    body: payload,
    accessToken: getAccessToken() ?? undefined
  });
  return response.data;
}

export async function reportUser(userId: number, payload: CreateReportPayload): Promise<ReportItem> {
  const response = await requestJson<ApiResponse<ReportItem>>(`/api/v1/users/${userId}/reports`, {
    method: 'POST',
    body: payload,
    accessToken: getAccessToken() ?? undefined
  });
  return response.data;
}

export async function listMyReports(size = 20): Promise<ReportItem[]> {
  const response = await requestJson<ApiResponse<ReportItem[]>>(`/api/v1/reports/me?size=${size}`, {
    accessToken: getAccessToken() ?? undefined
  });
  return response.data;
}

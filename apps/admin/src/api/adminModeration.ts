import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type { AdminPageParams, AdminPage } from '../types/pagination';

export type AdminUserStatus = 'ACTIVE' | 'DISABLED';
export type AdminPostStatus = 'PUBLISHED' | 'DELETED';
export type AdminReportStatus = 'PENDING' | 'RESOLVED' | 'DISMISSED' | 'ALL';
export type AdminReportTargetType = 'POST' | 'USER';
export type AdminReportReason = 'SPAM' | 'HARASSMENT' | 'INAPPROPRIATE' | 'MISLEADING' | 'OTHER';
export type AdminReportResolutionAction = 'NONE' | 'HIDE_POST' | 'DISABLE_USER';
export type AdminAuditActionType = 'USER_STATUS_UPDATED' | 'POST_STATUS_UPDATED' | 'REPORT_REVIEWED';
export type AdminAuditTargetType = 'POST' | 'USER';
export type LanguagePreference = 'zh-CN' | 'en-US';

export interface AdminDashboardOverview {
  totalUsers: number;
  activeUsers: number;
  disabledUsers: number;
  publishedPosts: number;
  hiddenPosts: number;
  pendingReports: number;
  resolvedReports: number;
}

export interface AdminUserSummary {
  id: number;
  username: string;
  email: string;
  displayName: string;
  preferredLanguage: LanguagePreference;
  status: AdminUserStatus;
  createdAt: string;
  updatedAt: string;
  lastLoginAt: string | null;
}

export interface AdminPostSummary {
  id: number;
  title: string;
  authorUsername: string;
  authorDisplayName: string;
  categoryCode: string;
  categoryNameZh: string;
  categoryNameEn: string;
  status: AdminPostStatus;
  likeCount: number;
  commentCount: number;
  favoriteCount: number;
  publishedAt: string | null;
  updatedAt: string;
  deletedAt: string | null;
}

export interface AdminReportSummary {
  id: number;
  reporterId: number;
  reporterUsername: string;
  reporterDisplayName: string;
  targetType: AdminReportTargetType;
  targetId: number;
  targetLabel: string;
  targetStatus: string;
  reason: AdminReportReason;
  detail: string | null;
  status: AdminReportStatus;
  resolutionAction: AdminReportResolutionAction;
  resolutionNote: string | null;
  reviewedByAdminDisplayName: string | null;
  createdAt: string;
  reviewedAt: string | null;
  updatedAt: string;
}

export interface AdminAuditLog {
  id: number;
  adminUsername: string;
  adminDisplayName: string;
  actionType: AdminAuditActionType;
  targetType: AdminAuditTargetType;
  targetId: number;
  targetLabel: string;
  previousState: string | null;
  nextState: string | null;
  resolutionAction: AdminReportResolutionAction | null;
  contextLabel: string | null;
  createdAt: string;
}

export async function fetchAdminOverview(): Promise<AdminDashboardOverview> {
  const response = await requestJson<ApiResponse<AdminDashboardOverview>>('/api/v1/admin/overview');
  return response.data;
}

export async function fetchAdminUsers(params: AdminPageParams = {}): Promise<AdminPage<AdminUserSummary>> {
  const query = new URLSearchParams();
  query.set('size', String(params.size ?? 12));
  query.set('page', String(params.page ?? 1));
  if (params.search) {
    query.set('search', params.search);
  }
  const response = await requestJson<ApiResponse<AdminPage<AdminUserSummary>>>(`/api/v1/admin/users?${query.toString()}`);
  
  // 后端直接返回用户数组，需要包装成AdminPage格式
  const userData = response.data;
  if (Array.isArray(userData)) {
    return {
      items: userData,
      currentPage: params.page ?? 1,
      pageSize: params.size ?? 12,
      totalPages: Math.ceil(userData.length / (params.size ?? 12)),
      totalItems: userData.length,
      hasNext: false,
      hasPrevious: false
    };
  }
  
  return userData;
}

export async function updateAdminUserStatus(
  userId: number,
  status: AdminUserStatus
): Promise<AdminUserSummary> {
  const response = await requestJson<ApiResponse<AdminUserSummary>>(
    `/api/v1/admin/users/${userId}/status`,
    {
      method: 'PATCH',
      body: { status }
    }
  );
  return response.data;
}

export async function fetchAdminPosts(params: AdminPageParams = {}): Promise<AdminPage<AdminPostSummary>> {
  const query = new URLSearchParams();
  query.set('size', String(params.size ?? 12));
  query.set('page', String(params.page ?? 1));
  if (params.search) {
    query.set('search', params.search);
  }
  const response = await requestJson<ApiResponse<AdminPage<AdminPostSummary>>>(`/api/v1/admin/posts?${query.toString()}`);
  
  // 后端直接返回帖子数组，需要包装成AdminPage格式
  const postData = response.data;
  if (Array.isArray(postData)) {
    return {
      items: postData,
      currentPage: params.page ?? 1,
      pageSize: params.size ?? 12,
      totalPages: Math.ceil(postData.length / (params.size ?? 12)),
      totalItems: postData.length,
      hasNext: false,
      hasPrevious: false
    };
  }
  
  return postData;
}

export async function updateAdminPostStatus(
  postId: number,
  status: AdminPostStatus
): Promise<AdminPostSummary> {
  const response = await requestJson<ApiResponse<AdminPostSummary>>(
    `/api/v1/admin/posts/${postId}/status`,
    {
      method: 'PATCH',
      body: { status }
    }
  );
  return response.data;
}

export async function fetchAdminReports(params: AdminPageParams & { status?: AdminReportStatus }): Promise<AdminPage<AdminReportSummary>> {
  const query = new URLSearchParams();
  query.set('size', String(params.size ?? 12));
  query.set('page', String(params.page ?? 1));
  if (params.search) {
    query.set('search', params.search);
  }
  if (params.status) {
    query.set('status', params.status);
  }
  const response = await requestJson<ApiResponse<AdminPage<AdminReportSummary>>>(`/api/v1/admin/reports?${query.toString()}`);
  
  // 后端直接返回举报数组，需要包装成AdminPage格式
  const reportData = response.data;
  if (Array.isArray(reportData)) {
    return {
      items: reportData,
      currentPage: params.page ?? 1,
      pageSize: params.size ?? 12,
      totalPages: Math.ceil(reportData.length / (params.size ?? 12)),
      totalItems: reportData.length,
      hasNext: false,
      hasPrevious: false
    };
  }
  
  return reportData;
}

export async function fetchAdminAuditLogs(params: AdminPageParams = {}): Promise<AdminPage<AdminAuditLog>> {
  const query = new URLSearchParams();
  query.set('size', String(params.size ?? 12));
  query.set('page', String(params.page ?? 1));
  if (params.search) {
    query.set('search', params.search);
  }
  const response = await requestJson<ApiResponse<AdminPage<AdminAuditLog>>>(`/api/v1/admin/audit-logs?${query.toString()}`);
  
  // 后端直接返回审计日志数组，需要包装成AdminPage格式
  const auditData = response.data;
  if (Array.isArray(auditData)) {
    return {
      items: auditData,
      currentPage: params.page ?? 1,
      pageSize: params.size ?? 12,
      totalPages: Math.ceil(auditData.length / (params.size ?? 12)),
      totalItems: auditData.length,
      hasNext: false,
      hasPrevious: false
    };
  }
  
  return auditData;
}

export async function reviewAdminReport(
  reportId: number,
  payload: {
    status: AdminReportStatus;
    resolutionAction: AdminReportResolutionAction;
    resolutionNote?: string | null;
  }
): Promise<AdminReportSummary> {
  const response = await requestJson<ApiResponse<AdminReportSummary>>(`/api/v1/admin/reports/${reportId}`, {
    method: 'PATCH',
    body: payload
  });
  return response.data;
}

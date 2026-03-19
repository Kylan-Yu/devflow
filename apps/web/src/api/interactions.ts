import { requestJson } from './client';
import type { ApiResponse } from '../types/api';
import type {
  CommentItem,
  FollowStatus,
  PostInteractionState,
  PostInteractionSummary
} from '../types/interaction';
import { getAccessToken } from '../utils/authStorage';

function currentToken(): string | undefined {
  return getAccessToken() ?? undefined;
}

export async function likePost(postId: number): Promise<PostInteractionSummary> {
  const response = await requestJson<ApiResponse<PostInteractionSummary>>(`/api/v1/posts/${postId}/likes`, {
    method: 'POST',
    accessToken: currentToken()
  });
  return response.data;
}

export async function unlikePost(postId: number): Promise<PostInteractionSummary> {
  const response = await requestJson<ApiResponse<PostInteractionSummary>>(`/api/v1/posts/${postId}/likes`, {
    method: 'DELETE',
    accessToken: currentToken()
  });
  return response.data;
}

export async function favoritePost(postId: number): Promise<PostInteractionSummary> {
  const response = await requestJson<ApiResponse<PostInteractionSummary>>(
    `/api/v1/posts/${postId}/favorites`,
    {
      method: 'POST',
      accessToken: currentToken()
    }
  );
  return response.data;
}

export async function unfavoritePost(postId: number): Promise<PostInteractionSummary> {
  const response = await requestJson<ApiResponse<PostInteractionSummary>>(
    `/api/v1/posts/${postId}/favorites`,
    {
      method: 'DELETE',
      accessToken: currentToken()
    }
  );
  return response.data;
}

export async function getPostInteractionState(postId: number): Promise<PostInteractionState> {
  const response = await requestJson<ApiResponse<PostInteractionState>>(
    `/api/v1/posts/${postId}/interaction-status`,
    {
      accessToken: currentToken()
    }
  );
  return response.data;
}

export async function listPostComments(postId: number): Promise<CommentItem[]> {
  const response = await requestJson<ApiResponse<CommentItem[]>>(`/api/v1/posts/${postId}/comments`);
  return response.data;
}

export async function createComment(postId: number, content: string): Promise<CommentItem> {
  const response = await requestJson<ApiResponse<CommentItem>>(`/api/v1/posts/${postId}/comments`, {
    method: 'POST',
    accessToken: currentToken(),
    body: { content }
  });
  return response.data;
}

export async function deleteComment(commentId: number): Promise<PostInteractionSummary> {
  const response = await requestJson<ApiResponse<PostInteractionSummary>>(`/api/v1/comments/${commentId}`, {
    method: 'DELETE',
    accessToken: currentToken()
  });
  return response.data;
}

export async function followUser(userId: number): Promise<FollowStatus> {
  const response = await requestJson<ApiResponse<FollowStatus>>(`/api/v1/users/${userId}/follow`, {
    method: 'POST',
    accessToken: currentToken()
  });
  return response.data;
}

export async function unfollowUser(userId: number): Promise<FollowStatus> {
  const response = await requestJson<ApiResponse<FollowStatus>>(`/api/v1/users/${userId}/follow`, {
    method: 'DELETE',
    accessToken: currentToken()
  });
  return response.data;
}

export async function getFollowStatus(userId: number): Promise<FollowStatus> {
  const response = await requestJson<ApiResponse<FollowStatus>>(`/api/v1/users/${userId}/follow-status`, {
    accessToken: currentToken()
  });
  return response.data;
}

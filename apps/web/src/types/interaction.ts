export interface PostInteractionSummary {
  postId: number;
  likeCount: number;
  commentCount: number;
  favoriteCount: number;
  hotScore: number;
}

export interface PostInteractionState {
  liked: boolean;
  favorited: boolean;
}

export interface CommentItem {
  id: number;
  postId: number;
  userId: number;
  userDisplayName: string;
  content: string;
  createdAt: string;
}

export interface FollowStatus {
  following: boolean;
}

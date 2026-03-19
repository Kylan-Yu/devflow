export type NotificationType = 'LIKE' | 'COMMENT' | 'FOLLOW';
export type NotificationTargetType = 'POST' | 'COMMENT' | 'USER';

export interface NotificationItem {
  id: number;
  receiverId: number;
  actorId: number;
  actorDisplayName: string;
  type: NotificationType;
  targetType: NotificationTargetType;
  targetId: number;
  messageCode: string;
  preview: string;
  read: boolean;
  createdAt: string;
}

export interface UnreadCountPayload {
  unreadCount: number;
}

export interface NotificationPushMessage {
  notificationId: number;
  eventType: 'POST_LIKED' | 'POST_COMMENTED' | 'USER_FOLLOWED';
  actorId: number;
  receiverId: number;
  targetType: NotificationTargetType;
  targetId: number;
  messageCode: string;
  preview: string;
  unreadCount: number;
  createdAt: string;
}

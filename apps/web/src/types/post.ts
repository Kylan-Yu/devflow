export interface CategoryItem {
  id: number;
  code: string;
  nameZh: string;
  nameEn: string;
  sortOrder: number;
}

export interface TagItem {
  id: number;
  name: string;
  localeKey: string | null;
}

export interface PostAuthor {
  id: number;
  username: string;
  displayName: string;
}

export interface PostSummary {
  id: number;
  title: string;
  excerpt: string;
  coverImageUrl: string | null;
  author: PostAuthor;
  category: CategoryItem;
  tags: TagItem[];
  likeCount: number;
  commentCount: number;
  favoriteCount: number;
  hotScore: number;
  publishedAt: string;
}

export interface PostDetail {
  id: number;
  title: string;
  content: string;
  contentType: 'MARKDOWN' | 'RICH_TEXT';
  coverImageUrl: string | null;
  author: PostAuthor;
  category: CategoryItem;
  tags: TagItem[];
  visibility: 'PUBLIC';
  likeCount: number;
  commentCount: number;
  favoriteCount: number;
  hotScore: number;
  publishedAt: string;
  updatedAt: string;
}

export interface CursorPage<T> {
  items: T[];
  nextCursor: string | null;
  hasMore: boolean;
}

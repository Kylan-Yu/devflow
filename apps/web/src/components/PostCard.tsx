import { Link } from 'react-router-dom';
import type { PostSummary } from '../types/post';

interface PostCardProps {
  post: PostSummary;
  showScore?: boolean;
}

function formatTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
}

export default function PostCard({ post, showScore = false }: PostCardProps) {
  return (
    <article className="post-card">
      <header className="post-card-header">
        <Link to={`/posts/${post.id}`} className="post-title-link">
          {post.title}
        </Link>
        <span className="post-time">{formatTime(post.publishedAt)}</span>
      </header>

      <p className="post-excerpt">{post.excerpt}</p>

      <div className="meta-row">
        <Link to={`/users/${post.author.id}`}>{post.author.displayName}</Link>
        <span>{post.category.nameEn}</span>
        <span>♥ {post.likeCount}</span>
        <span>💬 {post.commentCount}</span>
        <span>★ {post.favoriteCount}</span>
        {showScore ? <span>hot {post.hotScore.toFixed(4)}</span> : null}
      </div>

      <div className="tag-row">
        {post.tags.map((tag) => (
          <span key={tag.id} className="tag-chip">
            #{tag.name}
          </span>
        ))}
      </div>
    </article>
  );
}

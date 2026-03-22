import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import type { PostSummary } from '../types/post';

interface PostCardProps {
  post: PostSummary;
  showScore?: boolean;
}

function formatTime(value: string, locale: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString(locale);
}

export default function PostCard({ post, showScore = false }: PostCardProps) {
  const { i18n, t } = useTranslation();
  const categoryName = i18n.language === 'zh-CN' ? post.category.nameZh : post.category.nameEn;

  return (
    <article className="post-card">
      {post.coverImageUrl ? (
        <img className="cover-preview cover-preview-card" src={post.coverImageUrl} alt={post.title} />
      ) : null}

      <header className="post-card-header">
        <Link to={`/posts/${post.id}`} className="post-title-link">
          {post.title}
        </Link>
        <span className="post-time">{formatTime(post.publishedAt, i18n.language)}</span>
      </header>

      <p className="post-excerpt">{post.excerpt}</p>

      <div className="meta-row">
        <Link to={`/users/${post.author.id}`}>{post.author.displayName}</Link>
        <span>{categoryName}</span>
        <span>{t('interaction.like')} {post.likeCount}</span>
        <span>{t('interaction.comment_count')} {post.commentCount}</span>
        <span>{t('interaction.favorite')} {post.favoriteCount}</span>
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

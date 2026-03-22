import { FormEvent, useCallback, useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { deletePost, getPostDetail } from '../api/posts';
import {
  createComment,
  deleteComment,
  favoritePost,
  getPostInteractionState,
  likePost,
  listPostComments,
  unfavoritePost,
  unlikePost
} from '../api/interactions';
import type { PostDetail } from '../types/post';
import type { CommentItem, PostInteractionSummary } from '../types/interaction';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import ReportComposer from '../components/ReportComposer';
import { reportPost } from '../api/reports';

function formatTime(value: string): string {
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
}

export default function PostDetailPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { id } = useParams<{ id: string }>();
  const currentUserId = useCurrentUserId();

  const [post, setPost] = useState<PostDetail | null>(null);
  const [comments, setComments] = useState<CommentItem[]>([]);
  const [liked, setLiked] = useState(false);
  const [favorited, setFavorited] = useState(false);
  const [commentContent, setCommentContent] = useState('');
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const applySummary = useCallback((summary: PostInteractionSummary) => {
    setPost((previous) =>
      previous
        ? {
            ...previous,
            likeCount: summary.likeCount,
            commentCount: summary.commentCount,
            favoriteCount: summary.favoriteCount,
            hotScore: summary.hotScore
          }
        : previous
    );
  }, []);

  const load = useCallback(async () => {
    if (!id) {
      return;
    }
    const postId = Number(id);
    if (!Number.isFinite(postId)) {
      return;
    }

    setLoading(true);
    setMessage(null);
    try {
      const [detail, commentList] = await Promise.all([
        getPostDetail(postId),
        listPostComments(postId)
      ]);
      setPost(detail);
      setComments(commentList);

      if (currentUserId) {
        try {
          const state = await getPostInteractionState(postId);
          setLiked(state.liked);
          setFavorited(state.favorited);
        } catch {
          setLiked(false);
          setFavorited(false);
        }
      } else {
        setLiked(false);
        setFavorited(false);
      }
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  }, [currentUserId, id, t]);

  useEffect(() => {
    load().catch(() => undefined);
  }, [load]);

  const canEdit = post && currentUserId && post.author.id === currentUserId;

  const onDeletePost = async () => {
    if (!post) {
      return;
    }
    setLoading(true);
    try {
      await deletePost(post.id);
      navigate('/feed/latest');
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  const onToggleLike = async () => {
    if (!post) {
      return;
    }
    setMessage(null);
    try {
      const summary = liked ? await unlikePost(post.id) : await likePost(post.id);
      setLiked(!liked);
      applySummary(summary);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    }
  };

  const onToggleFavorite = async () => {
    if (!post) {
      return;
    }
    setMessage(null);
    try {
      const summary = favorited ? await unfavoritePost(post.id) : await favoritePost(post.id);
      setFavorited(!favorited);
      applySummary(summary);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    }
  };

  const onCreateComment = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!post || !commentContent.trim()) {
      return;
    }
    setLoading(true);
    setMessage(null);
    try {
      const created = await createComment(post.id, commentContent.trim());
      setComments((previous) => [created, ...previous]);
      setCommentContent('');
      const latestDetail = await getPostDetail(post.id);
      setPost(latestDetail);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  const onDeleteComment = async (commentId: number) => {
    setLoading(true);
    setMessage(null);
    try {
      const summary = await deleteComment(commentId);
      setComments((previous) => previous.filter((item) => item.id !== commentId));
      applySummary(summary);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  if (!post) {
    return (
      <main className="page-shell">
        <h1>{t('post.detail_title')}</h1>
        {loading ? <p>{t('common.loading')}</p> : null}
        {message ? <p className="hint-text">{message}</p> : null}
      </main>
    );
  }

  const categoryName = i18n.language === 'zh-CN' ? post.category.nameZh : post.category.nameEn;

  return (
    <main className="page-shell">
      <header className="top-row">
        <h1>{post.title}</h1>
        {canEdit ? (
          <div className="action-row">
            <Link className="btn btn-secondary" to={`/posts/${post.id}/edit`}>
              {t('post.edit')}
            </Link>
            <button className="btn btn-danger" type="button" onClick={onDeletePost} disabled={loading}>
              {t('post.delete')}
            </button>
          </div>
        ) : null}
      </header>

      <p className="hint-text">
        {t('post.by_author')} <Link to={`/users/${post.author.id}`}>{post.author.displayName}</Link> |{' '}
        {formatTime(post.publishedAt)}
      </p>

      <p className="hint-text">{categoryName}</p>

      <div className="tag-row">
        {post.tags.map((tag) => (
          <span key={tag.id} className="tag-chip">
            #{tag.name}
          </span>
        ))}
      </div>

      {post.coverImageUrl ? (
        <img className="cover-preview cover-preview-detail" src={post.coverImageUrl} alt={post.title} />
      ) : null}

      <article className="post-detail-body">{post.content}</article>

      <div className="action-row">
        <button type="button" className="btn btn-secondary" disabled={!currentUserId || loading} onClick={onToggleLike}>
          {liked ? t('interaction.unlike') : t('interaction.like')} ({post.likeCount})
        </button>
        <button
          type="button"
          className="btn btn-secondary"
          disabled={!currentUserId || loading}
          onClick={onToggleFavorite}
        >
          {favorited ? t('interaction.unfavorite') : t('interaction.favorite')} ({post.favoriteCount})
        </button>
      </div>

      {currentUserId && !canEdit ? (
        <ReportComposer
          triggerLabel={t('report.report_post')}
          title={t('report.report_post')}
          onSubmit={async (payload) => {
            await reportPost(post.id, payload);
          }}
        />
      ) : null}

      {!currentUserId ? <p className="hint-text">{t('post.login_required')}</p> : null}

      <p className="hint-text">
        {t('interaction.comment_count')}: {post.commentCount} | hot {post.hotScore.toFixed(4)}
      </p>

      {currentUserId ? (
        <form className="auth-form" onSubmit={onCreateComment}>
          <label>
            {t('interaction.comment_input')}
            <textarea
              value={commentContent}
              onChange={(event) => setCommentContent(event.target.value)}
              maxLength={2000}
              rows={4}
              required
            />
          </label>
          <button type="submit" className="btn btn-primary" disabled={loading || !commentContent.trim()}>
            {t('interaction.comment_submit')}
          </button>
        </form>
      ) : null}

      <section className="comment-list">
        <h2>{t('interaction.comments_title')}</h2>
        {comments.length === 0 ? <p className="hint-text">{t('interaction.no_comments')}</p> : null}
        {comments.map((comment) => (
          <article key={comment.id} className="comment-item">
            <header className="top-row">
              <p>
                <strong>{comment.userDisplayName}</strong>
              </p>
              <p className="hint-text">{formatTime(comment.createdAt)}</p>
            </header>
            <p>{comment.content}</p>
            {currentUserId && currentUserId === comment.userId ? (
              <button
                type="button"
                className="btn btn-secondary"
                onClick={() => onDeleteComment(comment.id)}
                disabled={loading}
              >
                {t('interaction.comment_delete')}
              </button>
            ) : null}
          </article>
        ))}
      </section>

      {message ? <p className="hint-text">{message}</p> : null}
    </main>
  );
}

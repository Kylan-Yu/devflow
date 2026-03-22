import { useEffect, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { getUserProfile } from '../api/users';
import { listUserPosts } from '../api/posts';
import type { PostSummary } from '../types/post';
import type { PublicUserProfile } from '../api/users';
import PostCard from '../components/PostCard';
import { followUser, getFollowStatus, unfollowUser } from '../api/interactions';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import ReportComposer from '../components/ReportComposer';
import { reportUser } from '../api/reports';

export default function UserProfilePage() {
  const { t } = useTranslation();
  const { id } = useParams<{ id: string }>();
  const [profile, setProfile] = useState<PublicUserProfile | null>(null);
  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [cursor, setCursor] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);
  const [following, setFollowing] = useState(false);
  const [actionLoading, setActionLoading] = useState(false);
  const currentUserId = useCurrentUserId();

  useEffect(() => {
    const userId = Number(id);
    if (!Number.isFinite(userId)) {
      return;
    }

    const load = async () => {
      setLoading(true);
      setMessage(null);
      try {
        const [profileData, postPage] = await Promise.all([
          getUserProfile(userId),
          listUserPosts(userId, null, 10)
        ]);
        setProfile(profileData);
        setPosts(postPage.items);
        setCursor(postPage.nextCursor);
        setHasMore(postPage.hasMore);
        if (currentUserId && currentUserId !== userId) {
          try {
            const status = await getFollowStatus(userId);
            setFollowing(status.following);
          } catch {
            setFollowing(false);
          }
        } else {
          setFollowing(false);
        }
      } catch (error) {
        const text = error instanceof Error ? error.message : 'common.request_failed';
        setMessage(t(`messages.${text}`, { defaultValue: text }));
      } finally {
        setLoading(false);
      }
    };

    load().catch(() => undefined);
  }, [currentUserId, id, t]);

  const loadMore = async () => {
    if (!id || !cursor || loading) {
      return;
    }
    const userId = Number(id);
    if (!Number.isFinite(userId)) {
      return;
    }

    setLoading(true);
    try {
      const page = await listUserPosts(userId, cursor, 10);
      setPosts((prev) => [...prev, ...page.items]);
      setCursor(page.nextCursor);
      setHasMore(page.hasMore);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="page-shell">
      <h1>{t('profile.title')}</h1>

      {profile ? (
        <section className="profile-card">
          <header className="top-row">
            <div className="profile-header-block">
              {profile.avatarUrl ? (
                <img className="avatar-preview avatar-preview-small" src={profile.avatarUrl} alt={profile.displayName} />
              ) : (
                <div className="avatar-placeholder avatar-preview-small">
                  {(profile.displayName || profile.username || 'D').slice(0, 1).toUpperCase()}
                </div>
              )}
              <div>
                <h2>{profile.displayName}</h2>
                <p>@{profile.username}</p>
              </div>
            </div>
            {currentUserId === profile.id ? (
              <Link to="/settings" className="btn btn-secondary">
                {t('profile.edit_profile')}
              </Link>
            ) : currentUserId ? (
              <div className="stacked-actions">
                <button
                  type="button"
                  className="btn btn-secondary"
                  disabled={actionLoading}
                  onClick={async () => {
                    setActionLoading(true);
                    setMessage(null);
                    try {
                      if (following) {
                        const result = await unfollowUser(profile.id);
                        setFollowing(result.following);
                      } else {
                        const result = await followUser(profile.id);
                        setFollowing(result.following);
                      }
                    } catch (error) {
                      const text = error instanceof Error ? error.message : 'common.request_failed';
                      setMessage(t(`messages.${text}`, { defaultValue: text }));
                    } finally {
                      setActionLoading(false);
                    }
                  }}
                >
                  {following ? t('profile.unfollow') : t('profile.follow')}
                </button>
                <ReportComposer
                  triggerLabel={t('report.report_user')}
                  title={t('report.report_user')}
                  onSubmit={async (payload) => {
                    await reportUser(profile.id, payload);
                  }}
                />
              </div>
            ) : null}
          </header>
          {profile.bio ? <p>{profile.bio}</p> : <p className="hint-text">{t('profile.no_bio')}</p>}
        </section>
      ) : null}

      <h2>{t('profile.posts_title')}</h2>
      <section className="post-list">
        {posts.map((post) => (
          <PostCard key={post.id} post={post} />
        ))}
      </section>

      {loading ? <p className="hint-text">{t('common.loading')}</p> : null}
      {message ? <p className="hint-text">{message}</p> : null}

      {hasMore ? (
        <button className="btn btn-secondary" onClick={loadMore} type="button" disabled={loading}>
          {t('feed.load_more')}
        </button>
      ) : (
        <p className="hint-text">{t('feed.no_more')}</p>
      )}
    </main>
  );
}

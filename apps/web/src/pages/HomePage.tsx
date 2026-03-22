import { FormEvent, useCallback, useEffect, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { logoutCurrentSession } from '../api/auth';
import { fetchUnreadCount } from '../api/notifications';
import { latestFeed } from '../api/feed';
import { listCategories } from '../api/catalog';
import LanguageSwitcher from '../components/LanguageSwitcher';
import PostCard from '../components/PostCard';
import Pagination from '../components/Pagination';
import BackButton from '../components/BackButton';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import { useNotificationSocket } from '../hooks/useNotificationSocket';
import type { NotificationPushMessage } from '../types/notification';
import type { CategoryItem, PostSummary } from '../types/post';

export default function HomePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const currentUserId = useCurrentUserId();
  const [unreadCount, setUnreadCount] = useState(0);
  const [loggingOut, setLoggingOut] = useState(false);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [posts, setPosts] = useState<PostSummary[]>([]);
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [selectedCategoryId, setSelectedCategoryId] = useState<number | null>(null);
  const [postsLoading, setPostsLoading] = useState(false);
  const [postsError, setPostsError] = useState<string | null>(null);
  
  // 游标分页状态
  const [nextCursor, setNextCursor] = useState<string | null>(null);
  const [previousCursors, setPreviousCursors] = useState<string[]>([]);
  const [pageSize, setPageSize] = useState(10);
  const [hasMore, setHasMore] = useState(false);

  const loadUnread = useCallback(async () => {
    if (!currentUserId) {
      setUnreadCount(0);
      return;
    }
    try {
      setUnreadCount(await fetchUnreadCount());
    } catch {
      setUnreadCount(0);
    }
  }, [currentUserId]);

  const loadPosts = useCallback(async (cursor: string | null = null, reset: boolean = false) => {
    setPostsLoading(true);
    setPostsError(null);
    
    try {
      const page = await latestFeed({ 
        size: pageSize, 
        categoryId: selectedCategoryId,
        cursor: cursor
      });
      
      if (reset) {
        setPosts(page.items);
        setPreviousCursors([]);
      } else {
        setPosts(page.items);
      }
      
      setNextCursor(page.nextCursor);
      setHasMore(page.hasMore);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setPostsError(t(`messages.${text}`, { defaultValue: text }));
      setPosts([]);
      setNextCursor(null);
      setHasMore(false);
    } finally {
      setPostsLoading(false);
    }
  }, [pageSize, selectedCategoryId, t]);

  const loadCategories = useCallback(async () => {
    try {
      setCategories(await listCategories());
    } catch {
      setCategories([]);
    }
  }, []);

  useEffect(() => {
    loadCategories();
    loadPosts(null, true);
  }, [loadCategories]);

  useEffect(() => {
    loadPosts(null, true);
  }, [selectedCategoryId, pageSize, loadPosts]);

  useEffect(() => {
    loadUnread().catch(() => undefined);
  }, [loadUnread]);

  const handleNext = () => {
    if (nextCursor && hasMore && !postsLoading) {
      const currentCursor = nextCursor;
      setPreviousCursors(prev => [...prev, currentCursor]);
      loadPosts(currentCursor, false);
    }
  };

  const handlePrevious = () => {
    if (previousCursors.length > 0 && !postsLoading) {
      const newCursors = [...previousCursors];
      const previousCursor = newCursors.pop() || null;
      setPreviousCursors(newCursors);
      loadPosts(previousCursor, false);
    }
  };

  const handlePageSizeChange = (newSize: number) => {
    setPageSize(newSize);
    // 页面大小改变时重置到第一页
    loadPosts(null, true);
  };

  const handleSocketMessage = useCallback((payload: NotificationPushMessage) => {
    setUnreadCount(payload.unreadCount);
  }, []);

  const handleLogout = useCallback(async () => {
    setLoggingOut(true);
    try {
      await logoutCurrentSession();
      setUnreadCount(0);
      navigate('/');
    } finally {
      setLoggingOut(false);
    }
  }, [navigate]);

  const handleSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    const query = new URLSearchParams();
    if (searchKeyword.trim()) {
      query.set('q', searchKeyword.trim());
    }
    navigate(`/search${query.toString() ? `?${query.toString()}` : ''}`);
  };

  useNotificationSocket({
    enabled: Boolean(currentUserId),
    userId: currentUserId,
    onMessage: handleSocketMessage
  });

  return (
    <main className="page-shell">
      <header className="top-row">
    <div className="header-left">
      <BackButton />
      <h1>{t('app.title')}</h1>
    </div>
    <div className="action-row compact-row">
      {currentUserId ? (
        <button
          type="button"
          className="btn btn-secondary"
          onClick={handleLogout}
          disabled={loggingOut}
        >
          {loggingOut ? t('common.loading') : t('auth.logout')}
        </button>
      ) : null}
      <LanguageSwitcher />
    </div>
  </header>

      <p>{t('home.subtitle')}</p>

      {/* 分类筛选器 */}
      {categories.length > 0 && (
        <div className="filter-row">
          <label>
            {t('feed.category_filter')}
            <select
              value={selectedCategoryId ?? ''}
              onChange={(event) =>
                setSelectedCategoryId(event.target.value ? Number(event.target.value) : null)
              }
            >
              <option value="">{t('feed.all_categories')}</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {category.nameEn} / {category.nameZh}
                </option>
              ))}
            </select>
          </label>
        </div>
      )}

      <form className="auth-form search-form home-search-form" onSubmit={handleSearch}>
        <label>
          {t('search.keyword')}
          <input
            value={searchKeyword}
            onChange={(event) => setSearchKeyword(event.target.value)}
            placeholder={t('search.placeholder')}
            maxLength={80}
          />
        </label>
        <div className="action-row compact-row">
          <button type="submit" className="btn btn-primary">
            {t('search.submit')}
          </button>
          <Link to="/search" className="btn btn-secondary">
            {t('search.advanced')}
          </Link>
        </div>
      </form>

      {!currentUserId ? (
        <div className="action-row">
          <Link to="/login" className="btn btn-primary">
            {t('auth.login')}
          </Link>
          <Link to="/register" className="btn btn-secondary">
            {t('auth.register')}
          </Link>
        </div>
      ) : null}

      {/* 帖子列表 */}
      <section className="post-list">
        {postsLoading ? (
          <p className="hint-text">{t('common.loading')}</p>
        ) : postsError ? (
          <p className="hint-text">{postsError}</p>
        ) : posts.length > 0 ? (
          <>
            {posts.map((post) => (
              <PostCard key={post.id} post={post} />
            ))}
            <div className="action-row">
              <Link to="/feed/latest" className="btn btn-primary">
                {t('feed.view_all_latest')}
              </Link>
              <Link to="/feed/hot" className="btn btn-secondary">
                {t('feed.hot_title')}
              </Link>
            </div>
          </>
        ) : (
          <p className="hint-text">{t('feed.no_posts')}</p>
        )}
      </section>

      {/* 分页控件 */}
      {posts.length > 0 && (
        <Pagination
          hasNext={hasMore}
          hasPrevious={previousCursors.length > 0}
          pageSize={pageSize}
          onNext={handleNext}
          onPrevious={handlePrevious}
          onPageSizeChange={handlePageSizeChange}
          loading={postsLoading}
        />
      )}

      <div className="action-row">
        <Link to="/posts/new" className="btn btn-secondary">
          {t('post.create_title')}
        </Link>
        {currentUserId ? (
          <Link to="/notifications" className="btn btn-secondary notification-link">
            {t('notification.title')}
            {unreadCount > 0 ? <span className="badge">{unreadCount}</span> : null}
          </Link>
        ) : null}
        {currentUserId ? (
          <Link to={`/users/${currentUserId}`} className="btn btn-secondary">
            {t('profile.title')}
          </Link>
        ) : null}
        {currentUserId ? (
          <Link to="/settings" className="btn btn-secondary">
            {t('profile.settings_link')}
          </Link>
        ) : null}
        {currentUserId ? (
          <Link to="/reports/me" className="btn btn-secondary">
            {t('report.my_reports')}
          </Link>
        ) : null}
      </div>
    </main>
  );
}

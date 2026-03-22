import { useEffect, useState } from 'react';
import { useTranslation } from 'react-i18next';
import { hotFeed } from '../api/feed';
import { listCategories } from '../api/catalog';
import type { CategoryItem, PostSummary } from '../types/post';
import PostCard from '../components/PostCard';
import BackButton from '../components/BackButton';

export default function HotFeedPage() {
  const { t } = useTranslation();
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [items, setItems] = useState<PostSummary[]>([]);
  const [cursor, setCursor] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => undefined);
  }, []);

  useEffect(() => {
    setItems([]);
    setCursor(null);
    setHasMore(false);
    loadFirstPage(categoryId);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [categoryId]);

  const loadFirstPage = async (nextCategoryId: number | null) => {
    setLoading(true);
    setMessage(null);
    try {
      const page = await hotFeed({ size: 10, categoryId: nextCategoryId });
      setItems(page.items);
      setCursor(page.nextCursor);
      setHasMore(page.hasMore);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  const loadMore = async () => {
    if (!cursor || loading) {
      return;
    }
    setLoading(true);
    setMessage(null);
    try {
      const page = await hotFeed({ cursor, size: 10, categoryId });
      setItems((prev) => [...prev, ...page.items]);
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
      <header className="top-row">
        <div className="header-left">
          <BackButton />
          <h1>{t('feed.hot_title')}</h1>
        </div>
      </header>

      <div className="filter-row">
        <label>
          {t('feed.category_filter')}
          <select
            value={categoryId ?? ''}
            onChange={(event) =>
              setCategoryId(event.target.value ? Number(event.target.value) : null)
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

      <section className="post-list">
        {items.map((post) => (
          <PostCard key={post.id} post={post} showScore />
        ))}
      </section>

      {message ? <p className="hint-text">{message}</p> : null}

      {hasMore ? (
        <button className="btn btn-secondary" type="button" onClick={loadMore} disabled={loading}>
          {loading ? t('common.loading') : t('feed.load_more')}
        </button>
      ) : (
        <p className="hint-text">{t('feed.no_more')}</p>
      )}
    </main>
  );
}

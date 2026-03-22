import { FormEvent, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { listCategories } from '../api/catalog';
import { searchPosts } from '../api/search';
import type { CategoryItem, PostSummary } from '../types/post';
import PostCard from '../components/PostCard';

export default function SearchPage() {
  const { t, i18n } = useTranslation();
  const [searchParams, setSearchParams] = useSearchParams();
  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [draftKeyword, setDraftKeyword] = useState(searchParams.get('q') ?? '');
  const [draftCategoryId, setDraftCategoryId] = useState(searchParams.get('categoryId') ?? '');
  const [items, setItems] = useState<PostSummary[]>([]);
  const [cursor, setCursor] = useState<string | null>(null);
  const [hasMore, setHasMore] = useState(false);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const keyword = searchParams.get('q')?.trim() ?? '';
  const categoryIdParam = searchParams.get('categoryId');
  const categoryId = categoryIdParam ? Number(categoryIdParam) : null;
  const hasConditions = keyword.length > 0 || Number.isFinite(categoryId);

  useEffect(() => {
    listCategories().then(setCategories).catch(() => undefined);
  }, []);

  useEffect(() => {
    setDraftKeyword(searchParams.get('q') ?? '');
    setDraftCategoryId(searchParams.get('categoryId') ?? '');
  }, [searchParams]);

  useEffect(() => {
    if (!hasConditions) {
      setItems([]);
      setCursor(null);
      setHasMore(false);
      setMessage(null);
      return;
    }

    const load = async () => {
      setLoading(true);
      setMessage(null);
      try {
        const page = await searchPosts({
          keyword,
          categoryId: Number.isFinite(categoryId) ? categoryId : null,
          size: 10
        });
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

    void load();
  }, [categoryId, hasConditions, keyword, t]);

  const submitSearch = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();

    const nextParams = new URLSearchParams();
    if (draftKeyword.trim()) {
      nextParams.set('q', draftKeyword.trim());
    }
    if (draftCategoryId) {
      nextParams.set('categoryId', draftCategoryId);
    }
    setSearchParams(nextParams);
  };

  const loadMore = async () => {
    if (!cursor || loading) {
      return;
    }

    setLoading(true);
    setMessage(null);
    try {
      const page = await searchPosts({
        keyword,
        categoryId: Number.isFinite(categoryId) ? categoryId : null,
        cursor,
        size: 10
      });
      setItems((previous) => [...previous, ...page.items]);
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
        <div>
          <h1>{t('search.title')}</h1>
          <p className="hint-text">{t('search.subtitle')}</p>
        </div>
      </header>

      <form className="auth-form search-form" onSubmit={submitSearch}>
        <div className="search-form-grid">
          <label>
            {t('search.keyword')}
            <input
              value={draftKeyword}
              onChange={(event) => setDraftKeyword(event.target.value)}
              placeholder={t('search.placeholder')}
              maxLength={80}
            />
          </label>

          <label>
            {t('search.category')}
            <select value={draftCategoryId} onChange={(event) => setDraftCategoryId(event.target.value)}>
              <option value="">{t('feed.all_categories')}</option>
              {categories.map((category) => (
                <option key={category.id} value={category.id}>
                  {i18n.language === 'zh-CN' ? category.nameZh : category.nameEn}
                </option>
              ))}
            </select>
          </label>
        </div>

        <div className="action-row compact-row">
          <button type="submit" className="btn btn-primary" disabled={loading}>
            {t('search.submit')}
          </button>
          <button
            type="button"
            className="btn btn-secondary"
            onClick={() => {
              setDraftKeyword('');
              setDraftCategoryId('');
              setSearchParams(new URLSearchParams());
            }}
            disabled={loading}
          >
            {t('search.clear')}
          </button>
        </div>
      </form>

      {!hasConditions ? <p className="hint-text">{t('search.empty_state')}</p> : null}
      {hasConditions && !loading && items.length === 0 && !message ? (
        <p className="hint-text">{t('search.no_results')}</p>
      ) : null}
      {message ? <p className="hint-text">{message}</p> : null}

      <section className="post-list">
        {items.map((post) => (
          <PostCard key={post.id} post={post} />
        ))}
      </section>

      {hasMore ? (
        <button className="btn btn-secondary" type="button" onClick={loadMore} disabled={loading}>
          {loading ? t('common.loading') : t('feed.load_more')}
        </button>
      ) : null}
    </main>
  );
}

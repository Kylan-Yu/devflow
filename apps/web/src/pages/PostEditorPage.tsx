import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { listCategories, listTags } from '../api/catalog';
import { uploadPostCoverImage } from '../api/media';
import { createPost, getPostDetail, updatePost } from '../api/posts';
import { useCurrentUserId } from '../hooks/useCurrentUserId';
import BackButton from '../components/BackButton';
import type { CategoryItem, TagItem } from '../types/post';

export default function PostEditorPage() {
  const { t, i18n } = useTranslation();
  const navigate = useNavigate();
  const { id } = useParams<{ id?: string }>();
  const editingPostId = useMemo(() => (id ? Number(id) : null), [id]);

  const [categories, setCategories] = useState<CategoryItem[]>([]);
  const [tags, setTags] = useState<TagItem[]>([]);
  const [title, setTitle] = useState('');
  const [content, setContent] = useState('');
  const [coverImageUrl, setCoverImageUrl] = useState('');
  const [categoryId, setCategoryId] = useState<number | null>(null);
  const [selectedTagIds, setSelectedTagIds] = useState<number[]>([]);
  const [loading, setLoading] = useState(false);
  const [uploadingCover, setUploadingCover] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const currentUserId = useCurrentUserId();
  const hasSession = currentUserId !== null;

  useEffect(() => {
    const loadCatalog = async () => {
      const [categoryList, tagList] = await Promise.all([listCategories(), listTags()]);
      setCategories(categoryList);
      setTags(tagList);
      setCategoryId((current) => (current ? current : categoryList[0]?.id ?? null));
    };
    loadCatalog().catch(() => undefined);
  }, []);

  useEffect(() => {
    const loadPost = async () => {
      if (!editingPostId) {
        return;
      }
      setLoading(true);
      try {
        const post = await getPostDetail(editingPostId);
        setTitle(post.title);
        setContent(post.content);
        setCoverImageUrl(post.coverImageUrl ?? '');
        setCategoryId(post.category.id);
        setSelectedTagIds(post.tags.map((item) => item.id));
      } catch (error) {
        const text = error instanceof Error ? error.message : 'common.request_failed';
        setMessage(t(`messages.${text}`, { defaultValue: text }));
      } finally {
        setLoading(false);
      }
    };
    loadPost().catch(() => undefined);
  }, [editingPostId, t]);

  const toggleTag = (tagId: number) => {
    setSelectedTagIds((prev) =>
      prev.includes(tagId) ? prev.filter((item) => item !== tagId) : [...prev, tagId]
    );
  };

  const onCoverSelected = async (file: File | null) => {
    if (!file) {
      return;
    }

    setUploadingCover(true);
    setMessage(null);
    try {
      const uploaded = await uploadPostCoverImage(file);
      setCoverImageUrl(uploaded.url);
      setMessage(t('messages.media.cover_upload_success'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setUploadingCover(false);
    }
  };

  const submit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    if (!categoryId) {
      setMessage(t('messages.category.not_found'));
      return;
    }
    setLoading(true);
    setMessage(null);

    try {
      const payload = {
        title,
        content,
        coverImageUrl: coverImageUrl.trim() || null,
        contentType: 'MARKDOWN' as const,
        categoryId,
        tagIds: selectedTagIds
      };

      const result = editingPostId
        ? await updatePost(editingPostId, payload)
        : await createPost(payload);
      navigate(`/posts/${result.id}`);
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setLoading(false);
    }
  };

  if (!hasSession) {
    return (
      <main className="page-shell">
        <h1>{t('post.editor_title')}</h1>
        <p className="hint-text">{t('post.login_required')}</p>
        <Link to="/login">{t('auth.login')}</Link>
      </main>
    );
  }

  return (
    <main className="page-shell auth-shell-wide">
      <div className="page-header">
        <BackButton fallback="/" />
        <h1>{editingPostId ? t('post.edit_title') : t('post.create_title')}</h1>
      </div>

      <form className="auth-form" onSubmit={submit}>
        <label>
          {t('post.title')}
          <input value={title} onChange={(event) => setTitle(event.target.value)} required minLength={5} />
        </label>

        <label>
          {t('post.category')}
          <select
            value={categoryId ?? ''}
            onChange={(event) => setCategoryId(Number(event.target.value))}
            required
          >
            {categories.map((category) => (
              <option key={category.id} value={category.id}>
                {i18n.language === 'zh-CN' ? category.nameZh : category.nameEn}
              </option>
            ))}
          </select>
        </label>

        <label>
          {t('post.cover_image')}
          <input value={coverImageUrl} onChange={(event) => setCoverImageUrl(event.target.value)} />
        </label>

        <label>
          {t('post.cover_upload')}
          <input
            type="file"
            accept="image/*"
            onChange={(event) => {
              void onCoverSelected(event.target.files?.[0] ?? null);
              event.currentTarget.value = '';
            }}
            disabled={uploadingCover || loading}
          />
        </label>
        <p className="form-help">
          {uploadingCover ? t('common.loading') : t('post.cover_upload_hint')}
        </p>

        {coverImageUrl ? (
          <img className="cover-preview" src={coverImageUrl} alt={t('post.cover_preview')} />
        ) : null}

        <label>
          {t('post.content')}
          <textarea
            value={content}
            onChange={(event) => setContent(event.target.value)}
            required
            minLength={10}
            rows={12}
          />
        </label>

        <fieldset className="tag-fieldset">
          <legend>{t('post.tags')}</legend>
          <div className="tag-selector">
            {tags.map((tag) => (
              <label key={tag.id} className="tag-option">
                <input
                  type="checkbox"
                  checked={selectedTagIds.includes(tag.id)}
                  onChange={() => toggleTag(tag.id)}
                />
                <span>#{tag.name}</span>
              </label>
            ))}
          </div>
        </fieldset>

        <button className="btn btn-primary" type="submit" disabled={loading || uploadingCover}>
          {loading ? t('common.loading') : editingPostId ? t('post.save') : t('post.publish')}
        </button>
      </form>

      {message ? <p className="hint-text">{message}</p> : null}
    </main>
  );
}

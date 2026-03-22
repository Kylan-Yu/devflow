import { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import type { AdminPage } from '../types/pagination';
import './AdminPagination.css';

interface AdminPaginationProps<T> {
  data: AdminPage<T>;
  onPageChange: (page: number) => void;
  onPageSizeChange: (size: number) => void;
  onSearchChange: (search: string) => void;
  loading?: boolean;
}

export default function AdminPagination<T>({
  data,
  onPageChange,
  onPageSizeChange,
  onSearchChange,
  loading = false,
}: AdminPaginationProps<T>) {
  const { t } = useTranslation();

  const handlePageChange = useCallback((event: React.ChangeEvent<HTMLSelectElement>) => {
    const newPage = Number(event.target.value);
    onPageChange(newPage);
  }, [onPageChange]);

  const handlePageSizeChange = useCallback((event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(event.target.value);
    onPageSizeChange(newSize);
  }, [onPageSizeChange]);

  const handleSearchChange = useCallback((event: React.ChangeEvent<HTMLInputElement>) => {
    const newSearch = event.target.value;
    onSearchChange(newSearch);
  }, [onSearchChange]);

  const handlePrevious = useCallback(() => {
    if (data.hasPrevious && !loading) {
      onPageChange(data.currentPage - 1);
    }
  }, [data, onPageChange, loading]);

  const handleNext = useCallback(() => {
    if (data.hasNext && !loading) {
      onPageChange(data.currentPage + 1);
    }
  }, [data, onPageChange, loading]);

  const renderPageNumbers = () => {
    const pages = [];
    const startPage = Math.max(1, data.currentPage - 2);
    const endPage = Math.min(data.totalPages, data.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  };

  return (
    <div className="admin-pagination">
      <div className="pagination-controls">
        <div className="search-box">
          <input
            type="text"
            placeholder={t('admin.search_placeholder')}
            value={data.search || ''}
            onChange={handleSearchChange}
            disabled={loading}
          />
        </div>
        
        <div className="pagination-info">
          <span>{t('admin.showing', { 
            start: (data.currentPage - 1) * data.pageSize + 1,
            end: Math.min(data.currentPage * data.pageSize, data.totalItems),
            total: data.totalItems 
          })}</span>
        </div>

        <div className="pagination-buttons">
          <select
            value={data.currentPage}
            onChange={handlePageChange}
            disabled={loading}
          >
            {renderPageNumbers().map(page => (
              <option key={page} value={page}>
                {t('admin.page', { page })}
              </option>
            ))}
          </select>

          <button
            type="button"
            onClick={handlePrevious}
            disabled={!data.hasPrevious || loading}
            className="btn btn-secondary btn-small"
          >
            ← {t('admin.previous')}
          </button>

          <button
            type="button"
            onClick={handleNext}
            disabled={!data.hasNext || loading}
            className="btn btn-secondary btn-small"
          >
            {t('admin.next')} →
          </button>
        </div>

        <div className="page-size-selector">
          <label>
            {t('admin.page_size')}
            <select
              value={data.pageSize}
              onChange={handlePageSizeChange}
              disabled={loading}
            >
              <option value={10}>10</option>
              <option value={20}>20</option>
              <option value={50}>50</option>
              <option value={100}>100</option>
            </select>
          </label>
        </div>
      </div>
    </div>
  );
}

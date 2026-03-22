import { useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { AdminPage } from '../types/pagination';
import './AdminDataTable.css';

interface AdminDataTableProps<T> {
  data: AdminPage<T>;
  columns: {
    key: keyof T;
    label: string;
    render?: (item: T) => React.ReactNode;
  }[];
  actions?: (item: T) => React.ReactNode;
  loading?: boolean;
  onPageChange?: (page: number) => void;
  onPageSizeChange?: (size: number) => void;
  onSearchChange?: (search: string) => void;
}

export default function AdminDataTable<T>({
  data,
  columns,
  actions,
  loading = false,
  onPageChange,
  onPageSizeChange,
  onSearchChange,
}: AdminDataTableProps<T>) {
  const { t } = useTranslation();
  const [searchInput, setSearchInput] = useState(data.search || '');

  const handlePageChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const newPage = Number(event.target.value);
    onPageChange?.(newPage);
  };

  const handlePageSizeChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(event.target.value);
    onPageSizeChange?.(newSize);
  };

  const handleSearchChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    const newSearch = event.target.value;
    setSearchInput(newSearch);
  };

  const handleSearchSubmit = () => {
    onSearchChange?.(searchInput);
  };

  const handleSearchKeyPress = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleSearchSubmit();
    }
  };

  const handlePrevious = () => {
    if (data.hasPrevious && onPageChange) {
      onPageChange(data.currentPage - 1);
    }
  };

  const handleNext = () => {
    if (data.hasNext && onPageChange) {
      onPageChange(data.currentPage + 1);
    }
  };

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
    <div className="admin-data-table">
      <div className="table-shell">
        <table className="data-table">
          <thead>
            <tr>
              {columns.map((column) => (
                <th key={String(column.key)}>{column.label}</th>
              ))}
              {actions && <th>{t('admin.actions')}</th>}
            </tr>
          </thead>
          <tbody>
            {loading ? (
              <tr>
                <td colSpan={columns.length + (actions ? 1 : 0)} className="loading-cell">
                  {t('common.loading')}
                </td>
              </tr>
            ) : data && data.items && data.items.length > 0 ? (
              data.items.map((item, index) => (
                <tr key={index}>
                  {columns.map((column) => (
                    <td key={String(column.key)}>
                      {column.render ? column.render(item) : String(item[column.key] || '')}
                    </td>
                  ))}
                  {actions && <td>{actions(item)}</td>}
                </tr>
              ))
            ) : (
              <tr>
                <td colSpan={columns.length + (actions ? 1 : 0)} className="empty-cell">
                  {t('admin.no_data')}
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
      
      {(onPageChange || onPageSizeChange || onSearchChange) && (
        <div className="pagination-controls">
          {onSearchChange && (
            <div className="search-box">
              <input
                type="text"
                placeholder={t('admin.search_placeholder')}
                value={searchInput}
                onChange={handleSearchChange}
                onKeyPress={handleSearchKeyPress}
              />
              <button
                type="button"
                className="btn btn-primary btn-small"
                onClick={handleSearchSubmit}
                disabled={loading}
              >
                {t('admin.search')}
              </button>
            </div>
          )}
          
          <div className="pagination-info">
            <span>{t('admin.showing', { 
              start: data.totalItems > 0 ? (data.currentPage - 1) * (data.pageSize || 0) + 1 : 0,
              end: data.totalItems > 0 ? Math.min(data.currentPage * (data.pageSize || 0), data.totalItems || 0) : 0,
              total: data.totalItems || 0
            })}</span>
          </div>

          {onPageChange && (
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
          )}

          {onPageSizeChange && (
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
          )}
        </div>
      )}
    </div>
  );
}

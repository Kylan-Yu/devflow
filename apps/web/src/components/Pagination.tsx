import { useCallback } from 'react';
import { useTranslation } from 'react-i18next';
import './Pagination.css';

interface PaginationProps {
  hasNext: boolean;
  hasPrevious: boolean;
  pageSize: number;
  onNext: () => void;
  onPrevious: () => void;
  onPageSizeChange: (size: number) => void;
  loading?: boolean;
}

export default function Pagination({
  hasNext,
  hasPrevious,
  pageSize,
  onNext,
  onPrevious,
  onPageSizeChange,
  loading = false,
}: PaginationProps) {
  const { t } = useTranslation();

  const handlePageSizeChange = useCallback((event: React.ChangeEvent<HTMLSelectElement>) => {
    const newSize = Number(event.target.value);
    onPageSizeChange(newSize);
  }, [onPageSizeChange]);

  return (
    <div className="pagination-controls">
      <div className="pagination-row">
        <button
          className="btn btn-secondary"
          type="button"
          onClick={onPrevious}
          disabled={!hasPrevious || loading}
        >
          {t('pagination.previous')}
        </button>

        <div className="page-size-selector">
          <label>
            {t('pagination.page_size')}
            <select
              value={pageSize}
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

        <button
          className="btn btn-secondary"
          type="button"
          onClick={onNext}
          disabled={!hasNext || loading}
        >
          {t('pagination.next')}
        </button>
      </div>
    </div>
  );
}

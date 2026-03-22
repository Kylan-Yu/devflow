import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import './BackButton.css';

interface BackButtonProps {
  fallback?: string;
  className?: string;
}

export default function BackButton({ fallback = '/', className = '' }: BackButtonProps) {
  const navigate = useNavigate();
  const { t } = useTranslation();

  const handleBack = () => {
    // 如果有历史记录，返回上一页，否则跳转到fallback
    if (window.history.length > 1) {
      window.history.back();
    } else {
      navigate(fallback);
    }
  };

  return (
    <button
      type="button"
      className={`btn btn-secondary back-button ${className}`}
      onClick={handleBack}
    >
      ← {t('common.back')}
    </button>
  );
}

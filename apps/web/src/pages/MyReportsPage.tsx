import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { listMyReports, type ReportItem } from '../api/reports';
import { useCurrentUserId } from '../hooks/useCurrentUserId';

function formatTime(value: string | null): string {
  if (!value) {
    return '-';
  }
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) {
    return value;
  }
  return date.toLocaleString();
}

export default function MyReportsPage() {
  const { t } = useTranslation();
  const currentUserId = useCurrentUserId();
  const [reports, setReports] = useState<ReportItem[]>([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  useEffect(() => {
    if (!currentUserId) {
      return;
    }

    const load = async () => {
      setLoading(true);
      setMessage(null);
      try {
        setReports(await listMyReports());
      } catch (error) {
        const text = error instanceof Error ? error.message : 'common.request_failed';
        setMessage(t(`messages.${text}`, { defaultValue: text }));
      } finally {
        setLoading(false);
      }
    };

    void load();
  }, [currentUserId, t]);

  if (!currentUserId) {
    return (
      <main className="page-shell">
        <h1>{t('report.my_reports')}</h1>
        <p className="hint-text">{t('report.login_required')}</p>
      </main>
    );
  }

  return (
    <main className="page-shell">
      <header className="top-row">
        <div>
          <h1>{t('report.my_reports')}</h1>
          <p className="hint-text">{t('report.my_reports_hint')}</p>
        </div>
        <Link to="/" className="btn btn-secondary">
          {t('report.back_home')}
        </Link>
      </header>

      {loading ? <p className="hint-text">{t('common.loading')}</p> : null}
      {message ? <p className="hint-text">{message}</p> : null}
      {!loading && reports.length === 0 ? <p className="hint-text">{t('report.no_reports')}</p> : null}

      <section className="report-list">
        {reports.map((report) => {
          const canOpenTarget =
            (report.targetType === 'POST' && report.targetStatus !== 'DELETED') ||
            (report.targetType === 'USER' && report.targetStatus !== 'DISABLED');
          const targetPath = report.targetType === 'POST' ? `/posts/${report.targetId}` : `/users/${report.targetId}`;

          return (
            <article key={report.id} className="report-item">
              <header className="top-row">
                <div>
                  <strong>{report.targetLabel}</strong>
                  <div className="table-meta">
                    {t(`report.target_${report.targetType.toLowerCase()}`)} #{report.targetId}
                  </div>
                </div>
                <span className={`status-pill status-${report.status.toLowerCase()}`}>
                  {t(`report.status_${report.status.toLowerCase()}`)}
                </span>
              </header>

              <div className="report-meta-grid">
                <span>{t('report.reason')}: {t(`report.reason_${report.reason.toLowerCase()}`)}</span>
                <span>{t('report.target_status')}: {t(`report.target_status_${report.targetStatus.toLowerCase()}`, { defaultValue: report.targetStatus })}</span>
                <span>{t('report.submitted_at')}: {formatTime(report.createdAt)}</span>
                <span>{t('report.reviewed_at')}: {formatTime(report.reviewedAt)}</span>
                <span>{t('report.resolution_action')}: {t(`report.action_${report.resolutionAction.toLowerCase()}`)}</span>
              </div>

              {report.detail ? <p>{report.detail}</p> : <p className="hint-text">{t('report.no_detail')}</p>}
              {report.resolutionNote ? <p className="hint-text">{report.resolutionNote}</p> : null}

              {canOpenTarget ? (
                <div className="action-row compact-row">
                  <Link to={targetPath} className="btn btn-secondary">
                    {t('report.open_target')}
                  </Link>
                </div>
              ) : null}
            </article>
          );
        })}
      </section>
    </main>
  );
}

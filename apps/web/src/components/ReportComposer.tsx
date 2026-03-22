import { FormEvent, useState } from 'react';
import { useTranslation } from 'react-i18next';
import type { CreateReportPayload, ReportReason } from '../api/reports';

interface ReportComposerProps {
  triggerLabel: string;
  title: string;
  onSubmit: (payload: CreateReportPayload) => Promise<void>;
}

const REPORT_REASONS: ReportReason[] = ['SPAM', 'HARASSMENT', 'INAPPROPRIATE', 'MISLEADING', 'OTHER'];

export default function ReportComposer({ triggerLabel, title, onSubmit }: ReportComposerProps) {
  const { t } = useTranslation();
  const [open, setOpen] = useState(false);
  const [reason, setReason] = useState<ReportReason>('SPAM');
  const [detail, setDetail] = useState('');
  const [submitting, setSubmitting] = useState(false);
  const [message, setMessage] = useState<string | null>(null);

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setSubmitting(true);
    setMessage(null);

    try {
      await onSubmit({
        reason,
        detail: detail.trim() || null
      });
      setDetail('');
      setReason('SPAM');
      setOpen(false);
      setMessage(t('messages.report.submit_success'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: text }));
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <section className="report-box">
      <div className="action-row compact-row">
        <button type="button" className="btn btn-secondary" onClick={() => setOpen((previous) => !previous)}>
          {open ? t('report.cancel') : triggerLabel}
        </button>
      </div>

      {open ? (
        <form className="auth-form report-form" onSubmit={handleSubmit}>
          <h3>{title}</h3>
          <p className="hint-text">{t('report.description')}</p>

          <label>
            {t('report.reason')}
            <select value={reason} onChange={(event) => setReason(event.target.value as ReportReason)}>
              {REPORT_REASONS.map((item) => (
                <option key={item} value={item}>
                  {t(`report.reason_${item.toLowerCase()}`)}
                </option>
              ))}
            </select>
          </label>

          <label>
            {t('report.detail')}
            <textarea
              value={detail}
              onChange={(event) => setDetail(event.target.value)}
              rows={4}
              maxLength={500}
              placeholder={t('report.detail_placeholder')}
            />
          </label>

          <div className="action-row compact-row">
            <button type="submit" className="btn btn-primary" disabled={submitting}>
              {submitting ? t('common.loading') : t('report.submit')}
            </button>
            <button type="button" className="btn btn-secondary" onClick={() => setOpen(false)} disabled={submitting}>
              {t('report.cancel')}
            </button>
          </div>
        </form>
      ) : null}

      {message ? <p className="hint-text">{message}</p> : null}
    </section>
  );
}

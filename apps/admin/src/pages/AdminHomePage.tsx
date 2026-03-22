import { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import {
  fetchAdminAuditLogs,
  fetchAdminOverview,
  fetchAdminPosts,
  fetchAdminReports,
  fetchAdminUsers,
  reviewAdminReport,
  updateAdminPostStatus,
  updateAdminUserStatus
} from '../api/adminModeration';
import type {
  AdminAuditLog,
  AdminDashboardOverview,
  AdminPostSummary,
  AdminReportResolutionAction,
  AdminReportStatus,
  AdminReportSummary,
  AdminUserSummary
} from '../api/adminModeration';
import { logoutAdminSession } from '../api/adminAuth';
import LanguageSwitcher from '../components/LanguageSwitcher';
import AdminDataTable from '../components/AdminDataTable';
import { getAdminDisplayName } from '../utils/adminSession';
import { useAdminAccessToken } from '../hooks/useAdminAccessToken';
import type { AdminPageParams, AdminPage } from '../types/pagination';

export default function AdminHomePage() {
  const { i18n, t } = useTranslation();
  const accessToken = useAdminAccessToken();
  
  // Redirect to login if not authenticated
  if (!accessToken) {
    return <Navigate to="/login" replace />;
  }
  
  const [overview, setOverview] = useState<AdminDashboardOverview | null>(null);
  const [users, setUsers] = useState<AdminPage<AdminUserSummary> | null>(null);
  const [posts, setPosts] = useState<AdminPage<AdminPostSummary> | null>(null);
  const [reports, setReports] = useState<AdminPage<AdminReportSummary> | null>(null);
  const [auditLogs, setAuditLogs] = useState<AdminPage<AdminAuditLog> | null>(null);
  const [reportFilter, setReportFilter] = useState<AdminReportStatus>('PENDING');
  const [userSearch, setUserSearch] = useState<string>('');
  const [postSearch, setPostSearch] = useState<string>('');
  const [reportSearch, setReportSearch] = useState<string>('');
  const [auditLogSearch, setAuditLogSearch] = useState<string>('');
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState<string | null>(null);
  const [busyKey, setBusyKey] = useState<string | null>(null);

  const adminName = getAdminDisplayName();

  const loadDashboard = async () => {
    setLoading(true);
    setMessage(null);

    try {
      const [nextOverview, nextUsers, nextPosts, nextReports, nextAuditLogs] = await Promise.all([
        fetchAdminOverview(),
        fetchAdminUsers({ page: 1, size: 12, ...(userSearch && { search: userSearch }) }),
        fetchAdminPosts({ page: 1, size: 12, ...(postSearch && { search: postSearch }) }),
        fetchAdminReports({ page: 1, size: 12, ...(reportSearch && { search: reportSearch }), status: reportFilter === 'ALL' ? undefined : reportFilter }),
        fetchAdminAuditLogs({ page: 1, size: 12, ...(auditLogSearch && { search: auditLogSearch }) })
      ]);
      
      setOverview(nextOverview);
      setUsers({ ...nextUsers, search: userSearch });
      setPosts({ ...nextPosts, search: postSearch });
      setReports({ ...nextReports, search: reportSearch });
      setAuditLogs({ ...nextAuditLogs, search: auditLogSearch });
    } catch (error) {
      const text = error instanceof Error ? error.message : 'admin.dashboard_load_failed';
      setMessage(t(`messages.${text}`, { defaultValue: t('messages.admin.dashboard_load_failed') }));
    } finally {
      setLoading(false);
      setBusyKey(null);
    }
  };

  useEffect(() => {
    void loadDashboard();
  }, [reportFilter, userSearch, postSearch, reportSearch, auditLogSearch]);

  const formatDateTime = (value: string | null) => {
    if (!value) {
      return '-';
    }

    return new Intl.DateTimeFormat(i18n.language === 'zh-CN' ? 'zh-CN' : 'en-US', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit'
    }).format(new Date(value));
  };

  const categoryLabel = (post: AdminPostSummary) =>
    i18n.language === 'zh-CN' ? post.categoryNameZh : post.categoryNameEn;

  const toggleUserStatus = async (user: AdminUserSummary) => {
    const nextStatus = user.status === 'ACTIVE' ? 'DISABLED' : 'ACTIVE';
    setBusyKey(`user-${user.id}`);
    setMessage(null);

    try {
      await updateAdminUserStatus(user.id, nextStatus);
      await loadDashboard();
      setMessage(t('messages.admin.user_status_updated'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: t('messages.common.request_failed') }));
      setBusyKey(null);
    }
  };

  const togglePostStatus = async (post: AdminPostSummary) => {
    const nextStatus = post.status === 'PUBLISHED' ? 'DELETED' : 'PUBLISHED';
    setBusyKey(`post-${post.id}`);
    setMessage(null);

    try {
      await updateAdminPostStatus(post.id, nextStatus);
      await loadDashboard();
      setMessage(t('messages.admin.post_status_updated'));
    } catch (error) {
      const text = error instanceof Error ? error.message : 'common.request_failed';
      setMessage(t(`messages.${text}`, { defaultValue: t('messages.common.request_failed') }));
      setBusyKey(null);
    }
  };

  const signOut = () => {
    logoutAdminSession();
  };

  if (loading) {
    return (
      <main className="page-shell dashboard-shell">
        <header className="top-row">
          <h1>{t('app.title')}</h1>
          <LanguageSwitcher />
        </header>
        <p>{t('admin.loading_dashboard')}</p>
      </main>
    );
  }

  return (
    <main className="page-shell dashboard-shell">
      <header className="top-row">
        <div>
          <h1>{t('app.title')}</h1>
          <p className="hint-text">
            {adminName ? `${t('admin.current_admin')}: ${adminName}` : t('admin.no_session')}
          </p>
        </div>
        <div className="toolbar-actions">
          <LanguageSwitcher />
          <button type="button" className="btn btn-secondary" onClick={() => void loadDashboard()}>
            {t('admin.refresh_data')}
          </button>
          <button type="button" className="btn btn-secondary" onClick={signOut}>
            {t('admin.logout')}
          </button>
        </div>
      </header>

      <p>{t('admin.subtitle')}</p>
      {message ? <p className="hint-text">{message}</p> : null}

      <section className="stats-grid">
        <article className="stat-card">
          <span className="stat-label">{t('admin.total_users')}</span>
          <strong>{overview?.totalUsers ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.active_users')}</span>
          <strong>{overview?.activeUsers ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.disabled_users')}</span>
          <strong>{overview?.disabledUsers ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.published_posts')}</span>
          <strong>{overview?.publishedPosts ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.hidden_posts')}</span>
          <strong>{overview?.hiddenPosts ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.pending_reports')}</span>
          <strong>{overview?.pendingReports ?? 0}</strong>
        </article>
        <article className="stat-card">
          <span className="stat-label">{t('admin.resolved_reports')}</span>
          <strong>{overview?.resolvedReports ?? 0}</strong>
        </article>
      </section>

      <section className="dashboard-section">
        <div className="section-header">
          <h2>{t('admin.user_management')}</h2>
          <span className="hint-text">{t('admin.latest_users_hint')}</span>
        </div>
        
        {users && (
          <AdminDataTable
            data={users}
            columns={[
              { key: 'displayName', label: t('admin.username'), render: (user) => (
                <div>
                  <strong>{user.displayName}</strong>
                  <div className="table-meta">@{user.username}</div>
                </div>
              )},
              { key: 'email', label: t('admin.email') },
              { key: 'preferredLanguage', label: t('admin.preferred_language'), render: (user) => t(`admin.lang_${user.preferredLanguage}`) },
              { key: 'lastLoginAt', label: t('admin.last_login_at'), render: (user) => formatDateTime(user.lastLoginAt) },
              { key: 'status', label: t('admin.status'), render: (user) => (
                <span className={`status-chip status-${user.status.toLowerCase()}`}>
                  {t(`admin.status_${user.status.toLowerCase()}`)}
                </span>
              )},
            ]}
            actions={(user) => (
              <button
                type="button"
                className="btn btn-secondary btn-small"
                disabled={busyKey === `user-${user.id}`}
                onClick={() => void toggleUserStatus(user)}
              >
                {busyKey === `user-${user.id}`
                  ? t('common.loading')
                  : user.status === 'ACTIVE'
                    ? t('admin.disable_user')
                    : t('admin.enable_user')}
              </button>
            )}
            loading={loading}
            onPageChange={async (page: number) => {
              const result = await fetchAdminUsers({ page, size: 12, ...(userSearch && { search: userSearch }) });
              setUsers({ ...result, search: userSearch });
            }}
            onPageSizeChange={async (size: number) => {
              const result = await fetchAdminUsers({ page: 1, size, ...(userSearch && { search: userSearch }) });
              setUsers({ ...result, search: userSearch });
            }}
            onSearchChange={(search: string) => setUserSearch(search)}
          />
        )}
      </section>

      <section className="dashboard-section">
        <div className="section-header">
          <h2>{t('admin.post_management')}</h2>
          <span className="hint-text">{t('admin.latest_posts_hint')}</span>
        </div>
        
        {posts && (
          <AdminDataTable
            data={posts}
            columns={[
              { key: 'title', label: t('admin.post'), render: (post) => (
                <div>
                  <strong>{post.title}</strong>
                  <div className="table-meta">#{post.id}</div>
                </div>
              )},
              { key: 'authorDisplayName', label: t('admin.author'), render: (post) => (
                <div>
                  {post.authorDisplayName}
                  <div className="table-meta">@{post.authorUsername}</div>
                </div>
              )},
              { key: 'categoryCode', label: t('admin.category'), render: (post) => categoryLabel(post) },
              { key: 'updatedAt', label: t('admin.updated_at'), render: (post) => formatDateTime(post.updatedAt) },
              { key: 'status', label: t('admin.status'), render: (post) => (
                <span className={`status-chip status-${post.status.toLowerCase()}`}>
                  {t(`admin.status_${post.status.toLowerCase()}`)}
                </span>
              )},
            ]}
            actions={(post) => (
              <button
                type="button"
                className="btn btn-secondary btn-small"
                disabled={busyKey === `post-${post.id}`}
                onClick={() => void togglePostStatus(post)}
              >
                {busyKey === `post-${post.id}`
                  ? t('common.loading')
                  : post.status === 'PUBLISHED'
                    ? t('admin.hide_post')
                    : t('admin.restore_post')}
              </button>
            )}
            loading={loading}
            onPageChange={async (page: number) => {
              const result = await fetchAdminPosts({ page, size: 12, ...(postSearch && { search: postSearch }) });
              setPosts({ ...result, search: postSearch });
            }}
            onPageSizeChange={async (size: number) => {
              const result = await fetchAdminPosts({ page: 1, size, ...(postSearch && { search: postSearch }) });
              setPosts({ ...result, search: postSearch });
            }}
            onSearchChange={(search: string) => setPostSearch(search)}
          />
        )}
      </section>

      <section className="dashboard-section">
        <div className="section-header">
          <div>
            <h2>{t('admin.report_management')}</h2>
            <span className="hint-text">{t('admin.latest_reports_hint')}</span>
          </div>
          <div className="action-row compact-row">
            {(['PENDING', 'RESOLVED', 'DISMISSED', 'ALL'] as const).map((item) => (
              <button
                key={item}
                type="button"
                className="btn btn-secondary btn-small"
                disabled={reportFilter === item}
                onClick={() => setReportFilter(item as AdminReportStatus)}
              >
                {t(`admin.report_filter_${item.toLowerCase()}`)}
              </button>
            ))}
          </div>
        </div>
        
        {reports && (
          <AdminDataTable
            data={reports}
            columns={[
              { key: 'reporterDisplayName', label: t('admin.reporter'), render: (report) => (
                <div>
                  <strong>{report.reporterDisplayName}</strong>
                  <div className="table-meta">@{report.reporterUsername}</div>
                </div>
              )},
              { key: 'targetLabel', label: t('admin.target'), render: (report) => (
                <div>
                  <strong>{report.targetLabel}</strong>
                  <div className="table-meta">
                    {t(`admin.target_${report.targetType.toLowerCase()}`)} #{report.targetId}
                  </div>
                </div>
              )},
              { key: 'reason', label: t('admin.reason'), render: (report) => (
                <div>
                  <strong>{t(`admin.reason_${report.reason.toLowerCase()}`)}</strong>
                  <div className="table-meta">{report.detail || t('admin.no_report_detail')}</div>
                </div>
              )},
              { key: 'status', label: t('admin.status'), render: (report) => (
                <span className={`status-chip status-${report.status.toLowerCase()}`}>
                  {t(`admin.status_${report.status.toLowerCase()}`)}
                </span>
              )},
              { key: 'createdAt', label: t('admin.submitted_at'), render: (report) => formatDateTime(report.createdAt) },
            ]}
            loading={loading}
            onPageChange={async (page: number) => {
              const result = await fetchAdminReports({ page, size: 12, ...(reportSearch && { search: reportSearch }), status: reportFilter === 'ALL' ? undefined : reportFilter });
              setReports({ ...result, search: reportSearch });
            }}
            onPageSizeChange={async (size: number) => {
              const result = await fetchAdminReports({ page: 1, size, ...(reportSearch && { search: reportSearch }), status: reportFilter === 'ALL' ? undefined : reportFilter });
              setReports({ ...result, search: reportSearch });
            }}
            onSearchChange={(search: string) => setReportSearch(search)}
          />
        )}
      </section>

      <section className="dashboard-section">
        <div className="section-header">
          <h2>{t('admin.audit_trail')}</h2>
          <span className="hint-text">{t('admin.latest_audit_logs_hint')}</span>
        </div>
        
        {auditLogs && (
          <AdminDataTable
            data={auditLogs}
            columns={[
              { key: 'adminDisplayName', label: t('admin.admin_actor'), render: (log) => (
                <div>
                  <strong>{log.adminDisplayName}</strong>
                  <div className="table-meta">@{log.adminUsername}</div>
                </div>
              )},
              { key: 'actionType', label: t('admin.action'), render: (log) => (
                <div>
                  <strong>{t(`admin.audit_action_${log.actionType.toLowerCase()}`)}</strong>
                  {log.contextLabel ? <div className="table-meta">{log.contextLabel}</div> : null}
                </div>
              )},
              { key: 'targetLabel', label: t('admin.target'), render: (log) => (
                <div>
                  <strong>{log.targetLabel}</strong>
                  <div className="table-meta">
                    {t(`admin.target_${log.targetType.toLowerCase()}`)} #{log.targetId}
                  </div>
                </div>
              )},
              { key: 'createdAt', label: t('admin.created_at'), render: (log) => formatDateTime(log.createdAt) },
            ]}
            loading={loading}
            onPageChange={async (page: number) => {
              const result = await fetchAdminAuditLogs({ page, size: 12, ...(auditLogSearch && { search: auditLogSearch }) });
              setAuditLogs({ ...result, search: auditLogSearch });
            }}
            onPageSizeChange={async (size: number) => {
              const result = await fetchAdminAuditLogs({ page: 1, size, ...(auditLogSearch && { search: auditLogSearch }) });
              setAuditLogs({ ...result, search: auditLogSearch });
            }}
            onSearchChange={(search: string) => setAuditLogSearch(search)}
          />
        )}
      </section>
    </main>
  );
}

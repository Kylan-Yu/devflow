import { Navigate, createBrowserRouter } from 'react-router-dom';
import RequireAdminSession from '../components/RequireAdminSession';
import AdminHomePage from '../pages/AdminHomePage';
import AdminLoginPage from '../pages/AdminLoginPage';

export const adminRouter = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/dashboard" replace />
  },
  {
    path: '/login',
    element: <AdminLoginPage />
  },
  {
    path: '/dashboard',
    element: (
      <RequireAdminSession>
        <AdminHomePage />
      </RequireAdminSession>
    )
  }
]);

import { Navigate, createBrowserRouter } from 'react-router-dom';
import AdminHomePage from '../pages/AdminHomePage';
import AdminLoginPage from '../pages/AdminLoginPage';

export const adminRouter = createBrowserRouter([
  {
    path: '/',
    element: <Navigate to="/login" replace />
  },
  {
    path: '/login',
    element: <AdminLoginPage />
  },
  {
    path: '/dashboard',
    element: <AdminHomePage />
  }
]);

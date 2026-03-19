import { createBrowserRouter } from 'react-router-dom';
import HomePage from '../pages/HomePage';
import LoginPage from '../pages/LoginPage';
import RegisterPage from '../pages/RegisterPage';
import LatestFeedPage from '../pages/LatestFeedPage';
import HotFeedPage from '../pages/HotFeedPage';
import PostDetailPage from '../pages/PostDetailPage';
import PostEditorPage from '../pages/PostEditorPage';
import UserProfilePage from '../pages/UserProfilePage';
import NotificationsPage from '../pages/NotificationsPage';

export const webRouter = createBrowserRouter([
  {
    path: '/',
    element: <HomePage />
  },
  {
    path: '/login',
    element: <LoginPage />
  },
  {
    path: '/register',
    element: <RegisterPage />
  },
  {
    path: '/feed/latest',
    element: <LatestFeedPage />
  },
  {
    path: '/feed/hot',
    element: <HotFeedPage />
  },
  {
    path: '/posts/new',
    element: <PostEditorPage />
  },
  {
    path: '/posts/:id',
    element: <PostDetailPage />
  },
  {
    path: '/posts/:id/edit',
    element: <PostEditorPage />
  },
  {
    path: '/users/:id',
    element: <UserProfilePage />
  },
  {
    path: '/notifications',
    element: <NotificationsPage />
  }
]);

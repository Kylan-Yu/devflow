import { RouterProvider } from 'react-router-dom';
import { adminRouter } from '../router';

export default function App() {
  return <RouterProvider router={adminRouter} />;
}

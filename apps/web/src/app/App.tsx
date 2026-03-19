import { RouterProvider } from 'react-router-dom';
import { webRouter } from '../router';

export default function App() {
  return <RouterProvider router={webRouter} />;
}

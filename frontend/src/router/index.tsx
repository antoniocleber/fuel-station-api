import { createBrowserRouter } from 'react-router-dom';
import AppLayout from '../components/layout/AppLayout';
import DashboardPage from '../pages/Dashboard/DashboardPage';
import FuelTypesPage from '../pages/FuelTypes/FuelTypesPage';
import FuelPumpsPage from '../pages/FuelPumps/FuelPumpsPage';
import FuelingsPage from '../pages/Fuelings/FuelingsPage';

const router = createBrowserRouter([
  {
    path: '/',
    element: <AppLayout />,
    children: [
      { index: true, element: <DashboardPage /> },
      { path: 'fuel-types', element: <FuelTypesPage /> },
      { path: 'fuel-pumps', element: <FuelPumpsPage /> },
      { path: 'fuelings', element: <FuelingsPage /> },
    ],
  },
]);

export default router;

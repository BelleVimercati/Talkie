import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/AuthPage/LoginPage';
import RegisterPage from '@/pages/AuthPage/RegisterPage';
import HomePage from '@/pages/HomePage';
import CreateOccurrencePage from '@/pages/OccurrencesPage/CreateOccurrencePage';
import NotificationsPage from '@/pages/NotificationsPage';
import CategoriesListPage from '@/pages/SettingsPage/CategoriesListPage';
import CreateCategoryPage from '@/pages/SettingsPage/CreateCategoryPage';
import CreateSubcategoryPage from '@/pages/SettingsPage/CreateSubcategoryPage';
import UsersListPage from '@/pages/SettingsPage/UsersListPage';
import NotFoundPage from '@/pages/NotFoundPage';
import ProtectedRoute from '@/components/common/ProtectedRoute';
function App() {
    return (_jsx(BrowserRouter, { children: _jsxs(Routes, { children: [_jsx(Route, { path: "/login", element: _jsx(LoginPage, {}) }), _jsx(Route, { path: "/register", element: _jsx(RegisterPage, {}) }), _jsx(Route, { path: "/", element: _jsx(ProtectedRoute, { children: _jsx(HomePage, {}) }) }), _jsx(Route, { path: "/ocorrencias/nova", element: _jsx(ProtectedRoute, { children: _jsx(CreateOccurrencePage, {}) }) }), _jsx(Route, { path: "/notificacoes", element: _jsx(ProtectedRoute, { children: _jsx(NotificationsPage, {}) }) }), _jsx(Route, { path: "/configuracoes", element: _jsx(ProtectedRoute, { requireAdmin: true, children: _jsx(CategoriesListPage, {}) }) }), _jsx(Route, { path: "/configuracoes/categorias/nova", element: _jsx(ProtectedRoute, { requireAdmin: true, children: _jsx(CreateCategoryPage, {}) }) }), _jsx(Route, { path: "/configuracoes/subcategorias/nova", element: _jsx(ProtectedRoute, { requireAdmin: true, children: _jsx(CreateSubcategoryPage, {}) }) }), _jsx(Route, { path: "/usuarios", element: _jsx(ProtectedRoute, { requireAdmin: true, children: _jsx(UsersListPage, {}) }) }), _jsx(Route, { path: "*", element: _jsx(NotFoundPage, {}) })] }) }));
}
export default App;
//# sourceMappingURL=App.js.map
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import LoginPage from '@/pages/AuthPage/LoginPage';
import RegisterPage from '@/pages/AuthPage/RegisterPage';
import HomePage from '@/pages/HomePage';
import NotFoundPage from '@/pages/NotFoundPage';
import ProtectedRoute from '@/components/common/ProtectedRoute';
function App() {
    return (_jsx(BrowserRouter, { children: _jsxs(Routes, { children: [_jsx(Route, { path: "/login", element: _jsx(LoginPage, {}) }), _jsx(Route, { path: "/register", element: _jsx(RegisterPage, {}) }), _jsx(Route, { path: "/", element: _jsx(ProtectedRoute, { children: _jsx(HomePage, {}) }) }), _jsx(Route, { path: "*", element: _jsx(NotFoundPage, {}) })] }) }));
}
export default App;
//# sourceMappingURL=App.js.map
import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { useLocation, useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';
import { getInitials } from '@/utils/initials';
import { Grid, Bell, LogOut, Settings, Users } from 'lucide-react';
export function Sidebar() {
    const { user, logout } = useAuthStore();
    const navigate = useNavigate();
    const location = useLocation();
    const handleLogout = () => {
        logout();
        navigate('/login');
    };
    const isGeral = location.pathname === '/';
    const isNotificacoes = location.pathname.startsWith('/notificacoes');
    const isConfiguracoes = location.pathname.startsWith('/configuracoes');
    const isUsers = location.pathname.startsWith('/usuarios');
    return (_jsxs("div", { className: "h-screen w-64 bg-white shadow-md flex flex-col", children: [_jsx("div", { className: "px-6 py-6 border-b", children: _jsx("div", { className: "flex items-center gap-2", children: _jsx("div", { className: "text-xl font-bold font-logo text-brand-logo", children: "Talkie" }) }) }), _jsxs("div", { className: "px-6 py-6 border-b flex items-center gap-4", children: [_jsx("div", { className: "h-12 w-12 rounded-full bg-brand-blue text-white flex items-center justify-center font-bold text-sm", children: user ? getInitials(user.email) : '?' }), _jsxs("div", { className: "flex-1 min-w-0", children: [_jsx("p", { className: "font-medium text-sm text-black-900", children: user ? user.email.split('@')[0] : 'Usuário' }), _jsx("p", { className: "text-xs text-brand-muted truncate", children: user?.email })] })] }), _jsxs("nav", { className: "flex-1 px-4 py-6 space-y-2", children: [_jsxs("button", { onClick: () => navigate('/'), className: `w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition-colors ${isGeral
                            ? 'bg-brand-orange bg-opacity-10 text-brand-orange'
                            : 'text-black-700 hover:bg-black-50'}`, children: [_jsx(Grid, { size: 18 }), _jsx("span", { className: "font-medium text-sm", children: "Geral" })] }), _jsxs("button", { onClick: () => navigate('/notificacoes'), className: `w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition-colors ${isNotificacoes
                            ? 'bg-brand-orange bg-opacity-10 text-brand-orange'
                            : 'text-black-700 hover:bg-black-50'}`, children: [_jsx(Bell, { size: 18 }), _jsx("span", { className: "font-medium text-sm", children: "Notifica\u00E7\u00F5es" })] }), user?.role === 'ADMIN' && (_jsxs(_Fragment, { children: [_jsxs("button", { onClick: () => navigate('/configuracoes'), className: `w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition-colors ${isConfiguracoes
                                    ? 'bg-brand-orange bg-opacity-10 text-brand-orange'
                                    : 'text-black-700 hover:bg-black-50'}`, children: [_jsx(Settings, { size: 18 }), _jsx("span", { className: "font-medium text-sm", children: "Configura\u00E7\u00F5es" })] }), _jsxs("button", { onClick: () => navigate('/usuarios'), className: `w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition-colors ${isUsers
                                    ? 'bg-brand-orange bg-opacity-10 text-brand-orange'
                                    : 'text-black-700 hover:bg-black-50'}`, children: [_jsx(Users, { size: 18 }), _jsx("span", { className: "font-medium text-sm", children: "Usu\u00E1rios" })] })] }))] }), _jsx("div", { className: "px-4 py-6 border-t", children: _jsxs("button", { onClick: handleLogout, className: "w-full px-4 py-3 rounded-lg flex items-center gap-3 text-black-700 hover:bg-black-50 transition-colors", children: [_jsx(LogOut, { size: 18 }), _jsx("span", { className: "font-medium text-sm", children: "Sair" })] }) })] }));
}
//# sourceMappingURL=Sidebar.js.map
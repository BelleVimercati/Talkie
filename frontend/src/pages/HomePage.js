import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '@/stores/authStore';
import Button from '@/components/ui/Button';
function HomePage() {
    const navigate = useNavigate();
    const { user, logout } = useAuthStore();
    const handleLogout = () => {
        logout();
        navigate('/login');
    };
    return (_jsx("div", { className: "min-h-screen bg-gradient-to-br from-brand-blue to-brand-navy p-8", children: _jsxs("div", { className: "mx-auto max-w-md rounded-lg bg-white p-8 shadow-lg", children: [_jsx("h1", { className: "mb-4 text-2xl font-bold text-black-900", children: "Bem-vindo ao Talkie!" }), _jsx("p", { className: "mb-2 text-black-800", children: "Voc\u00EA est\u00E1 logado como:" }), _jsx("p", { className: "mb-6 break-all font-mono text-sm font-semibold text-brand-blue", children: user?.email }), _jsxs("p", { className: "mb-6 text-sm text-black-500", children: ["Seu perfil: ", _jsx("span", { className: "font-semibold text-black-800", children: user?.role })] }), _jsx("p", { className: "mb-6 text-xs text-black-500", children: "Esta p\u00E1gina est\u00E1 em constru\u00E7\u00E3o. O dashboard real ser\u00E1 implementado em breve." }), _jsx("div", { className: "flex gap-3", children: _jsx(Button, { onClick: handleLogout, variant: "primary", children: "Logout" }) })] }) }));
}
export default HomePage;
//# sourceMappingURL=HomePage.js.map
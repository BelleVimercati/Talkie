import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import { RoleBadge } from '@/components/ui/RoleBadge';
import { useUsers } from '@/hooks/useUsers';
import { Search } from 'lucide-react';
function UsersListPage() {
    const navigate = useNavigate();
    const [searchTerm, setSearchTerm] = useState('');
    const { users, isLoading } = useUsers();
    const filtered = useMemo(() => users.filter((user) => user.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        user.email.toLowerCase().includes(searchTerm.toLowerCase())), [users, searchTerm]);
    const formatDate = (dateString) => {
        if (!dateString)
            return '-';
        try {
            return new Date(dateString).toLocaleDateString('pt-BR');
        }
        catch {
            return '-';
        }
    };
    return (_jsx(AppLayout, { children: _jsxs("div", { className: "p-8", children: [_jsxs("div", { className: "mb-8", children: [_jsx("h1", { className: "mb-2 text-3xl font-bold font-roboto text-black-900", children: "Usu\u00E1rios" }), _jsxs("p", { className: "text-sm text-brand-blue", children: [_jsx("button", { onClick: () => navigate('/'), className: "hover:underline", children: "Geral" }), ` > Usuários`] })] }), _jsx("div", { className: "mb-6 flex gap-4 items-center", children: _jsxs("div", { className: "flex-1 relative", children: [_jsx("div", { className: "absolute left-4 top-1/2 -translate-y-1/2", children: _jsx(Search, { size: 18, className: "text-brand-muted" }) }), _jsx("input", { type: "text", placeholder: "Pesquisar", value: searchTerm, onChange: (e) => setSearchTerm(e.target.value), className: "w-full pl-10 pr-4 py-3 border border-black-100 rounded-2xl text-sm placeholder-brand-muted focus:outline-none focus:ring-2 focus:ring-brand-blue focus:ring-opacity-50" })] }) }), _jsx("div", { className: "bg-white rounded-lg overflow-hidden shadow-sm", children: isLoading ? (_jsxs("div", { className: "p-8 text-center", children: [_jsx("div", { className: "inline-block h-8 w-8 animate-spin rounded-full border-4 border-brand-blue border-t-transparent" }), _jsx("p", { className: "mt-2 text-black-500", children: "Carregando..." })] })) : filtered.length === 0 ? (_jsx("div", { className: "p-8 text-center", children: _jsx("p", { className: "text-black-500", children: "Nenhum usu\u00E1rio encontrado" }) })) : (_jsx("div", { className: "overflow-x-auto", children: _jsxs("table", { className: "w-full text-sm", children: [_jsx("thead", { children: _jsxs("tr", { className: "border-b border-black-100 bg-black-50", children: [_jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Nome" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Email" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "CPF" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Data de cria\u00E7\u00E3o" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Permiss\u00E3o" })] }) }), _jsx("tbody", { children: filtered.map((user, idx) => (_jsxs("tr", { className: "border-b border-black-100 hover:bg-black-50", children: [_jsx("td", { className: "px-6 py-4 text-black-900 font-medium", children: user.name }), _jsx("td", { className: "px-6 py-4 text-black-500", children: user.email }), _jsx("td", { className: "px-6 py-4 text-black-500", children: user.cpf }), _jsx("td", { className: "px-6 py-4 text-black-500", children: formatDate(user.createdAt) }), _jsx("td", { className: "px-6 py-4", children: _jsx(RoleBadge, { role: user.role }) })] }, idx))) })] }) })) })] }) }));
}
export default UsersListPage;
//# sourceMappingURL=UsersListPage.js.map
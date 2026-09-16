import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { AppLayout } from '@/components/layout/AppLayout';
import Button from '@/components/ui/Button';
import { Badge } from '@/components/ui/Badge';
import { SubscriptionCard } from '@/components/notifications/SubscriptionCard';
import { SubscribeModal } from '@/components/notifications/SubscribeModal';
import { useSubscriptions } from '@/hooks/useSubscriptions';
import { useNotifications } from '@/hooks/useNotifications';
import { Search } from 'lucide-react';
function NotificationsPage() {
    const navigate = useNavigate();
    const { subscriptions, refetch: refetchSubscriptions } = useSubscriptions();
    const { occurrences, isLoading: notificationsLoading } = useNotifications();
    const [searchTerm, setSearchTerm] = useState('');
    const [isModalOpen, setIsModalOpen] = useState(false);
    const filtered = useMemo(() => occurrences.filter((occ) => occ.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
        occ.categoryName.toLowerCase().includes(searchTerm.toLowerCase())), [occurrences, searchTerm]);
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
    const handleSubscribeSuccess = () => {
        refetchSubscriptions();
    };
    return (_jsxs(AppLayout, { children: [_jsxs("div", { className: "p-8", children: [_jsxs("div", { className: "mb-8", children: [_jsx("h1", { className: "mb-2 text-3xl font-bold font-roboto text-black-900", children: "Notifica\u00E7\u00F5es" }), _jsxs("p", { className: "text-sm text-brand-blue", children: [_jsx("button", { onClick: () => navigate('/'), className: "hover:underline", children: "Geral" }), ` > Notificações`] })] }), _jsxs("div", { className: "mb-12", children: [_jsxs("div", { className: "flex items-center justify-between mb-6", children: [_jsx("h2", { className: "text-xl font-semibold font-roboto text-black-900", children: "Inscri\u00E7\u00F5es" }), _jsx(Button, { onClick: () => setIsModalOpen(true), className: "text-sm", children: "+ Nova Inscri\u00E7\u00E3o" })] }), subscriptions.length === 0 ? (_jsx("div", { className: "p-8 text-center bg-white rounded-lg border border-black-100", children: _jsx("p", { className: "text-black-500", children: "Voc\u00EA ainda n\u00E3o est\u00E1 inscrito em nenhuma categoria" }) })) : (_jsx("div", { className: "grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4", children: subscriptions.map((sub) => (_jsx(SubscriptionCard, { icon: "\uD83D\uDCCC", categoryName: sub.categoryName }, sub.categoryId))) }))] }), _jsx("div", { className: "border-t border-black-100 mb-12" }), _jsxs("div", { children: [_jsx("h2", { className: "text-xl font-semibold font-roboto text-black-900 mb-6", children: "Todas as Notifica\u00E7\u00F5es" }), _jsx("div", { className: "mb-6 flex gap-4 items-center", children: _jsxs("div", { className: "flex-1 relative", children: [_jsx("div", { className: "absolute left-4 top-1/2 -translate-y-1/2", children: _jsx(Search, { size: 18, className: "text-brand-muted" }) }), _jsx("input", { type: "text", placeholder: "Pesquisar", value: searchTerm, onChange: (e) => setSearchTerm(e.target.value), className: "w-full pl-10 pr-4 py-3 border border-black-100 rounded-2xl text-sm placeholder-brand-muted focus:outline-none focus:ring-2 focus:ring-brand-blue focus:ring-opacity-50" })] }) }), _jsx("div", { className: "bg-white rounded-lg overflow-hidden shadow-sm", children: notificationsLoading ? (_jsxs("div", { className: "p-8 text-center", children: [_jsx("div", { className: "inline-block h-8 w-8 animate-spin rounded-full border-4 border-brand-blue border-t-transparent" }), _jsx("p", { className: "mt-2 text-black-500", children: "Carregando..." })] })) : filtered.length === 0 ? (_jsx("div", { className: "p-8 text-center", children: _jsx("p", { className: "text-black-500", children: "Nenhuma notifica\u00E7\u00E3o encontrada" }) })) : (_jsx("div", { className: "overflow-x-auto", children: _jsxs("table", { className: "w-full text-sm", children: [_jsx("thead", { children: _jsxs("tr", { className: "border-b border-black-100 bg-black-50", children: [_jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "T\u00EDtulo da Ocorr\u00EAncia" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Categoria" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Subcategoria" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Autor" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Data de cria\u00E7\u00E3o" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Status" }), _jsx("th", { className: "px-6 py-4 text-left font-medium text-black-700", children: "Data de resolu\u00E7\u00E3o" })] }) }), _jsx("tbody", { children: filtered.map((occ, idx) => (_jsxs("tr", { className: "border-b border-black-100 hover:bg-black-50", children: [_jsx("td", { className: "px-6 py-4 text-black-900 font-medium", children: occ.title }), _jsx("td", { className: "px-6 py-4 text-black-500", children: occ.categoryName }), _jsx("td", { className: "px-6 py-4 text-black-500", children: occ.subcategoryName }), _jsx("td", { className: "px-6 py-4 text-black-500", children: occ.ownerName }), _jsx("td", { className: "px-6 py-4 text-black-500", children: formatDate(occ.createdAt) }), _jsx("td", { className: "px-6 py-4", children: _jsx(Badge, { status: occ.status }) }), _jsx("td", { className: "px-6 py-4 text-black-500", children: formatDate(occ.resolvedAt) })] }, idx))) })] }) })) })] })] }), _jsx(SubscribeModal, { isOpen: isModalOpen, onClose: () => setIsModalOpen(false), onSubscribe: handleSubscribeSuccess })] }));
}
export default NotificationsPage;
//# sourceMappingURL=NotificationsPage.js.map
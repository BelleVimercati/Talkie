import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Card } from '@/components/ui/Card';
import { Folder, Clock, CheckCircle2, XCircle } from 'lucide-react';
export function StatsCards({ occurrences }) {
    const total = occurrences.length;
    const pendentes = occurrences.filter((o) => o.status === 'ABERTO' || o.status === 'EM_ANALISE').length;
    const resolvidas = occurrences.filter((o) => o.status === 'RESOLVIDO').length;
    const fechadas = occurrences.filter((o) => o.status === 'FECHADO').length;
    const stats = [
        {
            label: 'Total de Ocorrências',
            value: total,
            icon: Folder,
            bgColor: 'bg-blue-50',
            iconColor: 'text-brand-blue',
        },
        {
            label: 'Pendentes',
            value: pendentes,
            icon: Clock,
            bgColor: 'bg-yellow-50',
            iconColor: 'text-yellow-600',
        },
        {
            label: 'Resolvidas',
            value: resolvidas,
            icon: CheckCircle2,
            bgColor: 'bg-green-50',
            iconColor: 'text-green-600',
        },
        {
            label: 'Fechadas',
            value: fechadas,
            icon: XCircle,
            bgColor: 'bg-red-50',
            iconColor: 'text-red-600',
        },
    ];
    return (_jsx("div", { className: "grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4", children: stats.map((stat) => {
            const Icon = stat.icon;
            return (_jsx(Card, { children: _jsxs("div", { className: "flex items-start justify-between", children: [_jsxs("div", { children: [_jsx("p", { className: "text-sm text-black-500 font-medium", children: stat.label }), _jsx("p", { className: "mt-2 text-3xl font-bold text-black-900", children: stat.value })] }), _jsx("div", { className: `p-3 rounded-full ${stat.bgColor}`, children: _jsx(Icon, { className: `h-6 w-6 ${stat.iconColor}` }) })] }) }, stat.label));
        }) }));
}
//# sourceMappingURL=StatsCards.js.map
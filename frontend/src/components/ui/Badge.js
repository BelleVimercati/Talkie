import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
const STATUS_CONFIG = {
    ABERTO: {
        bg: 'bg-status-openBg',
        text: 'text-status-openText',
        label: 'Aberto',
    },
    EM_ANALISE: {
        bg: 'bg-status-analysisBg',
        text: 'text-status-analysisText',
        label: 'Em Análise',
    },
    RESOLVIDO: {
        bg: 'bg-status-resolvedBg',
        text: 'text-status-resolvedText',
        label: 'Resolvido',
    },
    FECHADO: {
        bg: 'bg-status-closedBg',
        text: 'text-status-closedText',
        label: 'Fechado',
    },
};
export function Badge({ status }) {
    const config = STATUS_CONFIG[status];
    return (_jsxs("div", { className: `inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${config.bg} ${config.text}`, children: [_jsx("div", { className: "h-2 w-2 rounded-full", style: { backgroundColor: 'currentColor' } }), _jsx("span", { className: "text-xs font-medium", children: config.label })] }));
}
export default Badge;
//# sourceMappingURL=Badge.js.map
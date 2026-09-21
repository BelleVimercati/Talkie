import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
const PRIORITY_CONFIG = {
    BAIXA: {
        bg: 'bg-status-resolvedBg',
        text: 'text-status-resolvedText',
        label: 'Baixa',
    },
    MEDIA: {
        bg: 'bg-status-analysisBg',
        text: 'text-status-analysisText',
        label: 'Média',
    },
    ALTA: {
        bg: 'bg-status-closedBg',
        text: 'text-status-closedText',
        label: 'Alta',
    },
};
const FALLBACK_CONFIG = {
    bg: 'bg-black-50',
    text: 'text-black-500',
    label: '—',
};
export function PriorityBadge({ priority }) {
    const config = priority ? PRIORITY_CONFIG[priority] : null;
    const finalConfig = config || FALLBACK_CONFIG;
    return (_jsxs("div", { className: `inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${finalConfig.bg} ${finalConfig.text}`, children: [_jsx("div", { className: "h-2 w-2 rounded-full", style: { backgroundColor: 'currentColor' } }), _jsx("span", { className: "text-xs font-medium", children: finalConfig.label })] }));
}
export default PriorityBadge;
//# sourceMappingURL=PriorityBadge.js.map
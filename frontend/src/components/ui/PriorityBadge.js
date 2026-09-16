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
export function PriorityBadge({ priority }) {
    const config = PRIORITY_CONFIG[priority];
    return (_jsxs("div", { className: `inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${config.bg} ${config.text}`, children: [_jsx("div", { className: "h-2 w-2 rounded-full", style: { backgroundColor: 'currentColor' } }), _jsx("span", { className: "text-xs font-medium", children: config.label })] }));
}
export default PriorityBadge;
//# sourceMappingURL=PriorityBadge.js.map
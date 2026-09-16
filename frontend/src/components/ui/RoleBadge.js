import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
const ROLE_CONFIG = {
    USER: {
        bg: 'bg-status-resolvedBg',
        text: 'text-status-resolvedText',
        label: 'Usuário',
    },
    ADMIN: {
        bg: 'bg-status-closedBg',
        text: 'text-status-closedText',
        label: 'Administrador',
    },
};
const FALLBACK_CONFIG = {
    bg: 'bg-black-50',
    text: 'text-black-500',
    label: '—',
};
export function RoleBadge({ role }) {
    const config = role ? ROLE_CONFIG[role] : null;
    const finalConfig = config || FALLBACK_CONFIG;
    return (_jsxs("div", { className: `inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${finalConfig.bg} ${finalConfig.text}`, children: [_jsx("div", { className: "h-2 w-2 rounded-full", style: { backgroundColor: 'currentColor' } }), _jsx("span", { className: "text-xs font-medium", children: finalConfig.label })] }));
}
export default RoleBadge;
//# sourceMappingURL=RoleBadge.js.map
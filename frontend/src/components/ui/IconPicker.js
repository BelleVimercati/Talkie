import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
const CATEGORY_ICONS = ['⛈', '🚓', '🦔', '👷🏽‍♀️', '🚨', '🚍', '🖥', '🔒', '🚗', '🏠', '💼', '🌳', '🏥', '🎓'];
export function IconPicker({ value, onChange, error }) {
    return (_jsxs("div", { className: "space-y-2", children: [_jsx("label", { className: "block font-roboto text-sm font-medium text-black-800 tracking-wide03", children: "\u00CDcone*" }), _jsx("div", { className: "grid grid-cols-7 gap-3", children: CATEGORY_ICONS.map((icon) => (_jsx("button", { type: "button", onClick: () => onChange(icon), className: `h-14 w-14 rounded-lg flex items-center justify-center text-2xl transition-all ${value === icon
                        ? 'ring-2 ring-brand-blue bg-brand-blue bg-opacity-5'
                        : 'border border-black-100 hover:border-black-200'}`, children: icon }, icon))) }), error && _jsx("p", { className: "text-xs text-red-600", children: error })] }));
}
export default IconPicker;
//# sourceMappingURL=IconPicker.js.map
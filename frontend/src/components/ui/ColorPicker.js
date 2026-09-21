import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { Check } from 'lucide-react';
const CATEGORY_COLORS = [
    '#FF6B6B',
    '#4ECDC4',
    '#45B7D1',
    '#FFA07A',
    '#98D8C8',
    '#F7DC6F',
    '#BB8FCE',
    '#85C1E2',
    '#F8B739',
    '#52C4A1',
    '#E8A87C',
    '#D4A5E8',
    '#6C5CE7',
    '#00B894',
    '#FDCB6E',
    '#E17055',
];
export function ColorPicker({ value, onChange, error }) {
    return (_jsxs("div", { className: "space-y-2", children: [_jsx("label", { className: "block font-roboto text-sm font-medium text-black-800 tracking-wide03", children: "Cor*" }), _jsx("div", { className: "grid grid-cols-8 gap-3", children: CATEGORY_COLORS.map((color) => (_jsx("button", { type: "button", onClick: () => onChange(color), className: "h-12 w-12 rounded-lg flex items-center justify-center transition-all relative", style: {
                        backgroundColor: color,
                        border: value === color ? '3px solid white' : 'none',
                        boxShadow: value === color ? `0 0 0 2px #4667AC` : 'none',
                    }, children: value === color && _jsx(Check, { size: 20, className: "text-white" }) }, color))) }), error && _jsx("p", { className: "text-xs text-red-600", children: error })] }));
}
export default ColorPicker;
//# sourceMappingURL=ColorPicker.js.map
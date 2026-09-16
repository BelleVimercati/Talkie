import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { forwardRef } from 'react';
import { ChevronDown } from 'lucide-react';
const Select = forwardRef(({ label, error, options, className = '', ...props }, ref) => {
    return (_jsxs("div", { className: "w-full", children: [label && (_jsx("label", { className: "mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03", children: label })), _jsxs("div", { className: "relative", children: [_jsxs("select", { ref: ref, className: `h-12 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 pr-10 font-roboto text-[15px] text-black-900 placeholder-black-500 transition-colors focus:border-brand-blue focus:outline-none focus:ring-1 focus:ring-brand-blue appearance-none ${error ? 'border-red-500' : ''} ${className}`, ...props, children: [_jsx("option", { value: "", children: "Selecione uma op\u00E7\u00E3o" }), options.map((opt) => (_jsx("option", { value: opt.value, children: opt.label }, opt.value)))] }), _jsx(ChevronDown, { size: 18, className: "absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-black-500" })] }), error && _jsx("p", { className: "mt-1 text-xs text-red-500", children: error })] }));
});
Select.displayName = 'Select';
export default Select;
//# sourceMappingURL=Select.js.map
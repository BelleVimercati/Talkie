import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { forwardRef } from 'react';
const Input = forwardRef(({ label, error, rightIcon, className = '', ...props }, ref) => {
    return (_jsxs("div", { className: "w-full", children: [label && (_jsx("label", { className: "mb-2 block font-roboto text-base font-normal text-black-800 tracking-wide03", children: label })), _jsxs("div", { className: "relative", children: [_jsx("input", { ref: ref, className: `h-12 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 font-roboto text-sm text-black-900 placeholder-black-500 transition-colors focus:border-brand-blue focus:outline-none focus:ring-1 focus:ring-brand-blue ${error ? 'border-red-500' : ''} ${className}`, ...props }), rightIcon && (_jsx("div", { className: "absolute right-3 top-1/2 -translate-y-1/2", children: rightIcon }))] }), error && _jsx("p", { className: "mt-1 text-xs text-red-500", children: error })] }));
});
Input.displayName = 'Input';
export default Input;
//# sourceMappingURL=Input.js.map
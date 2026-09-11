import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
function Button({ variant = 'primary', isLoading = false, children, disabled, className = '', ...props }) {
    const baseStyles = 'h-12 rounded-md font-roboto font-bold text-sm tracking-wide03 transition-colors';
    const variants = {
        primary: 'w-full bg-brand-blue text-white hover:bg-opacity-90 disabled:bg-opacity-60 disabled:cursor-not-allowed',
    };
    return (_jsx("button", { disabled: disabled || isLoading, className: `${baseStyles} ${variants[variant]} ${className}`, ...props, children: isLoading ? (_jsxs("div", { className: "flex items-center justify-center gap-2", children: [_jsx("div", { className: "h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent" }), _jsx("span", { children: "Carregando..." })] })) : (children) }));
}
export default Button;
//# sourceMappingURL=Button.js.map
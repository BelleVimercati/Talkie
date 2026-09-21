import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { AlertCircle, CheckCircle } from 'lucide-react';
function Alert({ variant, children, onClose }) {
    const isError = variant === 'error';
    return (_jsxs("div", { className: `mb-4 flex items-start gap-3 rounded-md p-3 ${isError ? 'bg-red-50' : 'bg-green-50'}`, children: [isError ? (_jsx(AlertCircle, { className: "h-5 w-5 flex-shrink-0 text-red-500" })) : (_jsx(CheckCircle, { className: "h-5 w-5 flex-shrink-0 text-green-600" })), _jsxs("div", { className: "flex flex-1 items-center justify-between", children: [_jsx("p", { className: `text-sm ${isError ? 'text-red-800' : 'text-green-800'}`, children: children }), onClose && (_jsx("button", { onClick: onClose, className: "ml-2 text-gray-400 hover:text-gray-600", children: "\u00D7" }))] })] }));
}
export default Alert;
//# sourceMappingURL=Alert.js.map
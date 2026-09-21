import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useNavigate } from 'react-router-dom';
import Button from '@/components/ui/Button';
function NotFoundPage() {
    const navigate = useNavigate();
    return (_jsx("div", { className: "flex min-h-screen items-center justify-center bg-gray-50 px-4", children: _jsxs("div", { className: "text-center", children: [_jsx("h1", { className: "mb-4 text-6xl font-bold text-brand-blue", children: "404" }), _jsx("h2", { className: "mb-2 text-2xl font-semibold text-black-900", children: "P\u00E1gina n\u00E3o encontrada" }), _jsx("p", { className: "mb-8 text-black-500", children: "A p\u00E1gina que voc\u00EA est\u00E1 procurando n\u00E3o existe." }), _jsx(Button, { onClick: () => navigate('/login'), className: "mx-auto w-full max-w-sm", children: "Voltar para Login" })] }) }));
}
export default NotFoundPage;
//# sourceMappingURL=NotFoundPage.js.map
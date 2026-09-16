import { jsx as _jsx, jsxs as _jsxs, Fragment as _Fragment } from "react/jsx-runtime";
import { useState } from 'react';
import { X } from 'lucide-react';
import Select from '@/components/ui/Select';
import Button from '@/components/ui/Button';
import { useCategories } from '@/hooks/useCategories';
import { useSubscriptions } from '@/hooks/useSubscriptions';
import { subscriptionService } from '@/services/subscriptionService';
import { getErrorMessage } from '@/utils/errorHandler';
export function SubscribeModal({ isOpen, onClose, onSubscribe }) {
    const { categories } = useCategories();
    const { subscriptions } = useSubscriptions();
    const [selectedCategoryId, setSelectedCategoryId] = useState('');
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState(null);
    const subscribedCategoryIds = new Set(subscriptions.map((s) => s.categoryId));
    const availableCategories = categories.filter((c) => !subscribedCategoryIds.has(c.id));
    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!selectedCategoryId)
            return;
        try {
            setError(null);
            setIsSubmitting(true);
            await subscriptionService.subscribe(Number(selectedCategoryId));
            onSubscribe();
            setSelectedCategoryId('');
            onClose();
        }
        catch (err) {
            setError(getErrorMessage(err));
        }
        finally {
            setIsSubmitting(false);
        }
    };
    if (!isOpen)
        return null;
    return (_jsxs(_Fragment, { children: [_jsx("div", { className: "fixed inset-0 bg-black bg-opacity-50 z-40", onClick: onClose }), _jsxs("div", { className: "fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-white rounded-lg shadow-lg p-8 max-w-sm w-full mx-4", children: [_jsxs("div", { className: "flex items-center justify-between mb-6", children: [_jsx("h2", { className: "text-lg font-semibold text-black-900", children: "Nova Inscri\u00E7\u00E3o" }), _jsx("button", { onClick: onClose, className: "text-black-500 hover:text-black-700", children: _jsx(X, { size: 20 }) })] }), _jsx("form", { onSubmit: handleSubmit, className: "space-y-4", children: availableCategories.length === 0 ? (_jsx("p", { className: "text-sm text-black-500 text-center py-4", children: "Voc\u00EA j\u00E1 est\u00E1 inscrito em todas as categorias" })) : (_jsxs(_Fragment, { children: [_jsx(Select, { label: "Categoria", options: availableCategories.map((c) => ({
                                        value: c.id,
                                        label: `${c.icon} ${c.name}`,
                                    })), value: selectedCategoryId, onChange: (e) => setSelectedCategoryId(e.target.value), error: error || undefined }), error && _jsx("p", { className: "text-xs text-red-500", children: error }), _jsxs("div", { className: "flex gap-3 pt-2", children: [_jsx(Button, { type: "button", variant: "secondary", className: "flex-1", onClick: onClose, disabled: isSubmitting, children: "Cancelar" }), _jsx(Button, { type: "submit", className: "flex-1", disabled: isSubmitting || !selectedCategoryId, isLoading: isSubmitting, children: "Inscrever" })] })] })) })] })] }));
}
export default SubscribeModal;
//# sourceMappingURL=SubscribeModal.js.map
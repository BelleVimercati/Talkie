import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AppLayout } from '@/components/layout/AppLayout';
import Input from '@/components/ui/Input';
import Select from '@/components/ui/Select';
import Button from '@/components/ui/Button';
import Alert from '@/components/ui/Alert';
import { useCategories } from '@/hooks/useCategories';
import { createSubcategorySchema } from '@/schemas/createSubcategorySchema';
import { subcategoryService } from '@/services/categoryService';
import { getErrorMessage } from '@/utils/errorHandler';
function CreateSubcategoryPage() {
    const navigate = useNavigate();
    const [globalError, setGlobalError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const { categories } = useCategories();
    const { register, control, handleSubmit, formState: { errors }, } = useForm({
        resolver: zodResolver(createSubcategorySchema),
        defaultValues: {
            categoryId: 0,
        },
    });
    const onSubmit = async (data) => {
        try {
            setGlobalError(null);
            setIsLoading(true);
            await subcategoryService.create(data);
            navigate('/configuracoes');
        }
        catch (err) {
            const message = getErrorMessage(err);
            setGlobalError(message);
        }
        finally {
            setIsLoading(false);
        }
    };
    return (_jsx(AppLayout, { children: _jsxs("div", { className: "p-8", children: [_jsxs("div", { className: "mb-8", children: [_jsx("h1", { className: "mb-2 text-3xl font-bold font-roboto text-black-900", children: "Nova Subcategoria" }), _jsxs("p", { className: "text-sm text-brand-blue", children: [_jsx("button", { onClick: () => navigate('/'), className: "hover:underline", children: "Geral" }), ` > `, _jsx("button", { onClick: () => navigate('/configuracoes'), className: "hover:underline", children: "Configura\u00E7\u00F5es" }), ` > Subcategorias`] })] }), globalError && (_jsx(Alert, { variant: "error", onClose: () => setGlobalError(null), children: globalError })), _jsxs("form", { onSubmit: handleSubmit(onSubmit), className: "max-w-2xl space-y-6", children: [_jsx(Controller, { name: "categoryId", control: control, render: ({ field }) => (_jsx(Select, { label: "Categoria*", error: errors.categoryId?.message, options: categories.map((c) => ({
                                    value: c.id,
                                    label: c.name,
                                })), ...field })) }), _jsx(Input, { label: "Nome da subcategoria*", placeholder: "Digite o nome da subcategoria", error: errors.name?.message, ...register('name') }), _jsxs("div", { className: "flex gap-3 justify-end", children: [_jsx(Button, { type: "button", variant: "secondary", onClick: () => navigate('/configuracoes'), disabled: isLoading, className: "w-auto px-8", children: "Cancelar" }), _jsx(Button, { type: "submit", variant: "dashboard", isLoading: isLoading, disabled: isLoading, className: "w-auto px-8", children: "Criar subcategoria" })] })] })] }) }));
}
export default CreateSubcategoryPage;
//# sourceMappingURL=CreateSubcategoryPage.js.map
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
import { IconPicker } from '@/components/ui/IconPicker';
import { ColorPicker } from '@/components/ui/ColorPicker';
import { createCategorySchema } from '@/schemas/createCategorySchema';
import { categoryService } from '@/services/categoryService';
import { getErrorMessage } from '@/utils/errorHandler';
function CreateCategoryPage() {
    const navigate = useNavigate();
    const [globalError, setGlobalError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const { register, control, handleSubmit, formState: { errors }, } = useForm({
        resolver: zodResolver(createCategorySchema),
        defaultValues: {
            priority: 'MEDIA',
        },
    });
    const onSubmit = async (data) => {
        try {
            setGlobalError(null);
            setIsLoading(true);
            await categoryService.create(data);
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
    return (_jsx(AppLayout, { children: _jsxs("div", { className: "p-8", children: [_jsxs("div", { className: "mb-8", children: [_jsx("h1", { className: "mb-2 text-3xl font-bold font-roboto text-black-900", children: "Nova Categoria" }), _jsxs("p", { className: "text-sm text-brand-blue", children: [_jsx("button", { onClick: () => navigate('/'), className: "hover:underline", children: "Geral" }), ` > `, _jsx("button", { onClick: () => navigate('/configuracoes'), className: "hover:underline", children: "Configura\u00E7\u00F5es" }), ` > Categorias`] })] }), globalError && (_jsx(Alert, { variant: "error", onClose: () => setGlobalError(null), children: globalError })), _jsxs("form", { onSubmit: handleSubmit(onSubmit), className: "max-w-4xl space-y-6", children: [_jsxs("div", { className: "grid grid-cols-2 gap-6", children: [_jsx(Input, { label: "Nome da categoria*", placeholder: "Escolha um nome", error: errors.name?.message, ...register('name') }), _jsx(Controller, { name: "priority", control: control, render: ({ field }) => (_jsx(Select, { label: "Prioridade*", error: errors.priority?.message, options: [
                                            { value: 'BAIXA', label: 'Baixa' },
                                            { value: 'MEDIA', label: 'Média' },
                                            { value: 'ALTA', label: 'Alta' },
                                        ], ...field })) })] }), _jsx(Controller, { name: "icon", control: control, render: ({ field }) => (_jsx(IconPicker, { value: field.value, onChange: field.onChange, error: errors.icon?.message })) }), _jsx(Controller, { name: "color", control: control, render: ({ field }) => (_jsx(ColorPicker, { value: field.value, onChange: field.onChange, error: errors.color?.message })) }), _jsxs("div", { className: "flex gap-3 justify-end", children: [_jsx(Button, { type: "button", variant: "secondary", onClick: () => navigate('/configuracoes'), disabled: isLoading, className: "w-auto px-8", children: "Cancelar" }), _jsx(Button, { type: "submit", variant: "dashboard", isLoading: isLoading, disabled: isLoading, className: "w-auto px-8", children: "Criar categoria" })] })] })] }) }));
}
export default CreateCategoryPage;
//# sourceMappingURL=CreateCategoryPage.js.map
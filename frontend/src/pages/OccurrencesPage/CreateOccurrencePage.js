import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { AppLayout } from '@/components/layout/AppLayout';
import Input from '@/components/ui/Input';
import Select from '@/components/ui/Select';
import Textarea from '@/components/ui/Textarea';
import Button from '@/components/ui/Button';
import Alert from '@/components/ui/Alert';
import { useCategories } from '@/hooks/useCategories';
import { useSubcategories } from '@/hooks/useSubcategories';
import { createOccurrenceSchema } from '@/schemas/createOccurrenceSchema';
import { occurrenceService } from '@/services/occurrenceService';
import { getErrorMessage, getFieldFromBackendMessage } from '@/utils/errorHandler';
function CreateOccurrencePage() {
    const navigate = useNavigate();
    const [globalError, setGlobalError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const { categories } = useCategories();
    const { subcategories } = useSubcategories();
    const { register, control, handleSubmit, setError, watch, setValue, formState: { errors }, } = useForm({
        resolver: zodResolver(createOccurrenceSchema),
        defaultValues: {
            categoryId: 0,
            subcategoryId: 0,
        },
    });
    const selectedCategoryId = watch('categoryId');
    // Atualizar subcategorias disponíveis quando categoria muda
    useEffect(() => {
        if (selectedCategoryId) {
            const selected = categories.find((c) => c.id === selectedCategoryId);
            if (selected) {
                // Filtrar subcategorias por categoria
                const filtered = subcategories.filter((s) => s.categoryName === selected.name);
                if (filtered.length > 0) {
                    setValue('subcategoryId', 0); // Reset
                }
            }
        }
        else {
            setValue('subcategoryId', 0);
        }
    }, [selectedCategoryId, categories, subcategories, setValue]);
    const availableSubcategories = selectedCategoryId
        ? (() => {
            const selected = categories.find((c) => c.id === selectedCategoryId);
            if (!selected)
                return [];
            return subcategories.filter((s) => s.categoryName === selected.name);
        })()
        : [];
    const onSubmit = async (data) => {
        try {
            setGlobalError(null);
            setIsLoading(true);
            await occurrenceService.create(data);
            navigate('/', { state: { created: true } });
        }
        catch (err) {
            const message = getErrorMessage(err);
            const field = getFieldFromBackendMessage(message);
            if (field) {
                setError(field, { message });
            }
            else {
                setGlobalError(message);
            }
        }
        finally {
            setIsLoading(false);
        }
    };
    return (_jsx(AppLayout, { children: _jsxs("div", { className: "p-8", children: [_jsxs("div", { className: "mb-8", children: [_jsx("h1", { className: "mb-2 text-3xl font-bold font-roboto text-black-900", children: "Criar uma nova ocorr\u00EAncia" }), _jsxs("p", { className: "text-sm text-brand-blue", children: [_jsx("button", { onClick: () => navigate('/'), className: "hover:underline", children: "Minhas Ocorr\u00EAncias" }), ` > Nova Ocorrência`] })] }), globalError && (_jsx(Alert, { variant: "error", onClose: () => setGlobalError(null), children: globalError })), _jsxs("form", { onSubmit: handleSubmit(onSubmit), className: "max-w-4xl space-y-6", children: [_jsxs("div", { className: "grid grid-cols-2 gap-6", children: [_jsx(Input, { label: "T\u00EDtulo*", placeholder: "T\u00EDtulo da ocorr\u00EAncia", error: errors.title?.message, ...register('title') }), _jsx(Input, { label: "Data*", type: "date", disabled: true, defaultValue: "2020-12-12", className: "opacity-50 cursor-not-allowed" }), _jsx(Controller, { name: "categoryId", control: control, render: ({ field }) => (_jsx(Select, { label: "Categoria*", error: errors.categoryId?.message, options: categories.map((c) => ({
                                            value: c.id,
                                            label: c.name,
                                        })), ...field })) }), _jsx(Controller, { name: "subcategoryId", control: control, render: ({ field }) => (_jsx(Select, { label: "Subcategoria*", error: errors.subcategoryId?.message, disabled: !selectedCategoryId, options: availableSubcategories.map((s) => ({
                                            value: s.subcategoryId,
                                            label: s.name,
                                        })), ...field })) }), _jsx(Input, { label: "Localiza\u00E7\u00E3o*", placeholder: "Endere\u00E7o ou descri\u00E7\u00E3o de local", error: errors.location?.message, ...register('location') }), _jsx(Input, { label: "Hor\u00E1rio*", type: "time", disabled: true, defaultValue: "12:00", className: "opacity-50 cursor-not-allowed" })] }), _jsxs("div", { className: "flex items-center gap-3 px-4 py-2 border border-black-100 rounded-md bg-black-50 opacity-50 cursor-not-allowed", children: [_jsx("input", { type: "checkbox", disabled: true, className: "w-4 h-4" }), _jsx("span", { className: "text-sm text-black-500", children: "Usar Localiza\u00E7\u00E3o e hora atuais" })] }), _jsxs("div", { className: "opacity-50 cursor-not-allowed", children: [_jsx("label", { className: "mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03", children: "Anexo" }), _jsx("div", { className: "relative", children: _jsx("input", { type: "file", disabled: true, className: "h-12 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 text-black-500" }) })] }), _jsx(Textarea, { label: "Descri\u00E7\u00E3o*", placeholder: "Descreva o problema encontrado...", error: errors.description?.message, ...register('description') }), _jsx("p", { className: "text-sm text-black-500", children: "Usu\u00E1rios inscritos nesta categoria ser\u00E3o notificados automaticamente quando esta ocorr\u00EAncia for criada." }), _jsxs("div", { className: "flex gap-3 justify-end", children: [_jsx(Button, { type: "button", variant: "secondary", onClick: () => navigate('/'), disabled: isLoading, className: "w-auto px-8", children: "Cancelar" }), _jsx(Button, { type: "submit", variant: "dashboard", isLoading: isLoading, disabled: isLoading, className: "w-auto px-8", children: "Criar ocorr\u00EAncia" })] })] })] }) }));
}
export default CreateOccurrencePage;
//# sourceMappingURL=CreateOccurrencePage.js.map
import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useForm, Controller } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import AuthLayout from '@/components/layout/AuthLayout';
import Input from '@/components/ui/Input';
import PasswordInput from '@/components/ui/PasswordInput';
import Button from '@/components/ui/Button';
import Alert from '@/components/ui/Alert';
import { registerSchema } from '@/schemas/registerSchema';
import { authService } from '@/services/authService';
import { formatCpf } from '@/utils/cpf';
import { getErrorMessage, getFieldFromBackendMessage } from '@/utils/errorHandler';
function RegisterPage() {
    const navigate = useNavigate();
    const [globalError, setGlobalError] = useState(null);
    const [isLoading, setIsLoading] = useState(false);
    const { register, control, handleSubmit, setError, formState: { errors }, } = useForm({
        resolver: zodResolver(registerSchema),
    });
    const onSubmit = async (data) => {
        try {
            setGlobalError(null);
            setIsLoading(true);
            const { confirmPassword, ...payload } = data;
            await authService.register(payload);
            navigate('/login', { state: { registered: true } });
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
    return (_jsx(AuthLayout, { children: _jsxs("div", { children: [_jsx("h1", { className: "mb-6 font-poppins text-4xl font-semibold text-black-900", children: "Registre-se" }), globalError && (_jsx(Alert, { variant: "error", onClose: () => setGlobalError(null), children: globalError })), _jsxs("form", { onSubmit: handleSubmit(onSubmit), children: [_jsxs("div", { className: "space-y-4", children: [_jsx(Input, { label: "Nome", placeholder: "Nome Completo", error: errors.name?.message, ...register('name') }), _jsx(Controller, { name: "cpf", control: control, render: ({ field }) => (_jsx(Input, { label: "CPF", placeholder: "000.000.000-00", error: errors.cpf?.message, ...field, onChange: (e) => {
                                            field.onChange(formatCpf(e.target.value));
                                        }, value: formatCpf(field.value || '') })) }), _jsx(Input, { label: "Email", placeholder: "email@email.com", type: "email", error: errors.email?.message, ...register('email') }), _jsx(PasswordInput, { label: "Password", placeholder: "\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022", error: errors.password?.message, ...register('password') }), _jsx(PasswordInput, { label: "Confirm Password", placeholder: "\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022\u2022", error: errors.confirmPassword?.message, ...register('confirmPassword') })] }), _jsx(Button, { type: "submit", isLoading: isLoading, className: "mt-8", children: "Sign up" })] }), _jsx("div", { className: "mt-8 text-center", children: _jsxs("p", { className: "text-sm text-black-900", children: ["J\u00E1 tem uma conta?", ' ', _jsx("button", { onClick: () => navigate('/login'), className: "font-semibold text-brand-blue hover:underline", children: "Entrar" })] }) })] }) }));
}
export default RegisterPage;
//# sourceMappingURL=RegisterPage.js.map
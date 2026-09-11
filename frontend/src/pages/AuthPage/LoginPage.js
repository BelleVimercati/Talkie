import { jsx as _jsx, jsxs as _jsxs } from "react/jsx-runtime";
import { useEffect, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import AuthLayout from '@/components/layout/AuthLayout';
import Input from '@/components/ui/Input';
import PasswordInput from '@/components/ui/PasswordInput';
import Button from '@/components/ui/Button';
import Alert from '@/components/ui/Alert';
import Divider from '@/components/ui/Divider';
import { loginSchema } from '@/schemas/loginSchema';
import { useAuthStore } from '@/stores/authStore';
function LoginPage() {
    const navigate = useNavigate();
    const location = useLocation();
    const [showSuccess, setShowSuccess] = useState(location.state?.registered ?? false);
    const { register, handleSubmit, formState: { errors } } = useForm({
        resolver: zodResolver(loginSchema),
    });
    const { login, isLoading, error, logout, clearError } = useAuthStore();
    useEffect(() => {
        logout();
    }, [logout]);
    const onSubmit = async (data) => {
        try {
            clearError();
            await login(data.email, data.password);
            navigate('/');
        }
        catch {
            // Error is stored in the store
        }
    };
    return (_jsx(AuthLayout, { children: _jsxs("div", { children: [_jsx("h1", { className: "mb-8 font-poppins text-4xl font-semibold text-black-900", children: "Bem vindo!" }), showSuccess && (_jsx(Alert, { variant: "success", onClose: () => setShowSuccess(false), children: "Conta criada com sucesso! Fa\u00E7a login." })), error && (_jsx(Alert, { variant: "error", onClose: clearError, children: error })), _jsxs("form", { onSubmit: handleSubmit(onSubmit), className: "space-y-6", children: [_jsx(Input, { label: "Login", placeholder: "Email or phone number", type: "email", error: errors.email?.message, ...register('email') }), _jsx(PasswordInput, { label: "Password", placeholder: "Enter password", error: errors.password?.message, ...register('password') }), _jsx(Button, { type: "submit", isLoading: isLoading, children: "Sign in" })] }), _jsx(Divider, {}), _jsx("div", { className: "text-center", children: _jsxs("p", { className: "text-sm text-black-900", children: ["Ainda n\u00E3o tem conta?", ' ', _jsx("button", { onClick: () => navigate('/register'), className: "font-semibold text-brand-blue hover:underline", children: "Cadastre-se" })] }) })] }) }));
}
export default LoginPage;
//# sourceMappingURL=LoginPage.js.map
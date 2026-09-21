import { z } from 'zod';
import { isValidCpf } from '@/utils/cpf';
export const registerSchema = z
    .object({
    name: z.string().min(3, 'Nome deve ter no mínimo 3 caracteres'),
    cpf: z.string().refine(isValidCpf, 'CPF inválido'),
    email: z.string().min(1, 'Informe seu email').email('Email inválido'),
    password: z.string().min(6, 'Senha deve ter no mínimo 6 caracteres'),
    confirmPassword: z.string().min(1, 'Confirme sua senha'),
})
    .refine((data) => data.password === data.confirmPassword, {
    message: 'As senhas não coincidem',
    path: ['confirmPassword'],
});
//# sourceMappingURL=registerSchema.js.map
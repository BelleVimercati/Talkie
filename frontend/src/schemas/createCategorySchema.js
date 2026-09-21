import { z } from 'zod';
export const createCategorySchema = z.object({
    name: z.string().min(2, 'Nome deve ter no mínimo 2 caracteres'),
    icon: z.string().min(1, 'Escolha um ícone'),
    priority: z.enum(['BAIXA', 'MEDIA', 'ALTA'], {
        errorMap: () => ({ message: 'Prioridade é obrigatória' }),
    }),
    color: z.string().min(1, 'Escolha uma cor'),
});
//# sourceMappingURL=createCategorySchema.js.map
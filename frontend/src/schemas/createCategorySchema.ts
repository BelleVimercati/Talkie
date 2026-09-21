import { z } from 'zod'
import { type CategoryPriority } from '@/types/category'

export const createCategorySchema = z.object({
  name: z.string().min(2, 'Nome deve ter no mínimo 2 caracteres'),
  icon: z.string().min(1, 'Escolha um ícone'),
  priority: z.enum(['BAIXA', 'MEDIA', 'ALTA'] as const, {
    errorMap: () => ({ message: 'Prioridade é obrigatória' }),
  }) as z.ZodType<CategoryPriority>,
  color: z.string().min(1, 'Escolha uma cor'),
})

export type CreateCategoryFormData = z.infer<typeof createCategorySchema>

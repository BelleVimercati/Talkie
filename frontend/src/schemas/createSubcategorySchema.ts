import { z } from 'zod'

export const createSubcategorySchema = z.object({
  name: z.string().min(2, 'Nome deve ter no mínimo 2 caracteres'),
  categoryId: z.coerce.number().min(1, 'Categoria é obrigatória'),
})

export type CreateSubcategoryFormData = z.infer<typeof createSubcategorySchema>

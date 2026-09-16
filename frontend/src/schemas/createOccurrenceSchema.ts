import { z } from 'zod'

export const createOccurrenceSchema = z.object({
  title: z.string().min(3, 'Título deve ter no mínimo 3 caracteres'),
  description: z.string().min(10, 'Descrição deve ter no mínimo 10 caracteres'),
  location: z.string().min(1, 'Localização é obrigatória'),
  categoryId: z.coerce.number().min(1, 'Categoria é obrigatória'),
  subcategoryId: z.coerce.number().min(1, 'Subcategoria é obrigatória'),
})

export type CreateOccurrenceFormData = z.infer<typeof createOccurrenceSchema>

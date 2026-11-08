import { z } from 'zod'

// Inputs vazios chegam como '' — trate como "não informado" (senão o coerce vira 0)
const emptyToUndefined = (value: unknown) => (value === '' || value === null ? undefined : value)

export const createOccurrenceSchema = z.object({
  title: z.string().min(3, 'Título deve ter no mínimo 3 caracteres'),
  description: z.string().min(10, 'Descrição deve ter no mínimo 10 caracteres'),
  location: z.string().min(1, 'Localização é obrigatória'),
  categoryId: z.coerce.number().min(1, 'Categoria é obrigatória'),
  subcategoryId: z.coerce.number().min(1, 'Subcategoria é obrigatória'),
  // datetime-local envia hora local sem fuso (ex.: 2026-10-14T14:30), que o backend lê como LocalDateTime
  occurredAt: z.preprocess(
    emptyToUndefined,
    z
      .string()
      .datetime({ local: true, message: 'Data e hora inválidas' })
      .refine((value) => new Date(value) <= new Date(), 'A data do fato não pode ser no futuro')
      .optional()
  ),
  latitude: z.preprocess(
    emptyToUndefined,
    z.coerce
      .number({ invalid_type_error: 'Latitude inválida' })
      .min(-90, 'Latitude deve estar entre -90 e 90')
      .max(90, 'Latitude deve estar entre -90 e 90')
      .optional()
  ),
  longitude: z.preprocess(
    emptyToUndefined,
    z.coerce
      .number({ invalid_type_error: 'Longitude inválida' })
      .min(-180, 'Longitude deve estar entre -180 e 180')
      .max(180, 'Longitude deve estar entre -180 e 180')
      .optional()
  ),
}).refine(
  (data) => (data.latitude === null || data.latitude === undefined) || (data.longitude !== null && data.longitude !== undefined),
  {
    message: 'Latitude e longitude devem ser fornecidas juntas',
    path: ['latitude'],
  }
).refine(
  (data) => (data.longitude === null || data.longitude === undefined) || (data.latitude !== null && data.latitude !== undefined),
  {
    message: 'Latitude e longitude devem ser fornecidas juntas',
    path: ['longitude'],
  }
)

export type CreateOccurrenceFormData = z.infer<typeof createOccurrenceSchema>

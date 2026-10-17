import { z } from 'zod'

export const deleteAccountSchema = z.object({
  password: z.string().min(1, 'Informe sua senha para confirmar'),
})

export type DeleteAccountFormData = z.infer<typeof deleteAccountSchema>

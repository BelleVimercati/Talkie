export type OccurrenceStatus = 'ABERTO' | 'EM_ANALISE' | 'RESOLVIDO' | 'FECHADO'

export interface Occurrence {
  id: number
  title: string
  description: string
  location: string
  status: OccurrenceStatus
  categoryName: string
  subcategoryName: string
  /** null quando o autor excluiu a conta */
  ownerId: string | null
  ownerName: string | null
  createdAt: string
  resolvedAt: string | null
  occurredAt?: string
  latitude?: number
  longitude?: number
  imageUrl?: string
}

export interface OccurrenceStatusHistory {
  id: number
  previousStatus: OccurrenceStatus | null
  newStatus: OccurrenceStatus
  reason: string | null
  changedAt: string
  /** null quando o autor do evento excluiu a conta */
  changedById: string | null
  changedByName: string | null
}

export interface CreateOccurrenceDTO {
  title: string
  description: string
  location: string
  categoryId: number
  subcategoryId: number
  occurredAt?: string
  latitude?: number
  longitude?: number
}

export type OccurrenceStatus = 'ABERTO' | 'EM_ANALISE' | 'RESOLVIDO' | 'FECHADO'

export interface Occurrence {
  id: number
  title: string
  description: string
  location: string
  status: OccurrenceStatus
  categoryName: string
  subcategoryName: string
  ownerId: string
  ownerName: string
  createdAt: string
  resolvedAt: string | null
}

export interface CreateOccurrenceDTO {
  title: string
  description: string
  location: string
  categoryId: number
  subcategoryId: number
}

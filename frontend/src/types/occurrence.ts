export type OccurrenceStatus = 'ABERTO' | 'EM_ANALISE' | 'RESOLVIDO' | 'FECHADO'

export interface Occurrence {
  title: string
  description: string
  location: string
  status: OccurrenceStatus
  categoryName: string
  subcategoryName: string
  ownerId: string
}

export interface CreateOccurrenceDTO {
  title: string
  description: string
  location: string
  categoryId: number
  subcategoryId: number
}

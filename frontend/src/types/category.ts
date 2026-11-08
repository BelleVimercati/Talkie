export type CategoryPriority = 'BAIXA' | 'MEDIA' | 'ALTA'

export interface Category {
  id: number
  name: string
  icon: string
  /** null quando o admin que criou a categoria excluiu a conta */
  userId: string | null
  priority: CategoryPriority
  color: string
  createdAt: string
  /** false = desativada (exclusão lógica) */
  active: boolean
}

export interface Subcategory {
  subcategoryId: number
  name: string
  categoryName: string
  categoryId: number
  /** false = desativada (exclusão lógica) */
  active: boolean
}

export interface CreateCategoryDTO {
  name: string
  icon: string
  priority: CategoryPriority
  color: string
}

export interface CreateSubcategoryDTO {
  name: string
  categoryId: number
}

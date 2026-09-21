export type CategoryPriority = 'BAIXA' | 'MEDIA' | 'ALTA'

export interface Category {
  id: number
  name: string
  icon: string
  userId: string
  priority: CategoryPriority
  color: string
  createdAt: string
}

export interface Subcategory {
  subcategoryId: number
  name: string
  categoryName: string
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

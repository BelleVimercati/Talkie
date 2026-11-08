import api from './api'
import { Category, Subcategory, CreateCategoryDTO, CreateSubcategoryDTO } from '@/types/category'
import { ApiResponse } from '@/types/api'

export const categoryService = {
  /** Por padrão só ativas; a tela de administração pede também as inativas */
  list: async (includeInactive = false) => {
    const response = await api.get<ApiResponse<Category[]>>('/categories', { params: { includeInactive } })
    return response.data.data
  },
  getById: async (id: number) => {
    const response = await api.get<ApiResponse<Category>>(`/categories/${id}`)
    return response.data.data
  },
  create: async (data: CreateCategoryDTO) => {
    const response = await api.post<ApiResponse<Category>>('/categories', data)
    return response.data.data
  },
  update: async (id: number, data: CreateCategoryDTO) => {
    const response = await api.put<ApiResponse<Category>>(`/categories/${id}`, data)
    return response.data.data
  },
  /** Exclusão lógica: a categoria continua nas ocorrências antigas */
  deactivate: async (id: number) => {
    const response = await api.delete<ApiResponse<Category>>(`/categories/${id}`)
    return response.data.data
  },
  reactivate: async (id: number) => {
    const response = await api.put<ApiResponse<Category>>(`/categories/${id}/reactivate`)
    return response.data.data
  },
}

export const subcategoryService = {
  /** Por padrão só subcategorias ativas de categorias ativas */
  list: async (includeInactive = false) => {
    const response = await api.get<ApiResponse<Subcategory[]>>('/subcategories', { params: { includeInactive } })
    return response.data.data
  },
  getById: async (id: number) => {
    const response = await api.get<ApiResponse<Subcategory>>(`/subcategories/${id}`)
    return response.data.data
  },
  create: async (data: CreateSubcategoryDTO) => {
    const response = await api.post<ApiResponse<Subcategory>>('/subcategories', data)
    return response.data.data
  },
  update: async (id: number, data: CreateSubcategoryDTO) => {
    const response = await api.put<ApiResponse<Subcategory>>(`/subcategories/${id}`, data)
    return response.data.data
  },
  deactivate: async (id: number) => {
    const response = await api.delete<ApiResponse<Subcategory>>(`/subcategories/${id}`)
    return response.data.data
  },
  reactivate: async (id: number) => {
    const response = await api.put<ApiResponse<Subcategory>>(`/subcategories/${id}/reactivate`)
    return response.data.data
  },
}

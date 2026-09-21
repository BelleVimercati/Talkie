import api from './api'
import { Category, Subcategory, CreateCategoryDTO, CreateSubcategoryDTO } from '@/types/category'
import { ApiResponse } from '@/types/api'

export const categoryService = {
  list: async () => {
    const response = await api.get<ApiResponse<Category[]> | Category[]>('/categories')
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },
  create: async (data: CreateCategoryDTO) => {
    const response = await api.post<ApiResponse<Category>>('/categories', data)
    return response.data.data
  },
}

export const subcategoryService = {
  list: async () => {
    const response = await api.get<ApiResponse<Subcategory[]> | Subcategory[]>('/subcategories')
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },
  create: async (data: CreateSubcategoryDTO) => {
    const response = await api.post<ApiResponse<Subcategory>>('/subcategories', data)
    return response.data.data
  },
}

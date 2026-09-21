import api from './api'
import { Occurrence, CreateOccurrenceDTO, OccurrenceStatus } from '@/types/occurrence'
import { ApiResponse } from '@/types/api'

export const occurrenceService = {
  getMine: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences/my')
    return response.data.data
  },

  getAll: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences')
    return response.data.data
  },

  getByCategory: async (categoryId: number) => {
    const response = await api.get<ApiResponse<Occurrence[]>>(`/occurrences/category/${categoryId}`)
    return response.data.data
  },

  create: async (data: CreateOccurrenceDTO) => {
    const response = await api.post<ApiResponse<Occurrence>>('/occurrences', data)
    return response.data.data
  },

  updateStatus: async (id: number, status: OccurrenceStatus) => {
    const response = await api.put<ApiResponse<Occurrence>>(`/occurrences/${id}/status`, { status })
    return response.data.data
  },
}

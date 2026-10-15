import api from './api'
import { Occurrence, CreateOccurrenceDTO, OccurrenceStatus, OccurrenceStatusHistory } from '@/types/occurrence'
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

  updateStatus: async (id: number, status: OccurrenceStatus, reason?: string) => {
    const response = await api.put<ApiResponse<Occurrence>>(`/occurrences/${id}/status`, { status, reason })
    return response.data.data
  },

  getHistory: async (id: number) => {
    const response = await api.get<ApiResponse<OccurrenceStatusHistory[]>>(`/occurrences/${id}/history`)
    return response.data.data
  },

  uploadImage: async (id: number, file: File) => {
    const formData = new FormData()
    formData.append('file', file)
    const response = await api.post(`/occurrences/${id}/image`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    })
    return response.data
  },

  getImage: async (id: number): Promise<Blob> => {
    const response = await api.get(`/occurrences/${id}/image`, {
      responseType: 'blob',
    })
    return response.data
  },
}

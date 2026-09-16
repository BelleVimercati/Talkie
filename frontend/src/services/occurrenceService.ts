import api from './api'
import { Occurrence, CreateOccurrenceDTO } from '@/types/occurrence'
import { ApiResponse } from '@/types/api'

export const occurrenceService = {
  getMine: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences/my')
    return response.data.data
  },

  create: async (data: CreateOccurrenceDTO) => {
    const response = await api.post<ApiResponse<Occurrence>>('/occurrences', data)
    return response.data.data
  },
}

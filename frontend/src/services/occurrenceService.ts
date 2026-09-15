import api from './api'
import { Occurrence } from '@/types/occurrence'
import { ApiResponse } from '@/types/api'

export const occurrenceService = {
  getMine: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences/my')
    return response.data.data
  },
}

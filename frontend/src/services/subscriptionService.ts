import api from './api'
import { Subscription } from '@/types/subscription'
import { ApiResponse } from '@/types/api'

export const subscriptionService = {
  listMine: async () => {
    const response = await api.get<ApiResponse<Subscription[]> | Subscription[]>('/subscriptions/my')
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },

  subscribe: async (categoryId: number) => {
    const response = await api.post<ApiResponse<Subscription>>(`/subscriptions/${categoryId}`)
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },
}

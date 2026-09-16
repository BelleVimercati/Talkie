import api from './api'
import { AdminUser } from '@/types/user'
import { ApiResponse } from '@/types/api'

export const userService = {
  list: async () => {
    const response = await api.get<ApiResponse<AdminUser[]> | AdminUser[]>('/users')
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },
}

import api from './api'
import { AdminUser } from '@/types/user'
import { ApiResponse } from '@/types/api'

export const userService = {
  list: async () => {
    const response = await api.get<ApiResponse<AdminUser[]> | AdminUser[]>('/users')
    const data = response.data
    return Array.isArray(data) ? data : data.data
  },

  deleteOwnAccount: async (password: string) => {
    const response = await api.delete<ApiResponse<null>>('/users/me', { data: { password } })
    return response.data.message
  },
}

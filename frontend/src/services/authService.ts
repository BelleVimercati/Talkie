import api from './api'
import type { ApiResponse, LoginRequestDTO, RegisterDTO, UserResponseDTO } from '@/types/api'

export const authService = {
  login: async (payload: LoginRequestDTO): Promise<string> => {
    const { data } = await api.post<ApiResponse<string>>('/auth/login', payload)
    return data.data
  },

  register: async (payload: RegisterDTO): Promise<UserResponseDTO> => {
    const { data } = await api.post<ApiResponse<UserResponseDTO>>('/auth/register', payload)
    return data.data
  },
}

export interface ApiResponse<T> {
  message: string
  data: T
}

export interface ErrorResponse {
  message: string
  status: number
  timestamp: string
}

export interface LoginRequestDTO {
  email: string
  password: string
}

export interface RegisterDTO {
  name: string
  email: string
  password: string
  cpf: string
}

export interface UserResponseDTO {
  id: string
  name: string
  email: string
}

export interface JwtPayload {
  sub: string
  role: 'USER' | 'ADMIN'
  iss: string
  exp: number
}

export interface AuthUser {
  email: string
  role: 'USER' | 'ADMIN'
}

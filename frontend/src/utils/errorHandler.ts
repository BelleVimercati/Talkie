import axios from 'axios'

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    if (
      error.response?.data?.message &&
      typeof error.response.data.message === 'string'
    ) {
      return error.response.data.message
    }
    if (error.response?.data?.error) {
      return error.response.data.error
    }
    return error.message || 'Erro na requisição'
  }
  if (error instanceof Error) {
    return error.message
  }
  return 'Erro desconhecido'
}

export function getHttpStatus(error: unknown): number {
  if (axios.isAxiosError(error)) {
    return error.response?.status ?? 500
  }
  return 500
}

export function getFieldFromBackendMessage(message: string): 'cpf' | 'email' | null {
  if (message.toLowerCase().includes('cpf')) return 'cpf'
  if (message.toLowerCase().includes('email')) return 'email'
  return null
}

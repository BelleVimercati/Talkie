export interface AdminUser {
  id: string
  name: string
  email: string
  cpf: string
  role: 'USER' | 'ADMIN'
  createdAt: string
}

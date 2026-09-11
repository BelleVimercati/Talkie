import { jwtDecode } from 'jwt-decode'
import type { AuthUser, JwtPayload } from '@/types/api'

export function decodeAuthUser(token: string): AuthUser {
  const payload = jwtDecode<JwtPayload>(token)
  return { email: payload.sub, role: payload.role }
}

export function isTokenValid(token: string): boolean {
  try {
    const payload = jwtDecode<JwtPayload>(token)
    return payload.exp * 1000 > Date.now()
  } catch {
    return false
  }
}

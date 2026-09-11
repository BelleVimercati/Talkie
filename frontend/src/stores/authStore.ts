import { create } from 'zustand'
import { authService } from '@/services/authService'
import { decodeAuthUser, isTokenValid } from '@/utils/jwt'
import { getErrorMessage } from '@/utils/errorHandler'
import type { AuthUser } from '@/types/api'

const TOKEN_KEY = import.meta.env.VITE_JWT_TOKEN_KEY

function readInitialToken(): string | null {
  const stored = localStorage.getItem(TOKEN_KEY)
  if (stored && isTokenValid(stored)) return stored
  if (stored) localStorage.removeItem(TOKEN_KEY)
  return null
}

interface AuthState {
  token: string | null
  user: AuthUser | null
  isAuthenticated: boolean
  isLoading: boolean
  error: string | null
  login: (email: string, password: string) => Promise<void>
  logout: () => void
  clearError: () => void
}

const initialToken = readInitialToken()

export const useAuthStore = create<AuthState>((set) => ({
  token: initialToken,
  user: initialToken ? decodeAuthUser(initialToken) : null,
  isAuthenticated: !!initialToken,
  isLoading: false,
  error: null,

  login: async (email, password) => {
    set({ isLoading: true, error: null })
    try {
      const token = await authService.login({ email, password })
      localStorage.setItem(TOKEN_KEY, token)
      set({
        token,
        user: decodeAuthUser(token),
        isAuthenticated: true,
        isLoading: false,
      })
    } catch (err) {
      set({ isLoading: false, error: getErrorMessage(err) })
      throw err
    }
  },

  logout: () => {
    localStorage.removeItem(TOKEN_KEY)
    set({ token: null, user: null, isAuthenticated: false })
  },

  clearError: () => set({ error: null }),
}))

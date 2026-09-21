import { create } from 'zustand';
import { authService } from '@/services/authService';
import { decodeAuthUser, isTokenValid } from '@/utils/jwt';
import { getErrorMessage } from '@/utils/errorHandler';
const TOKEN_KEY = import.meta.env.VITE_JWT_TOKEN_KEY;
function readInitialToken() {
    const stored = localStorage.getItem(TOKEN_KEY);
    if (stored && isTokenValid(stored))
        return stored;
    if (stored)
        localStorage.removeItem(TOKEN_KEY);
    return null;
}
const initialToken = readInitialToken();
export const useAuthStore = create((set) => ({
    token: initialToken,
    user: initialToken ? decodeAuthUser(initialToken) : null,
    isAuthenticated: !!initialToken,
    isLoading: false,
    error: null,
    login: async (email, password) => {
        set({ isLoading: true, error: null });
        try {
            const token = await authService.login({ email, password });
            localStorage.setItem(TOKEN_KEY, token);
            set({
                token,
                user: decodeAuthUser(token),
                isAuthenticated: true,
                isLoading: false,
            });
        }
        catch (err) {
            set({ isLoading: false, error: getErrorMessage(err) });
            throw err;
        }
    },
    logout: () => {
        localStorage.removeItem(TOKEN_KEY);
        set({ token: null, user: null, isAuthenticated: false });
    },
    clearError: () => set({ error: null }),
}));
//# sourceMappingURL=authStore.js.map
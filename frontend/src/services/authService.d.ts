import type { LoginRequestDTO, RegisterDTO, UserResponseDTO } from '@/types/api';
export declare const authService: {
    login: (payload: LoginRequestDTO) => Promise<string>;
    register: (payload: RegisterDTO) => Promise<UserResponseDTO>;
};
//# sourceMappingURL=authService.d.ts.map
import { jwtDecode } from 'jwt-decode';
export function decodeAuthUser(token) {
    const payload = jwtDecode(token);
    return { email: payload.sub, role: payload.role };
}
export function isTokenValid(token) {
    try {
        const payload = jwtDecode(token);
        return payload.exp * 1000 > Date.now();
    }
    catch {
        return false;
    }
}
//# sourceMappingURL=jwt.js.map
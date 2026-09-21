import api from './api';
export const userService = {
    list: async () => {
        const response = await api.get('/users');
        const data = response.data;
        return Array.isArray(data) ? data : data.data;
    },
};
//# sourceMappingURL=userService.js.map
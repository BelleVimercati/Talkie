import api from './api';
export const categoryService = {
    list: async () => {
        const response = await api.get('/categories');
        const data = response.data;
        return Array.isArray(data) ? data : data.data;
    },
    create: async (data) => {
        const response = await api.post('/categories', data);
        return response.data.data;
    },
};
export const subcategoryService = {
    list: async () => {
        const response = await api.get('/subcategories');
        const data = response.data;
        return Array.isArray(data) ? data : data.data;
    },
    create: async (data) => {
        const response = await api.post('/subcategories', data);
        return response.data.data;
    },
};
//# sourceMappingURL=categoryService.js.map
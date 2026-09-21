import api from './api';
export const occurrenceService = {
    getMine: async () => {
        const response = await api.get('/occurrences/my');
        return response.data.data;
    },
    getByCategory: async (categoryId) => {
        const response = await api.get(`/occurrences/category/${categoryId}`);
        return response.data.data;
    },
    create: async (data) => {
        const response = await api.post('/occurrences', data);
        return response.data.data;
    },
};
//# sourceMappingURL=occurrenceService.js.map
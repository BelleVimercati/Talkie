import api from './api';
export const subscriptionService = {
    listMine: async () => {
        const response = await api.get('/subscriptions/my');
        const data = response.data;
        return Array.isArray(data) ? data : data.data;
    },
    subscribe: async (categoryId) => {
        const response = await api.post(`/subscriptions/${categoryId}`);
        const data = response.data;
        return Array.isArray(data) ? data : data.data;
    },
};
//# sourceMappingURL=subscriptionService.js.map
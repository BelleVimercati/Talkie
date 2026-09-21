import { useState, useEffect } from 'react';
import { occurrenceService } from '@/services/occurrenceService';
import { useSubscriptions } from './useSubscriptions';
import { getErrorMessage } from '@/utils/errorHandler';
export function useNotifications() {
    const { subscriptions, isLoading: subsLoading } = useSubscriptions();
    const [occurrences, setOccurrences] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchNotifications = async () => {
            if (subsLoading)
                return;
            try {
                setError(null);
                setIsLoading(true);
                if (subscriptions.length === 0) {
                    setOccurrences([]);
                    setIsLoading(false);
                    return;
                }
                const promises = subscriptions.map((sub) => occurrenceService.getByCategory(sub.categoryId).catch(() => []));
                const results = await Promise.all(promises);
                const allOccurrences = results.flat();
                allOccurrences.sort((a, b) => {
                    const dateA = new Date(a.createdAt).getTime();
                    const dateB = new Date(b.createdAt).getTime();
                    return dateB - dateA;
                });
                setOccurrences(allOccurrences);
            }
            catch (err) {
                setError(getErrorMessage(err));
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchNotifications();
    }, [subscriptions, subsLoading]);
    return { occurrences, isLoading, error };
}
//# sourceMappingURL=useNotifications.js.map
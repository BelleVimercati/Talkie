import { useEffect, useState } from 'react';
import { occurrenceService } from '@/services/occurrenceService';
import { getErrorMessage } from '@/utils/errorHandler';
export function useOccurrences() {
    const [occurrences, setOccurrences] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchOccurrences = async () => {
            try {
                setIsLoading(true);
                setError(null);
                const data = await occurrenceService.getMine();
                setOccurrences(data || []);
            }
            catch (err) {
                setError(getErrorMessage(err));
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchOccurrences();
    }, []);
    return { occurrences, isLoading, error };
}
//# sourceMappingURL=useOccurrences.js.map
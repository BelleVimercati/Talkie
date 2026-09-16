import { useEffect, useState } from 'react';
import { categoryService } from '@/services/categoryService';
import { getErrorMessage } from '@/utils/errorHandler';
export function useCategories() {
    const [categories, setCategories] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchCategories = async () => {
            try {
                setIsLoading(true);
                setError(null);
                const data = await categoryService.list();
                setCategories(data || []);
            }
            catch (err) {
                setError(getErrorMessage(err));
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchCategories();
    }, []);
    return { categories, isLoading, error };
}
//# sourceMappingURL=useCategories.js.map
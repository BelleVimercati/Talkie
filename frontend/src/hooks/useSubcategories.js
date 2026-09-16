import { useEffect, useState } from 'react';
import { subcategoryService } from '@/services/categoryService';
import { getErrorMessage } from '@/utils/errorHandler';
export function useSubcategories() {
    const [subcategories, setSubcategories] = useState([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchSubcategories = async () => {
            try {
                setIsLoading(true);
                setError(null);
                const data = await subcategoryService.list();
                setSubcategories(data || []);
            }
            catch (err) {
                setError(getErrorMessage(err));
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchSubcategories();
    }, []);
    return { subcategories, isLoading, error };
}
//# sourceMappingURL=useSubcategories.js.map
import { useState, useEffect } from 'react';
import { userService } from '@/services/userService';
import { getErrorMessage } from '@/utils/errorHandler';
export function useUsers() {
    const [users, setUsers] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState(null);
    useEffect(() => {
        const fetchUsers = async () => {
            try {
                setError(null);
                const data = await userService.list();
                setUsers(data || []);
            }
            catch (err) {
                setError(getErrorMessage(err));
            }
            finally {
                setIsLoading(false);
            }
        };
        fetchUsers();
    }, []);
    return { users, isLoading, error };
}
//# sourceMappingURL=useUsers.js.map
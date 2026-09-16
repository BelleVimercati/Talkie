import { useState, useEffect } from 'react'
import { AdminUser } from '@/types/user'
import { userService } from '@/services/userService'
import { getErrorMessage } from '@/utils/errorHandler'

export function useUsers() {
  const [users, setUsers] = useState<AdminUser[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchUsers = async () => {
      try {
        setError(null)
        const data = await userService.list()
        setUsers(data || [])
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setIsLoading(false)
      }
    }

    fetchUsers()
  }, [])

  return { users, isLoading, error }
}

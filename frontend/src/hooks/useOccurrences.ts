import { useEffect, useState } from 'react'
import { occurrenceService } from '@/services/occurrenceService'
import { Occurrence, OccurrenceStatus } from '@/types/occurrence'
import { getErrorMessage } from '@/utils/errorHandler'
import { useAuthStore } from '@/stores/authStore'

export function useOccurrences() {
  const { user } = useAuthStore()
  const [occurrences, setOccurrences] = useState<Occurrence[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchOccurrences = async () => {
      try {
        setIsLoading(true)
        setError(null)
        const data = user?.role === 'ADMIN'
          ? await occurrenceService.getAll()
          : await occurrenceService.getMine()
        setOccurrences(data || [])
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setIsLoading(false)
      }
    }

    fetchOccurrences()
  }, [user?.role])

  const updateOccurrenceStatus = (id: number, status: OccurrenceStatus) => {
    setOccurrences((prev) =>
      prev.map((o) => (o.id === id ? { ...o, status } : o))
    )
  }

  return { occurrences, isLoading, error, updateOccurrenceStatus }
}

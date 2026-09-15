import { useEffect, useState } from 'react'
import { occurrenceService } from '@/services/occurrenceService'
import { Occurrence } from '@/types/occurrence'
import { getErrorMessage } from '@/utils/errorHandler'

export function useOccurrences() {
  const [occurrences, setOccurrences] = useState<Occurrence[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchOccurrences = async () => {
      try {
        setIsLoading(true)
        setError(null)
        const data = await occurrenceService.getMine()
        setOccurrences(data || [])
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setIsLoading(false)
      }
    }

    fetchOccurrences()
  }, [])

  return { occurrences, isLoading, error }
}

import { useState, useEffect } from 'react'
import { Subscription } from '@/types/subscription'
import { subscriptionService } from '@/services/subscriptionService'
import { getErrorMessage } from '@/utils/errorHandler'

export function useSubscriptions() {
  const [subscriptions, setSubscriptions] = useState<Subscription[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [error, setError] = useState<string | null>(null)

  const refetch = async () => {
    try {
      setError(null)
      setIsLoading(true)
      const data = await subscriptionService.listMine()
      setSubscriptions(data || [])
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }

  useEffect(() => {
    refetch()
  }, [])

  return { subscriptions, isLoading, error, refetch }
}

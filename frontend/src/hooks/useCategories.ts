import { useCallback, useEffect, useState } from 'react'
import { categoryService } from '@/services/categoryService'
import { Category } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'

interface UseCategoriesOptions {
  /** Inclui itens desativados (tela de administração) */
  includeInactive?: boolean
}

export function useCategories({ includeInactive = false }: UseCategoriesOptions = {}) {
  const [categories, setCategories] = useState<Category[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const reload = useCallback(async () => {
    try {
      setIsLoading(true)
      setError(null)
      const data = await categoryService.list(includeInactive)
      setCategories(data || [])
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }, [includeInactive])

  useEffect(() => {
    reload()
  }, [reload])

  return { categories, isLoading, error, reload }
}

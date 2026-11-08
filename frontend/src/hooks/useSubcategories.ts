import { useCallback, useEffect, useState } from 'react'
import { subcategoryService } from '@/services/categoryService'
import { Subcategory } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'

interface UseSubcategoriesOptions {
  /** Inclui itens desativados (tela de administração) */
  includeInactive?: boolean
}

export function useSubcategories({ includeInactive = false }: UseSubcategoriesOptions = {}) {
  const [subcategories, setSubcategories] = useState<Subcategory[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const reload = useCallback(async () => {
    try {
      setIsLoading(true)
      setError(null)
      const data = await subcategoryService.list(includeInactive)
      setSubcategories(data || [])
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsLoading(false)
    }
  }, [includeInactive])

  useEffect(() => {
    reload()
  }, [reload])

  return { subcategories, isLoading, error, reload }
}

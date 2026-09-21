import { useEffect, useState } from 'react'
import { subcategoryService } from '@/services/categoryService'
import { Subcategory } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'

export function useSubcategories() {
  const [subcategories, setSubcategories] = useState<Subcategory[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchSubcategories = async () => {
      try {
        setIsLoading(true)
        setError(null)
        const data = await subcategoryService.list()
        setSubcategories(data || [])
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setIsLoading(false)
      }
    }

    fetchSubcategories()
  }, [])

  return { subcategories, isLoading, error }
}

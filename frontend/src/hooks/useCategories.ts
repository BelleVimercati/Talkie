import { useEffect, useState } from 'react'
import { categoryService } from '@/services/categoryService'
import { Category } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'

export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    const fetchCategories = async () => {
      try {
        setIsLoading(true)
        setError(null)
        const data = await categoryService.list()
        setCategories(data || [])
      } catch (err) {
        setError(getErrorMessage(err))
      } finally {
        setIsLoading(false)
      }
    }

    fetchCategories()
  }, [])

  return { categories, isLoading, error }
}

import { useEffect, useState } from 'react'
import { X } from 'lucide-react'
import Select from '@/components/ui/Select'
import Button from '@/components/ui/Button'
import { useCategories } from '@/hooks/useCategories'
import { useSubscriptions } from '@/hooks/useSubscriptions'
import { subscriptionService } from '@/services/subscriptionService'
import { getErrorMessage } from '@/utils/errorHandler'
import { Subscription } from '@/types/subscription'

interface SubscribeModalProps {
  isOpen: boolean
  onClose: () => void
  onSubscribe: () => void
  editingSubscription?: Subscription | null
}

export function SubscribeModal({ isOpen, onClose, onSubscribe, editingSubscription }: SubscribeModalProps) {
  const { categories } = useCategories()
  const { subscriptions } = useSubscriptions()
  const [selectedCategoryId, setSelectedCategoryId] = useState<string>('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (editingSubscription) {
      setSelectedCategoryId(String(editingSubscription.categoryId))
      setError(null)
    } else if (isOpen) {
      setSelectedCategoryId('')
      setError(null)
    }
  }, [editingSubscription, isOpen])

  const subscribedCategoryIds = new Set(
    subscriptions
      .filter((s) => s.categoryId !== editingSubscription?.categoryId)
      .map((s) => s.categoryId)
  )
  const availableCategories = categories.filter((c) => !subscribedCategoryIds.has(c.id))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedCategoryId) return

    try {
      setError(null)
      setIsSubmitting(true)

      if (editingSubscription && Number(selectedCategoryId) !== editingSubscription.categoryId) {
        // Trocar de categoria: remover da antiga e criar na nova
        await subscriptionService.unsubscribe(editingSubscription.categoryId)
        await subscriptionService.subscribe(Number(selectedCategoryId))
      } else if (!editingSubscription) {
        // Criar nova inscrição
        await subscriptionService.subscribe(Number(selectedCategoryId))
      }
      // Se editando e a categoria for a mesma, não fazer nada

      onSubscribe()
      setSelectedCategoryId('')
      onClose()
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  if (!isOpen) return null

  return (
    <>
      <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={onClose} />
      <div className="fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-white rounded-lg shadow-lg p-8 max-w-sm w-full mx-4">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-black-900">
            {editingSubscription ? 'Editar Inscrição' : 'Nova Inscrição'}
          </h2>
          <button onClick={onClose} className="text-black-500 hover:text-black-700">
            <X size={20} />
          </button>
        </div>

        <form onSubmit={handleSubmit} className="space-y-4">
          {availableCategories.length === 0 ? (
            <p className="text-sm text-black-500 text-center py-4">
              Você já está inscrito em todas as categorias
            </p>
          ) : (
            <>
              <Select
                label="Categoria"
                options={availableCategories.map((c) => ({
                  value: c.id,
                  label: `${c.icon} ${c.name}`,
                }))}
                value={selectedCategoryId}
                onChange={(e) => setSelectedCategoryId(e.target.value)}
                error={error || undefined}
              />
              {error && <p className="text-xs text-red-500">{error}</p>}
              <div className="flex gap-3 pt-2">
                <Button
                  type="button"
                  variant="secondary"
                  className="flex-1"
                  onClick={onClose}
                  disabled={isSubmitting}
                >
                  Cancelar
                </Button>
                <Button
                  type="submit"
                  className="flex-1"
                  disabled={isSubmitting || !selectedCategoryId || (editingSubscription && Number(selectedCategoryId) === editingSubscription.categoryId)}
                  isLoading={isSubmitting}
                >
                  {editingSubscription ? 'Salvar' : 'Inscrever'}
                </Button>
              </div>
            </>
          )}
        </form>
      </div>
    </>
  )
}

export default SubscribeModal

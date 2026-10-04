import { useEffect, useState } from 'react'
import { X } from 'lucide-react'
import { Occurrence, OccurrenceStatus } from '@/types/occurrence'
import Button from '@/components/ui/Button'
import Select from '@/components/ui/Select'
import Badge from '@/components/ui/Badge'
import Alert from '@/components/ui/Alert'
import { STATUS_CONFIG } from '@/components/ui/Badge'
import { occurrenceService } from '@/services/occurrenceService'
import { getErrorMessage } from '@/utils/errorHandler'

interface OccurrenceDetailsModalProps {
  occurrence: Occurrence | null
  isOpen: boolean
  onClose: () => void
  onStatusUpdated: (id: number, status: OccurrenceStatus) => void
}

export function OccurrenceDetailsModal({
  occurrence,
  isOpen,
  onClose,
  onStatusUpdated,
}: OccurrenceDetailsModalProps) {
  const [selectedStatus, setSelectedStatus] = useState<string>('')
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (occurrence) {
      setSelectedStatus(occurrence.status)
      setError(null)
    }
  }, [occurrence])

  if (!isOpen || !occurrence) return null

  const statusOptions = (Object.keys(STATUS_CONFIG) as OccurrenceStatus[]).map((status) => ({
    value: status,
    label: STATUS_CONFIG[status].label,
  }))

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    if (!selectedStatus || selectedStatus === occurrence.status) return

    try {
      setError(null)
      setIsSubmitting(true)
      await occurrenceService.updateStatus(occurrence.id, selectedStatus as OccurrenceStatus)
      onStatusUpdated(occurrence.id, selectedStatus as OccurrenceStatus)
      onClose()
    } catch (err) {
      setError(getErrorMessage(err))
    } finally {
      setIsSubmitting(false)
    }
  }

  const createdDate = new Date(occurrence.createdAt).toLocaleDateString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })

  const resolvedDate = occurrence.resolvedAt
    ? new Date(occurrence.resolvedAt).toLocaleDateString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : null

  return (
    <>
      <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={onClose} />
      <div className="fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-white rounded-lg shadow-lg p-8 max-w-2xl w-full mx-4 max-h-[90vh] overflow-y-auto">
        <div className="flex items-center justify-between mb-6">
          <h2 className="text-lg font-semibold text-black-900">Detalhes da Ocorrência</h2>
          <button onClick={onClose} className="text-black-500 hover:text-black-700">
            <X size={20} />
          </button>
        </div>

        {error && <Alert variant="error" onClose={() => setError(null)} className="mb-4">{error}</Alert>}

        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Read-only fields */}
          <div className="space-y-4">
            <div>
              <label className="block text-sm font-medium text-black-700 mb-1">Título</label>
              <p className="px-4 py-2 bg-black-50 rounded-md text-black-900">{occurrence.title}</p>
            </div>

            <div>
              <label className="block text-sm font-medium text-black-700 mb-1">Descrição</label>
              <p className="px-4 py-2 bg-black-50 rounded-md text-black-900 whitespace-pre-wrap">
                {occurrence.description}
              </p>
            </div>

            <div>
              <label className="block text-sm font-medium text-black-700 mb-1">Localização</label>
              <p className="px-4 py-2 bg-black-50 rounded-md text-black-900">{occurrence.location}</p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-black-700 mb-1">Categoria</label>
                <p className="px-4 py-2 bg-black-50 rounded-md text-black-900">
                  {occurrence.categoryName}
                </p>
              </div>
              <div>
                <label className="block text-sm font-medium text-black-700 mb-1">Subcategoria</label>
                <p className="px-4 py-2 bg-black-50 rounded-md text-black-900">
                  {occurrence.subcategoryName}
                </p>
              </div>
            </div>

            <div>
              <label className="block text-sm font-medium text-black-700 mb-1">Solicitante</label>
              <p className="px-4 py-2 bg-black-50 rounded-md text-black-900">{occurrence.ownerName}</p>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <div>
                <label className="block text-sm font-medium text-black-700 mb-1">Criada em</label>
                <p className="px-4 py-2 bg-black-50 rounded-md text-black-900 text-sm">{createdDate}</p>
              </div>
              {resolvedDate && (
                <div>
                  <label className="block text-sm font-medium text-black-700 mb-1">Resolvida em</label>
                  <p className="px-4 py-2 bg-black-50 rounded-md text-black-900 text-sm">{resolvedDate}</p>
                </div>
              )}
            </div>

            <div>
              <label className="block text-sm font-medium text-black-700 mb-1">Status Atual</label>
              <div className="px-4 py-2 bg-black-50 rounded-md">
                <Badge status={occurrence.status} />
              </div>
            </div>
          </div>

          {/* Status update section */}
          <div className="border-t border-black-100 pt-6">
            <Select
              label="Novo Status"
              options={statusOptions}
              value={selectedStatus}
              onChange={(e) => setSelectedStatus(e.target.value)}
            />

            <div className="flex gap-3 pt-4">
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
                disabled={isSubmitting || selectedStatus === occurrence.status}
                isLoading={isSubmitting}
              >
                Salvar
              </Button>
            </div>
          </div>
        </form>
      </div>
    </>
  )
}

export default OccurrenceDetailsModal

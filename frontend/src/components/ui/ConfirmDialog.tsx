import { ReactNode } from 'react'
import { AlertTriangle, X } from 'lucide-react'

interface ConfirmDialogProps {
  isOpen: boolean
  title: string
  children: ReactNode
  confirmLabel?: string
  isLoading?: boolean
  onConfirm: () => void
  onCancel: () => void
}

function ConfirmDialog({
  isOpen,
  title,
  children,
  confirmLabel = 'Confirmar',
  isLoading = false,
  onConfirm,
  onCancel,
}: ConfirmDialogProps) {
  if (!isOpen) return null

  const close = () => {
    if (!isLoading) onCancel()
  }

  return (
    <>
      <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={close} />
      <div
        role="dialog"
        aria-modal="true"
        aria-labelledby="confirm-dialog-title"
        className="fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-white rounded-lg shadow-lg p-8 max-w-md w-full mx-4"
      >
        <div className="flex items-center justify-between mb-4">
          <h2 id="confirm-dialog-title" className="flex items-center gap-2 text-lg font-semibold text-black-900">
            <AlertTriangle size={20} className="text-brand-orange" />
            {title}
          </h2>
          <button onClick={close} className="text-black-500 hover:text-black-700" aria-label="Fechar">
            <X size={20} />
          </button>
        </div>

        <div className="mb-6 text-sm text-black-700">{children}</div>

        <div className="flex gap-3">
          <button
            type="button"
            onClick={close}
            disabled={isLoading}
            className="h-12 flex-1 rounded-md bg-[#b4b4b4] font-roboto text-sm font-bold text-white transition-colors hover:bg-opacity-90 disabled:opacity-60"
          >
            Cancelar
          </button>
          <button
            type="button"
            onClick={onConfirm}
            disabled={isLoading}
            className="h-12 flex-1 rounded-md bg-brand-navy font-roboto text-sm font-bold text-white transition-colors hover:bg-opacity-90 disabled:opacity-60"
          >
            {isLoading ? 'Aguarde...' : confirmLabel}
          </button>
        </div>
      </div>
    </>
  )
}

export default ConfirmDialog

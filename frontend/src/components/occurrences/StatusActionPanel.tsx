import { useState } from 'react'
import { CheckCircle2, RotateCcw, XCircle } from 'lucide-react'
import { OccurrenceStatus } from '@/types/occurrence'
import Badge from '@/components/ui/Badge'
import Textarea from '@/components/ui/Textarea'
import Alert from '@/components/ui/Alert'

interface StatusActionPanelProps {
  status: OccurrenceStatus
  isSubmitting: boolean
  error: string | null
  onClearError: () => void
  /** Retorna true se a mudança foi salva; em caso de erro o painel mantém a ação e o motivo */
  onSubmit: (status: OccurrenceStatus, reason: string) => Promise<boolean>
}

interface Action {
  target: OccurrenceStatus
  label: string
  description: string
  icon: typeof CheckCircle2
  // Cada ação tem uma cor própria para não confundir resolver com encerrar
  classes: string
}

const RESOLVE: Action = {
  target: 'RESOLVIDO',
  label: 'Marcar como resolvida',
  description: 'O problema foi solucionado.',
  icon: CheckCircle2,
  classes: 'border-status-resolvedText text-status-resolvedText hover:bg-status-resolvedBg',
}

const CLOSE: Action = {
  target: 'FECHADO',
  label: 'Encerrar sem resolução',
  description: 'Fora do escopo, duplicada ou improcedente.',
  icon: XCircle,
  classes: 'border-status-closedText text-status-closedText hover:bg-status-closedBg',
}

const REOPEN: Action = {
  target: 'ABERTO',
  label: 'Reabrir ocorrência',
  description: 'Volta para a fila de tratamento.',
  icon: RotateCcw,
  classes: 'border-status-openText text-status-openText hover:bg-status-openBg',
}

/* Ações possíveis a partir do status atual (o backend aceita ABERTO, RESOLVIDO e FECHADO) */
const actionsFor = (status: OccurrenceStatus): Action[] =>
  status === 'RESOLVIDO' || status === 'FECHADO' ? [REOPEN] : [RESOLVE, CLOSE]

export function StatusActionPanel({ status, isSubmitting, error, onClearError, onSubmit }: StatusActionPanelProps) {
  const [selected, setSelected] = useState<Action | null>(null)
  const [reason, setReason] = useState('')

  const cancel = () => {
    setSelected(null)
    setReason('')
    onClearError()
  }

  const confirm = async () => {
    if (!selected) return
    const saved = await onSubmit(selected.target, reason.trim())
    if (saved) {
      setSelected(null)
      setReason('')
    }
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <span className="text-sm text-[#6c757d]">Status atual</span>
        <Badge status={status} />
      </div>

      {error && <Alert variant="error" onClose={onClearError}>{error}</Alert>}

      {!selected ? (
        <div className="space-y-2">
          {actionsFor(status).map((action) => {
            const Icon = action.icon
            return (
              <button
                key={action.target}
                type="button"
                onClick={() => setSelected(action)}
                className={`flex w-full items-start gap-3 rounded-md border-2 bg-white px-4 py-3 text-left transition-colors ${action.classes}`}
              >
                <Icon size={20} className="mt-0.5 shrink-0" />
                <span>
                  <span className="block font-roboto font-bold">{action.label}</span>
                  <span className="block text-xs text-[#6c757d]">{action.description}</span>
                </span>
              </button>
            )
          })}
        </div>
      ) : (
        <div className="space-y-4">
          <p className="text-sm text-[#212529]">
            Confirmar: <strong>{selected.label.toLowerCase()}</strong>
          </p>
          <Textarea
            label="Motivo (opcional)"
            placeholder={
              selected.target === 'FECHADO'
                ? 'Explique por que a ocorrência foi encerrada sem resolução'
                : 'Descreva o que foi feito'
            }
            value={reason}
            onChange={(e) => setReason(e.target.value)}
            maxLength={1000}
            className="min-h-24"
          />
          <p className="form-text">O motivo fica registrado no histórico da ocorrência.</p>
          <div className="flex gap-2">
            <button
              type="button"
              onClick={cancel}
              disabled={isSubmitting}
              className="h-10 flex-1 rounded-md bg-[#b4b4b4] font-roboto text-sm font-bold text-white hover:bg-opacity-90 disabled:opacity-60"
            >
              Voltar
            </button>
            <button
              type="button"
              onClick={confirm}
              disabled={isSubmitting}
              className="h-10 flex-1 rounded-md bg-brand-navy font-roboto text-sm font-bold text-white hover:bg-opacity-90 disabled:opacity-60"
            >
              {isSubmitting ? 'Salvando...' : 'Confirmar'}
            </button>
          </div>
        </div>
      )}
    </div>
  )
}

export default StatusActionPanel

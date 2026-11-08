import { OccurrenceStatus, OccurrenceStatusHistory } from '@/types/occurrence'
import { STATUS_CONFIG } from '@/components/ui/Badge'
import { formatAuthor } from '@/utils/formatAuthor'

interface StatusTimelineProps {
  events: OccurrenceStatusHistory[]
}

// Cor do marcador de cada evento, pelos tokens de status do tema
const DOT_COLOR: Record<OccurrenceStatus, string> = {
  ABERTO: 'bg-status-openText',
  EM_ANALISE: 'bg-status-analysisText',
  RESOLVIDO: 'bg-status-resolvedText',
  FECHADO: 'bg-status-closedText',
}

const formatDateTime = (value: string) =>
  new Date(value).toLocaleString('pt-BR', {
    day: '2-digit',
    month: '2-digit',
    year: 'numeric',
    hour: '2-digit',
    minute: '2-digit',
  })

const label = (status: OccurrenceStatus) => STATUS_CONFIG[status]?.label ?? status

/* Histórico em linha do tempo: do mais recente para o mais antigo */
export function StatusTimeline({ events }: StatusTimelineProps) {
  if (events.length === 0) {
    return <p className="text-sm text-[#6c757d]">Nenhum evento registrado.</p>
  }

  const ordered = [...events].reverse()

  return (
    <ol className="relative ml-2 border-l-2 border-[#dee2e6]">
      {ordered.map((event, index) => (
        <li key={event.id} className={`relative pl-6 ${index < ordered.length - 1 ? 'pb-6' : ''}`}>
          <span
            className={`absolute -left-[9px] top-1 h-4 w-4 rounded-full ring-4 ring-white ${DOT_COLOR[event.newStatus]}`}
            aria-hidden="true"
          />
          <p className="font-medium text-[#212529]">
            {event.previousStatus
              ? `${label(event.previousStatus)} → ${label(event.newStatus)}`
              : `Ocorrência registrada (${label(event.newStatus)})`}
          </p>
          <p className="text-xs text-[#6c757d]">
            {formatDateTime(event.changedAt)} · {formatAuthor(event.changedByName)}
          </p>
          {event.reason && (
            <blockquote className="mt-2 whitespace-pre-wrap rounded-md border-l-4 border-[#dee2e6] bg-white px-3 py-2 text-sm text-[#212529]">
              {event.reason}
            </blockquote>
          )}
        </li>
      ))}
    </ol>
  )
}

export default StatusTimeline

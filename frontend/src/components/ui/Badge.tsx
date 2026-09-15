import { OccurrenceStatus } from '@/types/occurrence'

const STATUS_CONFIG: Record<OccurrenceStatus, { bg: string; text: string; label: string }> = {
  ABERTO: {
    bg: 'bg-status-openBg',
    text: 'text-status-openText',
    label: 'Aberto',
  },
  EM_ANALISE: {
    bg: 'bg-status-analysisBg',
    text: 'text-status-analysisText',
    label: 'Em Análise',
  },
  RESOLVIDO: {
    bg: 'bg-status-resolvedBg',
    text: 'text-status-resolvedText',
    label: 'Resolvido',
  },
  FECHADO: {
    bg: 'bg-status-closedBg',
    text: 'text-status-closedText',
    label: 'Fechado',
  },
}

interface BadgeProps {
  status: OccurrenceStatus
}

export function Badge({ status }: BadgeProps) {
  const config = STATUS_CONFIG[status]

  return (
    <div className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${config.bg} ${config.text}`}>
      <div className="h-2 w-2 rounded-full" style={{ backgroundColor: 'currentColor' }} />
      <span className="text-xs font-medium">{config.label}</span>
    </div>
  )
}

export default Badge

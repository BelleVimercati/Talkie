import { CategoryPriority } from '@/types/category'

const PRIORITY_CONFIG: Record<CategoryPriority, { bg: string; text: string; label: string }> = {
  BAIXA: {
    bg: 'bg-status-resolvedBg',
    text: 'text-status-resolvedText',
    label: 'Baixa',
  },
  MEDIA: {
    bg: 'bg-status-analysisBg',
    text: 'text-status-analysisText',
    label: 'Média',
  },
  ALTA: {
    bg: 'bg-status-closedBg',
    text: 'text-status-closedText',
    label: 'Alta',
  },
}

interface PriorityBadgeProps {
  priority: CategoryPriority
}

export function PriorityBadge({ priority }: PriorityBadgeProps) {
  const config = PRIORITY_CONFIG[priority]

  return (
    <div className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${config.bg} ${config.text}`}>
      <div className="h-2 w-2 rounded-full" style={{ backgroundColor: 'currentColor' }} />
      <span className="text-xs font-medium">{config.label}</span>
    </div>
  )
}

export default PriorityBadge

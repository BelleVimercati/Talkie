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

const FALLBACK_CONFIG = {
  bg: 'bg-black-50',
  text: 'text-black-500',
  label: '—',
}

interface PriorityBadgeProps {
  priority?: CategoryPriority | null
}

export function PriorityBadge({ priority }: PriorityBadgeProps) {
  const config = priority ? PRIORITY_CONFIG[priority] : null
  const finalConfig = config || FALLBACK_CONFIG

  return (
    <div className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${finalConfig.bg} ${finalConfig.text}`}>
      <div className="h-2 w-2 rounded-full" style={{ backgroundColor: 'currentColor' }} />
      <span className="text-xs font-medium">{finalConfig.label}</span>
    </div>
  )
}

export default PriorityBadge

const ROLE_CONFIG: Record<'USER' | 'ADMIN', { bg: string; text: string; label: string }> = {
  USER: {
    bg: 'bg-status-resolvedBg',
    text: 'text-status-resolvedText',
    label: 'Usuário',
  },
  ADMIN: {
    bg: 'bg-status-closedBg',
    text: 'text-status-closedText',
    label: 'Administrador',
  },
}

const FALLBACK_CONFIG = {
  bg: 'bg-black-50',
  text: 'text-black-500',
  label: '—',
}

interface RoleBadgeProps {
  role?: 'USER' | 'ADMIN' | null
}

export function RoleBadge({ role }: RoleBadgeProps) {
  const config = role ? ROLE_CONFIG[role] : null
  const finalConfig = config || FALLBACK_CONFIG

  return (
    <div className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 ${finalConfig.bg} ${finalConfig.text}`}>
      <div className="h-2 w-2 rounded-full" style={{ backgroundColor: 'currentColor' }} />
      <span className="text-xs font-medium">{finalConfig.label}</span>
    </div>
  )
}

export default RoleBadge

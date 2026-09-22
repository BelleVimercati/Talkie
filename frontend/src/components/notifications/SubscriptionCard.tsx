import { Pencil, Trash2 } from 'lucide-react'

interface SubscriptionCardProps {
  icon: string
  categoryName: string
  onEdit: () => void
  onRemove: () => void
}

export function SubscriptionCard({ icon, categoryName, onEdit, onRemove }: SubscriptionCardProps) {
  return (
    <div className="flex items-center justify-between gap-3 p-4 bg-white rounded-lg border border-black-100 shadow-sm hover:shadow-md transition-shadow">
      <div className="flex items-center gap-3 flex-1 min-w-0">
        <div className="text-2xl">{icon}</div>
        <span className="text-sm font-medium text-black-900 truncate">{categoryName}</span>
      </div>
      <div className="flex gap-2 flex-shrink-0">
        <button
          onClick={onEdit}
          className="p-2 text-black-500 hover:text-brand-blue hover:bg-blue-50 rounded transition-colors"
          title="Editar inscrição"
        >
          <Pencil size={16} />
        </button>
        <button
          onClick={onRemove}
          className="p-2 text-black-500 hover:text-red-500 hover:bg-red-50 rounded transition-colors"
          title="Remover inscrição"
        >
          <Trash2 size={16} />
        </button>
      </div>
    </div>
  )
}

export default SubscriptionCard

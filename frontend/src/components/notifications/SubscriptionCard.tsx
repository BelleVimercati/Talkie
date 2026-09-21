interface SubscriptionCardProps {
  icon: string
  categoryName: string
}

export function SubscriptionCard({ icon, categoryName }: SubscriptionCardProps) {
  return (
    <div className="flex items-center gap-3 p-4 bg-white rounded-lg border border-black-100 shadow-sm hover:shadow-md transition-shadow">
      <div className="text-2xl">{icon}</div>
      <span className="text-sm font-medium text-black-900">{categoryName}</span>
    </div>
  )
}

export default SubscriptionCard

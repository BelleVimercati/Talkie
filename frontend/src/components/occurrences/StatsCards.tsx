import { Card } from '@/components/ui/Card'
import { Occurrence } from '@/types/occurrence'
import { Folder, Clock, CheckCircle2, XCircle } from 'lucide-react'

interface StatsCardsProps {
  occurrences: Occurrence[]
}

export function StatsCards({ occurrences }: StatsCardsProps) {
  const total = occurrences.length
  const pendentes = occurrences.filter(
    (o) => o.status === 'ABERTO' || o.status === 'EM_ANALISE'
  ).length
  const resolvidas = occurrences.filter((o) => o.status === 'RESOLVIDO').length
  const fechadas = occurrences.filter((o) => o.status === 'FECHADO').length

  const stats = [
    {
      label: 'Total de Ocorrências',
      value: total,
      icon: Folder,
      bgColor: 'bg-blue-50',
      iconColor: 'text-brand-blue',
    },
    {
      label: 'Pendentes',
      value: pendentes,
      icon: Clock,
      bgColor: 'bg-yellow-50',
      iconColor: 'text-yellow-600',
    },
    {
      label: 'Resolvidas',
      value: resolvidas,
      icon: CheckCircle2,
      bgColor: 'bg-green-50',
      iconColor: 'text-green-600',
    },
    {
      label: 'Fechadas',
      value: fechadas,
      icon: XCircle,
      bgColor: 'bg-red-50',
      iconColor: 'text-red-600',
    },
  ]

  return (
    <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
      {stats.map((stat) => {
        const Icon = stat.icon
        return (
          <Card key={stat.label}>
            <div className="flex items-start justify-between">
              <div>
                <p className="text-sm text-black-500 font-medium">{stat.label}</p>
                <p className="mt-2 text-3xl font-bold text-black-900">{stat.value}</p>
              </div>
              <div className={`p-3 rounded-full ${stat.bgColor}`}>
                <Icon className={`h-6 w-6 ${stat.iconColor}`} />
              </div>
            </div>
          </Card>
        )
      })}
    </div>
  )
}

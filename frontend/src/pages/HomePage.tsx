import { useState } from 'react'
import { AppLayout } from '@/components/layout/AppLayout'
import { StatsCards } from '@/components/occurrences/StatsCards'
import { OccurrencesTable } from '@/components/occurrences/OccurrencesTable'
import Alert from '@/components/ui/Alert'
import { OccurrenceDetailsModal } from '@/components/occurrences/OccurrenceDetailsModal'
import { useOccurrences } from '@/hooks/useOccurrences'
import { useAuthStore } from '@/stores/authStore'
import { Occurrence } from '@/types/occurrence'

function HomePage() {
  const { user } = useAuthStore()
  const { occurrences, isLoading, error, updateOccurrenceStatus } = useOccurrences()
  const [selectedOccurrence, setSelectedOccurrence] = useState<Occurrence | null>(null)

  const isAdmin = user?.role === 'ADMIN'

  return (
    <AppLayout>
      <div className="p-8">
        <h1 className="mb-8 text-3xl font-bold font-roboto text-black-900">
          {isAdmin ? 'Todas as Ocorrências' : 'Minhas Ocorrências'}
        </h1>

        {error && <Alert variant="error">{error}</Alert>}

        {/* Overview Section */}
        <div className="mb-8">
          <h2 className="mb-4 text-lg font-medium text-black-500">Visão Geral</h2>
          <StatsCards occurrences={occurrences} />
        </div>

        {/* Registros Section */}
        <div>
          <h2 className="mb-4 text-lg font-medium text-black-500">Todos os registros</h2>
          <OccurrencesTable
            occurrences={occurrences}
            isLoading={isLoading}
            onRowClick={isAdmin ? setSelectedOccurrence : undefined}
          />
        </div>

        {/* Modal */}
        <OccurrenceDetailsModal
          occurrence={selectedOccurrence}
          isOpen={!!selectedOccurrence}
          onClose={() => setSelectedOccurrence(null)}
          onStatusUpdated={updateOccurrenceStatus}
        />
      </div>
    </AppLayout>
  )
}

export default HomePage

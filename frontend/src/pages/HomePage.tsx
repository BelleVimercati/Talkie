import { AppLayout } from '@/components/layout/AppLayout'
import { StatsCards } from '@/components/occurrences/StatsCards'
import { OccurrencesTable } from '@/components/occurrences/OccurrencesTable'
import Alert from '@/components/ui/Alert'
import { useOccurrences } from '@/hooks/useOccurrences'

function HomePage() {
  const { occurrences, isLoading, error } = useOccurrences()

  return (
    <AppLayout>
      <div className="p-8">
        <h1 className="mb-8 text-3xl font-bold font-roboto text-black-900">
          Minhas Ocorrências
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
          <OccurrencesTable occurrences={occurrences} isLoading={isLoading} />
        </div>
      </div>
    </AppLayout>
  )
}

export default HomePage

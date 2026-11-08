import { useCallback, useEffect, useState } from 'react'
import { useNavigate, useParams } from 'react-router-dom'
import { ArrowLeft, ExternalLink } from 'lucide-react'
import { AppLayout } from '@/components/layout/AppLayout'
import Card from '@/components/ui/Card'
import Alert from '@/components/ui/Alert'
import Badge from '@/components/ui/Badge'
import { LocationPicker } from '@/components/occurrences/LocationPicker'
import { OccurrenceImage } from '@/components/occurrences/OccurrenceImage'
import { StatusTimeline } from '@/components/occurrences/StatusTimeline'
import { StatusActionPanel } from '@/components/occurrences/StatusActionPanel'
import { occurrenceService } from '@/services/occurrenceService'
import { Occurrence, OccurrenceStatus, OccurrenceStatusHistory } from '@/types/occurrence'
import { getErrorMessage } from '@/utils/errorHandler'
import { formatAuthor } from '@/utils/formatAuthor'

const formatDateTime = (value?: string | null) =>
  value
    ? new Date(value).toLocaleString('pt-BR', {
        day: '2-digit',
        month: '2-digit',
        year: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
      })
    : '—'

function Field({ label, children }: { label: string; children: React.ReactNode }) {
  return (
    <div>
      <dt className="text-sm text-[#6c757d]">{label}</dt>
      <dd className="mt-0.5 text-[#212529]">{children}</dd>
    </div>
  )
}

function SectionTitle({ children }: { children: React.ReactNode }) {
  return <h2 className="mb-4 font-roboto text-lg font-bold text-[#212529]">{children}</h2>
}

/* Página do admin para analisar uma ocorrência (detalhes, imagem, mapa) e tratar o status */
function OccurrenceDetailsPage() {
  const navigate = useNavigate()
  const { id } = useParams()
  const occurrenceId = Number(id)

  const [occurrence, setOccurrence] = useState<Occurrence | null>(null)
  const [history, setHistory] = useState<OccurrenceStatusHistory[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [loadError, setLoadError] = useState<string | null>(null)
  const [historyError, setHistoryError] = useState<string | null>(null)
  const [isSubmitting, setIsSubmitting] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)

  const loadHistory = useCallback(async () => {
    try {
      setHistoryError(null)
      setHistory(await occurrenceService.getHistory(occurrenceId))
    } catch (err) {
      setHistoryError(getErrorMessage(err))
    }
  }, [occurrenceId])

  useEffect(() => {
    if (!Number.isFinite(occurrenceId)) {
      setLoadError('Ocorrência não encontrada')
      setIsLoading(false)
      return
    }
    setIsLoading(true)
    occurrenceService
      .getById(occurrenceId)
      .then(setOccurrence)
      .catch((err) => setLoadError(getErrorMessage(err)))
      .finally(() => setIsLoading(false))
    loadHistory()
  }, [occurrenceId, loadHistory])

  const handleStatusChange = async (status: OccurrenceStatus, reason: string) => {
    try {
      setActionError(null)
      setIsSubmitting(true)
      const updated = await occurrenceService.updateStatus(occurrenceId, status, reason || undefined)
      setOccurrence(updated)
      await loadHistory()
      return true
    } catch (err) {
      setActionError(getErrorMessage(err))
      return false
    } finally {
      setIsSubmitting(false)
    }
  }

  const hasCoordinates = occurrence?.latitude != null && occurrence?.longitude != null

  return (
    <AppLayout>
      <div className="p-8">
        <div className="mb-6">
          <button
            type="button"
            onClick={() => navigate('/')}
            className="mb-3 flex items-center gap-1 text-sm text-brand-blue hover:underline"
          >
            <ArrowLeft size={16} /> Voltar para as ocorrências
          </button>

          {occurrence && (
            <div className="flex flex-wrap items-center gap-3">
              <h1 className="text-3xl font-bold font-roboto text-black-900">{occurrence.title}</h1>
              <Badge status={occurrence.status} />
            </div>
          )}
          {occurrence && (
            <p className="mt-1 text-sm text-[#6c757d]">
              Ocorrência #{occurrence.id} · {occurrence.categoryName} / {occurrence.subcategoryName}
            </p>
          )}
        </div>

        {isLoading ? (
          <div className="p-8 text-center">
            <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-brand-blue border-t-transparent" />
            <p className="mt-2 text-black-500">Carregando...</p>
          </div>
        ) : loadError || !occurrence ? (
          <Alert variant="error">{loadError ?? 'Ocorrência não encontrada'}</Alert>
        ) : (
          <div className="grid grid-cols-1 gap-6 lg:grid-cols-3">
            {/* Coluna principal: o que foi reportado */}
            <div className="space-y-6 lg:col-span-2">
              <Card>
                <SectionTitle>Descrição</SectionTitle>
                <p className="whitespace-pre-wrap text-[#212529]">{occurrence.description}</p>

                <dl className="mt-6 grid grid-cols-1 gap-4 border-t border-[#dee2e6] pt-6 sm:grid-cols-2">
                  <Field label="Solicitante">{formatAuthor(occurrence.ownerName)}</Field>
                  <Field label="Data e hora do fato">{formatDateTime(occurrence.occurredAt)}</Field>
                  <Field label="Registrada em">{formatDateTime(occurrence.createdAt)}</Field>
                  <Field label="Resolvida em">{formatDateTime(occurrence.resolvedAt)}</Field>
                </dl>
              </Card>

              <Card>
                <SectionTitle>Imagem</SectionTitle>
                <OccurrenceImage occurrenceId={occurrence.id} hasImage={!!occurrence.imageUrl} />
              </Card>

              <Card>
                <SectionTitle>Localização</SectionTitle>
                <p className="mb-3 text-[#212529]">{occurrence.location}</p>
                {hasCoordinates ? (
                  <>
                    <LocationPicker latitude={occurrence.latitude} longitude={occurrence.longitude} readOnly />
                    <a
                      href={`https://www.openstreetmap.org/?mlat=${occurrence.latitude}&mlon=${occurrence.longitude}#map=18/${occurrence.latitude}/${occurrence.longitude}`}
                      target="_blank"
                      rel="noreferrer"
                      className="mt-2 inline-flex items-center gap-1 text-sm text-brand-blue hover:underline"
                    >
                      Abrir no OpenStreetMap <ExternalLink size={14} />
                    </a>
                  </>
                ) : (
                  <p className="text-sm text-[#6c757d]">O solicitante não marcou o ponto no mapa.</p>
                )}
              </Card>
            </div>

            {/* Coluna lateral: tratamento e histórico */}
            <div className="space-y-6">
              <Card className="border-t-4 border-brand-navy lg:sticky lg:top-6">
                <SectionTitle>Tratar ocorrência</SectionTitle>
                <StatusActionPanel
                  status={occurrence.status}
                  isSubmitting={isSubmitting}
                  error={actionError}
                  onClearError={() => setActionError(null)}
                  onSubmit={handleStatusChange}
                />
              </Card>

              <Card>
                <SectionTitle>Histórico</SectionTitle>
                {historyError ? (
                  <p className="invalid-feedback">{historyError}</p>
                ) : (
                  <StatusTimeline events={history} />
                )}
              </Card>
            </div>
          </div>
        )}
      </div>
    </AppLayout>
  )
}

export default OccurrenceDetailsPage

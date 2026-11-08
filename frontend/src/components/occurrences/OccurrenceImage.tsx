import { useEffect, useState } from 'react'
import { ImageOff, Maximize2, X } from 'lucide-react'
import { occurrenceService } from '@/services/occurrenceService'
import { getErrorMessage } from '@/utils/errorHandler'

interface OccurrenceImageProps {
  occurrenceId: number
  hasImage: boolean
}

/*
 * A imagem exige o token JWT, então não dá para usar <img src="/occurrences/:id/image">:
 * baixa como blob pelo service e exibe por um object URL (liberado ao desmontar).
 */
export function OccurrenceImage({ occurrenceId, hasImage }: OccurrenceImageProps) {
  const [url, setUrl] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(hasImage)
  const [isExpanded, setIsExpanded] = useState(false)

  useEffect(() => {
    if (!hasImage) return
    let objectUrl: string | null = null
    let active = true

    setIsLoading(true)
    occurrenceService
      .getImage(occurrenceId)
      .then((blob) => {
        if (!active) return
        objectUrl = URL.createObjectURL(blob)
        setUrl(objectUrl)
      })
      .catch((err) => active && setError(getErrorMessage(err)))
      .finally(() => active && setIsLoading(false))

    return () => {
      active = false
      if (objectUrl) URL.revokeObjectURL(objectUrl)
    }
  }, [occurrenceId, hasImage])

  useEffect(() => {
    if (!isExpanded) return
    const onKey = (e: KeyboardEvent) => e.key === 'Escape' && setIsExpanded(false)
    window.addEventListener('keydown', onKey)
    return () => window.removeEventListener('keydown', onKey)
  }, [isExpanded])

  if (!hasImage) {
    return (
      <div className="flex h-40 flex-col items-center justify-center gap-2 rounded-md border border-dashed border-[#dee2e6] text-[#6c757d]">
        <ImageOff size={28} />
        <p className="text-sm">Nenhuma imagem enviada</p>
      </div>
    )
  }

  if (isLoading) {
    return <div className="h-64 w-full animate-pulse rounded-md bg-black-100" />
  }

  if (error || !url) {
    return <p className="invalid-feedback">{error ?? 'Não foi possível carregar a imagem.'}</p>
  }

  return (
    <>
      <button
        type="button"
        onClick={() => setIsExpanded(true)}
        className="group relative block w-full overflow-hidden rounded-md border border-[#dee2e6]"
        aria-label="Ampliar imagem"
      >
        <img src={url} alt="Imagem enviada com a ocorrência" className="max-h-96 w-full object-contain bg-white" />
        <span className="absolute right-2 top-2 flex items-center gap-1 rounded-md bg-black bg-opacity-60 px-2 py-1 text-xs text-white opacity-0 transition-opacity group-hover:opacity-100">
          <Maximize2 size={14} /> Ampliar
        </span>
      </button>

      {isExpanded && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-black bg-opacity-80 p-4"
          onClick={() => setIsExpanded(false)}
          role="dialog"
          aria-modal="true"
          aria-label="Imagem ampliada"
        >
          <button
            type="button"
            className="absolute right-4 top-4 text-white hover:text-black-100"
            aria-label="Fechar"
            onClick={() => setIsExpanded(false)}
          >
            <X size={28} />
          </button>
          <img src={url} alt="Imagem enviada com a ocorrência" className="max-h-[90vh] max-w-full object-contain" />
        </div>
      )}
    </>
  )
}

export default OccurrenceImage

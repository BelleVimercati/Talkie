import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AppLayout } from '@/components/layout/AppLayout'
import Input from '@/components/ui/Input'
import Select from '@/components/ui/Select'
import Textarea from '@/components/ui/Textarea'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import { ImageUpload } from '@/components/occurrences/ImageUpload'
import { LocationPicker } from '@/components/occurrences/LocationPicker'
import { useCategories } from '@/hooks/useCategories'
import { useSubcategories } from '@/hooks/useSubcategories'
import { createOccurrenceSchema, type CreateOccurrenceFormData } from '@/schemas/createOccurrenceSchema'
import { occurrenceService } from '@/services/occurrenceService'
import { geocodingService } from '@/services/geocodingService'
import { getErrorMessage, getFieldFromBackendMessage } from '@/utils/errorHandler'

function CreateOccurrencePage() {
  const navigate = useNavigate()
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [imageFile, setImageFile] = useState<File | null>(null)
  const [isFetchingAddress, setIsFetchingAddress] = useState(false)
  const { categories } = useCategories()
  const { subcategories } = useSubcategories()

  const {
    register,
    control,
    handleSubmit,
    setError,
    watch,
    setValue,
    formState: { errors, dirtyFields },
  } = useForm<CreateOccurrenceFormData>({
    resolver: zodResolver(createOccurrenceSchema),
    defaultValues: {
      categoryId: 0,
      subcategoryId: 0,
    },
  })

  const selectedCategoryId = watch('categoryId')
  const latitude = watch('latitude')
  const longitude = watch('longitude')

  // Ponto escolhido no mapa (ou removido): atualiza as coordenadas e sugere o endereço
  const handleLocationChange = async (lat?: number, lng?: number) => {
    setValue('latitude', lat, { shouldValidate: true })
    setValue('longitude', lng, { shouldValidate: true })
    if (lat === undefined || lng === undefined) return

    // Endereço digitado à mão não é sobrescrito (setValue sem shouldDirty não marca o campo)
    if (dirtyFields.location) return

    setIsFetchingAddress(true)
    try {
      const address = await geocodingService.reverse(lat, lng)
      if (address) setValue('location', address, { shouldValidate: true })
    } catch {
      // Sem endereço automático: o usuário digita
    } finally {
      setIsFetchingAddress(false)
    }
  }

  // Atualizar subcategorias disponíveis quando categoria muda
  useEffect(() => {
    if (selectedCategoryId) {
      const selected = categories.find((c) => c.id === selectedCategoryId)
      if (selected) {
        // Filtrar subcategorias por categoria
        const filtered = subcategories.filter(
          (s) => s.categoryName === selected.name
        )
        if (filtered.length > 0) {
          setValue('subcategoryId', 0) // Reset
        }
      }
    } else {
      setValue('subcategoryId', 0)
    }
  }, [selectedCategoryId, categories, subcategories, setValue])

  const availableSubcategories = selectedCategoryId
    ? (() => {
        const selected = categories.find((c) => c.id === selectedCategoryId)
        if (!selected) return []
        return subcategories.filter((s) => s.categoryName === selected.name)
      })()
    : []

  const onSubmit = async (data: CreateOccurrenceFormData) => {
    try {
      setGlobalError(null)
      setIsLoading(true)

      const created = await occurrenceService.create(data)

      // Se houver imagem, fazer upload separado
      if (imageFile) {
        try {
          await occurrenceService.uploadImage(created.id, imageFile)
        } catch (err) {
          // Ocorrência foi criada, mas erro no upload de imagem
          setGlobalError('Ocorrência criada, mas falha ao enviar a imagem')
        }
      }

      navigate('/', { state: { created: true } })
    } catch (err) {
      const message = getErrorMessage(err)
      const field = getFieldFromBackendMessage(message)

      if (field) {
        setError(field as any, { message })
      } else {
        setGlobalError(message)
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <AppLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold font-roboto text-black-900">
            Criar uma nova ocorrência
          </h1>
          <p className="text-sm text-brand-blue">
            <button
              onClick={() => navigate('/')}
              className="hover:underline"
            >
              Minhas Ocorrências
            </button>
            {` > Nova Ocorrência`}
          </p>
        </div>

        {globalError && (
          <Alert variant="error" onClose={() => setGlobalError(null)}>
            {globalError}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="max-w-4xl space-y-6">
          {/* Grid de 2 colunas */}
          <div className="grid grid-cols-2 gap-6">
            {/* Título */}
            <Input
              label="Título*"
              placeholder="Título da ocorrência"
              error={errors.title?.message}
              {...register('title')}
            />

            {/* Data e Horário do Fato */}
            <Input
              label="Data e Horário do Fato (opcional)"
              type="datetime-local"
              error={errors.occurredAt?.message}
              {...register('occurredAt')}
            />

            {/* Categoria */}
            <Controller
              name="categoryId"
              control={control}
              render={({ field }) => (
                <Select
                  label="Categoria*"
                  error={errors.categoryId?.message}
                  options={categories.map((c) => ({
                    value: c.id,
                    label: c.name,
                  }))}
                  {...field}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                />
              )}
            />

            {/* Subcategoria */}
            <Controller
              name="subcategoryId"
              control={control}
              render={({ field }) => (
                <Select
                  label="Subcategoria*"
                  error={errors.subcategoryId?.message}
                  disabled={!selectedCategoryId}
                  options={availableSubcategories.map((s) => ({
                    value: s.subcategoryId,
                    label: s.name,
                  }))}
                  {...field}
                  onChange={(e) => field.onChange(Number(e.target.value))}
                />
              )}
            />

            {/* Localização */}
            <div className="col-span-2">
              <Input
                label="Localização*"
                placeholder="Endereço (preenchido pelo mapa ou digitado)"
                error={errors.location?.message}
                {...register('location')}
              />
              {isFetchingAddress && (
                <p className="mt-1 text-xs text-black-500">Buscando endereço...</p>
              )}
            </div>

            {/* Mapa */}
            <div className="col-span-2">
              <label className="mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03">
                Local no mapa (opcional)
              </label>
              <LocationPicker
                latitude={latitude}
                longitude={longitude}
                onChange={handleLocationChange}
              />
              {(errors.latitude || errors.longitude) && (
                <p className="mt-1 text-xs text-red-500">
                  {errors.latitude?.message ?? errors.longitude?.message}
                </p>
              )}
            </div>
          </div>

          {/* Imagem */}
          <ImageUpload
            value={imageFile}
            onChange={setImageFile}
          />

          {/* Descrição */}
          <Textarea
            label="Descrição*"
            placeholder="Descreva o problema encontrado..."
            error={errors.description?.message}
            {...register('description')}
          />

          {/* Texto informativo */}
          <p className="text-sm text-black-500">
            Usuários inscritos nesta categoria serão notificados automaticamente quando esta ocorrência for criada.
          </p>

          {/* Botões */}
          <div className="flex gap-3 justify-end">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate('/')}
              disabled={isLoading}
              className="w-auto px-8"
            >
              Cancelar
            </Button>
            <Button
              type="submit"
              variant="dashboard"
              isLoading={isLoading}
              disabled={isLoading}
              className="w-auto px-8"
            >
              Criar ocorrência
            </Button>
          </div>
        </form>
      </div>
    </AppLayout>
  )
}

export default CreateOccurrencePage

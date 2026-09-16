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
import { useCategories } from '@/hooks/useCategories'
import { useSubcategories } from '@/hooks/useSubcategories'
import { createOccurrenceSchema, type CreateOccurrenceFormData } from '@/schemas/createOccurrenceSchema'
import { occurrenceService } from '@/services/occurrenceService'
import { getErrorMessage, getFieldFromBackendMessage } from '@/utils/errorHandler'

function CreateOccurrencePage() {
  const navigate = useNavigate()
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const { categories } = useCategories()
  const { subcategories } = useSubcategories()

  const {
    register,
    control,
    handleSubmit,
    setError,
    watch,
    setValue,
    formState: { errors },
  } = useForm<CreateOccurrenceFormData>({
    resolver: zodResolver(createOccurrenceSchema),
    defaultValues: {
      categoryId: 0,
      subcategoryId: 0,
    },
  })

  const selectedCategoryId = watch('categoryId')

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

      await occurrenceService.create(data)

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

            {/* Data (decorativa, desabilitada) */}
            <Input
              label="Data*"
              type="date"
              disabled
              defaultValue="2020-12-12"
              className="opacity-50 cursor-not-allowed"
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
                />
              )}
            />

            {/* Localização */}
            <Input
              label="Localização*"
              placeholder="Endereço ou descrição de local"
              error={errors.location?.message}
              {...register('location')}
            />

            {/* Horário (decorativa, desabilitada) */}
            <Input
              label="Horário*"
              type="time"
              disabled
              defaultValue="12:00"
              className="opacity-50 cursor-not-allowed"
            />
          </div>

          {/* Toggle "usar localização e hora atuais" (decorativo) */}
          <div className="flex items-center gap-3 px-4 py-2 border border-black-100 rounded-md bg-black-50 opacity-50 cursor-not-allowed">
            <input
              type="checkbox"
              disabled
              className="w-4 h-4"
            />
            <span className="text-sm text-black-500">Usar Localização e hora atuais</span>
          </div>

          {/* Anexo (decorativo, desabilitado) */}
          <div className="opacity-50 cursor-not-allowed">
            <label className="mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03">
              Anexo
            </label>
            <div className="relative">
              <input
                type="file"
                disabled
                className="h-12 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 text-black-500"
              />
            </div>
          </div>

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

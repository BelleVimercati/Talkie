import { useEffect, useMemo, useState } from 'react'
import { useNavigate, useParams, useSearchParams } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AppLayout } from '@/components/layout/AppLayout'
import Input from '@/components/ui/Input'
import Select from '@/components/ui/Select'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import { useCategories } from '@/hooks/useCategories'
import { createSubcategorySchema, type CreateSubcategoryFormData } from '@/schemas/createSubcategorySchema'
import { subcategoryService } from '@/services/categoryService'
import { Subcategory } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'

function CreateSubcategoryPage() {
  const navigate = useNavigate()
  // Com :id na rota, a página edita a subcategoria; ?categoria= pré-seleciona a categoria na criação
  const { id } = useParams()
  const [searchParams] = useSearchParams()
  const subcategoryId = id ? Number(id) : null
  const isEditing = subcategoryId !== null
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const [editing, setEditing] = useState<Subcategory | null>(null)
  const { categories } = useCategories()

  const {
    register,
    control,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<CreateSubcategoryFormData>({
    resolver: zodResolver(createSubcategorySchema),
    defaultValues: {
      categoryId: Number(searchParams.get('categoria')) || 0,
    },
  })

  useEffect(() => {
    if (subcategoryId === null) return
    subcategoryService
      .getById(subcategoryId)
      .then((sub) => {
        setEditing(sub)
        reset({ name: sub.name, categoryId: sub.categoryId })
      })
      .catch((err) => setGlobalError(getErrorMessage(err)))
  }, [subcategoryId, reset])

  // Só categorias ativas; na edição, a categoria atual entra mesmo se estiver inativa
  const categoryOptions = useMemo(() => {
    const options = categories.map((c) => ({ value: c.id, label: c.name }))
    if (editing && !categories.some((c) => c.id === editing.categoryId)) {
      options.push({ value: editing.categoryId, label: `${editing.categoryName} (inativa)` })
    }
    return options
  }, [categories, editing])

  const onSubmit = async (data: CreateSubcategoryFormData) => {
    try {
      setGlobalError(null)
      setIsLoading(true)

      if (subcategoryId !== null) {
        await subcategoryService.update(subcategoryId, data)
      } else {
        await subcategoryService.create(data)
      }
      navigate('/configuracoes')
    } catch (err) {
      const message = getErrorMessage(err)
      setGlobalError(message)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <AppLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold font-roboto text-black-900">
            {isEditing ? 'Editar Subcategoria' : 'Nova Subcategoria'}
          </h1>
          <p className="text-sm text-brand-blue">
            <button
              onClick={() => navigate('/')}
              className="hover:underline"
            >
              Geral
            </button>
            {` > `}
            <button
              onClick={() => navigate('/configuracoes')}
              className="hover:underline"
            >
              Configurações
            </button>
            {` > Subcategorias`}
          </p>
        </div>

        {globalError && (
          <Alert variant="error" onClose={() => setGlobalError(null)}>
            {globalError}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="max-w-2xl space-y-6">
          {/* Categoria */}
          <Controller
            name="categoryId"
            control={control}
            render={({ field }) => (
              <Select
                label="Categoria*"
                error={errors.categoryId?.message}
                options={categoryOptions}
                {...field}
              />
            )}
          />

          {/* Nome da Subcategoria */}
          <Input
            label="Nome da subcategoria*"
            placeholder="Digite o nome da subcategoria"
            error={errors.name?.message}
            {...register('name')}
          />

          {/* Botões */}
          <div className="flex gap-3 justify-end">
            <Button
              type="button"
              variant="secondary"
              onClick={() => navigate('/configuracoes')}
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
              {isEditing ? 'Salvar alterações' : 'Criar subcategoria'}
            </Button>
          </div>
        </form>
      </div>
    </AppLayout>
  )
}

export default CreateSubcategoryPage

import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
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
import { getErrorMessage } from '@/utils/errorHandler'

function CreateSubcategoryPage() {
  const navigate = useNavigate()
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)
  const { categories } = useCategories()

  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateSubcategoryFormData>({
    resolver: zodResolver(createSubcategorySchema),
    defaultValues: {
      categoryId: 0,
    },
  })

  const onSubmit = async (data: CreateSubcategoryFormData) => {
    try {
      setGlobalError(null)
      setIsLoading(true)

      await subcategoryService.create(data)
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
            Nova Subcategoria
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
                options={categories.map((c) => ({
                  value: c.id,
                  label: c.name,
                }))}
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
              Criar subcategoria
            </Button>
          </div>
        </form>
      </div>
    </AppLayout>
  )
}

export default CreateSubcategoryPage

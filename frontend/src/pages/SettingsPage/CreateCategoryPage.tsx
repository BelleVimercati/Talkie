import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AppLayout } from '@/components/layout/AppLayout'
import Input from '@/components/ui/Input'
import Select from '@/components/ui/Select'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import { IconPicker } from '@/components/ui/IconPicker'
import { ColorPicker } from '@/components/ui/ColorPicker'
import { createCategorySchema, type CreateCategoryFormData } from '@/schemas/createCategorySchema'
import { categoryService } from '@/services/categoryService'
import { getErrorMessage } from '@/utils/errorHandler'

function CreateCategoryPage() {
  const navigate = useNavigate()
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    control,
    handleSubmit,
    formState: { errors },
  } = useForm<CreateCategoryFormData>({
    resolver: zodResolver(createCategorySchema),
    defaultValues: {
      priority: 'MEDIA',
    },
  })

  const onSubmit = async (data: CreateCategoryFormData) => {
    try {
      setGlobalError(null)
      setIsLoading(true)

      await categoryService.create(data)
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
            Nova Categoria
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
            {` > Categorias`}
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
            {/* Nome */}
            <Input
              label="Nome da categoria*"
              placeholder="Escolha um nome"
              error={errors.name?.message}
              {...register('name')}
            />

            {/* Prioridade */}
            <Controller
              name="priority"
              control={control}
              render={({ field }) => (
                <Select
                  label="Prioridade*"
                  error={errors.priority?.message}
                  options={[
                    { value: 'BAIXA', label: 'Baixa' },
                    { value: 'MEDIA', label: 'Média' },
                    { value: 'ALTA', label: 'Alta' },
                  ]}
                  {...field}
                />
              )}
            />
          </div>

          {/* Icon Picker */}
          <Controller
            name="icon"
            control={control}
            render={({ field }) => (
              <IconPicker
                value={field.value}
                onChange={field.onChange}
                error={errors.icon?.message}
              />
            )}
          />

          {/* Color Picker */}
          <Controller
            name="color"
            control={control}
            render={({ field }) => (
              <ColorPicker
                value={field.value}
                onChange={field.onChange}
                error={errors.color?.message}
              />
            )}
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
              Criar categoria
            </Button>
          </div>
        </form>
      </div>
    </AppLayout>
  )
}

export default CreateCategoryPage

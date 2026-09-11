import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm, Controller } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '@/components/layout/AuthLayout'
import Input from '@/components/ui/Input'
import PasswordInput from '@/components/ui/PasswordInput'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import Divider from '@/components/ui/Divider'
import { registerSchema, type RegisterFormData } from '@/schemas/registerSchema'
import { authService } from '@/services/authService'
import { formatCpf } from '@/utils/cpf'
import { getErrorMessage, getFieldFromBackendMessage } from '@/utils/errorHandler'

function RegisterPage() {
  const navigate = useNavigate()
  const [globalError, setGlobalError] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(false)

  const {
    register,
    control,
    handleSubmit,
    setError,
    formState: { errors },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  })

  const onSubmit = async (data: RegisterFormData) => {
    try {
      setGlobalError(null)
      setIsLoading(true)

      const { confirmPassword, ...payload } = data

      await authService.register(payload)

      navigate('/login', { state: { registered: true } })
    } catch (err) {
      const message = getErrorMessage(err)
      const field = getFieldFromBackendMessage(message)

      if (field) {
        setError(field, { message })
      } else {
        setGlobalError(message)
      }
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <AuthLayout>
      <div>
        <h1 className="mb-8 font-poppins text-4xl font-semibold text-black-900">
          Registre-se
        </h1>

        {globalError && (
          <Alert variant="error" onClose={() => setGlobalError(null)}>
            {globalError}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <Input
            label="Nome"
            placeholder="Nome Completo"
            error={errors.name?.message}
            {...register('name')}
          />

          <Controller
            name="cpf"
            control={control}
            render={({ field }) => (
              <Input
                label="CPF"
                placeholder="000.000.000-00"
                error={errors.cpf?.message}
                {...field}
                onChange={(e) => {
                  field.onChange(formatCpf(e.target.value))
                }}
                value={formatCpf(field.value || '')}
              />
            )}
          />

          <Input
            label="Email"
            placeholder="email@email.com"
            type="email"
            error={errors.email?.message}
            {...register('email')}
          />

          <PasswordInput
            label="Password"
            placeholder="••••••••••"
            error={errors.password?.message}
            {...register('password')}
          />

          <PasswordInput
            label="Confirm Password"
            placeholder="••••••••••"
            error={errors.confirmPassword?.message}
            {...register('confirmPassword')}
          />

          <Button type="submit" isLoading={isLoading}>
            Sign up
          </Button>
        </form>

        <Divider />

        <div className="text-center">
          <p className="text-sm text-black-900">
            Já tem uma conta?{' '}
            <button
              onClick={() => navigate('/login')}
              className="font-semibold text-brand-blue hover:underline"
            >
              Entrar
            </button>
          </p>
        </div>
      </div>
    </AuthLayout>
  )
}

export default RegisterPage

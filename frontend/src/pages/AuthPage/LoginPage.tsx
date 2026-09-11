import { useEffect, useState } from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import AuthLayout from '@/components/layout/AuthLayout'
import Input from '@/components/ui/Input'
import PasswordInput from '@/components/ui/PasswordInput'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import Divider from '@/components/ui/Divider'
import { loginSchema, type LoginFormData } from '@/schemas/loginSchema'
import { useAuthStore } from '@/stores/authStore'

function LoginPage() {
  const navigate = useNavigate()
  const location = useLocation()
  const [showSuccess, setShowSuccess] = useState(
    (location.state as any)?.registered ?? false
  )

  const { register, handleSubmit, formState: { errors } } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  })

  const { login, isLoading, error, logout, clearError } = useAuthStore()

  useEffect(() => {
    logout()
  }, [logout])

  const onSubmit = async (data: LoginFormData) => {
    try {
      clearError()
      await login(data.email, data.password)
      navigate('/')
    } catch {
      // Error is stored in the store
    }
  }

  return (
    <AuthLayout>
      <div>
        <h1 className="mb-8 font-poppins text-4xl font-semibold text-black-900">
          Bem vindo!
        </h1>

        {showSuccess && (
          <Alert variant="success" onClose={() => setShowSuccess(false)}>
            Conta criada com sucesso! Faça login.
          </Alert>
        )}

        {error && (
          <Alert variant="error" onClose={clearError}>
            {error}
          </Alert>
        )}

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-6">
          <Input
            label="Login"
            placeholder="Email or phone number"
            type="email"
            error={errors.email?.message}
            {...register('email')}
          />

          <PasswordInput
            label="Password"
            placeholder="Enter password"
            error={errors.password?.message}
            {...register('password')}
          />

          <Button type="submit" isLoading={isLoading}>
            Sign in
          </Button>
        </form>

        <Divider />

        <div className="text-center">
          <p className="text-sm text-black-900">
            Ainda não tem conta?{' '}
            <button
              onClick={() => navigate('/register')}
              className="font-semibold text-brand-blue hover:underline"
            >
              Cadastre-se
            </button>
          </p>
        </div>
      </div>
    </AuthLayout>
  )
}

export default LoginPage

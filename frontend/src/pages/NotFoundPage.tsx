import { useNavigate } from 'react-router-dom'
import Button from '@/components/ui/Button'

function NotFoundPage() {
  const navigate = useNavigate()

  return (
    <div className="flex min-h-screen items-center justify-center bg-gray-50 px-4">
      <div className="text-center">
        <h1 className="mb-4 text-6xl font-bold text-brand-blue">404</h1>
        <h2 className="mb-2 text-2xl font-semibold text-black-900">Página não encontrada</h2>
        <p className="mb-8 text-black-500">
          A página que você está procurando não existe.
        </p>
        <Button onClick={() => navigate('/login')} className="mx-auto w-full max-w-sm">
          Voltar para Login
        </Button>
      </div>
    </div>
  )
}

export default NotFoundPage

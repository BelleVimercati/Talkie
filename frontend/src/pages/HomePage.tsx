import { useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/stores/authStore'
import Button from '@/components/ui/Button'

function HomePage() {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  return (
    <div className="min-h-screen bg-gradient-to-br from-brand-blue to-brand-navy p-8">
      <div className="mx-auto max-w-md rounded-lg bg-white p-8 shadow-lg">
        <h1 className="mb-4 text-2xl font-bold text-black-900">Bem-vindo ao Talkie!</h1>
        <p className="mb-2 text-black-800">
          Você está logado como:
        </p>
        <p className="mb-6 break-all font-mono text-sm font-semibold text-brand-blue">
          {user?.email}
        </p>
        <p className="mb-6 text-sm text-black-500">
          Seu perfil: <span className="font-semibold text-black-800">{user?.role}</span>
        </p>
        <p className="mb-6 text-xs text-black-500">
          Esta página está em construção. O dashboard real será implementado em breve.
        </p>
        <div className="flex gap-3">
          <Button onClick={handleLogout} variant="primary">
            Logout
          </Button>
        </div>
      </div>
    </div>
  )
}

export default HomePage

import { useLocation, useNavigate } from 'react-router-dom'
import { useAuthStore } from '@/stores/authStore'
import { getInitials } from '@/utils/initials'
import { Grid, Bell, LogOut } from 'lucide-react'

export function Sidebar() {
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const isGeral = location.pathname === '/'

  return (
    <div className="h-screen w-64 bg-white shadow-md flex flex-col">
      {/* Logo */}
      <div className="px-6 py-6 border-b">
        <div className="flex items-center gap-2">
          <div className="text-xl font-bold font-logo text-brand-logo">Talkie</div>
        </div>
      </div>

      {/* User Profile */}
      <div className="px-6 py-6 border-b flex items-center gap-4">
        <div className="h-12 w-12 rounded-full bg-brand-blue text-white flex items-center justify-center font-bold text-sm">
          {user ? getInitials(user.email) : '?'}
        </div>
        <div className="flex-1 min-w-0">
          <p className="font-medium text-sm text-black-900">
            {user ? user.email.split('@')[0] : 'Usuário'}
          </p>
          <p className="text-xs text-brand-muted truncate">{user?.email}</p>
        </div>
      </div>

      {/* Menu Items */}
      <nav className="flex-1 px-4 py-6 space-y-2">
        {/* Geral */}
        <button
          onClick={() => navigate('/')}
          className={`w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 transition-colors ${
            isGeral
              ? 'bg-brand-orange bg-opacity-10 text-brand-orange'
              : 'text-black-700 hover:bg-black-50'
          }`}
        >
          <Grid size={18} />
          <span className="font-medium text-sm">Geral</span>
        </button>

        {/* Notificações */}
        <button className="w-full text-left px-4 py-3 rounded-lg flex items-center gap-3 text-black-700 hover:bg-black-50 transition-colors">
          <Bell size={18} />
          <span className="font-medium text-sm">Notificações</span>
        </button>
      </nav>

      {/* Logout Button */}
      <div className="px-4 py-6 border-t">
        <button
          onClick={handleLogout}
          className="w-full px-4 py-3 rounded-lg flex items-center gap-3 text-black-700 hover:bg-black-50 transition-colors"
        >
          <LogOut size={18} />
          <span className="font-medium text-sm">Sair</span>
        </button>
      </div>
    </div>
  )
}

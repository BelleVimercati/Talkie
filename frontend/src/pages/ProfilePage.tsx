import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useForm } from 'react-hook-form'
import { zodResolver } from '@hookform/resolvers/zod'
import { AlertTriangle, X } from 'lucide-react'
import { AppLayout } from '@/components/layout/AppLayout'
import Card from '@/components/ui/Card'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import PasswordInput from '@/components/ui/PasswordInput'
import { RoleBadge } from '@/components/ui/RoleBadge'
import { useAuthStore } from '@/stores/authStore'
import { userService } from '@/services/userService'
import { getErrorMessage } from '@/utils/errorHandler'
import { deleteAccountSchema, type DeleteAccountFormData } from '@/schemas/deleteAccountSchema'

const dangerButton =
  'h-12 rounded-md bg-red-600 px-6 font-roboto text-sm font-bold tracking-wide03 text-white transition-colors hover:bg-red-700 disabled:cursor-not-allowed disabled:opacity-60'

function ProfilePage() {
  const navigate = useNavigate()
  const { user, logout } = useAuthStore()
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [isDeleting, setIsDeleting] = useState(false)
  const [error, setError] = useState<string | null>(null)

  const {
    register,
    handleSubmit,
    reset,
    formState: { errors },
  } = useForm<DeleteAccountFormData>({
    resolver: zodResolver(deleteAccountSchema),
  })

  const closeModal = () => {
    if (isDeleting) return
    setIsModalOpen(false)
    setError(null)
    reset()
  }

  const onSubmit = async (data: DeleteAccountFormData) => {
    try {
      setError(null)
      setIsDeleting(true)
      await userService.deleteOwnAccount(data.password)
      logout()
      navigate('/login', { replace: true })
    } catch (err) {
      setError(getErrorMessage(err))
      setIsDeleting(false)
    }
  }

  return (
    <AppLayout>
      <div className="p-8 max-w-3xl">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold font-roboto text-black-900">Meu perfil</h1>
          <p className="text-sm text-brand-blue">
            <button onClick={() => navigate('/')} className="hover:underline">
              Geral
            </button>
            {` > Meu perfil`}
          </p>
        </div>

        <Card className="mb-6">
          <h2 className="mb-4 text-lg font-semibold text-black-900">Dados da conta</h2>
          <dl className="space-y-3 text-sm">
            <div>
              <dt className="font-medium text-black-700">E-mail</dt>
              <dd className="text-black-900">{user?.email}</dd>
            </div>
            <div>
              <dt className="mb-1 font-medium text-black-700">Perfil</dt>
              <dd>{user && <RoleBadge role={user.role} />}</dd>
            </div>
          </dl>
        </Card>

        <Card className="border border-red-200">
          <h2 className="mb-2 text-lg font-semibold text-red-700">Zona de perigo</h2>
          <p className="mb-4 text-sm text-black-700">
            Ao excluir sua conta, seus dados pessoais (nome, e-mail, CPF e senha) são apagados e você
            perde o acesso ao Talkie. As ocorrências que você registrou continuam existindo para que a
            equipe possa tratá-las, mas passam a aparecer como <strong>Usuário excluído</strong>.
            Esta ação não pode ser desfeita.
          </p>
          <button type="button" className={dangerButton} onClick={() => setIsModalOpen(true)}>
            Excluir conta
          </button>
        </Card>
      </div>

      {isModalOpen && (
        <>
          <div className="fixed inset-0 bg-black bg-opacity-50 z-40" onClick={closeModal} />
          <div
            role="dialog"
            aria-modal="true"
            aria-labelledby="delete-account-title"
            className="fixed left-1/2 top-1/2 -translate-x-1/2 -translate-y-1/2 z-50 bg-white rounded-lg shadow-lg p-8 max-w-md w-full mx-4"
          >
            <div className="flex items-center justify-between mb-4">
              <h2 id="delete-account-title" className="flex items-center gap-2 text-lg font-semibold text-black-900">
                <AlertTriangle size={20} className="text-red-600" />
                Excluir conta
              </h2>
              <button onClick={closeModal} className="text-black-500 hover:text-black-700" aria-label="Fechar">
                <X size={20} />
              </button>
            </div>

            <p className="mb-4 text-sm text-black-700">
              Digite sua senha para confirmar. Suas ocorrências serão mantidas sem identificação e suas
              assinaturas de categorias serão removidas.
            </p>

            {error && <Alert variant="error" onClose={() => setError(null)}>{error}</Alert>}

            <form onSubmit={handleSubmit(onSubmit)} className="space-y-4">
              <PasswordInput
                label="Senha"
                autoComplete="current-password"
                error={errors.password?.message}
                {...register('password')}
              />
              <div className="flex gap-3 pt-2">
                <Button type="button" variant="secondary" className="flex-1" onClick={closeModal} disabled={isDeleting}>
                  Cancelar
                </Button>
                <button type="submit" className={`${dangerButton} flex-1`} disabled={isDeleting}>
                  {isDeleting ? 'Excluindo...' : 'Excluir definitivamente'}
                </button>
              </div>
            </form>
          </div>
        </>
      )}
    </AppLayout>
  )
}

export default ProfilePage

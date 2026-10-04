import { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import { Badge } from '@/components/ui/Badge'
import { SubscriptionCard } from '@/components/notifications/SubscriptionCard'
import { SubscribeModal } from '@/components/notifications/SubscribeModal'
import { useSubscriptions } from '@/hooks/useSubscriptions'
import { useNotifications } from '@/hooks/useNotifications'
import { subscriptionService } from '@/services/subscriptionService'
import { getErrorMessage } from '@/utils/errorHandler'
import { Subscription } from '@/types/subscription'
import { Search } from 'lucide-react'

function NotificationsPage() {
  const navigate = useNavigate()
  const { subscriptions, refetch: refetchSubscriptions } = useSubscriptions()
  const { occurrences, isLoading: notificationsLoading } = useNotifications()
  const [searchTerm, setSearchTerm] = useState('')
  const [isModalOpen, setIsModalOpen] = useState(false)
  const [editingSubscription, setEditingSubscription] = useState<Subscription | null>(null)
  const [removeError, setRemoveError] = useState<string | null>(null)

  const filtered = useMemo(
    () =>
      occurrences.filter(
        (occ) =>
          occ.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          occ.categoryName.toLowerCase().includes(searchTerm.toLowerCase())
      ),
    [occurrences, searchTerm]
  )

  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return '-'
    try {
      return new Date(dateString).toLocaleDateString('pt-BR')
    } catch {
      return '-'
    }
  }

  const handleSubscribeSuccess = () => {
    setEditingSubscription(null)
    refetchSubscriptions()
  }

  const handleRemove = async (categoryId: number) => {
    if (!window.confirm('Tem certeza que deseja cancelar esta inscrição?')) return

    try {
      setRemoveError(null)
      await subscriptionService.unsubscribe(categoryId)
      refetchSubscriptions()
    } catch (err) {
      setRemoveError(getErrorMessage(err))
    }
  }

  const handleEdit = (subscription: Subscription) => {
    setEditingSubscription(subscription)
    setIsModalOpen(true)
  }

  const handleModalClose = () => {
    setIsModalOpen(false)
    setEditingSubscription(null)
  }

  return (
    <AppLayout>
      <div className="p-8">
        {/* Header */}
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold font-roboto text-black-900">
            Notificações
          </h1>
          <p className="text-sm text-brand-blue">
            <button onClick={() => navigate('/')} className="hover:underline">
              Geral
            </button>
            {` > Notificações`}
          </p>
        </div>

        {/* Seção Inscrições */}
        <div className="mb-12">
          <div className="flex items-center justify-between mb-6">
            <h2 className="text-xl font-semibold font-roboto text-black-900">
              Inscrições
            </h2>
            <div>
            <Button
              onClick={() => {
                setEditingSubscription(null)
                setIsModalOpen(true)
              }}
              className="text-sm"
            >
              + Nova Inscrição
            </Button>
            </div>
          </div>

          {removeError && <Alert variant="error" onClose={() => setRemoveError(null)} className="mb-4">{removeError}</Alert>}

          {subscriptions.length === 0 ? (
            <div className="p-8 text-center bg-white rounded-lg border border-black-100">
              <p className="text-black-500">Você ainda não está inscrito em nenhuma categoria</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
              {subscriptions.map((sub) => (
                <SubscriptionCard
                  key={sub.id}
                  icon="📌"
                  categoryName={sub.categoryName}
                  onEdit={() => handleEdit(sub)}
                  onRemove={() => handleRemove(sub.categoryId)}
                />
              ))}
            </div>
          )}
        </div>

        {/* Divisor */}
        <div className="border-t border-black-100 mb-12" />

        {/* Seção Todas as Notificações */}
        <div>
          <h2 className="text-xl font-semibold font-roboto text-black-900 mb-6">
            Todas as Notificações
          </h2>

          {/* Search Bar */}
          <div className="mb-6 flex gap-4 items-center">
            <div className="flex-1 relative">
              <div className="absolute left-4 top-1/2 -translate-y-1/2">
                <Search size={18} className="text-brand-muted" />
              </div>
              <input
                type="text"
                placeholder="Pesquisar"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                className="w-full pl-10 pr-4 py-3 border border-black-100 rounded-2xl text-sm placeholder-brand-muted focus:outline-none focus:ring-2 focus:ring-brand-blue focus:ring-opacity-50"
              />
            </div>
          </div>

          {/* Table */}
          <div className="bg-white rounded-lg overflow-hidden shadow-sm">
            {notificationsLoading ? (
              <div className="p-8 text-center">
                <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-brand-blue border-t-transparent"></div>
                <p className="mt-2 text-black-500">Carregando...</p>
              </div>
            ) : filtered.length === 0 ? (
              <div className="p-8 text-center">
                <p className="text-black-500">Nenhuma notificação encontrada</p>
              </div>
            ) : (
              <div className="overflow-x-auto">
                <table className="w-full text-sm">
                  <thead>
                    <tr className="border-b border-black-100 bg-black-50">
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Título da Ocorrência
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Categoria
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Subcategoria
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Autor
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Data de criação
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Status
                      </th>
                      <th className="px-6 py-4 text-left font-medium text-black-700">
                        Data de resolução
                      </th>
                    </tr>
                  </thead>
                  <tbody>
                    {filtered.map((occ, idx) => (
                      <tr key={idx} className="border-b border-black-100 hover:bg-black-50">
                        <td className="px-6 py-4 text-black-900 font-medium">
                          {occ.title}
                        </td>
                        <td className="px-6 py-4 text-black-500">
                          {occ.categoryName}
                        </td>
                        <td className="px-6 py-4 text-black-500">
                          {occ.subcategoryName}
                        </td>
                        <td className="px-6 py-4 text-black-500">
                          {occ.ownerName}
                        </td>
                        <td className="px-6 py-4 text-black-500">
                          {formatDate(occ.createdAt)}
                        </td>
                        <td className="px-6 py-4">
                          <Badge status={occ.status} />
                        </td>
                        <td className="px-6 py-4 text-black-500">
                          {formatDate(occ.resolvedAt)}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            )}
          </div>
        </div>
      </div>

      <SubscribeModal
        isOpen={isModalOpen}
        onClose={handleModalClose}
        onSubscribe={handleSubscribeSuccess}
        editingSubscription={editingSubscription}
      />
    </AppLayout>
  )
}

export default NotificationsPage

import { Fragment, useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import Button from '@/components/ui/Button'
import Alert from '@/components/ui/Alert'
import ConfirmDialog from '@/components/ui/ConfirmDialog'
import { PriorityBadge } from '@/components/ui/PriorityBadge'
import { useCategories } from '@/hooks/useCategories'
import { useSubcategories } from '@/hooks/useSubcategories'
import { categoryService, subcategoryService } from '@/services/categoryService'
import { Category, Subcategory } from '@/types/category'
import { getErrorMessage } from '@/utils/errorHandler'
import { Search, Plus, Pencil, Power, RotateCcw, ChevronDown, ChevronRight } from 'lucide-react'

type PendingDeactivation =
  | { kind: 'category'; item: Category }
  | { kind: 'subcategory'; item: Subcategory }

function InactiveBadge() {
  return (
    <span className="ml-2 rounded-full bg-black-100 px-2 py-0.5 text-xs font-medium text-black-500">
      Inativa
    </span>
  )
}

function CategoriesListPage() {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const [showInactive, setShowInactive] = useState(false)
  const [expandedId, setExpandedId] = useState<number | null>(null)
  const [pending, setPending] = useState<PendingDeactivation | null>(null)
  const [isSaving, setIsSaving] = useState(false)
  const [actionError, setActionError] = useState<string | null>(null)
  const { categories, isLoading, reload: reloadCategories } = useCategories({ includeInactive: true })
  const { subcategories, reload: reloadSubcategories } = useSubcategories({ includeInactive: true })

  const filtered = useMemo(
    () =>
      categories.filter(
        (cat) =>
          (showInactive || cat.active) &&
          cat.name.toLowerCase().includes(searchTerm.toLowerCase())
      ),
    [categories, searchTerm, showInactive]
  )

  const subcategoriesOf = (categoryId: number) =>
    subcategories.filter((s) => s.categoryId === categoryId && (showInactive || s.active))

  const getActiveSubcategoryCount = (categoryId: number): number =>
    subcategories.filter((s) => s.categoryId === categoryId && s.active).length

  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return '-'
    try {
      return new Date(dateString).toLocaleDateString('pt-BR')
    } catch {
      return '-'
    }
  }

  const runAction = async (action: () => Promise<unknown>) => {
    try {
      setActionError(null)
      setIsSaving(true)
      await action()
      await Promise.all([reloadCategories(), reloadSubcategories()])
    } catch (err) {
      setActionError(getErrorMessage(err))
    } finally {
      setIsSaving(false)
    }
  }

  const confirmDeactivation = async () => {
    if (!pending) return
    const target = pending
    await runAction(() =>
      target.kind === 'category'
        ? categoryService.deactivate(target.item.id)
        : subcategoryService.deactivate(target.item.subcategoryId)
    )
    setPending(null)
  }

  const iconButton =
    'rounded-md p-2 text-black-500 transition-colors hover:bg-black-100 hover:text-black-900 disabled:opacity-50'

  return (
    <AppLayout>
      <div className="p-8">
        <div className="mb-8">
          <h1 className="mb-2 text-3xl font-bold font-roboto text-black-900">
            Categorias
          </h1>
          <p className="text-sm text-brand-blue">
            <button
              onClick={() => navigate('/')}
              className="hover:underline"
            >
              Geral
            </button>
            {` > Configurações`}
          </p>
        </div>

        {actionError && (
          <Alert variant="error" onClose={() => setActionError(null)}>
            {actionError}
          </Alert>
        )}

        {/* Search Bar and Button */}
        <div className="mb-4 flex gap-4 items-center">
          <div className="flex-1 relative">
            <div className="absolute left-4 top-1/2 -translate-y-1/2">
              <Search size={18} className="text-brand-muted" />
            </div>
            <input
              type="text"
              placeholder="Pesquisar"
              value={searchTerm}
              onChange={(e) => setSearchTerm(e.target.value)}
              className="form-control h-12 pl-10"
            />
          </div>
          <Button
            variant="dashboard"
            onClick={() => navigate('/configuracoes/categorias/nova')}
            className="flex items-center gap-2"
          >
            <Plus size={18} />
            <span>Nova Categoria</span>
          </Button>
          <Button
            variant="secondary"
            onClick={() => navigate('/configuracoes/subcategorias/nova')}
            className="flex items-center gap-2 px-8"
          >
            <Plus size={18} />
            <span>Nova Subcategoria</span>
          </Button>
        </div>

        <label className="mb-6 flex w-fit cursor-pointer items-center gap-2 text-[#212529]">
          <input
            type="checkbox"
            checked={showInactive}
            onChange={(e) => setShowInactive(e.target.checked)}
            className="form-check-input"
          />
          Mostrar inativas
        </label>

        {/* Table */}
        <div className="bg-white rounded-lg overflow-hidden shadow-sm">
          {isLoading && categories.length === 0 ? (
            <div className="p-8 text-center">
              <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-brand-blue border-t-transparent"></div>
              <p className="mt-2 text-black-500">Carregando...</p>
            </div>
          ) : filtered.length === 0 ? (
            <div className="p-8 text-center">
              <p className="text-black-500">Nenhuma categoria encontrada</p>
            </div>
          ) : (
            <div className="overflow-x-auto">
              <table className="w-full text-sm">
                <thead>
                  <tr className="border-b border-black-100 bg-black-50">
                    <th className="px-6 py-4 text-left font-medium text-black-700">
                      Nome
                    </th>
                    <th className="px-6 py-4 text-left font-medium text-black-700">
                      Qnt. de Subcategorias
                    </th>
                    <th className="px-6 py-4 text-left font-medium text-black-700">
                      Data de Criação
                    </th>
                    <th className="px-6 py-4 text-left font-medium text-black-700">
                      Prioridade
                    </th>
                    <th className="px-6 py-4 text-right font-medium text-black-700">
                      Ações
                    </th>
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((category) => {
                    const isExpanded = expandedId === category.id
                    const subs = subcategoriesOf(category.id)
                    return (
                      <Fragment key={category.id}>
                        <tr
                          onClick={() => setExpandedId(isExpanded ? null : category.id)}
                          className={`border-b border-black-100 cursor-pointer hover:bg-black-50 ${
                            category.active ? '' : 'opacity-60'
                          }`}
                          aria-expanded={isExpanded}
                        >
                          <td className="px-6 py-4 text-black-900 font-medium">
                            <span className="inline-flex items-center gap-2">
                              {isExpanded ? <ChevronDown size={16} /> : <ChevronRight size={16} />}
                              {category.name}
                            </span>
                            {!category.active && <InactiveBadge />}
                          </td>
                          <td className="px-6 py-4 text-black-500">
                            {getActiveSubcategoryCount(category.id)}
                          </td>
                          <td className="px-6 py-4 text-black-500">
                            {formatDate(category.createdAt)}
                          </td>
                          <td className="px-6 py-4">
                            <PriorityBadge priority={category.priority} />
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex justify-end gap-1" onClick={(e) => e.stopPropagation()}>
                              <button
                                type="button"
                                title="Editar categoria"
                                aria-label={`Editar ${category.name}`}
                                className={iconButton}
                                onClick={() => navigate(`/configuracoes/categorias/${category.id}/editar`)}
                              >
                                <Pencil size={16} />
                              </button>
                              {category.active ? (
                                <button
                                  type="button"
                                  title="Desativar categoria"
                                  aria-label={`Desativar ${category.name}`}
                                  className={iconButton}
                                  disabled={isSaving}
                                  onClick={() => setPending({ kind: 'category', item: category })}
                                >
                                  <Power size={16} />
                                </button>
                              ) : (
                                <button
                                  type="button"
                                  title="Reativar categoria"
                                  aria-label={`Reativar ${category.name}`}
                                  className={iconButton}
                                  disabled={isSaving}
                                  onClick={() => runAction(() => categoryService.reactivate(category.id))}
                                >
                                  <RotateCcw size={16} />
                                </button>
                              )}
                            </div>
                          </td>
                        </tr>

                        {isExpanded && (
                          <tr className="border-b border-black-100 bg-black-50">
                            <td colSpan={5} className="px-6 py-4">
                              {subs.length === 0 ? (
                                <p className="pl-6 text-black-500">Nenhuma subcategoria.</p>
                              ) : (
                                <ul className="space-y-1 pl-6">
                                  {subs.map((sub) => (
                                    <li
                                      key={sub.subcategoryId}
                                      className={`flex items-center justify-between rounded-md bg-white px-4 py-2 ${
                                        sub.active ? '' : 'opacity-60'
                                      }`}
                                    >
                                      <span className="text-black-900">
                                        {sub.name}
                                        {!sub.active && <InactiveBadge />}
                                      </span>
                                      <div className="flex gap-1">
                                        <button
                                          type="button"
                                          title="Editar subcategoria"
                                          aria-label={`Editar ${sub.name}`}
                                          className={iconButton}
                                          onClick={() => navigate(`/configuracoes/subcategorias/${sub.subcategoryId}/editar`)}
                                        >
                                          <Pencil size={16} />
                                        </button>
                                        {sub.active ? (
                                          <button
                                            type="button"
                                            title="Desativar subcategoria"
                                            aria-label={`Desativar ${sub.name}`}
                                            className={iconButton}
                                            disabled={isSaving}
                                            onClick={() => setPending({ kind: 'subcategory', item: sub })}
                                          >
                                            <Power size={16} />
                                          </button>
                                        ) : (
                                          <button
                                            type="button"
                                            title="Reativar subcategoria"
                                            aria-label={`Reativar ${sub.name}`}
                                            className={iconButton}
                                            disabled={isSaving}
                                            onClick={() => runAction(() => subcategoryService.reactivate(sub.subcategoryId))}
                                          >
                                            <RotateCcw size={16} />
                                          </button>
                                        )}
                                      </div>
                                    </li>
                                  ))}
                                </ul>
                              )}
                              {category.active && (
                                <button
                                  type="button"
                                  className="mt-3 ml-6 flex items-center gap-1 text-sm font-medium text-brand-blue hover:underline"
                                  onClick={() => navigate(`/configuracoes/subcategorias/nova?categoria=${category.id}`)}
                                >
                                  <Plus size={14} /> Adicionar subcategoria
                                </button>
                              )}
                            </td>
                          </tr>
                        )}
                      </Fragment>
                    )
                  })}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>

      <ConfirmDialog
        isOpen={pending !== null}
        title={pending?.kind === 'category' ? 'Desativar categoria' : 'Desativar subcategoria'}
        confirmLabel="Desativar"
        isLoading={isSaving}
        onConfirm={confirmDeactivation}
        onCancel={() => setPending(null)}
      >
        {pending?.kind === 'category' ? (
          <>
            A categoria <strong>{pending.item.name}</strong> deixará de aparecer ao registrar ocorrências e
            nas assinaturas, junto com suas subcategorias. As ocorrências já registradas são mantidas.
            Você pode reativá-la depois.
          </>
        ) : (
          <>
            A subcategoria <strong>{pending?.item.name}</strong> deixará de aparecer ao registrar ocorrências.
            As ocorrências já registradas são mantidas. Você pode reativá-la depois.
          </>
        )}
      </ConfirmDialog>
    </AppLayout>
  )
}

export default CategoriesListPage

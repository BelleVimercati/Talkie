import { useState, useMemo } from 'react'
import { useNavigate } from 'react-router-dom'
import { AppLayout } from '@/components/layout/AppLayout'
import Button from '@/components/ui/Button'
import { PriorityBadge } from '@/components/ui/PriorityBadge'
import { useCategories } from '@/hooks/useCategories'
import { useSubcategories } from '@/hooks/useSubcategories'
import { Search, Plus } from 'lucide-react'

function CategoriesListPage() {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')
  const { categories, isLoading } = useCategories()
  const { subcategories } = useSubcategories()

  const filtered = useMemo(
    () =>
      categories.filter((cat) =>
        cat.name.toLowerCase().includes(searchTerm.toLowerCase())
      ),
    [categories, searchTerm]
  )

  const getSubcategoryCount = (categoryName: string): number => {
    return subcategories.filter((s) => s.categoryName === categoryName).length
  }

  const formatDate = (dateString: string | null | undefined) => {
    if (!dateString) return '-'
    try {
      return new Date(dateString).toLocaleDateString('pt-BR')
    } catch {
      return '-'
    }
  }

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

        {/* Search Bar and Button */}
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

        {/* Table */}
        <div className="bg-white rounded-lg overflow-hidden shadow-sm">
          {isLoading ? (
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
                  </tr>
                </thead>
                <tbody>
                  {filtered.map((category, idx) => (
                    <tr key={idx} className="border-b border-black-100 hover:bg-black-50">
                      <td className="px-6 py-4 text-black-900 font-medium">
                        {category.name}
                      </td>
                      <td className="px-6 py-4 text-black-500">
                        {getSubcategoryCount(category.name)}
                      </td>
                      <td className="px-6 py-4 text-black-500">
                        {formatDate(category.createdAt)}
                      </td>
                      <td className="px-6 py-4">
                        <PriorityBadge priority={category.priority} />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </div>
    </AppLayout>
  )
}

export default CategoriesListPage

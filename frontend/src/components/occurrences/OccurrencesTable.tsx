import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { Occurrence } from '@/types/occurrence'
import Button from '@/components/ui/Button'
import Badge from '@/components/ui/Badge'
import { Search, Plus } from 'lucide-react'

interface OccurrencesTableProps {
  occurrences: Occurrence[]
  isLoading: boolean
}

export function OccurrencesTable({ occurrences, isLoading }: OccurrencesTableProps) {
  const navigate = useNavigate()
  const [searchTerm, setSearchTerm] = useState('')

  const filtered = occurrences.filter(
    (occ) =>
      occ.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
      occ.categoryName.toLowerCase().includes(searchTerm.toLowerCase())
  )

  return (
    <div className="space-y-4">
      {/* Search Bar and Button */}
      <div className="flex gap-4 items-center">
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
          onClick={() => navigate('/ocorrencias/nova')}
          className="flex items-center gap-2"
        >
          <Plus size={18} />
          <span className="hidden sm:inline">Nova Ocorrência</span>
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
            <p className="text-black-500">Nenhuma ocorrência encontrada</p>
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
                    Status
                  </th>
                </tr>
              </thead>
              <tbody>
                {filtered.map((occurrence, idx) => (
                  <tr key={idx} className="border-b border-black-100 hover:bg-black-50">
                    <td className="px-6 py-4 text-black-900 font-medium">
                      {occurrence.title}
                    </td>
                    <td className="px-6 py-4 text-black-500">
                      {occurrence.categoryName}
                    </td>
                    <td className="px-6 py-4 text-black-500">
                      {occurrence.subcategoryName}
                    </td>
                    <td className="px-6 py-4">
                      <Badge status={occurrence.status} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>
    </div>
  )
}

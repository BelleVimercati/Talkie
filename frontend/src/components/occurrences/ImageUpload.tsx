import React, { useState } from 'react'
import { X, Upload } from 'lucide-react'

interface ImageUploadProps {
  value?: File | null
  onChange: (file: File | null) => void
  error?: string
}

export const ImageUpload: React.FC<ImageUploadProps> = ({ onChange, error }) => {
  const [preview, setPreview] = useState<string | null>(null)

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (file) {
      // Validar tipo
      if (!['image/jpeg', 'image/png', 'image/webp'].includes(file.type)) {
        onChange(null)
        return
      }

      // Validar tamanho (5MB)
      if (file.size > 5 * 1024 * 1024) {
        onChange(null)
        return
      }

      onChange(file)

      // Preview
      const reader = new FileReader()
      reader.onloadend = () => {
        setPreview(reader.result as string)
      }
      reader.readAsDataURL(file)
    }
  }

  const handleRemove = () => {
    onChange(null)
    setPreview(null)
  }

  return (
    <div className="space-y-2">
      <label className="block text-sm font-medium text-gray-900">Foto da ocorrência (opcional)</label>

      {preview ? (
        <div className="relative w-full h-48 bg-gray-100 rounded-lg overflow-hidden">
          <img src={preview} alt="Preview" className="w-full h-full object-cover" />
          <button
            type="button"
            onClick={handleRemove}
            className="absolute top-2 right-2 bg-red-500 hover:bg-red-600 text-white p-1 rounded-full transition"
          >
            <X size={20} />
          </button>
        </div>
      ) : (
        <label className="flex flex-col items-center justify-center w-full h-32 border-2 border-dashed border-gray-300 rounded-lg cursor-pointer hover:border-brand-blue transition">
          <div className="flex flex-col items-center justify-center pt-5 pb-6">
            <Upload size={32} className="text-gray-400 mb-2" />
            <p className="text-sm text-gray-600">Clique ou arraste uma imagem</p>
            <p className="text-xs text-gray-500 mt-1">PNG, JPG ou WebP (máx. 5MB)</p>
          </div>
          <input
            type="file"
            className="hidden"
            accept="image/png,image/jpeg,image/webp"
            onChange={handleFileChange}
          />
        </label>
      )}

      {error && <p className="text-xs text-red-500">{error}</p>}
    </div>
  )
}

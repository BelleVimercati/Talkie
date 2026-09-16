import { Check } from 'lucide-react'

interface ColorPickerProps {
  value: string
  onChange: (color: string) => void
  error?: string
}

const CATEGORY_COLORS = [
  '#FF6B6B',
  '#4ECDC4',
  '#45B7D1',
  '#FFA07A',
  '#98D8C8',
  '#F7DC6F',
  '#BB8FCE',
  '#85C1E2',
  '#F8B739',
  '#52C4A1',
  '#E8A87C',
  '#D4A5E8',
  '#6C5CE7',
  '#00B894',
  '#FDCB6E',
  '#E17055',
]

export function ColorPicker({ value, onChange, error }: ColorPickerProps) {
  return (
    <div className="space-y-2">
      <label className="block font-roboto text-sm font-medium text-black-800 tracking-wide03">
        Cor*
      </label>
      <div className="grid grid-cols-8 gap-3">
        {CATEGORY_COLORS.map((color) => (
          <button
            key={color}
            type="button"
            onClick={() => onChange(color)}
            className="h-12 w-12 rounded-lg flex items-center justify-center transition-all relative"
            style={{
              backgroundColor: color,
              border: value === color ? '3px solid white' : 'none',
              boxShadow: value === color ? `0 0 0 2px #4667AC` : 'none',
            }}
          >
            {value === color && <Check size={20} className="text-white" />}
          </button>
        ))}
      </div>
      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  )
}

export default ColorPicker

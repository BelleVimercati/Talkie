interface IconPickerProps {
  value: string
  onChange: (icon: string) => void
  error?: string
}

const CATEGORY_ICONS = ['⛈', '🚓', '🦔', '👷🏽‍♀️', '🚨', '🚍', '🖥', '🔒', '🚗', '🏠', '💼', '🌳', '🏥', '🎓']

export function IconPicker({ value, onChange, error }: IconPickerProps) {
  return (
    <div className="space-y-2">
      <label className="block font-roboto text-sm font-medium text-black-800 tracking-wide03">
        Ícone*
      </label>
      <div className="grid grid-cols-7 gap-3">
        {CATEGORY_ICONS.map((icon) => (
          <button
            key={icon}
            type="button"
            onClick={() => onChange(icon)}
            className={`h-14 w-14 rounded-lg flex items-center justify-center text-2xl transition-all ${
              value === icon
                ? 'ring-2 ring-brand-blue bg-brand-blue bg-opacity-5'
                : 'border border-black-100 hover:border-black-200'
            }`}
          >
            {icon}
          </button>
        ))}
      </div>
      {error && <p className="text-xs text-red-600">{error}</p>}
    </div>
  )
}

export default IconPicker

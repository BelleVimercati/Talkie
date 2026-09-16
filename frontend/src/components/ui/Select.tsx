import { forwardRef, SelectHTMLAttributes } from 'react'
import { ChevronDown } from 'lucide-react'

interface SelectOption {
  value: string | number
  label: string
}

interface SelectProps extends SelectHTMLAttributes<HTMLSelectElement> {
  label?: string
  error?: string
  options: SelectOption[]
}

const Select = forwardRef<HTMLSelectElement, SelectProps>(
  ({ label, error, options, className = '', ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03">
            {label}
          </label>
        )}
        <div className="relative">
          <select
            ref={ref}
            className={`h-12 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 pr-10 font-roboto text-[15px] text-black-900 placeholder-black-500 transition-colors focus:border-brand-blue focus:outline-none focus:ring-1 focus:ring-brand-blue appearance-none ${
              error ? 'border-red-500' : ''
            } ${className}`}
            {...props}
          >
            <option value="">Selecione uma opção</option>
            {options.map((opt) => (
              <option key={opt.value} value={opt.value}>
                {opt.label}
              </option>
            ))}
          </select>
          <ChevronDown
            size={18}
            className="absolute right-3 top-1/2 -translate-y-1/2 pointer-events-none text-black-500"
          />
        </div>
        {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
      </div>
    )
  },
)

Select.displayName = 'Select'

export default Select

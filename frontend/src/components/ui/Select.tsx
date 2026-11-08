import { forwardRef, SelectHTMLAttributes } from 'react'

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
          <label className="form-label font-roboto">
            {label}
          </label>
        )}
        <select
          ref={ref}
          className={`form-select font-roboto ${error ? 'is-invalid' : ''} ${className}`}
          {...props}
        >
          <option value="">Selecione uma opção</option>
          {options.map((opt) => (
            <option key={opt.value} value={opt.value}>
              {opt.label}
            </option>
          ))}
        </select>
        {error && <p className="invalid-feedback">{error}</p>}
      </div>
    )
  },
)

Select.displayName = 'Select'

export default Select

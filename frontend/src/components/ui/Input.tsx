import { forwardRef, InputHTMLAttributes, ReactNode } from 'react'

interface InputProps extends InputHTMLAttributes<HTMLInputElement> {
  label?: string
  error?: string
  rightIcon?: ReactNode
}

const Input = forwardRef<HTMLInputElement, InputProps>(
  ({ label, error, rightIcon, className = '', ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="form-label font-roboto">
            {label}
          </label>
        )}
        <div className="relative">
          <input
            ref={ref}
            className={`form-control font-roboto ${rightIcon ? 'pr-10' : ''} ${error ? 'is-invalid' : ''} ${className}`}
            {...props}
          />
          {rightIcon && (
            <div className="absolute right-3 top-1/2 -translate-y-1/2">{rightIcon}</div>
          )}
        </div>
        {error && <p className="invalid-feedback">{error}</p>}
      </div>
    )
  },
)

Input.displayName = 'Input'

export default Input

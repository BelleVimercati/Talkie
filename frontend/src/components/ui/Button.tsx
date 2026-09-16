import { ButtonHTMLAttributes, ReactNode } from 'react'

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: 'primary' | 'dashboard' | 'secondary'
  isLoading?: boolean
  children: ReactNode
}

function Button({
  variant = 'primary',
  isLoading = false,
  children,
  disabled,
  className = '',
  ...props
}: ButtonProps) {
  const baseStyles = 'h-12 rounded-md font-roboto font-bold text-sm tracking-wide03 transition-colors'

  const variants = {
    primary:
      'w-full bg-brand-blue text-white hover:bg-opacity-90 disabled:bg-opacity-60 disabled:cursor-not-allowed',
    dashboard:
      'bg-brand-navy text-white hover:bg-opacity-90 disabled:bg-opacity-60 disabled:cursor-not-allowed',
    secondary:
      'bg-[#b4b4b4] text-white hover:bg-opacity-90 disabled:bg-opacity-60 disabled:cursor-not-allowed',
  }

  return (
    <button
      disabled={disabled || isLoading}
      className={`${baseStyles} ${variants[variant]} ${className}`}
      {...props}
    >
      {isLoading ? (
        <div className="flex items-center justify-center gap-2">
          <div className="h-4 w-4 animate-spin rounded-full border-2 border-current border-t-transparent"></div>
          <span>Carregando...</span>
        </div>
      ) : (
        children
      )}
    </button>
  )
}

export default Button

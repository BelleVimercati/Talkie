import { ReactNode } from 'react'
import { AlertCircle, CheckCircle } from 'lucide-react'

interface AlertProps {
  variant: 'error' | 'success'
  children: ReactNode
  onClose?: () => void
}

function Alert({ variant, children, onClose }: AlertProps) {
  const isError = variant === 'error'

  return (
    <div
      className={`mb-4 flex items-start gap-3 rounded-md p-3 ${
        isError ? 'bg-red-50' : 'bg-green-50'
      }`}
    >
      {isError ? (
        <AlertCircle className="h-5 w-5 flex-shrink-0 text-red-500" />
      ) : (
        <CheckCircle className="h-5 w-5 flex-shrink-0 text-green-600" />
      )}
      <div className="flex flex-1 items-center justify-between">
        <p className={`text-sm ${isError ? 'text-red-800' : 'text-green-800'}`}>
          {children}
        </p>
        {onClose && (
          <button
            onClick={onClose}
            className="ml-2 text-gray-400 hover:text-gray-600"
          >
            ×
          </button>
        )}
      </div>
    </div>
  )
}

export default Alert

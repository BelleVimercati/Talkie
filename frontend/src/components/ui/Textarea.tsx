import { forwardRef, TextareaHTMLAttributes } from 'react'

interface TextareaProps extends TextareaHTMLAttributes<HTMLTextAreaElement> {
  label?: string
  error?: string
}

const Textarea = forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ label, error, className = '', ...props }, ref) => {
    return (
      <div className="w-full">
        {label && (
          <label className="mb-1.5 block font-roboto text-sm font-medium text-black-800 tracking-wide03">
            {label}
          </label>
        )}
        <textarea
          ref={ref}
          className={`min-h-32 w-full rounded-md border-[0.5px] border-black-100 bg-black-50 px-4 py-2 font-roboto text-[15px] text-black-900 placeholder-black-500 transition-colors focus:border-brand-blue focus:outline-none focus:ring-1 focus:ring-brand-blue resize-none ${
            error ? 'border-red-500' : ''
          } ${className}`}
          {...props}
        />
        {error && <p className="mt-1 text-xs text-red-500">{error}</p>}
      </div>
    )
  },
)

Textarea.displayName = 'Textarea'

export default Textarea

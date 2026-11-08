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
          <label className="form-label font-roboto">
            {label}
          </label>
        )}
        <textarea
          ref={ref}
          className={`form-control min-h-32 font-roboto ${error ? 'is-invalid' : ''} ${className}`}
          {...props}
        />
        {error && <p className="invalid-feedback">{error}</p>}
      </div>
    )
  },
)

Textarea.displayName = 'Textarea'

export default Textarea

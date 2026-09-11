function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen w-full bg-white">
      <div className="flex flex-1 flex-col px-6 py-8 sm:px-10 lg:px-24">
        <div className="flex items-center gap-3">
          <div className="h-10 w-10 rounded-md bg-brand-blue" />
          <span className="font-logo text-4xl font-bold tracking-logo text-brand-logo">
            Talkie
          </span>
        </div>
        <div className="flex flex-1 items-center justify-center">
          <div className="w-full max-w-md">{children}</div>
        </div>
      </div>

      <div className="relative hidden w-[830px] shrink-0 items-center justify-center overflow-hidden bg-brand-navy lg:flex">
        <div className="absolute right-0 top-1/2 -translate-y-1/2">
          <div className="h-96 w-96 rounded-full bg-yellow-400 opacity-50" />
        </div>
        <div className="relative z-10 flex h-64 w-64 items-center justify-center rounded-lg bg-gray-300">
          <span className="text-center text-gray-500">Ilustração do Talkie</span>
        </div>
      </div>
    </div>
  )
}

export default AuthLayout

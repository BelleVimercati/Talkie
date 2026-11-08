import logo from "@/assets/logo.png";
import logomin from "@/assets/logomin.png";

function AuthLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex min-h-screen w-full bg-white">
      <div className="flex flex-1 flex-col px-6 py-8 sm:px-10 lg:px-24">
        <div className="flex items-center gap-3">
          {/* Mesma altura da linha do nome (text-4xl = 2.5rem) */}
          <img src={logomin} alt="Logo do sistema" className="h-10 w-auto" />
          <span className="font-logo text-4xl font-bold tracking-logo text-brand-logo">
            Talkie
          </span>
        </div>
        <div className="flex flex-1 flex-col items-center justify-center overflow-y-auto py-10">
          <div className="w-full max-w-[540px]">{children}</div>
        </div>
      </div>

      <div className="relative hidden w-[830px] shrink-0 items-center justify-center overflow-hidden bg-brand-navy lg:flex">
        <div className="absolute right-0 top-1/2 -translate-y-1/2">
          <div className="h-96 w-96 rounded-full bg-yellow-400 opacity-70" />
        </div>
        {/* Apoiado na borda inferior do painel, com 70% da altura da tela */}
        <img
          src={logo}
          alt="Mascote do Talkie usando rádios comunicadores"
          className="absolute bottom-0 left-1/2 z-10 h-[95vh] w-auto max-w-none -translate-x-1/2 object-contain object-bottom"
        />
      </div>
    </div>
  );
}

export default AuthLayout;

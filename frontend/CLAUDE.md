# CLAUDE.md — Talkie Frontend

SPA do Talkie. Visão geral do projeto, contrato com a API e fluxo full-stack: [../CLAUDE.md](../CLAUDE.md).

## Stack

- Vite 5 + React 18 + TypeScript 5 (`strict`, `noUnusedLocals`, `noUnusedParameters`)
- react-router-dom 6 · zustand (estado de auth) · axios
- react-hook-form + zod (`@hookform/resolvers`) · jwt-decode
- Tailwind CSS 3 · lucide-react (ícones) · fontes via `@fontsource` (Poppins, Roboto, Inter)

**Não há** react-query, biblioteca de componentes (MUI etc.), testes automatizados nem ESLint instalado. Não adicione dependências novas sem necessidade clara.

## Comandos

```bash
npm install
npm run dev       # http://localhost:5173 (a API precisa estar em :8080)
npm run build     # tsc (checagem de tipos) + vite build → principal validação
npm run preview
```

`npm run lint` existe no `package.json`, mas o ESLint não está instalado nem configurado.

## Variáveis de ambiente

Copie `.env.example` para `.env.local` (ignorado pelo git):

```bash
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Talkie
VITE_JWT_TOKEN_KEY=talkie_auth_token
```

## Estrutura de `src/`

```text
src/
├── App.tsx              # todas as rotas
├── main.tsx
├── components/
│   ├── common/          # ProtectedRoute
│   ├── layout/          # AppLayout (com Sidebar), AuthLayout, Sidebar
│   ├── ui/              # primitivos: Button, Input, PasswordInput, Select, Textarea, Alert,
│   │                    #   Badge, PriorityBadge, RoleBadge, Card, ColorPicker, IconPicker
│   ├── occurrences/     # OccurrencesTable, OccurrenceDetailsModal, StatsCards, ImageUpload
│   └── notifications/   # SubscribeModal, SubscriptionCard
├── pages/               # telas (AuthPage/, OccurrencesPage/, SettingsPage/, HomePage, ...)
├── hooks/               # useCategories, useOccurrences, useSubscriptions, useUsers, ...
├── services/            # api.ts (axios) + xxxService.ts
├── schemas/             # schemas zod dos formulários
├── stores/              # authStore (zustand)
├── types/               # espelho dos DTOs do backend
└── utils/               # errorHandler, jwt, cpf, initials
```

## Rotas (`App.tsx`)

Paths em **português**. Toda rota autenticada é envolvida por `ProtectedRoute`; rotas de admin usam `requireAdmin`.

| Path | Página | Acesso |
|------|--------|--------|
| `/login`, `/register` | LoginPage, RegisterPage | público |
| `/` | HomePage (dashboard de ocorrências) | autenticado |
| `/ocorrencias/nova` | CreateOccurrencePage | autenticado |
| `/notificacoes` | NotificationsPage (assinaturas) | autenticado |
| `/configuracoes` | CategoriesListPage | ADMIN |
| `/configuracoes/categorias/nova`, `/configuracoes/subcategorias/nova` | CreateCategory/SubcategoryPage | ADMIN |
| `/usuarios` | UsersListPage | ADMIN |

Nova página: crie em `pages/`, envolva o conteúdo em `<AppLayout>`, registre a rota em `App.tsx` e, se fizer sentido, adicione o link em `components/layout/Sidebar.tsx`.

## Padrões

### Services (`services/`)

Use a instância de `services/api.ts`, que já injeta o `Bearer`. Agrupe as chamadas num objeto e retorne `response.data.data` (o envelope `ApiResponse<T>`):

```ts
export const categoryService = {
  create: async (data: CreateCategoryDTO) => {
    const response = await api.post<ApiResponse<Category>>('/categories', data)
    return response.data.data
  },
}
```

### Tipos (`types/`)

Um arquivo por domínio (`occurrence.ts`, `category.ts`, `subscription.ts`, `user.ts`, `api.ts`), espelhando os records do backend. Enums viram union types (`'ABERTO' | 'EM_ANALISE' | ...`). Ao mudar um DTO no backend, atualize aqui.

### Hooks de leitura (`hooks/`)

`useXxx()` com `useState` + `useEffect`, retornando `{ dados, isLoading, error }`. O erro vem de `getErrorMessage`:

```ts
export function useCategories() {
  const [categories, setCategories] = useState<Category[]>([])
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  useEffect(() => { /* try { setCategories(await categoryService.list()) } catch (err) { setError(getErrorMessage(err)) } */ }, [])
  return { categories, isLoading, error }
}
```

### Formulários (`schemas/` + react-hook-form)

- Schema zod em `schemas/createXxxSchema.ts`, com mensagens em português e o tipo exportado via `z.infer`:

  ```ts
  export const createCategorySchema = z.object({ name: z.string().min(2, 'Nome deve ter no mínimo 2 caracteres'), ... })
  export type CreateCategoryFormData = z.infer<typeof createCategorySchema>
  ```

- Na página: `useForm<FormData>({ resolver: zodResolver(schema) })`. Campos simples usam `register`; componentes customizados (pickers, selects controlados) usam `Controller`.
- Erro do backend: guarde em `globalError` (`getErrorMessage(err)`) e mostre com `<Alert variant="error">`. Para erro de campo vindo do backend, veja `getFieldFromBackendMessage`.
- Loading: `isLoading` local passado para `<Button isLoading>`.
- Modelo completo: `pages/SettingsPage/CreateCategoryPage.tsx`.

### Autenticação

- `stores/authStore.ts` (zustand): `token`, `user` (`{ email, role }` decodificado do JWT), `isAuthenticated`, `login()`, `logout()`. O token fica em `localStorage[VITE_JWT_TOKEN_KEY]`.
- Para checar papel: `useAuthStore((s) => s.user?.role === 'ADMIN')`.
- Use seletores (`useAuthStore((s) => s.campo)`) em vez de desestruturar a store inteira.

### Componentes

- Arquivos `PascalCase.tsx`, um componente por arquivo. Páginas e componentes de `ui/` usam `export default`; `AppLayout` e `Sidebar` usam named export. Siga o padrão do arquivo vizinho.
- Imports com o alias `@/` (ex.: `@/components/ui/Button`), nunca caminhos relativos longos.
- Reaproveite os primitivos de `components/ui/` antes de criar um novo. Variantes de `Button`: `primary` (full-width, azul), `dashboard` (navy), `secondary`.
- Ícones: `lucide-react`.
- Texto da interface em português.

### Estilo (Tailwind)

Use os tokens de `tailwind.config.ts` em vez de cores hex soltas:

- **Cores:** `brand-blue`, `brand-navy`, `brand-orange`, `brand-muted`, `brand-logo`; escala de cinzas `black-900 … black-50`.
- **Status de ocorrência:** `status-openBg/openText`, `status-analysisBg/analysisText`, `status-resolvedBg/resolvedText`, `status-closedBg/closedText`.
- **Fontes:** `font-roboto` (títulos e botões), `font-poppins`, `font-logo`.
- Títulos de página: `text-3xl font-bold font-roboto text-black-900`; container `p-8`.

## Regras

- **Nunca** gere nem commite `.js` / `.js.map` dentro de `src/`. O `tsconfig` tem `noEmit: true`; se aparecerem, apague-os (o Vite pode resolver o `.js` velho antes do `.tsx`).
- Não chame `axios` direto em componentes; passe por um service.
- Não use `any`; tipar respostas com `ApiResponse<T>`.
- Antes de concluir uma tarefa, rode `npm run build` e garanta que passa sem erros de tipo.

## Pendências conhecidas

- `occurrenceService.uploadImage` / `getImage` chamam `/occurrences/{id}/image`, que ainda não existe no backend.
- `categoryService.list` / `subcategoryService.list` aceitam resposta como array cru ou como `ApiResponse` (herança de inconsistência da API). Em código novo, espere sempre `ApiResponse<T>`.

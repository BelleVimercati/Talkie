# CLAUDE.md - Talkie Frontend

Arquivo de contexto para agentes de IA (Claude, Cursor, Copilot) e desenvolvedores garantirem consistência técnica do frontend do Talkie.

## 🎯 Visão Geral do Projeto

**Talkie Frontend** é a interface web da plataforma de gestão de ocorrências comunitárias. Os usuários acessam a aplicação para reportar problemas urbanos (infraestrutura, segurança, etc.), visualizar ocorrências, assinar em categorias de interesse e receber notificações.

### Conexão com Backend

- **Backend Repository:** talkie (Java/Spring Boot)
- **API Base URL:** `http://localhost:8080` (desenvolvimento) ou variável de ambiente `VITE_API_URL`
- **Autenticação:** JWT Token (bearer token no header `Authorization`)
- **Documentação API:** `http://localhost:8080/swagger-ui/index.html`

---

## 🛠️ Stack Tecnológica

### Core
- **Framework:** React 18+
- **Linguagem:** TypeScript
- **Build Tool:** Vite
- **Node.js:** 18+
- **Package Manager:** npm ou pnpm

### State Management & API
- **State Management:** Zustand (recomendado) ou Context API
- **HTTP Client:** Axios ou TanStack Query (React Query)
- **Autenticação:** JWT com localStorage/sessionStorage

### Styling & UI
- **CSS Framework:** Tailwind CSS (recomendado)
- **UI Components:** Headless UI / Radix UI (opcional, para componentes customizados)
- **Icons:** Lucide React ou Heroicons

### Testes
- **Unit Tests:** Vitest
- **E2E Tests:** Playwright ou Cypress
- **Component Testing:** Testing Library

### Utilities
- **Data Formatting:** date-fns, numeral.js
- **Form Handling:** React Hook Form
- **Validation:** Zod ou Yup
- **HTTP Status Codes:** axios interceptors para tratamento global

---

## 📂 Estrutura de Diretórios

```
talkie-web/
├── public/                          # Arquivos estáticos
│   └── favicon.ico
├── src/
│   ├── App.tsx                      # Componente raiz
│   ├── main.tsx                     # Entrypoint
│   ├── vite-env.d.ts               # Tipos do Vite
│   ├── index.css                    # Estilos globais (Tailwind)
│   ├── components/                  # Componentes reutilizáveis
│   │   ├── common/                  # Componentes genéricos
│   │   │   ├── Header.tsx
│   │   │   ├── Footer.tsx
│   │   │   ├── Navigation.tsx
│   │   │   └── LoadingSpinner.tsx
│   │   └── ui/                      # Componentes de UI
│   │       ├── Button.tsx
│   │       ├── Card.tsx
│   │       ├── Modal.tsx
│   │       ├── Input.tsx
│   │       └── Select.tsx
│   ├── pages/                       # Páginas/Views
│   │   ├── AuthPage/
│   │   │   ├── LoginPage.tsx
│   │   │   └── RegisterPage.tsx
│   │   ├── DashboardPage/
│   │   │   └── DashboardPage.tsx
│   │   ├── OccurrencesPage/
│   │   │   ├── OccurrencesListPage.tsx
│   │   │   ├── OccurrenceDetailPage.tsx
│   │   │   └── CreateOccurrencePage.tsx
│   │   ├── CategoriesPage/
│   │   │   └── CategoriesPage.tsx
│   │   ├── SubscriptionsPage/
│   │   │   └── SubscriptionsPage.tsx
│   │   └── NotFoundPage.tsx
│   ├── services/                    # Camada de API
│   │   ├── api.ts                   # Instância do Axios com interceptadores
│   │   ├── authService.ts           # Endpoints de autenticação
│   │   ├── occurrenceService.ts     # Endpoints de ocorrências
│   │   ├── categoryService.ts       # Endpoints de categorias
│   │   └── subscriptionService.ts   # Endpoints de assinaturas
│   ├── hooks/                       # Custom React Hooks
│   │   ├── useAuth.ts               # Gerenciamento de autenticação
│   │   ├── useOccurrences.ts        # Fetch de ocorrências
│   │   ├── useCategories.ts         # Fetch de categorias
│   │   └── useNotification.ts       # Toast/Snackbar notifications
│   ├── stores/                      # State Management (Zustand)
│   │   ├── authStore.ts             # Store de autenticação
│   │   ├── occurrenceStore.ts       # Store de ocorrências
│   │   └── uiStore.ts               # Store de UI (modals, etc)
│   ├── types/                       # TypeScript types/interfaces
│   │   ├── api.ts                   # Tipos da API (mirror do backend)
│   │   ├── forms.ts                 # Tipos de formulários
│   │   └── index.ts                 # Export de tipos
│   ├── utils/                       # Funções utilitárias
│   │   ├── formatters.ts            # Formatação de dados
│   │   ├── validators.ts            # Validação de input
│   │   ├── constants.ts             # Constantes da app
│   │   └── errorHandler.ts          # Tratamento de erros
│   ├── middleware/                  # Middleware (autenticação, etc)
│   │   ├── authMiddleware.ts        # Proteção de rotas
│   │   └── apiMiddleware.ts         # Interceptadores HTTP
│   ├── styles/                      # Estilos globais/temas
│   │   ├── tailwind.config.ts
│   │   └── globals.css
│   └── router/                      # Roteamento
│       └── index.ts                 # Configuração de rotas
├── .env.example                     # Variáveis de ambiente exemplo
├── .env.local                       # Variáveis de ambiente (gitignored)
├── .gitignore
├── .prettierrc                      # Prettier config
├── .eslintrc.json                   # ESLint config
├── tsconfig.json                    # TypeScript config
├── vite.config.ts                   # Vite config
├── vitest.config.ts                 # Vitest config
├── package.json
└── README.md
```

---

## 🔐 Autenticação

### Fluxo de Autenticação

1. **Login:**
   ```typescript
   POST /auth/login
   Body: { email, password }
   Response: { message, data: "<jwt-token-string>" }  // data é a string JWT crua, não um objeto
   ```

2. **Registro:**
   ```typescript
   POST /auth/register
   Body: { name, email, password, cpf }
   Response: { message, data: { id, name, email } }  // sem token, sem role/cpf
   // Após registro bem-sucedido, redirecionar para login (não há auto-login)
   ```

3. **Armazenar Token:**
   ```typescript
   // localStorage ou sessionStorage
   localStorage.setItem('token', response.data);  // response.data é a string JWT diretamente
   ```

4. **Decodificar JWT e Extrair Claims:**
   ```typescript
   // Usar biblioteca jwt-decode (npm install jwt-decode)
   import { jwtDecode } from 'jwt-decode';
   
   const token = localStorage.getItem('token');
   if (token) {
     const decoded = jwtDecode<{ sub: string; role: string }>(token);
     const userEmail = decoded.sub;  // 'sub' claim contém o email
     const userRole = decoded.role;  // 'role' claim contém USER ou ADMIN
     // Montar usuário logado: { email, role, id: uuid }
   }
   ```

5. **Usar Token em Requisições:**
   ```typescript
   // Interceptador Axios (implementar em api.ts)
   api.interceptors.request.use((config) => {
     const token = localStorage.getItem('token');
     if (token) {
       config.headers.Authorization = `Bearer ${token}`;
     }
     return config;
   });
   ```

6. **Logout:**
   - Remover token do localStorage
   - Limpar estado da aplicação (usuário, role, etc.)
   - Redirecionar para login

### Store de Autenticação (Zustand)

```typescript
// stores/authStore.ts
interface User {
  id: string;  // UUID string
  email: string;
  role: 'USER' | 'ADMIN';
}

interface AuthStore {
  user: User | null;
  token: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  setUser: (user: User) => void;
}

export const useAuthStore = create<AuthStore>((set) => ({
  user: null,
  token: localStorage.getItem('token'),
  isAuthenticated: !!localStorage.getItem('token'),
  // ... implementação
  // Nota: o objeto User é montado decodificando o JWT (claims 'sub' e 'role')
  // após login bem-sucedido, pois a API não retorna o User completo
}));
```

### ⚠️ Limitações Conhecidas do Backend

- **Não existe endpoint `/users/me`** — o frontend deve decodificar o JWT para obter `email` e `role`.
- **Registro não faz auto-login** — após `POST /auth/register`, fazer redirect para `/login`.
- **Login retorna apenas o token** — não há objeto User na resposta de login.

---

## 📡 Consumo da API

### Services Pattern

Cada entidade tem seu service correspondente:

```typescript
// services/occurrenceService.ts
import api from './api';
import { Occurrence, CreateOccurrenceDTO, ApiResponse, ErrorResponse } from '@/types/api';

export const occurrenceService = {
  list: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences');
    return response.data.data;
  },

  getMine: async () => {
    const response = await api.get<ApiResponse<Occurrence[]>>('/occurrences/my');
    return response.data.data;
  },

  getByCategory: async (categoryId: number) => {
    const response = await api.get<ApiResponse<Occurrence[]>>(`/occurrences/category/${categoryId}`);
    return response.data.data;
  },

  create: async (data: CreateOccurrenceDTO) => {
    const response = await api.post<ApiResponse<Occurrence>>('/occurrences', data);
    return response.data.data;
  },

  updateStatus: async (id: number, status: string) => {
    // Endpoint exclusivo para ROLE_ADMIN
    const response = await api.put<ApiResponse<Occurrence>>(`/occurrences/${id}/status`, { status });
    return response.data.data;
  },

  delete: async (id: number) => {
    // Retorna ErrorResponse mesmo em sucesso (não ApiResponse)
    const response = await api.delete<ErrorResponse>(`/occurrences/${id}`);
    return response.data;
  },
};

// services/categoryService.ts
export const categoryService = {
  list: async () => {
    // ⚠️ GET retorna raw List, não ApiResponse (verificar resposta)
    const response = await api.get<Category[]>('/categories');
    return Array.isArray(response.data) ? response.data : response.data.data;
  },

  getById: async (id: number) => {
    // ⚠️ GET retorna raw object, não ApiResponse
    const response = await api.get<Category>(`/categories/${id}`);
    return typeof response.data.id === 'number' ? response.data : response.data.data;
  },

  create: async (data: { name: string; icon: string }) => {
    const response = await api.post<ApiResponse<Category>>('/categories', data);
    return response.data.data;
  },

  update: async (id: number, data: { name: string; icon: string }) => {
    const response = await api.put<ApiResponse<Category>>(`/categories/${id}`, data);
    return response.data.data;
  },

  delete: async (id: number) => {
    // Retorna ErrorResponse mesmo em sucesso
    const response = await api.delete<ErrorResponse>(`/categories/${id}`);
    return response.data;
  },
};
```

**⚠️ Avisos importantes:**
- `GET /categories` e `GET /subcategories` retornam o DTO/array **puro**, não wrapped em `ApiResponse` — tratamento especial necessário nos services.
- `DELETE` endpoints retornam `ErrorResponse` mesmo em caso de sucesso (não `ApiResponse`).
- Ambos requerem `ROLE_ADMIN` (não apenas admin pode escrever).

### Tipos (Mirror do Backend)

```typescript
// types/api.ts

export interface User {
  id: string;  // UUID
  email: string;
  name: string;
  role: 'USER' | 'ADMIN';
}

export interface Category {
  id: number;
  name: string;
  icon: string;
  userId: string;  // UUID do criador
  // Nota: GET /categories requer ROLE_ADMIN (limitação do backend)
}

export interface Subcategory {
  subcategoryId: number;  // campo nomeado subcategoryId, não id
  name: string;
  categoryName: string;  // nome da categoria, não categoryId
  // Nota: GET /subcategories requer ROLE_ADMIN; sem filtro ?categoryId= (filtrar client-side)
}

export interface Occurrence {
  title: string;
  description: string;
  location: string;  // texto livre (sem latitude/longitude no backend)
  status: 'ABERTO' | 'EM_ANALISE' | 'RESOLVIDO' | 'FECHADO';
  categoryName: string;  // achatado, não objeto aninhado
  subcategoryName: string;  // achatado, não objeto aninhado
  ownerId: string;  // UUID do proprietário
  // ⚠️ Limitação: OccurrenceResponseDTO NÃO inclui 'id' do próprio recurso
  // ⚠️ Limitação: sem createdAt/updatedAt (não retornados pela API)
}

export interface ApiResponse<T> {
  message: string;
  data: T;
}

export interface ErrorResponse {
  message: string;
  status: number;
  timestamp: string;  // formato: YYYY-MM-DD (data apenas, sem hora)
}

// DTO de criação de ocorrência
export interface CreateOccurrenceDTO {
  title: string;
  description: string;
  location: string;  // texto livre, não coordinates
  categoryId: number;
  subcategoryId: number;
}
```

**⚠️ Notas sobre Response DTOs:**
- `OccurrenceResponseDTO` não inclui um campo `id` — limitação conhecida do backend (impossibilita detalhe/edição/exclusão de recurso específico por ID no frontend).
- `Category` GET endpoint requer `ROLE_ADMIN` (não apenas para escrita, mas para leitura também).
- `Subcategory` GET endpoint requer `ROLE_ADMIN`; não há filtro de query `?categoryId=` — filtrar client-side pelo `categoryName` se necessário.
- Dois formatos de erro: `ErrorResponse{message,status,timestamp}` (via `GlobalExceptionHandler`) vs. padrão Spring Boot `{timestamp,status,error,path}` (via filtro de segurança em tokens inválidos/ausentes).

---

## 🎨 Componentes e Pages

### Componentes Comuns (`components/common/`)

- **Header:** Navbar com logo, menu, perfil do usuário
- **Footer:** Links úteis, copyright
- **Navigation:** Menu lateral ou top navigation
- **LoadingSpinner:** Indicador de carregamento

### Componentes UI (`components/ui/`)

- **Button:** Botões reutilizáveis com variantes
- **Card:** Container com estilo
- **Modal:** Diálogos modais
- **Input:** Input customizado com validação
- **Select:** Dropdown customizado
- **Alert:** Alertas de erro/sucesso

### Pages

#### **AuthPage/**
- `LoginPage.tsx` - Formulário de login
- `RegisterPage.tsx` - Formulário de registro

#### **OccurrencesPage/**
- `OccurrencesListPage.tsx` - Lista de ocorrências com filtros
- `OccurrenceDetailPage.tsx` - Detalhe de uma ocorrência
- `CreateOccurrencePage.tsx` - Criar nova ocorrência

#### **CategoriesPage/**
- `CategoriesPage.tsx` - Listar/gerenciar categorias (admin)

#### **SubscriptionsPage/**
- `SubscriptionsPage.tsx` - Gerenciar assinaturas do usuário

#### **DashboardPage/**
- `DashboardPage.tsx` - Dashboard com resumo de ocorrências

---

## 📝 Convenções de Desenvolvimento

### Nomenclatura

- **Componentes:** `PascalCase` → `OccurrenceCard.tsx`
- **Arquivos utilitários:** `camelCase` → `formatDate.ts`
- **Hooks customizados:** `useNomePascalCase` → `useOccurrences.ts`
- **Variáveis/funções:** `camelCase` → `handleSubmit()`
- **Constantes:** `UPPER_SNAKE_CASE` → `API_BASE_URL`
- **CSS classes (Tailwind):** Use convenção padrão do Tailwind

### Padrões de Código

#### Componentes Funcionais
```typescript
interface OccurrenceCardProps {
  occurrence: Occurrence;
  onEdit?: (id: number) => void;
  onDelete?: (id: number) => void;
}

export function OccurrenceCard({ occurrence, onEdit, onDelete }: OccurrenceCardProps) {
  return (
    <div className="rounded-lg bg-white p-4 shadow">
      <h3 className="text-lg font-semibold">{occurrence.title}</h3>
      <p className="text-gray-600">{occurrence.description}</p>
      {/* ... */}
    </div>
  );
}

export default OccurrenceCard;
```

#### Custom Hooks
```typescript
// hooks/useOccurrences.ts
export function useOccurrences() {
  const [occurrences, setOccurrences] = useState<Occurrence[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const fetchOccurrences = async () => {
      try {
        setLoading(true);
        const data = await occurrenceService.list();
        setOccurrences(data);
      } catch (err) {
        setError((err as Error).message);
      } finally {
        setLoading(false);
      }
    };

    fetchOccurrences();
  }, []);

  return { occurrences, loading, error };
}
```

#### Formulários com React Hook Form
```typescript
import { useForm } from 'react-hook-form';
import { z } from 'zod';

const createOccurrenceSchema = z.object({
  title: z.string().min(3, 'Título deve ter no mínimo 3 caracteres'),
  description: z.string().min(10, 'Descrição deve ter no mínimo 10 caracteres'),
  latitude: z.number(),
  longitude: z.number(),
  categoryId: z.number(),
  subcategoryId: z.number(),
});

type CreateOccurrenceFormData = z.infer<typeof createOccurrenceSchema>;

export function CreateOccurrenceForm() {
  const { register, handleSubmit, formState: { errors } } = useForm<CreateOccurrenceFormData>({
    resolver: zodResolver(createOccurrenceSchema),
  });

  const onSubmit = async (data: CreateOccurrenceFormData) => {
    try {
      await occurrenceService.create(data);
      // Sucesso
    } catch (error) {
      // Erro
    }
  };

  return (
    <form onSubmit={handleSubmit(onSubmit)}>
      {/* Campos do formulário */}
    </form>
  );
}
```

### Estilo com Tailwind CSS

- Use classes Tailwind para tudo
- Evite arquivos CSS customizados quando possível
- Para componentes complexos, use `@apply` no CSS global
- Organize com mobile-first (sm:, md:, lg:)

```typescript
export function Button({ variant = 'primary', ...props }: ButtonProps) {
  const baseStyles = 'px-4 py-2 rounded font-medium transition-colors';
  const variants = {
    primary: 'bg-blue-600 text-white hover:bg-blue-700',
    secondary: 'bg-gray-200 text-gray-800 hover:bg-gray-300',
    danger: 'bg-red-600 text-white hover:bg-red-700',
  };

  return (
    <button className={`${baseStyles} ${variants[variant]}`} {...props} />
  );
}
```

### Tratamento de Erros

```typescript
// utils/errorHandler.ts
import axios from 'axios';

export function getErrorMessage(error: unknown): string {
  if (axios.isAxiosError(error)) {
    // Formato 1: ErrorResponse via GlobalExceptionHandler
    if (error.response?.data?.message && typeof error.response.data.message === 'string') {
      return error.response.data.message;
    }
    // Formato 2: erro padrão Spring Boot (filtro de segurança, token inválido)
    if (error.response?.data?.error) {
      return error.response.data.error;
    }
    // Fallback
    return error.message || 'Erro na requisição';
  }
  if (error instanceof Error) {
    return error.message;
  }
  return 'Erro desconhecido';
}

export function getHttpStatus(error: unknown): number {
  if (axios.isAxiosError(error)) {
    return error.response?.status || 500;
  }
  return 500;
}
```

**⚠️ Nota sobre tratamento de erro:**
- Existem dois formatos de erro retornados pelo backend:
  1. **`ErrorResponse{message,status,timestamp}`** — quando lançada uma exceção no código de aplicação (via `GlobalExceptionHandler`).
  2. **Padrão Spring Boot `{timestamp,status,error,path}`** — quando o filtro de segurança rejeita um token ausente/inválido antes de chegar ao handler.
- O código acima trata ambos os casos.

---

## 🧪 Testes

### Testes Unitários (Vitest)

```typescript
// components/__tests__/Button.test.tsx
import { render, screen } from '@testing-library/react';
import { Button } from '../Button';

describe('Button Component', () => {
  it('renders with correct text', () => {
    render(<Button>Click me</Button>);
    expect(screen.getByText('Click me')).toBeInTheDocument();
  });

  it('applies primary variant styles', () => {
    render(<Button variant="primary">Click me</Button>);
    const button = screen.getByRole('button');
    expect(button).toHaveClass('bg-blue-600');
  });
});
```

### Testes E2E (Playwright)

```typescript
// e2e/occurrences.spec.ts
import { test, expect } from '@playwright/test';

test('user can create an occurrence', async ({ page }) => {
  await page.goto('/login');
  await page.fill('input[type="email"]', 'user@example.com');
  await page.fill('input[type="password"]', 'password123');
  await page.click('button[type="submit"]');
  
  await page.goto('/occurrences/new');
  await page.fill('input[name="title"]', 'Buraco na rua');
  await page.click('button[type="submit"]');
  
  await expect(page).toHaveURL('/occurrences');
});
```

---

## 🔧 Variáveis de Ambiente

```bash
# .env.example
VITE_API_URL=http://localhost:8080
VITE_APP_NAME=Talkie
VITE_JWT_TOKEN_KEY=talkie_auth_token
```

---

## 🚀 Comandos de Desenvolvimento

```bash
# Instalar dependências
npm install

# Rodar em desenvolvimento
npm run dev

# Build para produção
npm run build

# Preview do build
npm run preview

# Rodar testes
npm run test

# Rodar testes com coverage
npm run test:coverage

# Rodar testes E2E
npm run test:e2e

# Lint
npm run lint

# Format código
npm run format
```

---

## 🎨 Design System / Referências

- **Cores:** Definir paleta baseada em Tailwind colors
- **Tipografia:** Use font-sizes padrão do Tailwind (sm, base, lg, xl, 2xl)
- **Spacing:** Use escala de padding/margin do Tailwind
- **Componentes:** Seguir padrão de componentes headless ou criar próprios baseado em designs

---

## 📱 Responsividade

- **Mobile-first:** Escrever estilos para mobile primeiro
- **Breakpoints do Tailwind:**
  - `sm: 640px`
  - `md: 768px`
  - `lg: 1024px`
  - `xl: 1280px`

```typescript
// Exemplo de layout responsivo
<div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
  {occurrences.map(occ => (
    <OccurrenceCard key={occ.id} occurrence={occ} />
  ))}
</div>
```

---

## 🔗 Integração com Backend

### Endpoints Principais

**⚠️ Base URL: `http://localhost:8080` (sem prefixo `/api`)**

| Método | Endpoint | Autenticação | Descrição | Notas |
|--------|----------|--------------|-----------|-------|
| POST | `/auth/login` | ❌ | Login | Retorna `{ message, data: "<jwt>" }` |
| POST | `/auth/register` | ❌ | Registro | Retorna `{ message, data: { id, name, email } }` (sem token) |
| GET | `/occurrences` | ✅ USER/ADMIN | Listar todas | Retorna `ApiResponse<Occurrence[]>` |
| GET | `/occurrences/my` | ✅ USER/ADMIN | Listar minhas | Retorna `ApiResponse<Occurrence[]>` |
| GET | `/occurrences/category/{categoryId}` | ✅ USER/ADMIN | Por categoria | Retorna `ApiResponse<Occurrence[]>` |
| POST | `/occurrences` | ✅ USER/ADMIN | Criar ocorrência | Retorna `ApiResponse<Occurrence>` |
| PUT | `/occurrences/{id}/status` | ✅ **ADMIN** | Atualizar status | Exclusive admin; retorna `ApiResponse<Occurrence>` |
| DELETE | `/occurrences/{id}` | ✅ **ADMIN** | Deletar | Retorna `ErrorResponse` em sucesso |
| GET | `/categories` | ✅ **ADMIN** | Listar categorias | ⚠️ Requer ADMIN (não apenas auth) |
| GET | `/categories/{id}` | ✅ **ADMIN** | Detalhe categoria | ⚠️ Requer ADMIN; retorna raw DTO |
| POST | `/categories` | ✅ **ADMIN** | Criar categoria | Retorna `ApiResponse<Category>` |
| PUT | `/categories/{id}` | ✅ **ADMIN** | Atualizar categoria | Retorna `ApiResponse<Category>` |
| DELETE | `/categories/{id}` | ✅ **ADMIN** | Deletar categoria | Retorna `ErrorResponse` em sucesso |
| GET | `/subcategories` | ✅ **ADMIN** | Listar subcategorias | ⚠️ Requer ADMIN; sem filtro categoryId |
| GET | `/subcategories/{id}` | ✅ **ADMIN** | Detalhe subcategoria | ⚠️ Requer ADMIN; retorna raw DTO |
| POST | `/subcategories` | ✅ **ADMIN** | Criar subcategoria | Retorna `ApiResponse<Subcategory>` |
| PUT | `/subcategories/{id}` | ✅ **ADMIN** | Atualizar subcategoria | Retorna `ApiResponse<Subcategory>` |
| DELETE | `/subcategories/{id}` | ✅ **ADMIN** | Deletar subcategoria | Retorna `ErrorResponse` em sucesso |
| POST | `/subscriptions/{categoryId}` | ✅ USER/ADMIN | Assinar categoria | Retorna `ApiResponse<Subscription>` |
| DELETE | `/subscriptions/{categoryId}` | ✅ USER/ADMIN | Desassinar | Retorna `ApiResponse<Void>` |
| GET | `/subscriptions/my` | ✅ USER/ADMIN | Minhas assinaturas | Retorna `ApiResponse<Subscription[]>` |

---

## ✅ Checklist para Novas Features

- [ ] Componente criado com TypeScript types completos
- [ ] Service criado para chamar API
- [ ] Tipos (interfaces) criados/atualizados
- [ ] Hook customizado criado se necessário
- [ ] Testes unitários adicionados
- [ ] Testes E2E adicionados
- [ ] Responsividade testada (mobile, tablet, desktop)
- [ ] Tratamento de erros implementado
- [ ] Loading states implementados
- [ ] Documentação adicionada

---

## 🚨 O que SEMPRE fazer

- ✅ Usar TypeScript em todos os arquivos
- ✅ Criar types para dados da API
- ✅ Usar Tailwind para estilos
- ✅ Separar lógica em services
- ✅ Validar input com Zod/Yup
- ✅ Tratamento de erro global
- ✅ Loader e estado de loading em requisições

---

## 🚫 O que EVITAR

- ❌ Lógica de API direta nos componentes (usar services)
- ❌ Estilos inline (usar Tailwind)
- ❌ Props drilling profundo (usar Context/Zustand)
- ❌ Componentes muito grandes (quebrar em menores)
- ❌ Requisições sem tratamento de erro
- ❌ Magic strings (usar constantes)
- ❌ Dados mockados em produção

---

## 📚 Recursos

- **Documentação React:** https://react.dev
- **Tailwind CSS:** https://tailwindcss.com
- **Vite:** https://vitejs.dev
- **TypeScript:** https://www.typescriptlang.org
- **Zustand:** https://github.com/pmndrs/zustand
- **API Backend (Swagger):** http://localhost:8080/swagger-ui/index.html

---

## 👥 Contato & Suporte

- Referência do backend: `talkie/CLAUDE.md`
- Documentação API: Swagger (URL acima)
- Issues: GitHub Issues do repositório

---

**Desenvolvido com ❤️ para melhorar a comunicação comunitária**

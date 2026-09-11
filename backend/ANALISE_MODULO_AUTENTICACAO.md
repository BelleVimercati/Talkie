# Análise Técnica: Módulo de Autenticação do Talkie

**Documento de apoio para TCC — Trabalho de Conclusão de Curso**

---

## Sumário

1. [Visão Geral](#visão-geral)
2. [Arquitetura e Fluxo de Autenticação](#arquitetura-e-fluxo-de-autenticação)
3. [Modelo de Dados](#modelo-de-dados)
4. [Pontos Fortes](#pontos-fortes)
5. [Pontos Fracos e Sugestões de Melhoria](#pontos-fracos-e-sugestões-de-melhoria)
6. [Sugestões de Redação para o TCC](#sugestões-de-redação-para-o-tcc)
7. [Diagramas Recomendados](#diagramas-recomendados)
8. [Checklist de Segurança](#checklist-de-segurança)

---

## Visão Geral

O **Talkie** é uma plataforma de gestão de ocorrências comunitárias desenvolvida em **Java 17** com **Spring Boot 3.3.5**. O módulo de autenticação utiliza os seguintes componentes:

- **Framework de Segurança:** Spring Security 6+
- **Criptografia de Token:** JWT (JSON Web Tokens) via biblioteca Auth0 java-jwt
- **Hash de Senha:** BCrypt (Spring Security `BCryptPasswordEncoder`)
- **Estratégia de Sessão:** Stateless (sem estado no servidor)
- **Banco de Dados:** PostgreSQL (produção), H2 in-memory (testes)

A análise foi realizada inspecionando em detalhes os arquivos de configuração, entidades de domínio, camada de segurança, serviços de negócio e testes de integração relacionados à autenticação.

---

## Arquitetura e Fluxo de Autenticação

O módulo de autenticação segue uma **arquitetura em camadas** com dois fluxos principais: **Registro (Register)** e **Autenticação (Login)**.

### Fluxo de Registro

```
Cliente → POST /auth/register (RegisterDTO)
    ↓
AuthController.register()
    ↓
AuthService.register()
    - Valida duplicação de CPF e Email
    - Hash de senha com BCrypt
    - Atribui role padrão (ROLE_USER)
    - Persiste novo User no banco
    ↓
Retorna UserResponseDTO (id, name, email)
```

### Fluxo de Login

```
Cliente → POST /auth/login (LoginRequestDTO)
    ↓
AuthController.login()
    - Busca usuário por email (UserRepository)
    - Valida senha com BCryptPasswordEncoder.matches()
    ↓
TokenService.generateToken()
    - Cria JWT com claims: issuer, subject (email), role, expiração (2 horas)
    - Assina com HMAC256 usando secret configurado
    ↓
Retorna token JWT em ApiResponse
```

### Fluxo de Validação de Requisição Autenticada

```
Cliente → GET /endpoint (com header "Authorization: Bearer <token>")
    ↓
SecurityFilter.doFilterInternal()
    - Extrai token do header Authorization
    - Valida token com TokenService.validateToken()
    - Extrai role via TokenService.getRole()
    ↓
UserRepository.findByEmail() → Recupera User do banco
    ↓
Cria UsernamePasswordAuthenticationToken com User como principal
    ↓
Injeta no SecurityContext
    ↓
Requisição prossegue autenticada
```

### Componentes Principais

| Componente | Responsabilidade | Padrão |
|---|---|---|
| `SecurityConfig` | Configuração centralizada de permissões (roles) | Configuration Bean + RBAC |
| `SecurityFilter` | Validação de token JWT em cada requisição | Filter pattern (Spring Security) |
| `TokenService` | Geração e validação de JWT | Serviço de infraestrutura |
| `AuthService` | Lógica de registro (validações e hash) | Serviço de negócio (@Service) |
| `AuthenticatedUser` | Utilitário stateless para acessar usuário logado | Padrão Facade + ThreadLocal (SecurityContext) |
| `CustomUserDetailsService` | Integração com Spring Security UserDetailsService | SPI pattern |

### Autorização por Roles

- **ROLE_USER** (padrão): Acesso a `/auth/login`, `/auth/register`, endpoints públicos e recurso próprio
- **ROLE_ADMIN**: Acesso pleno a `/categories/**`, `/subcategories/**`, PUT/DELETE em `/occurrences/**`

A configuração em `SecurityConfig.securityFilterChain()` centraliza todas as regras de autorização via `authorizeHttpRequests()`, facilitando auditoria e manutenção.

---

## Modelo de Dados

### Entidade User

A entidade `User` representa um usuário autenticado no sistema.

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;              // Identificador único (UUID)
    
    private String name;          // Nome completo do usuário
    
    @Column(unique = true)
    private String email;         // Email único (usado como "subject" no JWT)
    
    private String password;      // Hash bcrypt da senha
    
    @Column(unique = true)
    private String cpf;           // CPF único (identificação brasileira)
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;            // ROLE_USER ou ROLE_ADMIN
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Category> categories; // Categorias criadas pelo usuário
}
```

### Esquema de Banco de Dados

Tabela `users`:

| Coluna | Tipo | Restrições | Finalidade |
|---|---|---|---|
| `id` | UUID | PRIMARY KEY | Identificador único |
| `name` | VARCHAR(255) | NOT NULL | Nome do usuário |
| `email` | VARCHAR(255) | UNIQUE, NOT NULL | Login único + subject do JWT |
| `password` | VARCHAR(255) | NOT NULL | Hash bcrypt (60 caracteres) |
| `cpf` | VARCHAR(20) | UNIQUE, NOT NULL | Documento de identidade único |
| `role` | VARCHAR(20) | NOT NULL | `USER` ou `ADMIN` (armazenado como string enum) |

### Relacionamentos

- **1:N com Category** — Um usuário pode criar múltiplas categorias (com cascade delete).
- Não há relacionamento direto com Occurrence (a ocorrência referencia o usuário via campo `owner`).

---

## Pontos Fortes

### 1. Arquitetura Stateless Bem Implementada
- Uso de JWT elimina necessidade de sessão no servidor, facilitando escalabilidade horizontal
- Spring Security configurado corretamente com `SessionCreationPolicy.STATELESS`

### 2. Separação de Responsabilidades Clara
- Lógica de registro isolada em `AuthService`
- Validação de token separada em `TokenService`
- Utilitário `AuthenticatedUser` fornece interface limpa para acessar o usuário logado (sem espionar SecurityContext em todo o código)

### 3. Criptografia Robusta
- Uso de `BCryptPasswordEncoder` com salt automático (padrão de 10 rodadas)
- Não há senhas em texto plano ou hash fraco (MD5, SHA1)

### 4. DTOs com Validação (Bean Validation)
- Records imutáveis para `LoginRequestDTO` e `RegisterDTO` reduzem superfície de ataque
- Anotações `@Email`, `@NotBlank` validam dados na entrada
- Validação ocorre antes de chegar ao serviço

### 5. Testes de Integração Cobrindo Fluxos Críticos
- `AuthControllerIT` valida cenários: registro bem-sucedido, duplicação de CPF/email, login com credenciais válidas/inválidas
- Testes BDD (`AuthSteps`) suportam cenários de autenticação nos testes de aceitação
- Perfil `@ActiveProfiles("test")` garante isolamento de ambiente

### 6. Tratamento de Exceções Consistente
- Exceção `UnauthorizedException` lançada quando usuário não está autenticado
- `NotFoundException` e `BadRequestException` reutilizadas para casos específicos
- `GlobalExceptionHandler` centraliza respostas de erro

### 7. Configuração de Segurança Centralizada
- `SecurityConfig` é a fonte única de verdade para permissões de rota
- Fácil auditar quem pode acessar o quê

---

## Pontos Fracos e Sugestões de Melhoria

### ⚠️ 1. Secret do JWT Armazenado em Texto Plano (CRÍTICO)

**Problema:** `api.security.token.secret=my-secret` no arquivo `application.properties` é um exemplo fraco e exposto no git.

**Impacto:** Em produção, se o repositório for comprometido, qualquer pessoa pode forjar JWTs válidos.

**Sugestão:** Usar variáveis de ambiente:
```properties
api.security.token.secret=${JWT_SECRET:fallback-dev-only}
```

**Prioridade:** 🔴 CRÍTICA

---

### ⚠️ 2. Token JWT com Expiração Curta (2 horas) sem Refresh Token (MÉDIA)

**Problema:** Usuários são deslogados a cada 2 horas sem mecanismo de refresh.

**Sugestão:** Implementar refresh token:
- Access token: 15 min
- Refresh token: 7 dias (armazenado na tabela `refresh_tokens`)
- Cliente reutiliza refresh token para obter novo access token

**Prioridade:** 🟡 MÉDIA

---

### ⚠️ 3. Falha Silenciosa em Validação de Token (ALTA)

**Problema:** Em `SecurityFilter.doFilterInternal()`, se `tokenService.validateToken()` retorna `null`, a requisição passa sem autenticação.

**Código problemático:**
```java
if (token != null) {
    var login = tokenService.validateToken(token);
    if (login != null && role != null) {
        // autentica
    }
}
// se token inválido, apenas continua sem autenticar!
```

**Impacto:** Um token expirado ou inválido não causa erro explícito; requisição protegida falha apenas quando `@Secured` é validado.

**Sugestão:** Lançar exceção `InvalidTokenException` ao invés de retornar `null`.

**Prioridade:** 🔴 ALTA

---

### ⚠️ 4. Sem Rate Limiting ou Proteção contra Brute Force (MÉDIA)

**Problema:** `/auth/login` permite tentativas ilimitadas de adivinhação de senha.

**Sugestão:** Implementar rate limiting (ex: 5 tentativas fallidas em 15 min = bloqueio temporário).

**Prioridade:** 🟡 MÉDIA

---

### ⚠️ 5. CustomUserDetailsService não é Usado (BAIXA)

**Problema:** Implementa `UserDetailsService` mas nunca é chamado no fluxo de autenticação.

**Sugestão:** Remover se não for necessário ou integrar no fluxo.

**Prioridade:** 🟢 BAIXA

---

### ⚠️ 6. Sem Logging de Eventos de Segurança (MÉDIA)

**Problema:** Logins bem-sucedidos/falhados não são registrados para auditoria.

**Sugestão:** Adicionar logs estruturados em `AuthService`, `AuthController` e `SecurityFilter`.

**Prioridade:** 🟡 MÉDIA

---

### ⚠️ 7. Sem Testes Unitários para TokenService (MÉDIA)

**Problema:** Geração e validação de JWT não possuem testes unitários (apenas integração).

**Sugestão:** Adicionar `TokenServiceTest` com casos: expiração, secret inválido, claims corretos.

**Prioridade:** 🟡 MÉDIA

---

## Sugestões de Redação para o TCC

### Exemplo 1: Seção de Arquitetura de Autenticação

> O módulo de autenticação foi arquitetado seguindo o padrão **stateless** com JSON Web Tokens (JWT), eliminando a necessidade de manutenção de sessões no servidor. Esse padrão facilita a escalabilidade horizontal, permitindo que múltiplas instâncias da aplicação compartilhem validação de token sem sincronização de estado.
> 
> O fluxo de autenticação é dividido em três etapas: (1) **Registro**, onde novas credenciais são criadas, validadas e armazenadas com hash bcrypt; (2) **Login**, onde as credenciais são verificadas e um token JWT com validade de 2 horas é emitido; e (3) **Autorização**, onde cada requisição subsequente inclui o token no header `Authorization: Bearer <token>`, validado por um filtro customizado (`SecurityFilter`) que injeta o usuário autenticado no contexto de segurança do Spring.
> 
> A autorização é baseada em papéis (_role-based access control_, RBAC), com dois níveis: usuários comuns (`ROLE_USER`) acessam apenas recursos próprios, enquanto administradores (`ROLE_ADMIN`) gerenciam categorias e moderação de ocorrências. As regras de acesso são centralizadas em uma classe de configuração (`SecurityConfig`), facilitando auditoria e manutenção.

### Exemplo 2: Seção de Modelo de Dados (Entidades)

> A entidade `User` representa um usuário autenticado no sistema, identificado de forma única por um UUID gerado automaticamente no banco de dados. Os atributos principais incluem nome, email (campo único utilizado como identificador de login), CPF (documento de identidade único no contexto brasileiro) e a senha armazenada em hash bcrypt com salt automático. Um campo adicional armazena o papel do usuário (USER ou ADMIN), permitindo diferenciação de permissões.
> 
> A entidade mantém um relacionamento one-to-many com a tabela de categorias, indicando que um usuário pode criar múltiplas categorias. A deleção em cascata foi configurada, de modo que ao remover um usuário, suas categorias são igualmente removidas, mantendo integridade referencial.

### Exemplo 3: Seção de Limitações e Trabalhos Futuros

> Entre as limitações identificadas no módulo de autenticação, destaca-se a ausência de mecanismo de _refresh token_, obrigando os usuários a efetuar novo login a cada expiração de token (2 horas). Futuros desenvolvimentos poderiam implementar um fluxo de refresh token de longa validade, reduzindo a frequência de logins.
>
> Além disso, o sistema carece de proteção contra ataques de força bruta no endpoint de login, permitindo tentativas ilimitadas de adivinhação de senha. A implementação de _rate limiting_ baseado em IP ou email seria uma melhoria relevante. Por fim, não há auditoria estruturada de eventos de autenticação (logins bem-sucedidos, falhas, mudanças de permissão), essencial para conformidade com regulamentos de segurança em sistemas de produção.

### Exemplo 4: Parágrafo sobre Escolhas Técnicas

> A escolha de JWT em detrimento de sessões servidor foi motivada pela necessidade de statelessness, permitindo que a API seja implantada em múltiplos nós sem dependência de cache compartilhado (Redis, Memcached). O algoritmo de criptografia utilizado é HMAC256, oferecendo bom equilíbrio entre segurança e performance. A expiração configurada em 2 horas reduz o risco de token capturado, sendo um valor recomendado para aplicações de risco moderado; sistemas de maior criticidade poderiam optar por expiração mais curta (15 minutos) com refresh token.

---

## Diagramas Recomendados

Para enriquecer seu TCC, considere incluir os seguintes diagramas:

1. **Diagrama de Sequência (UML)** — Fluxo de login passo a passo
2. **Diagrama de Classes** — Relacionamentos entre `User`, `Role`, `TokenService`, `SecurityFilter`, `AuthService`
3. **Diagrama de Componentes** — Integração de Spring Security, JWT, Banco de Dados
4. **Tabela de Entidade-Relacionamento (ER)** — Entidade `User` e seus relacionamentos

---

## Checklist de Segurança

Use este checklist para validar a cobertura de segurança do seu módulo de autenticação:

- [x] Autenticação implementada
- [x] Criptografia de senha (bcrypt)
- [x] Autorização baseada em papéis
- [x] Validação de entrada (DTOs)
- [x] Testes de integração
- [ ] Testes unitários de token (TokenService)
- [ ] Proteção contra brute force
- [ ] Refresh token / renovação de sessão
- [ ] Logging e auditoria de segurança
- [ ] Secret do JWT não hardcoded
- [ ] Documentação de fluxo de autenticação

---

## Nota Final

**Este documento é um rascunho de apoio para sua monografia.** Revise, adapte ao estilo acadêmico de sua instituição (ABNT, IEEE, etc.) e complemente com referências bibliográficas:

- Spring Security Documentation: https://spring.io/projects/spring-security
- JWT RFC 7519: https://tools.ietf.org/html/rfc7519
- OWASP Authentication Cheat Sheet: https://cheatsheetseries.owasp.org/

**Não entregue este texto como está; use-o como base para sua própria redação acadêmica.**

---

**Documento gerado:** 21 de agosto de 2026  
**Projeto:** Talkie  
**Foco:** Módulo de Autenticação

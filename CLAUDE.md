# CLAUDE.md — Talkie

Guia para agentes de IA e desenvolvedores implementarem novas funcionalidades no Talkie de forma consistente.

## Visão geral

O **Talkie** é uma plataforma de gestão de ocorrências comunitárias (TCC — UFF). Cidadãos reportam problemas classificados em categorias/subcategorias; administradores acompanham e mudam o status; usuários assinam categorias e recebem notificações por e-mail.

Monorepo:

| Pasta | Stack | Guia específico |
|-------|-------|-----------------|
| `backend/` | Java 17, Spring Boot 3.3.5, PostgreSQL, Flyway, JWT | [backend/CLAUDE.md](backend/CLAUDE.md) |
| `frontend/` | Vite 5, React 18, TypeScript, Tailwind, zustand | [frontend/CLAUDE.md](frontend/CLAUDE.md) |

Documentação de negócio: `backend/docs/` (requisitos, regras de negócio, fluxo de eventos, plano do TCC).

**Idioma:** código (classes, variáveis) em inglês; mensagens ao usuário, rotas do front, commits e documentação em **português**.

## Rodando localmente

```bash
# Infra (Postgres 16 :5432, MailHog SMTP :1025 / UI :8025) + API (:8080)
cd backend && docker compose up -d postgres mailhog

# API (Windows: mvnw.cmd)
cd backend && ./mvnw spring-boot:run

# Frontend (:5173) — copiar .env.example para .env.local na primeira vez
cd frontend && npm install && npm run dev
```

- Swagger: http://localhost:8080/swagger-ui.html
- E-mails enviados: http://localhost:8025 (MailHog)
- Admin criado pela migration V7: `admin@talkie.local` / `123456`

## Contrato backend ↔ frontend

- **Sucesso:** sempre `ApiResponse<T>` → `{ "message": "...", "data": ... }` (`dto/response/ApiResponse.java` ↔ `frontend/src/types/api.ts`). No front, os services retornam `response.data.data`.
- **Erro:** `ErrorResponse` → `{ "message", "status", "timestamp" }`. No front, use `getErrorMessage(err)` de `src/utils/errorHandler.ts`.
- **Tipos:** `frontend/src/types/*.ts` espelham os records de `backend/.../dto/`. Ao alterar um DTO, atualize o tipo correspondente no mesmo trabalho.
- **Auth:** JWT stateless. O front guarda o token em `localStorage[VITE_JWT_TOKEN_KEY]` e o interceptor de `services/api.ts` envia `Authorization: Bearer`. O token traz `sub` (e-mail) e `role`.
- **CORS:** só `http://localhost:5173` (`SecurityConfig.corsConfigurationSource`). Novos métodos/headers precisam ser liberados lá.

## Domínio

| Entidade | Pontos-chave |
|----------|--------------|
| `User` | ID `UUID`; `Role` = `USER` \| `ADMIN` |
| `Category` | tabela `types`; `CategoryPriority` (BAIXA, MEDIA, ALTA), cor, ícone |
| `Subcategory` | pertence a uma `Category` |
| `Occurrence` | título, descrição, local, `occurredAt`, latitude/longitude, `imagePath`, `OccurrenceStatus` (ABERTO, EM_ANALISE, RESOLVIDO, FECHADO), `resolvedAt` |
| `Subscription` | usuário assina uma categoria |
| `OccurrenceStatusHistory` | evento de criação/mudança de status, com motivo e autor (anulável) |

Fluxo de eventos: `OccurrenceService.create` publica `OccurrenceCreatedEvent` → `OccurrenceNotificationListener` → `EmailService` envia e-mail aos assinantes da categoria.

Permissões (`infra/security/SecurityConfig.java`): `ADMIN` lista todas as ocorrências, muda status, deleta ocorrências, gerencia categorias/subcategorias e usuários. Demais rotas exigem apenas autenticação; `/auth/login` e `/auth/register` são públicas. No front, rotas de admin usam `<ProtectedRoute requireAdmin>`.

## Fluxo para uma feature full-stack

1. **Migration** `backend/src/main/resources/db/migration/V{n}__descricao_em_snake_case.sql`.
2. **Entidade** em `domain/<contexto>/`.
3. **Repository** (`JpaRepository`).
4. **DTOs** (`record`) em `dto/request` e `dto/response`.
5. **Service** com a regra de negócio e o mapeamento entidade → DTO.
6. **Controller** retornando `ResponseEntity<ApiResponse<T>>`.
7. **`SecurityConfig`** se a rota tiver permissão específica.
8. **Teste** `*ControllerIT` (e/ou cenário Cucumber).
9. **Front:** tipo em `types/` → função em `services/` → hook `useXxx` (se for leitura reutilizável) → schema zod em `schemas/` (se houver formulário) → página/componente → rota em `App.tsx`.

## Regras importantes

- **Flyway + `ddl-auto=validate`:** toda mudança em entidade exige nova migration; senão a aplicação não sobe. Nunca edite uma migration já aplicada. Confira o maior `V{n}` existente antes de criar a próxima (já houve conflito de versão).
- **Não commitar** sem pedido explícito. Ao commitar: prefixos `feat:`, `fix:`, `refactor:`, `test:`, descrição em português, minúscula e no imperativo (ex.: `feat: adicionar filtro por status`). Branches `feat/<descricao>`; PR para `main`.
- **Não gerar nem versionar `.js`/`.js.map` dentro de `frontend/src`.** O `tsconfig` usa `noEmit`; se aparecerem, apague-os (o Vite pode servir o `.js` velho no lugar do `.tsx`).
- Não versionar segredos (`.env.local`, credenciais). O `api.security.token.secret` em `application.properties` é só para desenvolvimento.

## Armadilhas conhecidas / pendências

- **Lombok:** precisa ser ≥ 1.18.38 com o JDK atual (1.18.30 quebra a compilação). A versão aparece no `pom.xml` em dois lugares (dependência e `annotationProcessorPaths`).
- **Bean Validation:** `spring-boot-starter-validation` **não** está no `pom.xml`, então `@Valid` / `@NotBlank` nos DTOs não são aplicados. Valide no service ou adicione a dependência (e trate `MethodArgumentNotValidException` no `GlobalExceptionHandler`).
- **Upload de imagem:** o front chama `POST/GET /occurrences/{id}/image`, mas o endpoint ainda não existe no backend (a coluna `image_path` e o `imageUrl` do DTO já existem).
- **`GlobalExceptionHandler`** converte qualquer `RuntimeException` em 400. Lance `NotFoundException` / `BadRequestException` / `UnauthorizedException` para ter o status correto.
- **Exclusão de conta** anonimiza em vez de apagar ocorrências: `owner`, `changedBy` e `Category.user` podem ser `null` ("Usuário excluído" no front, via `formatAuthor`). Ao criar uma nova FK para `users`, trate-a em `UserService.deleteAccount`. Detalhes: `backend/docs/EXCLUSAO_DE_CONTA.md`.
- **Geolocalização** (`useGeolocation`/`LocationPicker`): a Geolocation API só funciona em `localhost` ou HTTPS — o deploy precisa de HTTPS. Endereço sugerido via Nominatim (OpenStreetMap), chamado direto do navegador. O backend valida o par latitude/longitude e as faixas em `OccurrenceService.validateCoordinates`.
- **JWT** carrega `uid`; o `SecurityFilter` rejeita tokens cujo `uid` não bate com o usuário do e-mail.
- **`UserController`** é inconsistente (retorna listas cruas / `ErrorResponse` no sucesso). Não use como modelo; siga `OccurrenceController`.
- **Frontend sem testes e sem ESLint instalado:** valide com `npm run build` (roda `tsc`). O script `lint` existe, mas o ESLint não está nas dependências.

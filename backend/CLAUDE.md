# CLAUDE.md — Talkie Backend

API REST do Talkie. Visão geral do projeto, contrato com o frontend e fluxo full-stack: [../CLAUDE.md](../CLAUDE.md).

## Stack

- Java 17, Spring Boot 3.3.5, Maven (wrapper `mvnw` / `mvnw.cmd`)
- Spring Web, Security, Data JPA, Mail (+ Thymeleaf para templates de e-mail)
- PostgreSQL 16 + **Flyway** (produção/dev) · H2 (testes)
- JWT com `com.auth0:java-jwt` · BCrypt
- Lombok **1.18.38** (versões antigas quebram com o JDK atual) · springdoc-openapi (Swagger)
- Testes: JUnit 5, MockMvc, Cucumber 7 (BDD)

## Comandos

```bash
./mvnw spring-boot:run                      # API em :8080 (precisa do Postgres rodando)
./mvnw clean package -DskipTests            # build
./mvnw test                                 # todos os testes (IT + Cucumber), usam H2
./mvnw test -Dtest=OccurrenceControllerIT   # uma classe
./mvnw test -Dtest=CucumberRunnerTest       # só os cenários BDD
docker compose up -d postgres mailhog       # infra local (Postgres :5432, MailHog :1025/:8025)
```

No Windows/PowerShell use `.\mvnw.cmd` no lugar de `./mvnw`.

## Configuração

- `src/main/resources/application.properties`: datasource via env (`SPRING_DATASOURCE_*`, padrão `localhost:5432/tcc_db`), `ddl-auto=validate`, SMTP do MailHog, `api.security.token.secret`.
- `src/main/resources/application-test.properties` e `src/test/resources/application.properties`: H2 em memória com `create-drop` (`spring.flyway.enabled=false`: as migrations são específicas do PostgreSQL).
- Swagger: `http://localhost:8080/swagger-ui.html`.

## Estrutura

```
src/main/java/com/tcc/talkie/
├── config/          # OpenApiConfig
├── controller/      # REST: Auth, Category, Subcategory, Occurrence, Subscription, User
├── domain/          # Entidades JPA + enums, por contexto: user/, category/, occurrence/
├── dto/
│   ├── request/     # records de entrada (OccurrenceDTO, CategoryCreateDTO, ...)
│   ├── response/    # records de saída (XxxResponseDTO) + ApiResponse<T>
│   └── ErrorResponse.java
├── events/          # OccurrenceCreatedEvent
├── listeners/       # OccurrenceNotificationListener (@EventListener)
├── infra/
│   ├── exceptions/  # GlobalExceptionHandler + BadRequest/NotFound/Unauthorized
│   └── security/    # SecurityConfig, SecurityFilter, TokenService, AuthenticatedUser
├── repository/      # JpaRepository
└── service/         # regra de negócio (inclui EmailService)
src/main/resources/db/migration/   # Flyway
```

## Convenções de código

**Controllers**
- `@RestController` + `@RequestMapping("/recurso")` + `@RequiredArgsConstructor`; dependências `private final`.
- Retornam `ResponseEntity<ApiResponse<T>>` com mensagem em português: `ResponseEntity.ok(new ApiResponse<>("Ocorrência criada com sucesso", dto))`.
- Sem regra de negócio; só delegam para o service.
- Modelo a seguir: `OccurrenceController`. **Não** copie `UserController` (retorna listas cruas e `ErrorResponse` em caso de sucesso).

**Services**
- `@Service` + `@RequiredArgsConstructor` (+ `@Slf4j` quando loga).
- Concentram validação e regra de negócio; lançam `NotFoundException`, `BadRequestException` ou `UnauthorizedException`.
- Usuário logado: `AuthenticatedUser.get()` / `getId()` / `getEmail()` (`infra/security/AuthenticatedUser.java`).
- Mapeamento entidade → DTO fica no service (ex.: `OccurrenceService.mapToResponse`). Nunca exponha entidade JPA no controller.
- Ações relevantes publicam eventos via `ApplicationEventPublisher` (ex.: `OccurrenceCreatedEvent`), consumidos em `listeners/`.

**DTOs**
- Sempre `record`. Request: `XxxDTO` / `XxxCreateDTO` / `XxxUpdateDTO` em `dto/request`; response: `XxxResponseDTO` em `dto/response`.
- ⚠️ `spring-boot-starter-validation` **não** está no `pom.xml`: anotações `jakarta.validation` e `@Valid` não têm efeito hoje. Valide no service ou adicione a dependência (e um handler para `MethodArgumentNotValidException`).

**Entidades**
- `@Entity @Table(name = "...")` com `@Getter @Setter @NoArgsConstructor`.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)`; `User` usa `UUID`, demais `Long`.
- Relacionamentos `@ManyToOne` + `@JoinColumn`; enums com `@Enumerated(EnumType.STRING)`.
- Tabelas `snake_case` no plural (exceção histórica: `Category` → tabela `types`).

**Exceções**
- `GlobalExceptionHandler` (`infra/exceptions/`) mapeia `BadRequestException`→400, `NotFoundException`→404, `UnauthorizedException`→401 e **qualquer outra `RuntimeException`→400**.
- Corpo: `ErrorResponse(message, status, timestamp)`.

**Segurança**
- Regras de URL centralizadas em `SecurityConfig.securityFilterChain`; adicione a rota nova lá se exigir `hasRole("ADMIN")`. O padrão é `authenticated()`.
- Roles: `USER` (padrão no cadastro) e `ADMIN`. O JWT tem `subject` = e-mail e claim `role`.
- CORS em `SecurityConfig.corsConfigurationSource` (somente `http://localhost:5173`).

## Migrations Flyway

Local: `src/main/resources/db/migration/`. Nome: `V{n}__descricao_em_snake_case.sql`.

| Versão | Conteúdo |
|--------|----------|
| V1 | schema inicial |
| V2 | status em occurrences |
| V3 | atualização do enum de status |
| V4 | prioridade, cor e created_at em categorias |
| V5 | created_at em users |
| V6 | resolved_at em occurrences |
| V7 | seed do admin (`admin@talkie.local` / `123456`) |
| V8 | occurred_at, latitude, longitude e image_path em occurrences |
| V9 | exclusão de conta (FKs `SET NULL`/`CASCADE`, `owner_id`/`user_id` anuláveis) e tabela `occurrence_status_history` |

Regras:
- Com `ddl-auto=validate`, **toda** alteração de entidade precisa de migration correspondente.
- Nunca altere uma migration já aplicada; crie uma nova.
- Use sempre o próximo número livre (confira a pasta antes; atualize esta tabela).

## Testes

**Integração (`src/test/java/.../controller/*ControllerIT.java`)**
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class XxxControllerIT {
    @Autowired MockMvc mockMvc;           // em testes, @Autowired em campo é o padrão
    @Autowired TokenService tokenService;

    @BeforeEach
    void setup() {
        // limpar repositórios (ordem: filhos → pais), criar usuários USER e ADMIN
        userToken = tokenService.generateToken(savedUser);
    }

    @Test
    @DisplayName("Deve ... ")
    void deveFazerAlgo() throws Exception {
        mockMvc.perform(post("/rota").header("Authorization", "Bearer " + userToken) ...)
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.campo").value(...));
    }
}
```
- Nomes de teste e `@DisplayName` em português.
- Cubra: sucesso, 404, 400 e acesso negado (USER em rota de ADMIN → 403).

**BDD (Cucumber)**
- Features em `src/test/resources/features/*.feature` (em português); steps em `bdd/steps/`; contexto compartilhado em `bdd/TestContext.java`; runner `bdd/CucumberRunnerTest`.

Outros: `listeners/OccurrenceNotificationListenerIT`, `service/EmailServiceIT`. Guias em `src/test/resources/doc/`.

## Checklist de nova funcionalidade

- [ ] Migration Flyway (próximo `V{n}`) e tabela acima atualizada
- [ ] Entidade + Repository
- [ ] Records de request/response
- [ ] Service com regra, exceções corretas e mapeamento para DTO
- [ ] Controller retornando `ApiResponse<T>`
- [ ] `SecurityConfig` atualizado (se a permissão não for só "autenticado")
- [ ] `*ControllerIT` cobrindo sucesso e erros
- [ ] Tipo/serviço correspondente no frontend (ver `../frontend/CLAUDE.md`)

## Estado atual

**Implementado:** autenticação JWT; CRUD de categorias, subcategorias e ocorrências; status da ocorrência (ADMIN); assinaturas; e-mail para assinantes via evento + MailHog; data/hora e geolocalização da ocorrência (V8); histórico de status com motivo e exclusão de conta com anonimização (V9, ver `docs/EXCLUSAO_DE_CONTA.md`).

**Em andamento:** upload/download de imagem (`POST/GET /occurrences/{id}/image`). O front já consome e o `mapToResponse` já gera `imageUrl`, mas o endpoint não existe.

**Planejado:** upvotes, dashboard administrativo com métricas.

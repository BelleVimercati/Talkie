# CLAUDE.md

Este arquivo serve como guia de contexto para agentes de IA (Claude, Cursor, Copilot) e desenvolvedores para garantir a consistência técnica e arquitetural do projeto Talkie.

# Visão Geral do Projeto
O **Talkie** é uma plataforma de gestão de ocorrências comunitárias. O sistema permite que cidadãos reportem problemas (infraestrutura, segurança, etc.), organizados por uma taxonomia de categorias e subcategorias, com suporte a assinaturas de interesse e notificações via eventos internos.

# Stack Tecnológica
- **Linguagem:** Java 17
- **Framework:** Spring Boot 3.3.5
- **Build Tool:** Maven
- **Banco de Dados:** PostgreSQL (Produção), H2 (Testes)
- **Segurança:** Spring Security, JWT (Auth0 java-jwt)
- **Persistência:** Spring Data JPA / Hibernate
- **Utilitários:** Lombok, Springdoc OpenAPI (Swagger), Jackson
- **Testes:** JUnit 5, Cucumber (BDD)

# Comandos de Desenvolvimento

## Build e Execução
```bash
# Build do projeto
mvn clean install

# Rodar aplicação (porta 8080)
mvn spring-boot:run

# Build sem testes
mvn clean install -DskipTests
```

## Testes
```bash
# Rodar todos os testes
mvn test

# Rodar apenas testes de integração (IT)
mvn verify

# Rodar teste específico
mvn test -Dtest=OccurrenceControllerIT

# Rodar testes BDD (Cucumber)
mvn test -Dgroups=cucumber
```

## Ferramentas Úteis
- **Swagger/OpenAPI:** Acesse `http://localhost:8080/swagger-ui.html` após rodar a aplicação
- **H2 Console (Testes):** Configurado automaticamente para testes com Spring Boot

# Configuração de Ambiente
- **Arquivo de Config:** `src/main/resources/application.properties`
- **Env de Testes:** `src/main/resources/application-test.properties` (utiliza H2 in-memory)
- **BD Produção:** Requer PostgreSQL configurado na variável `spring.datasource.url`

# Arquitetura
O projeto segue uma **Arquitetura em Camadas** padrão com fluxo unidirecional:
1.  **Controller:** Camada de entrada (HTTP REST). Valida DTOs e chama serviços.
2.  **Service:** Camada de lógica de negócio. Orquestra entidades, repositórios e publica eventos.
3.  **Repository:** Camada de acesso a dados (Interfaces JpaRepository).
4.  **Domain/Entity:** Modelos de dados persistentes.
5.  **DTO:** Objetos de transferência para Request e Response.

**Padrões Identificados:**
- **Record DTOs:** DTOs modernos utilizando `record` para imutabilidade.
- **Service-Based Logic:** Toda regra de negócio reside em `@Service`.
- **Event-Driven:** Uso de `ApplicationEventPublisher` para desacoplamento (ex: criação de ocorrência).
- **Stateless Auth:** Autenticação via Token JWT.

# Estrutura de Diretórios
```
src/main/java/com/tcc/talkie/
├── Application.java            # Entrypoint
├── config/                     # Configurações globais
├── controller/                 # Endpoints REST
├── domain/                     # Entidades JPA (user, category, occurrence)
├── dto/                        # Records de Request e Response
├── events/                     # Definição de eventos de aplicação
├── infra/                      # Infraestrutura (security, exceptions)
├── listeners/                  # Ouvintes de eventos
├── repository/                 # Interfaces de persistência
└── service/                    # Lógica de negócio
```

# Convenções de Desenvolvimento

## Nomenclatura
- **Classes:** `PascalCase` (ex: `OccurrenceService`)
- **Variáveis/Métodos:** `camelCase` (ex: `findByOwnerId`)
- **Pacotes:** `lowercase`
- **Tabelas DB:** `snake_case` plural (ex: `occurrences`)

## Backend
- **Controllers:** Devem usar `@RestController` e `@RequestMapping`. Injeção de dependência via `@RequiredArgsConstructor`.
- **Services:** Devem ser anotados com `@Service`. Não devem expor entidades diretamente para o Controller se houver um DTO correspondente.
- **DTOs:** Utilizar `record` para novos DTOs. Pacotes separados para `request` e `response`.
- **Repositórios:** Interfaces estendendo `JpaRepository<Entity, ID>`.

## Segurança
- **Acesso ao Usuário Logado:** Sempre utilizar a classe utilitária `AuthenticatedUser.get()` para obter o objeto `User` atual ou `AuthenticatedUser.getId()`.
- **Roles:** `ROLE_USER` (padrão) e `ROLE_ADMIN` (gestão).
- **Configuração:** Regras de URL centralizadas em `SecurityConfig.java`.

## Tratamento de Exceções
- Utilizar `GlobalExceptionHandler` para capturar exceções (localizado em `infra/exception`).
- Exceções customizadas devem herdar de `RuntimeException` (ex: `NotFoundException`, `BadRequestException`).
- Respostas de erro devem seguir o padrão `ErrorResponse`.

## Events e Listeners
- **Publicação:** Use `ApplicationEventPublisher.publishEvent()` no Service após operações críticas.
- **Listeners:** Anotação `@EventListener` em métodos de classe `@Component` em `listeners/`.
- **Exemplo:** `OccurrenceCreatedEvent` publicado ao criar ocorrência; listener pode dispara notificações.

# Banco de Dados
- **ID Strategy:** `GenerationType.IDENTITY`.
- **UUID:** O sistema utiliza `UUID` para IDs de usuários e `Long` para ocorrências e categorias.
- **Relacionamentos:** Uso de `@ManyToOne` com `@JoinColumn`.

# Frontend
*Atualmente o projeto é focado em uma API REST. Pastas `static` e `templates` estão presentes mas não possuem implementação robusta de UI. Desenvolvimento de frontend é planejado em etapas futuras.*

# Estado Atual e Roadmap
## Implementado ✅
- Autenticação (JWT, BCrypt)
- CRUD de Ocorrências, Categorias, Subcategorias
- Sistema de Assinaturas (Subscription)
- Event Publisher (OccurrenceCreatedEvent)
- Testes de integração (JUnit 5 + Cucumber)

## Em Desenvolvimento 🔄
- Pub/Sub de notificações (listener implementado mas vazio)
- Frontend (design + implementação)

## Planejado 📋
- Notificações por e-mail/push
- Status de ocorrência (ABERTO, EM_ANALISE, RESOLVIDO)
- Upload de mídia
- Sistema de upvotes
- Dashboard administrativo

# Testes
- **Unitários/Integração:** JUnit 5.
- **BDD:** Cucumber com arquivos `.feature` em `src/test/resources/features`.
- **Configuração:** `CucumberSpringConfig` para injetar o contexto do Spring nos testes BDD.

# Regras de Negócio Identificadas
1.  **Auditoria:** Ocorrências gravam `createdAt` automaticamente no `Service`.
2.  **Unicidade:** Categorias possuem nomes únicos (case insensitive).
3.  **Autorização:** Apenas `ADMIN` pode criar/editar categorias e deletar qualquer ocorrência.
4.  **Eventos:** A criação de ocorrência publica um `OccurrenceCreatedEvent`.

# Instruções para IA

### O que SEMPRE seguir:
- Use `record` para novos DTOs de Request.
- Injecte dependências via `@RequiredArgsConstructor` (Lombok) e campos `final`.
- Use `AuthenticatedUser` para obter o contexto do usuário logado.
- Retorne `ResponseEntity<ApiResponse<T>>` para manter a consistência das respostas.
- Adicione as novas rotas no `SecurityConfig` se precisarem de permissões específicas.

### O que EVITAR:
- Não use `@Autowired` em campos (prefira construtor via Lombok).
- Não coloque lógica de negócio em Controllers.
- Não retorne Entidades JPA diretamente nos endpoints (use DTOs).

### Como implementar novas funcionalidades:
1.  Crie a **Entidade** no pacote `domain`.
2.  Crie o **Repository** interface.
3.  Crie os **DTOs** de Request e Response.
4.  Implemente a lógica no **Service**.
5.  Exponha via **Controller**.
6.  Atualize o `SecurityConfig` se necessário.
7.  Adicione um cenário no Cucumber ou teste unitário.

# Exemplos de Código do Projeto

### Controller Padrão
```java
@RestController
@RequestMapping("/path")
@RequiredArgsConstructor
public class MyController {
    private final MyService service;

    @PostMapping
    public ResponseEntity<ApiResponse<MyResponseDTO>> create(@RequestBody MyRequestDTO data) {
        var result = service.execute(data);
        return ResponseEntity.ok(new ApiResponse<>("Sucesso", result));
    }
}
```

### Uso do AuthenticatedUser (Service)
```java
User user = AuthenticatedUser.get();
occurrence.setOwner(user);
```

### Registro de Evento (Service)
```java
eventPublisher.publishEvent(new MyCreatedEvent(savedEntity));
```

### Listener de Evento
```java
@Component
public class MyEventListener {
    @EventListener
    public void onMyCreatedEvent(MyCreatedEvent event) {
        // Lógica assíncrona: enviar notificação, atualizar cache, etc.
    }
}
```

### Padrão de Resposta API
```java
// Controller retorna sempre ApiResponse
return ResponseEntity.ok(new ApiResponse<>("Mensagem de sucesso", data));

// Resposta JSON:
{
  "message": "Mensagem de sucesso",
  "data": { ... }
}
```

# Checklist para Novas Funcionalidades
- [ ] Entidade criada com anotações JPA adequadas.
- [ ] Repository estendendo JpaRepository.
- [ ] Record DTOs criados para Entrada e Saída.
- [ ] Service implementando a regra de negócio e validações.
- [ ] Controller mapeando os endpoints REST.
- [ ] Exceções específicas lançadas para casos de erro.
- [ ] `SecurityConfig` atualizado com as permissões da nova rota.
- [ ] Teste de integração ou cenário BDD adicionado.
- [ ] Documentado no Swagger (OpenAPI) via anotações se necessário.

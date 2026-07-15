# 🗣️ Talkie

> Uma plataforma colaborativa de gestão de ocorrências comunitárias, permitindo que cidadãos reportem e acompanhem problemas da infraestrutura urbana.

[![Java 17](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.5-6DB33F?style=for-the-badge&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Maven](https://img.shields.io/badge/Maven-Build-3776AB?style=for-the-badge&logo=apache-maven)](https://maven.apache.org/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-336791?style=for-the-badge&logo=postgresql)](https://www.postgresql.org/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg?style=for-the-badge)](LICENSE)

## 🎯 Visão Geral

**Talkie** é uma aplicação backend desenvolvida com Spring Boot que permite que cidadãos reportem ocorrências comunitárias (problemas de infraestrutura, segurança, etc.), organizadas por uma taxonomia de categorias e subcategorias, com suporte a assinaturas de interesse e notificações via eventos internos.

### ✨ Principais Funcionalidades

- 🔐 **Autenticação segura** com JWT e BCrypt
- 📝 **CRUD completo** de ocorrências, categorias e subcategorias
- 👤 **Gestão de usuários** com roles (USER e ADMIN)
- 🔔 **Sistema de assinaturas** para notificações de tópicos
- 📡 **Event-driven architecture** com ApplicationEventPublisher
- 📊 **API RESTful** documentada com Swagger/OpenAPI
- ✅ **Testes de integração** com JUnit 5 e Cucumber (BDD)

---

## 🛠️ Stack Tecnológica

| Componente | Versão | Descrição |
|-----------|--------|-----------|
| **Java** | 17 | Linguagem de programação |
| **Spring Boot** | 3.3.5 | Framework web |
| **Maven** | Latest | Build tool |
| **PostgreSQL** | Latest | Banco de dados produção |
| **H2** | Latest | Banco de dados testes |
| **Spring Security** | Latest | Autenticação e autorização |
| **JWT** | Latest | Tokens JWT (Auth0) |
| **Spring Data JPA** | Latest | ORM e persistência |
| **Hibernate** | Latest | Mapeamento objeto-relacional |
| **Springdoc OpenAPI** | Latest | Documentação Swagger |
| **JUnit 5** | Latest | Testes unitários |
| **Cucumber** | Latest | Testes BDD |
| **Lombok** | Latest | Redução de boilerplate |

---

## 🚀 Início Rápido

### Pré-requisitos

- **Java 17+** instalado
- **Maven 3.8+** instalado
- **PostgreSQL 12+** (para ambiente de produção)
- **Git** para clonar o repositório

### Instalação

1. **Clone o repositório:**
   ```bash
   git clone https://github.com/seu-usuario/talkie.git
   cd talkie
   ```

2. **Configure o banco de dados:**
   
   Crie um arquivo `.env` ou edite `src/main/resources/application.properties`:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/talkie
   spring.datasource.username=seu_usuario
   spring.datasource.password=sua_senha
   spring.jpa.hibernate.ddl-auto=validate
   ```

3. **Instale as dependências e build:**
   ```bash
   mvn clean install
   ```

4. **Execute a aplicação:**
   ```bash
   mvn spring-boot:run
   ```

5. **Acesse a aplicação:**
   - **API REST:** `http://localhost:8080`
   - **Swagger UI:** `http://localhost:8080/swagger-ui/index.html`
   - **H2 Console (testes):** `http://localhost:8080/h2-console`

---

## 🏗️ Arquitetura

O projeto segue uma **arquitetura em camadas** com fluxo unidirecional e separação clara de responsabilidades:

```
src/main/java/com/tcc/talkie/
├── Application.java                 # Entrypoint da aplicação
├── config/                          # Configurações globais (Security, etc)
├── controller/                      # Endpoints REST
│   ├── OccurrenceController
│   ├── CategoryController
│   ├── UserController
│   └── AuthController
├── domain/                          # Entidades JPA
│   ├── Occurrence
│   ├── Category
│   ├── Subcategory
│   ├── User
│   └── Subscription
├── dto/                             # Data Transfer Objects
│   ├── request/                     # DTOs de entrada
│   └── response/                    # DTOs de saída
├── events/                          # Definição de eventos
│   └── OccurrenceCreatedEvent
├── infra/                           # Infraestrutura
│   ├── exception/                   # Tratamento de exceções
│   └── security/                    # Configurações de segurança
├── listeners/                       # Event listeners
│   └── OccurrenceEventListener
├── repository/                      # Data Access Layer
│   ├── OccurrenceRepository
│   ├── CategoryRepository
│   ├── UserRepository
│   └── SubscriptionRepository
└── service/                         # Lógica de negócio
    ├── OccurrenceService
    ├── CategoryService
    ├── UserService
    ├── AuthService
    └── SubscriptionService
```

### Camadas

| Camada | Responsabilidade |
|--------|-----------------|
| **Controller** | Validar requisições HTTP, chamar serviços e retornar respostas |
| **Service** | Orquestrar entidades, repositórios, publicar eventos e aplicar regras de negócio |
| **Repository** | Abstrair acesso a dados via JpaRepository |
| **Domain** | Entidades JPA com mapeamento objeto-relacional |
| **DTO** | Transferência de dados entre camadas (imutável via `record`) |

---

## 📡 API REST

### Autenticação

Todos os endpoints requerem autenticação via token JWT no header:

```bash
Authorization: Bearer <seu_token_jwt>
```

### Exemplos de Endpoints

#### 🔐 Autenticação
```bash
# Login
POST /auth/login
Content-Type: application/json

{
  "email": "usuario@example.com",
  "password": "senha123"
}

# Resposta
{
  "message": "Login realizado com sucesso",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

#### 📝 Ocorrências
```bash
# Listar todas as ocorrências
GET /occurrences

# Criar nova ocorrência
POST /occurrences
Content-Type: application/json

{
  "title": "Buraco na rua",
  "description": "Há um buraco grande no asfalto",
  "latitude": -22.9035,
  "longitude": -43.1791,
  "categoryId": 1,
  "subcategoryId": 1
}

# Buscar ocorrência por ID
GET /occurrences/{id}

# Atualizar ocorrência
PUT /occurrences/{id}

# Deletar ocorrência (admin)
DELETE /occurrences/{id}
```

#### 📂 Categorias
```bash
# Listar categorias
GET /categories

# Criar categoria (admin)
POST /categories
Content-Type: application/json

{
  "name": "Infraestrutura",
  "description": "Problemas de infraestrutura urbana"
}

# Atualizar categoria (admin)
PUT /categories/{id}

# Deletar categoria (admin)
DELETE /categories/{id}
```

#### 🔔 Assinaturas
```bash
# Assinar em uma categoria
POST /subscriptions/{categoryId}

# Desassinar de uma categoria
DELETE /subscriptions/{categoryId}

# Listar minhas assinaturas
GET /subscriptions/me
```

### 📚 Documentação Completa

Para ver toda a documentação interativa da API, acesse:
```
http://localhost:8080/swagger-ui/index.html
```

---

## ✅ Testes

O projeto utiliza **JUnit 5** para testes unitários e **Cucumber** para testes BDD.

### Executar todos os testes
```bash
mvn test
```

### Executar testes de integração
```bash
mvn verify
```

### Executar teste específico
```bash
mvn test -Dtest=OccurrenceControllerIT
```

### Executar testes BDD (Cucumber)
```bash
mvn test -Dgroups=cucumber
```

### Estrutura de testes
```
src/test/
├── java/
│   └── com/tcc/talkie/
│       ├── controller/              # Testes de endpoints
│       ├── service/                 # Testes de lógica de negócio
│       └── integration/             # Testes de integração
└── resources/
    └── features/                    # Cenários BDD Cucumber
        ├── occurrence.feature
        ├── category.feature
        ├── auth.feature
        └── subscription.feature
```

---

## 🔐 Segurança

### Autenticação
- **JWT Tokens** com assinatura segura
- **BCrypt** para hash de senhas
- **HTTPS recommended** em produção

### Autorização
- **Roles:** `ROLE_USER` (padrão) e `ROLE_ADMIN`
- **Acesso centralizado:** `SecurityConfig.java`
- **Proteção de rotas:** Apenas ADMIN pode gerenciar categorias e deletar ocorrências

### Regras de Segurança
1. Senhas são obrigatoriamente hasheadas com BCrypt
2. Tokens JWT contêm o usuário e suas roles
3. Apenas o dono pode editar sua própria ocorrência
4. Apenas ADMIN pode deletar qualquer ocorrência

---

## 🧪 Regras de Negócio

1. **Auditoria:** Ocorrências registram automaticamente `createdAt`
2. **Unicidade:** Nomes de categorias são únicos (case-insensitive)
3. **Autorização:** Apenas ADMIN pode criar/editar categorias
4. **Eventos:** Criação de ocorrência publica `OccurrenceCreatedEvent`
5. **Notificações:** Listeners podem disparar notificações assíncronas

---

## 🚦 Status do Projeto

### ✅ Implementado
- [x] Autenticação (JWT + BCrypt)
- [x] CRUD de Ocorrências, Categorias, Subcategorias
- [x] Sistema de Assinaturas (Subscription)
- [x] Event Publisher (OccurrenceCreatedEvent)
- [x] Testes de integração (JUnit 5 + Cucumber)
- [x] Swagger/OpenAPI documentação
- [x] Global Exception Handler
- [x] Role-based authorization

### 🔄 Em Desenvolvimento
- [ ] Notificações em tempo real (Pub/Sub)
- [ ] Frontend (design + implementação)
- [ ] Envio de notificações por e-mail/push

### 📋 Planejado
- [ ] Status de ocorrência (ABERTO, EM_ANALISE, RESOLVIDO)
- [ ] Upload de mídia (imagens/vídeos)
- [ ] Sistema de upvotes/downvotes
- [ ] Dashboard administrativo
- [ ] Relatórios e analytics

---

## 📖 Convenções de Desenvolvimento

### Nomenclatura
- **Classes:** `PascalCase` → `OccurrenceService`
- **Métodos/Variáveis:** `camelCase` → `findByOwnerId()`
- **Pacotes:** `lowercase` → `com.tcc.talkie.service`
- **Tabelas DB:** `snake_case` plural → `occurrences`

### Padrões de Código
- ✅ Usar `record` para novos DTOs
- ✅ Injeção de dependência via `@RequiredArgsConstructor` (Lombok)
- ✅ Usar `AuthenticatedUser.get()` para obter usuário logado
- ✅ Retornar `ResponseEntity<ApiResponse<T>>` em controllers
- ❌ Não usar `@Autowired` em campos
- ❌ Não colocar lógica de negócio em controllers
- ❌ Não retornar entidades JPA diretamente (usar DTOs)

---

## 🤝 Contribuindo

Contribuições são bem-vindas! Por favor, siga o processo abaixo:

1. **Fork** o repositório
2. **Crie uma branch** para sua feature (`git checkout -b feature/AmazingFeature`)
3. **Commit** suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. **Push** para a branch (`git push origin feature/AmazingFeature`)
5. **Abra um Pull Request**

### Checklist para novas funcionalidades
- [ ] Entidade criada com anotações JPA
- [ ] Repository estendendo JpaRepository
- [ ] Record DTOs para entrada e saída
- [ ] Service implementando regra de negócio
- [ ] Controller expondo endpoint REST
- [ ] Exceções específicas lançadas
- [ ] SecurityConfig atualizado com permissões
- [ ] Testes de integração/BDD adicionados
- [ ] Documentação Swagger implementada

---

## 📝 Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## 👥 Autores

- **Isabelle Vimercati** - _Desenvolvimento inicial_

---
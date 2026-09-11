# Documento de Engenharia de Software: Requisitos Implementados - Projeto Talkie

## 1. Visão Geral do Sistema
O **Talkie** é uma plataforma backend robusta desenvolvida em Spring Boot, projetada para facilitar o relato e a gestão de ocorrências comunitárias. O sistema permite que cidadãos registrem eventos geolocalizados, classificados por categorias e subcategorias, promovendo a transparência e a organização de informações urbanas ou comunitárias. A plataforma conta com um sistema de autenticação seguro e controle de acesso baseado em perfis (RBAC), garantindo que apenas usuários autorizados realizem ações administrativas.

---

## 2. Requisitos Funcionais (RF)
Abaixo estão os requisitos funcionais identificados através da análise dos controladores e serviços do sistema:

### RF01: Gestão de Usuários e Autenticação
O sistema fornece um fluxo completo de identidade e acesso.
- **Cadastro de Usuários:** Permite que novos cidadãos se registrem na plataforma fornecendo nome, e-mail e senha.
- **Autenticação:** Login seguro utilizando e-mail e senha, retornando um token JWT para sessões subsequentes.
- **Exemplos de Rotas:**
  - `POST /auth/register`: Registra um novo perfil.
  - `POST /auth/login`: Autentica e gera o token de acesso.

### RF02: Gestão de Ocorrências
Funcionalidade principal para o reporte de eventos na comunidade.
- **Registro de Ocorrência:** Usuários autenticados podem criar relatos com título, descrição detalhada, localização e associação obrigatória a uma categoria e subcategoria.
- **Consulta de Ocorrências:**
  - Listagem global de todas as ocorrências registradas.
  - Filtro para visualização exclusiva das ocorrências criadas pelo próprio usuário autenticado.
- **Remoção de Ocorrências:** Permite a exclusão de relatos (funcionalidade restrita ao perfil ADMIN).
- **Exemplos de Rotas:**
  - `POST /occurrences`: Cria um novo relato.
  - `GET /occurrences`: Retorna todos os registros.
  - `GET /occurrences/my`: Retorna apenas os registros do usuário logado.
  - `DELETE /occurrences/{id}`: Remove um registro (Admin).

### RF03: Taxonomia (Categorias e Subcategorias)
Estrutura organizacional para os dados do sistema.
- **Gestão de Categorias:** Operações de CRUD (Criar, Ler, Atualizar, Deletar) para as categorias principais (ex: "Infraestrutura", "Segurança").
- **Gestão de Subcategorias:** Organização granular dentro das categorias (ex: "Iluminação" dentro de "Infraestrutura").
- **Acesso Restrito:** Apenas usuários com a role `ADMIN` podem modificar a estrutura de categorias.
- **Exemplos de Rotas:**
  - `POST /categories`: Cria uma nova categoria.
  - `PUT /categories/{id}`: Atualiza dados de uma categoria existente.
  - `GET /subcategories`: Lista as subdivisões disponíveis.

### RF04: Assinaturas e Eventos Internos
Mecanismo para expansibilidade e engajamento.
- **Inscrição em Categorias:** O sistema possui suporte para que usuários se "inscrevam" em categorias específicas (armazenado na entidade `Subscription`).
- **Disparo de Eventos:** Ao salvar uma nova ocorrência, o sistema publica um evento interno (`OccurrenceCreatedEvent`), permitindo que listeners (ouvintes) futuros disparem notificações ou integrações externas de forma desacoplada.

---

## 3. Requisitos Não Funcionais (RNF)

### RNF01: Segurança e Proteção de Dados
- **Autenticação Stateless:** Utilização de JWT (JSON Web Token) para gerenciar sessões sem estado no servidor.
- **Criptografia:** Senhas são armazenadas utilizando o algoritmo de hashing `BCrypt`.
- **RBAC (Role-Based Access Control):** Diferenciação de permissões entre usuários comuns (`ROLE_USER`) e gestores (`ROLE_ADMIN`).

### RNF02: Arquitetura de Software
- **Layered Architecture:** Divisão clara entre camadas de apresentação (Controller), lógica de negócio (Service), persistência (Repository) e modelo de dados (Domain).
- **Desacoplamento via DTOs:** Uso intensivo de *Data Transfer Objects* para evitar a exposição direta das entidades de banco de dados na API.
- **Event-Driven Design:** Implementação parcial de padrões orientados a eventos para processos assíncronos e desacoplados.

### RNF03: Pilha Tecnológica (Stack)
- **Linguagem:** Java 17 (LTS).
- **Framework Principal:** Spring Boot 3.3.5.
- **Persistência de Dados:** Spring Data JPA com Hibernate.
- **Banco de Dados:** PostgreSQL (Produção) e H2 (Ambiente de Testes).
- **Documentação da API:** OpenAPI 3 / Swagger (disponível em `/swagger-ui.html`).
- **Produtividade:** Project Lombok para geração automática de getters, setters e construtores.

### RNF04: Qualidade e Confiabilidade
- **Tratamento de Exceções:** Implementação de um `GlobalExceptionHandler` que captura erros de negócio e retorna respostas JSON padronizadas (ex: `404 Not Found`, `401 Unauthorized`).
- **Validations:** Validação de integridade de dados via anotações JPA e regras em nível de serviço.
- **Testes:** Presença de suíte de testes de integração e suporte a BDD (Cucumber) para validação de cenários de negócio.

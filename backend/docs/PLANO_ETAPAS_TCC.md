# 📋 Plano de Etapas de Entrega - Projeto Talkie

## 🎯 Resumo Executivo

O Talkie é uma plataforma de gestão de ocorrências com backend Spring Boot funcional. Este plano divide o desenvolvimento em **6 etapas** para chegar de um sistema parcialmente implementado a uma solução completa e pronta para apresentação no TCC.

---

## 📊 Análise do Estado Atual

### ✅ Já Implementado
- Autenticação JWT com Spring Security
- API REST completa (Users, Categories, Subcategories, Occurrences)
- Testes de integração (JUnit 5 + Cucumber BDD)
- Swagger/OpenAPI documentado

### ⚠️ Incompleto ou com Bugs
- **Pub/Sub:** `OccurrenceNotificationListener` vazio; evento nunca é disparado
- **Assinaturas:** `SubscriptionRepository` com métodos com tipos errados
- **Validações:** DTOs sem `@Valid` ou `@NotBlank`
- **Frontend:** Não existe (apenas API REST)

---

## 🚀 As 6 Etapas

### **Etapa 1: Correção de Bugs e Code Review**
**Prioridade:** 🔴 ALTA | **Tempo estimado:** 3-5 dias

Deixar o código existente correto e pronto antes de adicionar novas features.

**Tarefas:**
- [ ] Corrigir assinaturas do `SubscriptionRepository`
- [ ] Adicionar `@Valid` + validações nos DTOs
- [ ] Publicar evento `OccurrenceCreatedEvent` no `OccurrenceService.create()`
- [ ] Melhorar `OccurrenceResponseDTO` (retornar nomes, não IDs)
- [ ] Remover imports desnecessários
- [ ] Externalizar secrets do `application.properties`
- [ ] Todos os testes passando ✅

**Entregável:** PR com correções; build clean.

---

### **Etapa 2: Pub/Sub + Sistema de Assinaturas**
**Prioridade:** 🔴 ALTA | **Tempo estimado:** 5-7 dias
**Dependência:** Etapa 1

Implementar o fluxo completo: usuário se inscreve → ocorrência criada → inscritos notificados.

**Tarefas:**
- [ ] `SubscriptionService` — lógica de inscrever/desinscrever
- [ ] `SubscriptionController` — endpoints (POST, DELETE, GET)
- [ ] Atualizar `SecurityConfig` para `/subscriptions/**`
- [ ] Implementar `OccurrenceNotificationListener` com `@EventListener`
- [ ] Adicionar dependência `spring-boot-starter-mail` ao `pom.xml`
- [ ] Entidade `Notification` (tabela no banco) para histórico interno
- [ ] Enviar e-mail aos inscritos quando ocorrência é criada
- [ ] Testes de integração para as novas rotas

**Entregável:** Fluxo completo funcionando; usuário recebe e-mail e notificação interna.

---

### **Etapa 3: Enriquecimento de Funcionalidades da API**
**Prioridade:** 🟡 MÉDIA | **Tempo estimado:** 5-7 dias
**Dependência:** Etapa 2

Adicionar features de valor que enriquecem o TCC e preparar a API para o frontend.

**Tarefas (priorizar nessa ordem):**
1. [ ] **Status de Ocorrência** — enum (ABERTO, EM_ANALISE, RESOLVIDO, FECHADO)
2. [ ] **Paginação** — adicionar `Pageable` em `GET /occurrences` e `GET /categories`
3. [ ] **Soft Delete** — campo `deletedAt` na Occurrence
4. [ ] **Upvotes** — entidade `OccurrenceUpvote` e endpoint `POST /occurrences/{id}/upvote`
5. [ ] **Dashboard Admin** — `GET /admin/stats` com totais por categoria e status

**Entregável:** Pelo menos Status + Paginação implementados e testados.

---

### **Etapa 4: Design do Sistema**
**Prioridade:** 🟡 MÉDIA | **Tempo estimado:** 3-5 dias
**Dependência:** Paralela com Etapa 3 (não bloqueadora)

Definir identidade visual e fluxo de telas antes de codificar o frontend.

**Tarefas:**
- [ ] Definir paleta de cores, tipografia, componentes base
- [ ] Wireframes das telas principais:
  - [ ] Login / Registro
  - [ ] Feed de Ocorrências (listagem + filtros)
  - [ ] Criar Ocorrência (formulário)
  - [ ] Perfil do Usuário
  - [ ] Painel Admin (gestão)
- [ ] Decidir: Axios vs React Query para state management
- [ ] Criar protótipo navegável em Figma (opcional mas recomendado)

**Tecnologia definida:** React + TypeScript (SPA)

**Entregável:** Protótipo aprovado; guia de componentes definido.

---

### **Etapa 5: Desenvolvimento do Frontend**
**Prioridade:** 🔴 ALTA | **Tempo estimado:** 10-14 dias
**Dependência:** Etapas 3 e 4

Implementar as telas e integrar com a API REST.

**Fases internas:**
1. [ ] **Setup** — criar projeto React, roteamento, estrutura de pastas
2. [ ] **Autenticação** — telas de login/registro, armazenar JWT
3. [ ] **Feed** — listar ocorrências com filtro por categoria e status
4. [ ] **Criar Ocorrência** — formulário com validações
5. [ ] **Perfil + Assinaturas** — ver próprias ocorrências, gerenciar inscrições
6. [ ] **Painel Admin** — criar/editar categorias, alterar status
7. [ ] **Testes E2E** — fluxos completos funcionando

**Entregável:** Aplicação React funcional com todos os fluxos principais.

---

### **Etapa 6: Testes Finais, Documentação e Entrega**
**Prioridade:** 🟡 MÉDIA | **Tempo estimado:** 3-5 dias
**Dependência:** Etapa 5

Garantir qualidade, documentação e preparação para apresentação.

**Tarefas:**
- [ ] Completar cobertura de testes BDD (Cucumber) para novos cenários
- [ ] Revisar documentação Swagger (todos os endpoints documentados)
- [ ] Escrever seção técnica do TCC (arquitetura, decisões, stack)
- [ ] Revisar segurança: remover secrets hardcoded, validar CORS, HTTPS
- [ ] Preparar ambiente de demo (local ou cloud)
- [ ] Testar fluxos completos ponta a ponta
- [ ] Documentar como executar o projeto (README)

**Entregável:** Projeto completo, documentado, seguro e pronto para apresentação.

---

## 📈 Timeline Sugerida

| Etapa | Tema | Dias | Acumulado |
|-------|------|------|-----------|
| 1 | Code Review | 4 | 4 dias |
| 2 | Pub/Sub | 6 | 10 dias |
| 3 | API Enhancements | 6 | 16 dias |
| 4 | Design | 4 | 20 dias (paralela) |
| 5 | Frontend | 12 | 32 dias |
| 6 | Finalização | 4 | 36 dias |

**Total estimado: ~5-6 semanas** (com priorização adequada)

---

## 🎯 Decisões Tomadas

✅ **Notificações:** E-mail (SMTP) + Tabela de notificações internas  
✅ **Frontend:** React + TypeScript (SPA separada do backend)  
✅ **Banco de Dados:** PostgreSQL (produção), H2 (testes)  
✅ **Autenticação:** JWT com Spring Security (já implementado)

---

## 📌 Próximos Passos

1. **Agora:** Escolher por qual etapa começar
2. **Etapa 1:** Corrigir bugs existentes (recomendado começar aqui)
3. **Etapa 2:** Implementar notificações
4. **Etapa 3+:** Seguir a sequência planejada

---

## 📚 Documentação de Referência

- `CLAUDE.md` — Guia de desenvolvimento (arquitetura, padrões, comandos)
- `DOCUMENTACAO_REQUISITOS.md` — Requisitos funcionais e não-funcionais
- `REGRAS_DE_NEGOCIO.md` — Regras de negócio identificadas
- Plano detalhado: `.claude/plans/fluttering-jingling-zephyr.md`

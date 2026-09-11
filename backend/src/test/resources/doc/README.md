# Documentação de Testes de Integração - Projeto Talkie

## Índice de Documentação

Bem-vindo à documentação de testes de integração do projeto **Talkie**. Este diretório contém a especificação completa de todos os casos de uso testados no sistema.

---

## 📋 Estrutura da Documentação

```
doc/
├── README.md (este arquivo)
├── USER_CONTROLLER_TEST_CASES.md
├── CATEGORY_CONTROLLER_TEST_CASES.md (em desenvolvimento)
├── OCCURRENCE_CONTROLLER_TEST_CASES.md (em desenvolvimento)
├── SUBCATEGORY_CONTROLLER_TEST_CASES.md (em desenvolvimento)
├── AUTH_CONTROLLER_TEST_CASES.md (em desenvolvimento)
└── TESTING_GUIDELINES.md (em desenvolvimento)
```

---

## ✅ Controladores Testados

### 1. **UserController** ✅ COMPLETO
**Status:** Implementado e Validado  
**Data:** 16 de junho de 2026  
**Testes:** 18 casos de uso  
**Resultado:** 100% aprovado  

**Documento:** [USER_CONTROLLER_TEST_CASES.md](./USER_CONTROLLER_TEST_CASES.md)

**Endpoints Cobertos:**
- `GET /users` - Listar todos os usuários
- `GET /users/{id}` - Obter usuário por ID
- `PUT /users/{id}` - Atualizar usuário
- `DELETE /users/{id}` - Deletar usuário

**Cenários Testados:**
- ✅ Autenticação obrigatória em todos os endpoints
- ✅ Operações CRUD completas
- ✅ Tratamento de erros (404, 401)
- ✅ Autorização e permissões
- ✅ Atualização parcial de dados
- ✅ Deleção com integridade referencial

---

### 2. **CategoryController** 🔄 EM DESENVOLVIMENTO
**Status:** Testes de integração parcialmente implementados  
**Testes Esperados:** 14 casos de uso  
**Endpoints:**
- `GET /categories`
- `GET /categories/{id}`
- `POST /categories`
- `PUT /categories/{id}`
- `DELETE /categories/{id}`

**Próximas Ações:**
- [ ] Expandir testes BDD (Cucumber)
- [ ] Documentar todos os 14 cenários

---

### 3. **OccurrenceController** 🔄 EM DESENVOLVIMENTO
**Status:** Testes de integração parcialmente implementados  
**Testes Esperados:** 10 casos de uso  
**Endpoints:**
- `GET /occurrences`
- `GET /occurrences/my`
- `POST /occurrences`
- `DELETE /occurrences/{id}`

---

### 4. **SubcategoryController** 🔄 EM DESENVOLVIMENTO
**Status:** Testes de integração parcialmente implementados  
**Testes Esperados:** 13 casos de uso  
**Endpoints:**
- `GET /subcategories`
- `GET /subcategories/{id}`
- `POST /subcategories`
- `PUT /subcategories/{id}`
- `DELETE /subcategories/{id}`

---

### 5. **AuthController** 🔄 EM DESENVOLVIMENTO
**Status:** Testes de integração parcialmente implementados  
**Testes Esperados:** 5 casos de uso  
**Endpoints:**
- `POST /auth/register`
- `POST /auth/login`

---

## 📊 Resumo Geral de Testes

| Controlador | Testes Implementados | Status | Aprovados | Taxa Sucesso |
|------------|----------------------|--------|-----------|--------------|
| **User** | 18 | ✅ Completo | 18 | 100% |
| **Category** | 14 | 🔄 Parcial | N/A | N/A |
| **Occurrence** | 10 | 🔄 Parcial | N/A | N/A |
| **Subcategory** | 13 | 🔄 Parcial | N/A | N/A |
| **Auth** | 5 | 🔄 Parcial | N/A | N/A |
| **TOTAL** | **60** | | | |

---

## 🎯 Cobertura por Tipo de Teste

### Testes de Integração (IT - Integration Tests)
Testam a integração completa: Controller → Service → Repository → Database

**Implementados:**
- ✅ AuthControllerIT (3 testes)
- ✅ CategoryControllerIT (14 testes)
- ✅ OccurrenceControllerIT (8 testes)
- ✅ SubcategoryControllerIT (14 testes)
- ✅ **UserControllerIT (18 testes)** ← NOVO
- **Total: 57 testes de integração**

### Testes BDD (Cucumber)
Testam cenários em linguagem natural (Gherkin)

**Em Desenvolvimento:**
- 🔄 categoria.feature
- 🔄 ocorrencia.feature
- ❌ auth.feature (não criado)
- ❌ user.feature (não criado)
- ❌ subcategory.feature (não criado)

---

## 🔒 Padrões de Segurança Testados

### Autenticação
- ✅ JWT (JSON Web Tokens)
- ✅ Rejeição de requisições sem token
- ✅ Validação de token expirado
- ✅ Suporte a múltiplos roles (USER, ADMIN)

### Autorização
- ✅ Controle de acesso por papel (Role-Based Access Control)
- ✅ Operações restritas a ADMIN
- ✅ Operações permitidas a USER
- ✅ Validação de permissões por endpoint

### Validação de Dados
- ✅ Rejeição de IDs inválidos
- ✅ Verificação de unicidade
- ✅ Validação de tipos de dados
- ✅ Tratamento de valores nulos

---

## 📝 Formato de Documentação de Casos de Uso

Cada caso de uso segue o seguinte padrão:

```
#### ID: CT-XXX
**Descrição:** [Descrição clara do caso de uso]
**Pré-condição:** [Estado inicial necessário]
**Ator:** [Quem executa a ação]
**Cenário:** [Passos da execução]
**Dados de Entrada:** [Valores fornecidos]
**Resultado Esperado:** [Status HTTP e corpo da resposta]
**Código do Teste:** [Implementação JUnit 5]
```

---

## 🚀 Como Executar os Testes

### Executar Todos os Testes
```bash
./mvnw test
```

### Executar Testes de uma Classe Específica
```bash
./mvnw test -Dtest=UserControllerIT
```

### Executar Apenas Testes BDD (Cucumber)
```bash
./mvnw test -Dgroups="@bdd"
```

### Gerar Relatório de Cobertura
```bash
./mvnw test jacoco:report
```

---

## 📖 Documentação Técnica Relacionada

- **CLAUDE.md** - Guia de desenvolvimento do projeto
- **REGRAS_DE_NEGOCIO.md** - Regras de negócio da aplicação
- **DOCUMENTACAO_REQUISITOS.md** - Requisitos funcionais e não-funcionais

---

## 🔄 Fluxo de Desenvolvimento de Novos Testes

1. **Análise** - Identificar novo caso de uso
2. **Planejamento** - Documentar em caso de uso
3. **Implementação** - Criar teste em arquivo IT.java
4. **Validação** - Executar e verificar aprovação
5. **Documentação** - Registrar em arquivo .md
6. **Integração** - Atualizar README.md com resumo

---

## 📋 Checklist para Novos Testes

- [ ] Classe de teste criada em `src/test/java/.../controller/`
- [ ] Mínimo de 1 teste por verbo HTTP implementado
- [ ] Testes cobrem cenário de sucesso
- [ ] Testes cobrem cenários de erro (401, 403, 404, 400)
- [ ] Testes validam resposta HTTP
- [ ] Testes validam corpo da resposta
- [ ] Setup e teardown corretos (@BeforeEach, deleteAll)
- [ ] Documentação em arquivo .md criada
- [ ] README.md atualizado
- [ ] Todos os testes executados com sucesso

---

## 👥 Contribuidores

- Sistema de Testes Automatizados
- Data: 16 de junho de 2026

---

## 📞 Suporte

Para dúvidas sobre os testes, consulte:
1. Arquivo específico do controlador (ex: USER_CONTROLLER_TEST_CASES.md)
2. CLAUDE.md - Padrões do projeto
3. Código-fonte dos testes em `src/test/java/com/tcc/talkie/controller/`

---

## 📄 Licença

Documentação do Projeto Talkie - TCC  
Universidade Federal Fluminense (UFF)  
2026

---

**Última Atualização:** 16 de junho de 2026  
**Versão:** 1.0

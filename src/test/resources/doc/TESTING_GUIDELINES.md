# Guia de Desenvolvimento de Testes de Integração

**Documento de Padrões e Boas Práticas**  
**Versão:** 1.0  
**Data:** 16 de junho de 2026  

---

## 1. Introdução

Este guia estabelece os padrões e boas práticas para desenvolvimento de testes de integração no projeto **Talkie**. O objetivo é garantir cobertura consistente, código legível e qualidade elevada em todos os testes.

---

## 2. Estrutura de Arquivo de Teste

### 2.1 Nomenclatura
```
{NomeController}IT.java

Exemplo: UserControllerIT.java
```

### 2.2 Localização
```
src/test/java/com/tcc/talkie/controller/
```

### 2.3 Estrutura Básica
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class {NomeController}IT {
    
    // 1. Injeções
    @Autowired
    private MockMvc mockMvc;
    
    // 2. Atributos compartilhados
    private User testUser;
    private String testToken;
    
    // 3. Setup/Teardown
    @BeforeEach
    void setup() { }
    
    // 4. Testes agrupados por endpoint
    // GET Tests
    // POST Tests
    // PUT Tests
    // DELETE Tests
}
```

---

## 3. Padrão de Teste

### 3.1 Estrutura de um Teste
```java
@Test
@DisplayName("Descrição clara do que está sendo testado")
void nomeMetodoEmSnakeCase() throws Exception {
    // ARRANGE - Preparar dados
    UpdateDTO dto = new UpdateDTO("Nome", "email@test.com");
    
    // ACT - Executar ação
    mockMvc.perform(put("/users/{id}")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(objectMapper.writeValueAsString(dto)))
    
    // ASSERT - Validar resultado
    .andExpect(status().isOk())
    .andExpect(jsonPath("$.name").value("Nome"))
    .andExpect(jsonPath("$.email").value("email@test.com"));
}
```

### 3.2 Anotações Obrigatórias
```java
@Test                                    // JUnit 5
@DisplayName("Descrição clara")         // Documentação
void nomeMetodo() throws Exception {}
```

### 3.3 Nomenclatura de Métodos
```
✅ Bom:          deveListarTodosOsUsuarios()
✅ Bom:          naoDeveObterUsuarioSemAutenticacao()
❌ Ruim:         test1()
❌ Ruim:         userControllerTest()
```

---

## 4. Casos de Uso por Tipo de Operação

### 4.1 GET (Consulta)

#### Padrão Obrigatório:
1. Sucesso com autenticação
2. Erro sem autenticação (401)
3. Erro com recurso inexistente (404)

#### Exemplo:
```java
// CT-001: Sucesso
@Test
@DisplayName("Deve listar usuários com autenticação")
void deveListarUsuarios() throws Exception {
    mockMvc.perform(get("/users")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$").isArray());
}

// CT-002: Sem autenticação
@Test
@DisplayName("Não deve listar usuários sem autenticação")
void naoDeveListarSemAutenticacao() throws Exception {
    mockMvc.perform(get("/users"))
        .andExpect(status().isUnauthorized());
}

// CT-003: Recurso não encontrado
@Test
@DisplayName("Não deve obter usuário inexistente")
void naoDeveObterInexistente() throws Exception {
    mockMvc.perform(get("/users/uuid-invalido")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
}
```

---

### 4.2 POST (Criação)

#### Padrão Obrigatório:
1. Sucesso com dados válidos
2. Erro sem autenticação (401)
3. Erro com dados inválidos (400)
4. Erro com conflito/duplicação (se aplicável)

#### Exemplo:
```java
// CT-004: Sucesso
@Test
@DisplayName("Deve criar usuário com sucesso")
void deveCriarUsuario() throws Exception {
    RegisterDTO dto = new RegisterDTO("João", "joao@test.com", "123456", "111.111.111-11", Role.USER);
    
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Usuário registrado com sucesso"));
}

// CT-005: Sem autenticação
@Test
@DisplayName("Não deve criar sem autenticação")
void naoDeveCriarSemAutenticacao() throws Exception {
    CreateDTO dto = new CreateDTO("Nome", "dados");
    
    mockMvc.perform(post("/endpoint")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
}

// CT-006: Dados inválidos
@Test
@DisplayName("Não deve criar com email duplicado")
void naoDeveCriarComDuplicado() throws Exception {
    // Criar primeiro
    userRepository.save(testUser);
    
    // Tentar criar duplicado
    RegisterDTO dto = new RegisterDTO("João", testUser.getEmail(), "123456", "111.111.111-11", Role.USER);
    
    mockMvc.perform(post("/auth/register")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
}
```

---

### 4.3 PUT (Atualização)

#### Padrão Obrigatório:
1. Sucesso com dados válidos
2. Erro sem autenticação (401)
3. Erro com recurso inexistente (404)
4. Atualização parcial (apenas campos específicos)

#### Exemplo:
```java
// CT-007: Sucesso
@Test
@DisplayName("Deve atualizar usuário com sucesso")
void deveAtualizar() throws Exception {
    UpdateDTO dto = new UpdateDTO("Novo Nome", "novo@test.com");
    
    mockMvc.perform(put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Novo Nome"));
}

// CT-008: Sem autenticação
@Test
@DisplayName("Não deve atualizar sem autenticação")
void naoDeveAtualizarSemAuth() throws Exception {
    UpdateDTO dto = new UpdateDTO("Nome", "email@test.com");
    
    mockMvc.perform(put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isUnauthorized());
}

// CT-009: Recurso inexistente
@Test
@DisplayName("Não deve atualizar usuário inexistente")
void naoDeveAtualizarInexistente() throws Exception {
    UpdateDTO dto = new UpdateDTO("Nome", "email@test.com");
    
    mockMvc.perform(put("/users/uuid-fake")
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isNotFound());
}

// CT-010: Atualização parcial
@Test
@DisplayName("Deve atualizar apenas nome")
void deveAtualizarApenasNome() throws Exception {
    UpdateDTO dto = new UpdateDTO("Novo Nome", "email-original@test.com");
    
    mockMvc.perform(put("/users/" + userId)
        .contentType(MediaType.APPLICATION_JSON)
        .header("Authorization", "Bearer " + token)
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.name").value("Novo Nome"))
        .andExpect(jsonPath("$.email").value("email-original@test.com"));
}
```

---

### 4.4 DELETE (Deleção)

#### Padrão Obrigatório:
1. Sucesso deletando recurso
2. Erro sem autenticação (401)
3. Erro com recurso inexistente (404)
4. Verificar persistência (recurso não existe mais)

#### Exemplo:
```java
// CT-011: Sucesso
@Test
@DisplayName("Deve deletar usuário com sucesso")
void deveDeletar() throws Exception {
    mockMvc.perform(delete("/users/" + userId)
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Usuário deletado com sucesso"));
    
    // Verificar que foi realmente deletado
    assert userRepository.findById(userId).isEmpty();
}

// CT-012: Sem autenticação
@Test
@DisplayName("Não deve deletar sem autenticação")
void naoDeveDeletarSemAuth() throws Exception {
    mockMvc.perform(delete("/users/" + userId))
        .andExpect(status().isUnauthorized());
}

// CT-013: Recurso inexistente
@Test
@DisplayName("Não deve deletar usuário inexistente")
void naoDeveDeletarInexistente() throws Exception {
    mockMvc.perform(delete("/users/uuid-fake")
        .header("Authorization", "Bearer " + token))
        .andExpect(status().isNotFound());
}
```

---

## 5. Validações de Resposta

### 5.1 Status HTTP

| Código | Uso | Exemplo |
|--------|-----|---------|
| **200** | Sucesso | GET, PUT, DELETE bem-sucedidos |
| **201** | Criado | POST bem-sucedido |
| **400** | Erro de Requisição | Dados inválidos, validação falhou |
| **401** | Não Autorizado | Sem autenticação |
| **403** | Proibido | Sem permissão (role insuficiente) |
| **404** | Não Encontrado | Recurso não existe |
| **409** | Conflito | Duplicação, violação de constrainta |

### 5.2 Validação de Corpo

```java
// Validar campo específico
.andExpect(jsonPath("$.id").value(expectedId))
.andExpect(jsonPath("$.name").value("Expected Name"))

// Validar tipo de array
.andExpect(jsonPath("$").isArray())
.andExpect(jsonPath("$.length()").value(3))

// Validar existência de campo
.andExpect(jsonPath("$.email").exists())
.andExpect(jsonPath("$.invalid").doesNotExist())

// Validar valores nulos
.andExpect(jsonPath("$.optional").isEmpty())

// Validar mensagem de erro
.andExpect(jsonPath("$.message").value("Erro esperado"))
```

---

## 6. Setup e Teardown

### 6.1 @BeforeEach - Executado Antes de Cada Teste

```java
@BeforeEach
void setup() {
    // 1. Limpar dados do teste anterior
    userRepository.deleteAll();
    categoryRepository.deleteAll();
    
    // 2. Criar usuários de teste
    commonUser = new User();
    commonUser.setName("João");
    commonUser.setEmail("joao@test.com");
    commonUser.setPassword(passwordEncoder.encode("123456"));
    commonUser.setCpf("111.111.111-11");
    commonUser.setRole(Role.USER);
    commonUser = userRepository.save(commonUser);
    
    // 3. Gerar tokens
    commonUserToken = tokenService.generateToken(commonUser);
    
    // 4. Criar dados auxiliares se necessário
    Category category = new Category();
    category.setName("Test Category");
    category.setUser(commonUser);
    category = categoryRepository.save(category);
}
```

### 6.2 Por que limpar antes?
- Isolamento: Cada teste começa com estado limpo
- Independência: Testes não dependem um do outro
- Previsibilidade: Resultados consistentes

---

## 7. Dados de Teste

### 7.1 Criar Dados Genéricos
```java
// Para reutilizar entre testes
private User createUser(String name, String email, Role role) {
    User user = new User();
    user.setName(name);
    user.setEmail(email);
    user.setPassword(passwordEncoder.encode("123456"));
    user.setCpf(UUID.randomUUID().toString().replaceAll("[^0-9]", "").substring(0, 11));
    user.setRole(role);
    return userRepository.save(user);
}
```

### 7.2 Usar IDs Válidos mas Inexistentes
```java
UUID idInexistente = UUID.randomUUID();  // ✅ Válido mas não existe
Long idInexistente = 99999L;             // ✅ Válido mas não existe
String idInvalido = "abc-123";           // ❌ Pode causar erro de parsing
```

---

## 8. Boas Práticas

### 8.1 ✅ Faça

```java
// ✅ Use nomes descritivos
@DisplayName("Deve listar usuários autenticados")

// ✅ Um assert por conceito lógico
.andExpect(status().isOk())
.andExpect(jsonPath("$.name").value("João"))

// ✅ Comente lógica complexa
// Criar usuário, deletar, verificar que não existe
User user = userRepository.save(...);
mockMvc.perform(delete(...))...;
assert userRepository.findById(user.getId()).isEmpty();

// ✅ Use variáveis significativas
String bearerToken = "Bearer " + commonUserToken;
UpdateDTO validUpdate = new UpdateDTO("New Name", "new@email.com");

// ✅ Isolamento completo
@BeforeEach void setup() { deleteAll(); createTestData(); }
```

### 8.2 ❌ Evite

```java
// ❌ Nomes genéricos
@DisplayName("Test")  // Não describe o teste
void test1() { }      // Nome inútil

// ❌ Testes dependentes
void test2() {
    // Depende de test1() ter rodado antes
}

// ❌ Dados hard-coded globais
private static final String EMAIL = "test@test.com";  // Pode causar conflito

// ❌ Múltiplos asserts diferentes
.andExpect(jsonPath("$.id").exists())      // Sobre existência
.andExpect(jsonPath("$.role").value("ADMIN")) // Sobre valor
.andExpect(jsonPath("$.createdAt").isNumber())  // Sobre tipo

// ❌ Sem validação de deleção
userRepository.delete(user);  // ❌ Não verificar se foi deletado
```

---

## 9. Padrão de Documentação de Caso de Uso

### 9.1 Template
```markdown
#### ID: CT-XXX
**Descrição:** [Uma linha clara]
**Pré-condição:** [Estado inicial]
**Ator:** [Quem executa]
**Cenário:** 
1. Passo 1
2. Passo 2
3. Passo 3

**Resultado Esperado:**
- Status: XXX
- Mensagem: [Esperado]
- Dados: [Esperado]

**Código do Teste:**
\`\`\`java
[Código aqui]
\`\`\`
```

### 9.2 Exemplo Completo
```markdown
#### ID: CT-008
**Descrição:** Atualizar dados de usuário com sucesso
**Pré-condição:** Usuário existe no banco de dados e está autenticado
**Ator:** Usuário comum autenticado

**Cenário:**
1. Usuário envia PUT para /users/{id}
2. Inclui novos valores para name e email
3. Sistema valida os dados
4. Sistema persiste as mudanças

**Resultado Esperado:**
- Status HTTP: 200 OK
- Corpo contém: id, name (atualizado), email (atualizado)

**Código do Teste:**
\`\`\`java
@Test
@DisplayName("Deve atualizar usuário com sucesso")
void deveAtualizar() throws Exception { ... }
\`\`\`
```

---

## 10. Checklist para Novos Testes

- [ ] Classe criada em `src/test/java/com/tcc/talkie/controller/`
- [ ] Nome segue padrão: `{Controller}IT.java`
- [ ] Anotações: `@SpringBootTest`, `@AutoConfigureMockMvc`, `@ActiveProfiles("test")`
- [ ] `@BeforeEach` limpa dados: `repository.deleteAll()`
- [ ] Pelo menos 1 teste para cada verbo HTTP
- [ ] Cada teste cobre: sucesso, 401, 404 (se aplicável)
- [ ] DisplayNames são descritivos
- [ ] Assertions validam status E corpo
- [ ] Deleção verifica persistência com `repository.findById().isEmpty()`
- [ ] Documentação em `.md` criada
- [ ] README.md atualizado
- [ ] Todos os testes passam: `mvn test -Dtest={ControllerName}IT`

---

## 11. Executando Testes

### 11.1 Executar Classe Específica
```bash
./mvnw test -Dtest=UserControllerIT
```

### 11.2 Executar Teste Específico
```bash
./mvnw test -Dtest=UserControllerIT#deveListarUsuarios
```

### 11.3 Todos os Testes
```bash
./mvnw test
```

### 11.4 Com Output Detalhado
```bash
./mvnw test -X
```

---

## 12. Estrutura de Pacotes

```
src/test/java/com/tcc/talkie/
├── controller/
│   ├── AuthControllerIT.java          ✅
│   ├── CategoryControllerIT.java       ✅
│   ├── OccurrenceControllerIT.java    ✅
│   ├── SubcategoryControllerIT.java   ✅
│   └── UserControllerIT.java          ✅
└── bdd/
    ├── steps/
    │   ├── AuthSteps.java
    │   ├── CategoriaSteps.java
    │   └── OcorrenciasSteps.java
    └── features/
        ├── categoria.feature
        └── ocorrencia.feature
```

---

## 13. Referências

- **Spring Boot Test Documentation:** https://spring.io/guides/gs/testing-web/
- **JUnit 5 Guide:** https://junit.org/junit5/docs/current/user-guide/
- **Mockito Documentation:** https://javadoc.io/doc/org.mockito/mockito-core/latest/org/mockito/Mockito.html
- **CLAUDE.md** - Padrões do projeto Talkie

---

## 14. Conclusão

Seguindo este guia, você garantirá:
- ✅ Cobertura consistente
- ✅ Código limpo e legível
- ✅ Documentação clara
- ✅ Testes independentes
- ✅ Qualidade acadêmica

---

**Guia preparado para projeto Talkie - TCC UFF**  
**Versão:** 1.0  
**Data:** 16 de junho de 2026

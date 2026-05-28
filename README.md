# Talkie

Para acessar o swagger: http://localhost:8080/swagger-ui/index.html

## Andamento das etapas do projeto

- [x] criação da tabela de usuários
- [x] criação da tabela de tipos
- [x] criação da tabela de subtipos
- [x] Corrigir funções para utilizar service e controller
  - [x] Auth
  - [x] User - falta apenas Get
- [x] Implementando métodos de CRUD que faltam
  - [x] Category: Update, FindByID
  - [x] User: Delete
- [x] Revisar as nomenclatura de rotas
- [x] Adicionar Swagger
- [x] Revisar a criação de um Handler Global
  - [x] Auth
  - [x] Category
  - [x] Subcategory
  - [x] User
- [x] Remover criptografia do CPF
- [x] Criar autenticação

### Próximos passos

- [x] Ajustar Autorização de rotas para incluir role
  - [x] incluir role no token
- [x] Criar a tabela de ocorrências
- [x] Refatorar o preenchimento de usuário no banco para usar o helper
- [x] Criação de testes automatizados
- [x] Criar rota de delete de ocorrencias apenas para admin
- [ ] Criar a inscrição nos tópicos
  
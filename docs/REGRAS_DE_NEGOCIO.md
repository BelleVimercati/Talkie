# Documento de Regras de Negócio (RN) - Projeto Talkie

Este documento descreve as diretrizes, restrições e comportamentos esperados do sistema Talkie, baseando-se na implementação atual do código-fonte.

---

## 1. Regras de Autenticação e Autorização (Security Business Rules)

*   **RN01 - Unicidade de Identidade:** Não é permitido o cadastro de dois usuários com o mesmo endereço de e-mail.
*   **RN02 - Persistência de Sessão:** O sistema deve utilizar tokens JWT com expiração definida para validar a identidade do usuário em cada requisição (Stateless).
*   **RN03 - Hierarquia de Acesso (RBAC):**
    *   **ROLE_USER:** Pode registrar ocorrências, visualizar suas próprias ocorrências e listar todas as ocorrências disponíveis.
    *   **ROLE_ADMIN:** Possui permissões totais, incluindo a gestão de taxonomia (categorias) e a moderação (exclusão) de qualquer ocorrência no sistema.

---

## 2. Regras de Taxonomia (Categorias e Subcategorias)

*   **RN04 - Unicidade de Categorias:** Não é permitida a criação de categorias com nomes duplicados (ignorando maiúsculas/minúsculas).
*   **RN05 - Vínculo de Propriedade:** Toda categoria criada deve estar associada ao usuário (`ADMIN`) que a cadastrou para fins de auditoria.
*   **RN06 - Integridade de Subcategorias:** Uma subcategoria não pode existir sem estar vinculada a uma categoria pai válida.

---

## 3. Regras de Ocorrências (Core Business)

*   **RN07 - Composição da Ocorrência:** Para ser válida, uma ocorrência deve conter obrigatoriamente: Título, Descrição, Localização, Categoria, Subcategoria e Proprietário (usuário logado).
*   **RN08 - Registro de Data:** Toda ocorrência deve ter sua data de criação (`createdAt`) registrada automaticamente no momento da persistência pelo servidor.
*   **RN09 - Visibilidade Restrita de Edição/Exclusão:** 
    *   Atualmente, a exclusão de ocorrências via API é restrita a administradores (`ROLE_ADMIN`).
    *   *Nota: O sistema ainda não implementou regra para que o proprietário exclua sua própria ocorrência.*

---

## 4. Regras de Assinaturas e Notificações

*   **RN10 - Inscrição Única:** Um usuário não pode se inscrever mais de uma vez na mesma categoria de interesse (restrição de chave única no banco de dados).
*   **RN11 - Disparo de Eventos:** A criação de uma ocorrência deve obrigatoriamente disparar um evento interno (`OccurrenceCreatedEvent`) para processamento assíncrono ou notificações.

---

## 5. Sugestões de Ajustes e Novas Implementações

Para evoluir a maturidade do projeto e melhorar a experiência do usuário, sugerem-se os seguintes pontos:

### Ajustes Funcionais (Correções de Lógica)
1.  **Exclusão pelo Proprietário:** Alterar a lógica no `OccurrenceService` e `SecurityConfig` para permitir que um usuário comum exclua suas próprias ocorrências, mantendo a restrição de ADMIN apenas para ocorrências de terceiros.
2.  **Validação de Endereço:** Integrar uma API de geocodificação ou validação de formato para o campo `location`, evitando dados inconsistentes (ex: "rua 123" vs "coordenadas lat/long").
3.  **Soft Delete:** Em vez de remover fisicamente as ocorrências do banco de dados, implementar um campo `active` ou `deletedAt` para manter histórico de auditoria.

### Novas Funcionalidades (Roadmap)
1.  **Ciclo de Vida da Ocorrência (Status):** Adicionar um campo `Status` (ex: `ABERTO`, `EM_ANALISE`, `RESOLVIDO`, `DUPLICADO`). Apenas administradores ou órgãos competentes poderiam alterar este status.
2.  **Upload de Mídia:** Permitir o anexo de imagens ou vídeos à ocorrência para comprovação visual do relato.
3.  **Sistema de Notificações Ativo:** Implementar o código no `OccurrenceNotificationListener` para enviar e-mails ou notificações push para os usuários inscritos na categoria da nova ocorrência.
4.  **Sistema de "Apoio" (Upvotes):** Permitir que outros usuários "apoiem" uma ocorrência existente em vez de criar uma duplicada, gerando um ranking de relevância/urgência.
5.  **Dashboard Administrativo:** Endpoint para gerar estatísticas de ocorrências por categoria e região, facilitando a tomada de decisão pública.

# Exclusão de conta

Como o Talkie exclui uma conta sem perder as ocorrências nem o histórico de tratamento.

## Regra

Ao excluir uma conta:

- a linha em `users` é **apagada fisicamente** (nome, e-mail, CPF e hash da senha deixam de existir);
- as **ocorrências** do usuário continuam existindo, com o status, as datas e o conteúdo inalterados; só a autoria é removida (`owner_id = NULL`);
- o **histórico de status** é preservado; eventos feitos pelo usuário ficam com `changed_by = NULL`;
- **categorias** criadas pelo usuário (admin) são mantidas, com `user_id = NULL`;
- **assinaturas** do usuário são apagadas (só servem para enviar e-mail a ele);
- tokens já emitidos para a conta deixam de funcionar.

Onde a autoria for exibida e a referência for nula, a interface mostra **"Usuário excluído"** (`frontend/src/utils/formatAuthor.ts`). O rótulo existe só na apresentação; nada do usuário original é guardado.

### Correspondência de status

O enum `OccurrenceStatus` usa `ABERTO` (pendente), `RESOLVIDO` (resolvida) e `FECHADO` (declinada), além de `EM_ANALISE` quando presente. A exclusão da conta não altera o status de nenhuma ocorrência, qualquer que seja ele.

## Fluxo

| Quem | Rota | Confirmação |
|------|------|-------------|
| O próprio usuário | `DELETE /users/me` com `{ "password": "..." }` | senha atual |
| Administrador | `DELETE /users/{id}` | papel `ADMIN` |

Os dois usam `UserService.deleteAccount`, em uma única transação (`@Transactional`):

1. carrega o usuário (o ID do `/me` vem do token, nunca do corpo);
2. bloqueia a exclusão do **único** administrador (`400`);
3. apaga as assinaturas → anula `occurrences.owner_id` → anula `occurrence_status_history.changed_by` → anula `types.user_id`;
4. apaga o usuário e faz `flush()`.

Qualquer falha reverte tudo: não existe estado "meio anonimizado". O log registra apenas o UUID da conta excluída.

### Tokens e sessões

A autenticação é JWT stateless. O token passa a carregar o claim `uid`, e o `SecurityFilter` só autentica se o usuário do `sub` existir **e** tiver o mesmo `uid`. Com isso:

- o token de uma conta excluída devolve `401` em qualquer rota protegida;
- se alguém cadastrar uma conta nova com o mesmo e-mail, o token antigo **não** dá acesso a ela;
- repetir `DELETE /users/me` com o mesmo token devolve `401`, sem efeito no banco (idempotente);
- tokens emitidos antes desta mudança (sem `uid`) são rejeitados: depois do deploy, todos fazem login de novo (os tokens expiram em 2h de qualquer forma).

## Banco — migration `V9__account_deletion_and_status_history.sql`

| Referência | Antes | Depois |
|------------|-------|--------|
| `occurrences.owner_id` | `NOT NULL`, FK sem ação | anulável, `ON DELETE SET NULL`, índice novo |
| `types.user_id` | `NOT NULL`, FK sem ação | anulável, `ON DELETE SET NULL` |
| `subscriptions.subscriber_id` | FK sem ação | `ON DELETE CASCADE` |
| `occurrence_status_history` | — | nova tabela; `changed_by` com `ON DELETE SET NULL`; `occurrence_id` com `ON DELETE CASCADE` |

A V9 também cria um evento inicial no histórico para cada ocorrência existente (apenas `INSERT`). Não há exclusão em cascata de usuários para ocorrências. O cascade do histórico só age quando um admin apaga a própria ocorrência (regra já existente).

Nos testes (H2 com `create-drop`) as FKs da migration não existem; por isso o service faz cada passo explicitamente, e as regras de FK do PostgreSQL ficam como defesa em profundidade.

### Reversão manual (Flyway Community não tem *undo*)

Só é segura se nenhuma ocorrência/categoria tiver perdido o autor (`owner_id`/`user_id` nulos):

```sql
DROP TABLE occurrence_status_history;
DROP INDEX idx_occurrences_owner_id;
ALTER TABLE occurrences DROP CONSTRAINT fk_occurrences_owner;
ALTER TABLE occurrences ADD CONSTRAINT fkm0l5p8ar9jv2lat213qua8fj3 FOREIGN KEY (owner_id) REFERENCES users(id);
ALTER TABLE occurrences ALTER COLUMN owner_id SET NOT NULL;
ALTER TABLE types DROP CONSTRAINT fk_types_user;
ALTER TABLE types ADD CONSTRAINT fke9jhleoesq8lrvbefjx4o7hh FOREIGN KEY (user_id) REFERENCES users(id);
ALTER TABLE types ALTER COLUMN user_id SET NOT NULL;
ALTER TABLE subscriptions DROP CONSTRAINT fk_subscriptions_subscriber;
ALTER TABLE subscriptions ADD CONSTRAINT fkoodc4352epkjrvxx79odlxbji FOREIGN KEY (subscriber_id) REFERENCES users(id);
DELETE FROM flyway_schema_history WHERE version = '9';
```

## Histórico de status

Toda criação e mudança de status grava um evento (`previous_status`, `new_status`, `reason` opcional, `changed_by`, `changed_at`). O admin pode informar o motivo no modal de detalhes, e o histórico é consultado em `GET /occurrences/{id}/history` (somente `ADMIN`).

## Pendências de negócio (não decididas no código)

- **Texto livre:** título, descrição, localização e motivo podem conter dados pessoais digitados pelos usuários. Não são anonimizados automaticamente. Precisa de uma política (revisão manual, prazo de retenção ou edição pelo admin).
- **Geolocalização:** latitude/longitude de uma ocorrência podem apontar para a residência do autor — ainda mais agora que podem vir do próprio aparelho (Geolocation API). Pode ser necessário reduzir a precisão ou apagar após a resolução. Ao usar o mapa, o navegador do usuário também envia a região e o IP aos servidores do OpenStreetMap (tiles e Nominatim); isso deve constar nos termos de uso.
- **Imagem:** o upload ainda não existe; quando existir, a imagem precisa da mesma análise.
- **Prazo de retenção:** não há obrigação legal/regulatória registrada no projeto; por isso nenhum dado pessoal é retido após a exclusão.
- **Último administrador:** não pode se excluir; é preciso promover outro antes (ainda não existe rota de promoção).
- **Logs:** `OccurrenceNotificationListener` registra nome e e-mail dos assinantes em log; recomenda-se remover.
- **Concorrência:** se um admin salvar uma mudança de status no exato instante da exclusão, a requisição dele pode falhar por FK e ser revertida; basta repetir. Os dados não ficam inconsistentes.

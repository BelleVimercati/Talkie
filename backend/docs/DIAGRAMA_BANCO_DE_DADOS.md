# Diagrama do Banco de Dados — Talkie

Modelo entidade-relacionamento do PostgreSQL, derivado das migrations Flyway `V1` a `V9`
(`backend/src/main/resources/db/migration/`).

```mermaid
erDiagram
    users {
        uuid id PK
        varchar cpf UK
        varchar email UK
        varchar name
        varchar password
        varchar role "USER | ADMIN"
        timestamp created_at
    }

    types {
        bigint id PK
        uuid user_id FK "criador (anulável, SET NULL)"
        varchar name UK
        varchar icon
        varchar priority "BAIXA | MEDIA | ALTA"
        varchar color
        timestamp created_at
    }

    subcategories {
        bigint id PK
        bigint category_id FK
        varchar name
    }

    occurrences {
        bigint id PK
        uuid owner_id FK "anulável, SET NULL"
        bigint category_id FK
        bigint subcategory_id FK
        varchar title
        text description
        varchar location
        double latitude
        double longitude
        varchar image_path
        varchar status "ABERTO | RESOLVIDO | FECHADO"
        timestamp occurred_at
        timestamp created_at
        timestamp resolved_at
    }

    subscriptions {
        bigint id PK
        uuid subscriber_id FK "UK (subscriber_id, category_id); CASCADE"
        bigint category_id FK "UK (subscriber_id, category_id)"
        timestamp subscribed_at
    }

    occurrence_status_history {
        bigint id PK
        bigint occurrence_id FK "CASCADE"
        varchar previous_status
        varchar new_status
        text reason
        uuid changed_by FK "anulável, SET NULL"
        timestamp changed_at
    }

    users |o--o{ types : "cria"
    users |o--o{ occurrences : "reporta"
    users ||--o{ subscriptions : "assina"
    types ||--o{ subcategories : "possui"
    types ||--o{ occurrences : "classifica"
    types ||--o{ subscriptions : "é assinada em"
    subcategories ||--o{ occurrences : "classifica"
    occurrences ||--o{ occurrence_status_history : "registra"
    users |o--o{ occurrence_status_history : "altera"
```

## Tabelas

| Tabela | Entidade JPA | Descrição |
|--------|--------------|-----------|
| `users` | `User` | Usuários do sistema (cidadãos e administradores). |
| `types` | `Category` | Categorias de ocorrência, com prioridade, cor e ícone. |
| `subcategories` | `Subcategory` | Subdivisões de uma categoria. |
| `occurrences` | `Occurrence` | Problemas reportados pelos cidadãos. |
| `subscriptions` | `Subscription` | Assinatura de um usuário em uma categoria (gera notificações por e-mail). |
| `occurrence_status_history` | `OccurrenceStatusHistory` | Eventos de criação e mudança de status, com motivo e autor. |

## Observações

- `subscriptions` tem restrição `UNIQUE (subscriber_id, category_id)`: um usuário assina cada categoria uma única vez.
- `occurrences.category_id` e `occurrences.subcategory_id` são FKs independentes: nem o banco nem o `OccurrenceService` verificam hoje se a subcategoria pertence à categoria informada.
- `subscriptions.subscriber_id` e `subscriptions.category_id` são anuláveis no schema, embora a aplicação sempre os preencha.
- O usuário administrador inicial é inserido pela migration `V7`.
- Ao excluir uma conta, as referências em `occurrences`, `types` e `occurrence_status_history` viram `NULL` e as assinaturas são apagadas (ver `EXCLUSAO_DE_CONTA.md`).

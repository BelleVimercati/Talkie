# language: pt

Funcionalidade: Gerenciamento de Ocorrências

    Cenário: Criar uma ocorrência com sucesso
        Dado que o usuário admin esteja autenticado
        E que já exista categoria cadastrada
        Quando o usuário enviar uma requisição "POST" para "/occurrences" com os seguintes dados:
            | title         | Teste |
            | description   | Teste |
            | location      | Rua 12 |
            | categoryId    | {categoryId} |
            | subcategoryId | {subcategoryId} |
        Então o status da resposta deve ser sucesso

    Cenário: Criar uma ocorrência sem autenticação
        Dado que o usuário não esteja autenticado
        E que já exista categoria cadastrada
        Quando o usuário enviar uma requisição "POST" para "/occurrences" com os seguintes dados:
            | title         | Teste |
            | description   | Teste |
            | location      | Rua 12 |
            | categoryId    | {categoryId} |
            | subcategoryId | {subcategoryId} |
        Então o status da resposta deve ser não autorizado

    
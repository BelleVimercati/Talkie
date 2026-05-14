# language: pt

Funcionalidade: Gerenciamento de Categorias

    Cenário: Criar uma categoria com sucesso
        Dado que o usuário admin esteja autenticado
        Quando o usuário enviar uma requisição "POST" para "/categories" com os seguintes dados:
            | name        | Violência  |
            | icon        | teste |
        Então o status da resposta deve ser 200

    Cenário: Criar uma categoria sendo um usuário comum
        Dado que o usuário comum esteja autenticado
        Quando o usuário enviar uma requisição "POST" para "/categories" com os seguintes dados:
            | name        | Violência  |
            | icon        | teste |
        Então o status da resposta deve ser 403

    Cenário: Criar uma categoria sem autenticação
        Dado que o usuário não esteja autenticado
        Quando o usuário enviar uma requisição "POST" para "/categories" com os seguintes dados:
            | name | Violência |
            | icon | teste |
        Então o status da resposta deve ser 401

    Cenário: Criar uma categoria com nome duplicado
        Dado que o usuário admin esteja autenticado
        E que já exista uma categoria cadastrada com nome "Violência"
        Quando o usuário enviar uma requisição "POST" para "/categories" com os seguintes dados:
            | name | Violência |
            | icon | teste |
        Então o status da resposta deve ser 400

    
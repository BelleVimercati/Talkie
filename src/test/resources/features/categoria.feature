# language: pt

Funcionalidade: Gerenciamento de Categorias

    Cenário: Criar uma categoria com sucesso
        Dado que o usuário esteja autenticado
        E que o usuário seja um administrador
        Quando o usuário enviar uma requisição "POST" para "/categories" com os seguintes dados:
            | name        | Violência  |
            | icon        | teste |
        Então o status da resposta deve ser 200

    
# language: pt
Funcionalidade: Notificações de Ocorrências
  Como um usuário subscrito em categorias
  Quero receber notificações por email quando novas ocorrências são reportadas
  Para me manter informado sobre problemas que me interessam

  Cenário: Enviar email para usuário subscrito quando nova ocorrência é criada
    Dado que um usuário "joao@example.com" está autenticado
    E um usuário "maria@example.com" está inscrito na categoria "Infraestrutura"
    Quando uma nova ocorrência "Buraco na rua" é criada na categoria "Infraestrutura"
    Então um email deve ser enviado para "maria@example.com"
    E o email deve conter o título "Buraco na rua"
    E o email deve conter a localização da ocorrência

  Cenário: Não enviar email para usuário não inscrito
    Dado que um usuário "joao@example.com" está autenticado
    E um usuário "pedro@example.com" não está inscrito na categoria "Infraestrutura"
    Quando uma nova ocorrência "Buraco na rua" é criada na categoria "Infraestrutura"
    Então nenhum email deve ser enviado para "pedro@example.com"

  Cenário: Usuário pode se inscrever em uma categoria
    Dado que um usuário "joao@example.com" está autenticado
    Quando o usuário se inscreve na categoria "Infraestrutura"
    Então a inscrição deve ser salva com sucesso

  Cenário: Usuário pode se desinscrever de uma categoria
    Dado que um usuário "joao@example.com" está autenticado
    E o usuário está inscrito na categoria "Infraestrutura"
    Quando o usuário se desinscreve da categoria "Infraestrutura"
    Então a inscrição deve ser removida com sucesso

  Cenário: Usuário não pode se inscrever na mesma categoria duas vezes
    Dado que um usuário "joao@example.com" está autenticado
    E o usuário está inscrito na categoria "Infraestrutura"
    Quando o usuário tenta se inscrever novamente na categoria "Infraestrutura"
    Então uma erro de inscrição duplicada deve ser retornado

  Cenário: Listar inscrições do usuário
    Dado que um usuário "joao@example.com" está autenticado
    E o usuário está inscrito na categoria "Infraestrutura"
    E o usuário está inscrito na categoria "Segurança"
    Quando o usuário solicita listar suas inscrições
    Então a lista deve conter 2 inscrições
    E deve incluir "Infraestrutura"
    E deve incluir "Segurança"

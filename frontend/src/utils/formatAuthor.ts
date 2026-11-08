export const DELETED_USER_LABEL = 'Usuário excluído'

/** Nome exibido para um autor; contas excluídas chegam da API sem nome. */
export function formatAuthor(name: string | null | undefined): string {
  return name ?? DELETED_USER_LABEL
}

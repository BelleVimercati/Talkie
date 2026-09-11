export function formatCpf(value: string): string {
  const digits = value.replace(/\D/g, '').slice(0, 11)
  return digits
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d)/, '$1.$2')
    .replace(/(\d{3})(\d{1,2})$/, '$1-$2')
}

export function unformatCpf(value: string): string {
  return value.replace(/\D/g, '')
}

export function isValidCpf(rawValue: string): boolean {
  const cpf = unformatCpf(rawValue)

  if (cpf.length !== 11) return false
  if (/^(\d)\1{10}$/.test(cpf)) return false

  const calcCheckDigit = (base: string): number => {
    let sum = 0
    let weight = base.length + 1
    for (const digit of base) {
      sum += Number(digit) * weight--
    }
    const rest = sum % 11
    return rest < 2 ? 0 : 11 - rest
  }

  const digit1 = calcCheckDigit(cpf.slice(0, 9))
  const digit2 = calcCheckDigit(cpf.slice(0, 9) + digit1)

  return cpf === cpf.slice(0, 9) + String(digit1) + String(digit2)
}

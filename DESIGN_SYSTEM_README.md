# 🎨 Design System Talkie - Guia de Implementação

Este guia descreve como o frontend deve usar o Design System do Talkie para manter consistência visual e garantir uma experiência de usuário coerente em toda a aplicação.

---

## 📁 Arquivos do Design System

1. **DESIGN_SYSTEM.md** - Documentação completa de todos os elementos, componentes e padrões
2. **design-tokens.css** - Arquivo CSS com variáveis de design prontas para usar
3. **componentes-exemplo.html** - Página interativa com exemplos visuais de todos os componentes
4. **DESIGN_SYSTEM_README.md** - Este arquivo (guia de implementação)

---

## 🚀 Como Começar

### Passo 1: Copie o arquivo `design-tokens.css`

Coloque o arquivo `design-tokens.css` na pasta do seu projeto (recomendado: `src/styles/` ou similar) e importe no seu HTML ou main CSS:

```html
<!-- No HTML -->
<link rel="stylesheet" href="path/to/design-tokens.css">
```

```css
/* Ou no CSS principal */
@import url('path/to/design-tokens.css');
```

### Passo 2: Use as Variáveis CSS

Agora você tem acesso a todas as variáveis de design. Use-as em seus estilos:

```css
.meu-botao {
  background-color: var(--color-primary);
  color: var(--color-white);
  padding: var(--spacing-md);
  border-radius: var(--radius-md);
  font-size: var(--font-size-body);
  font-weight: 600;
  transition: var(--transition-base);
}

.meu-botao:hover {
  background-color: var(--color-primary-dark);
}
```

### Passo 3: Use as Classes Utilitárias

O `design-tokens.css` inclui classes prontas para uso rápido:

```html
<!-- Espaçamento -->
<div class="p-md">Padding médio</div>
<div class="m-lg">Margin grande</div>
<div class="px-md py-lg">Padding horizontal médio e vertical grande</div>

<!-- Flexbox -->
<div class="flex gap-md">
  <div>Item 1</div>
  <div>Item 2</div>
</div>

<!-- Botões -->
<button class="btn btn-primary">Enviar</button>
<button class="btn btn-secondary">Cancelar</button>

<!-- Avatares -->
<div class="avatar avatar-md bg-avatar-blue">AB</div>

<!-- Badges -->
<span class="badge badge-success">Online</span>
```

---

## 🎨 Cores

### Usando Variáveis de Cor

```css
.elemento {
  color: var(--color-primary);           /* Azul principal */
  background: var(--color-bg-light);     /* Fundo claro */
  border-color: var(--color-border);     /* Borda padrão */
}
```

### Paleta de Cores Disponíveis

| Variável | Valor | Uso |
|----------|-------|-----|
| `--color-primary` | #2563EB | Botões, links, destaques |
| `--color-white` | #FFFFFF | Fundo principal |
| `--color-black` | #1F2937 | Texto principal |
| `--color-accent-yellow` | #FBBF24 | Destaque especial |
| `--color-bg-light` | #F3F4F6 | Fundo secundário |
| `--color-border` | #D1D5DB | Bordas |
| `--color-text-secondary` | #6B7280 | Texto secundário |
| `--color-status-online` | #10B981 | Status online |
| `--color-status-offline` | #EF4444 | Status offline |

---

## ✍️ Tipografia

### Tamanhos de Fonte

```css
.titulo {
  font-size: var(--font-size-h1);      /* 32px */
  font-weight: var(--font-weight-h1);  /* 700 */
}

.texto-normal {
  font-size: var(--font-size-body);    /* 14px */
  font-weight: var(--font-weight-body); /* 400 */
}

.texto-pequeno {
  font-size: var(--font-size-sm);      /* 12px */
}
```

### Usando Classes de Tipografia

```html
<h1 class="heading-1">Título Principal</h1>
<h2 class="heading-2">Subtítulo</h2>
<p class="body-large">Texto grande</p>
<p>Texto normal</p>
<p class="body-small">Texto pequeno</p>
<p class="caption">Caption</p>
<p class="text-secondary">Texto secundário</p>
```

---

## 📏 Espaçamento

O espaçamento segue um grid de 4px (variáveis xs, sm, md, lg, xl, 2xl).

### Usando Espaçamento

```css
.elemento {
  padding: var(--spacing-md);        /* 16px em todos os lados */
  margin-bottom: var(--spacing-lg);  /* 24px abaixo */
  gap: var(--spacing-sm);            /* 8px entre filhos */
}
```

### Classes de Espaçamento

```html
<!-- Padding -->
<div class="p-md">Padding em todos os lados</div>
<div class="px-lg">Padding horizontal</div>
<div class="py-sm">Padding vertical</div>

<!-- Margin -->
<div class="m-md">Margin em todos os lados</div>
<div class="mx-auto">Centralizar horizontalmente</div>

<!-- Gap (Flexbox) -->
<div class="flex gap-md">
  <div>Item 1</div>
  <div>Item 2</div>
</div>
```

---

## 🔘 Botões

### Botão Primário
```html
<button class="btn btn-primary">Enviar</button>
```

### Botão Secundário
```html
<button class="btn btn-secondary">Cancelar</button>
```

### Botão Desativado
```html
<button class="btn btn-disabled" disabled>Desativado</button>
```

### Botão Full Width
```html
<button class="btn btn-primary btn-full-width">Enviar</button>
```

---

## 📝 Inputs

### Input Básico
```html
<div class="form-group">
  <label class="form-label">Seu Nome</label>
  <input type="text" class="input" placeholder="Digite seu nome">
</div>
```

### Input com Erro
```html
<div class="form-group">
  <label class="form-label">Email</label>
  <input type="email" class="input input-error" placeholder="seu@email.com">
  <span class="form-error">Email inválido</span>
</div>
```

---

## 🎭 Avatares

### Avatar Simples
```html
<!-- Pequeno -->
<div class="avatar avatar-sm bg-avatar-blue">AB</div>

<!-- Médio -->
<div class="avatar avatar-md bg-avatar-pink">CD</div>

<!-- Grande -->
<div class="avatar avatar-lg bg-avatar-green">EF</div>
```

### Avatar com Status
```html
<div class="avatar avatar-md avatar-status online bg-avatar-blue">JS</div>
```

### Cores de Avatar Disponíveis
```
bg-avatar-red
bg-avatar-pink
bg-avatar-purple
bg-avatar-indigo
bg-avatar-blue
bg-avatar-cyan
bg-avatar-teal
bg-avatar-green
bg-avatar-lime
bg-avatar-yellow
bg-avatar-orange
bg-avatar-neutral
```

---

## 🏷️ Badges

### Badge Padrão
```html
<span class="badge">Label</span>
```

### Badges com Estilo
```html
<span class="badge badge-primary">Primário</span>
<span class="badge badge-success">Sucesso</span>
<span class="badge badge-error">Erro</span>
<span class="badge badge-warning">Aviso</span>
```

---

## 📦 Cards

### Card Simples
```html
<div class="card">
  <h3>Título do Card</h3>
  <p>Conteúdo do card</p>
</div>
```

### Card Pequeno
```html
<div class="card card-sm">
  Conteúdo compacto
</div>
```

### Card Sem Sombra
```html
<div class="card card-no-shadow">
  Conteúdo
</div>
```

---

## 🎯 Padrões Comuns

### Formulário Completo
```html
<form>
  <div class="form-group">
    <label class="form-label">Email</label>
    <input type="email" class="input" placeholder="seu@email.com">
  </div>

  <div class="form-group">
    <label class="form-label">Senha</label>
    <input type="password" class="input" placeholder="Sua senha">
  </div>

  <button class="btn btn-primary btn-full-width">Entrar</button>
</form>
```

### Chat Card
```html
<div class="chat-card">
  <div class="avatar avatar-md bg-avatar-blue">JS</div>
  <div class="chat-info">
    <div class="chat-header">
      <span class="chat-name">João Silva</span>
      <span class="chat-time">10:30</span>
    </div>
    <div class="chat-message">Última mensagem do chat...</div>
  </div>
</div>
```

### Perfil de Usuário
```html
<div class="profile-section">
  <div class="avatar avatar-lg bg-avatar-blue">MB</div>
  <div class="profile-info">
    <div class="profile-name">Maria Belém</div>
    <div class="profile-status">
      <div class="status-dot online"></div>
      <span>Online agora</span>
    </div>
    <div class="profile-actions">
      <button class="btn btn-primary">Mensagem</button>
      <button class="btn btn-secondary">Opções</button>
    </div>
  </div>
</div>
```

---

## 📱 Responsividade

O design system inclui breakpoints para diferentes tamanhos de tela:

```css
/* Mobile: < 640px */
@media (max-width: 640px) {
  /* Estilos para mobile */
}

/* Tablet: 640px - 1024px */
@media (min-width: 640px) and (max-width: 1024px) {
  /* Estilos para tablet */
}

/* Desktop: > 1024px */
@media (min-width: 1024px) {
  /* Estilos para desktop */
}
```

### Grid Responsivo
```html
<div class="component-grid">
  <div>Item 1</div>
  <div>Item 2</div>
  <div>Item 3</div>
</div>
```

---

## 🎬 Animações e Transições

### Transição Padrão
```css
.elemento {
  transition: var(--transition-base);  /* 200ms ease-in-out */
}

.elemento:hover {
  opacity: 0.8;
}
```

### Transições Disponíveis
- `--transition-fast` - 100ms
- `--transition-base` - 200ms (padrão)
- `--transition-slow` - 300ms

---

## ✅ Checklist de Implementação

Ao implementar uma página, verifique:

- [ ] Está usando variáveis CSS do `design-tokens.css`?
- [ ] As cores seguem a paleta definida?
- [ ] O espaçamento usa o grid de 4px?
- [ ] A tipografia está correta?
- [ ] Os botões têm os estados hover/active?
- [ ] Os inputs têm validação visual (error)?
- [ ] Os avatares usam as cores corretas?
- [ ] A responsividade foi testada em mobile/tablet/desktop?
- [ ] As transições são suaves e não distrativas?
- [ ] Acessibilidade foi considerada (contrast, focus, labels)?

---

## 🐛 Troubleshooting

### As variáveis CSS não funcionam
- Verifique se o arquivo `design-tokens.css` foi importado corretamente
- Verifique o caminho relativo do arquivo
- Certifique-se de que está dentro de um elemento dentro de `<body>`

### As cores parecem diferentes
- Verifique se está usando as variáveis corretas do design-tokens.css
- Não adicione estilos inline que sobrescrevam as variáveis

### Layout responsivo não funciona
- Verifique se o viewport meta tag está no `<head>`:
  ```html
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  ```

---

## 📖 Referências

- Para documentação completa: veja **DESIGN_SYSTEM.md**
- Para exemplos visuais: abra **componentes-exemplo.html** no navegador
- Para discussões sobre o design: consulte a página de design do Figma

---

## 💡 Dicas Importantes

1. **Mantenha a Consistência**: Sempre use as variáveis de design, não copie valores hexadecimais
2. **Prefira Classes Utilitárias**: Use `class="p-md gap-sm"` em vez de CSS customizado
3. **Teste Responsividade**: Teste em mobile, tablet e desktop antes de considerar pronto
4. **Acessibilidade Primeiro**: Garanta que todos os elementos tenham contraste adequado e sejam navegáveis via teclado
5. **Documentação**: Mantenha este documento atualizado conforme o design system evolui

---

**Última atualização**: 2026-09-11
**Design System versão**: 1.0

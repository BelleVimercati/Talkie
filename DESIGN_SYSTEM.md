# Design System - Talkie

Guia de design para desenvolvimento do frontend do Talkie. Todas as páginas e componentes devem seguir este padrão.

---

## 1. Paleta de Cores

### Cores Primárias
- **Azul Principal**: `#2563EB` - Usado em botões, links, destaques, status online
- **Branco**: `#FFFFFF` - Fundo principal
- **Preto/Cinza Escuro**: `#1F2937` - Texto principal

### Cores Secundárias
- **Amarelo Destaque**: `#FBBF24` - Elementos de destaque (ex: bolinha do avatar)
- **Cinza Claro**: `#F3F4F6` - Fundo secundário, cards
- **Cinza Médio**: `#D1D5DB` - Bordas, linhas de divisão
- **Cinza Texto**: `#6B7280` - Texto secundário, labels

### Cores de Status
- **Verde (Online)**: `#10B981` - Status online
- **Vermelho (Offline)**: `#EF4444` - Status offline/erro
- **Laranja (Away)**: `#F97316` - Status ausente

### Cores para Avatares
- Paleta variada com cores vibrantes para distinguir usuários (vermelho, rosa, roxo, azul, ciano, verde, amarelo, etc.)

---

## 2. Tipografia

### Fontes
- **Família Principal**: `Arial`, `Helvetica`, `sans-serif` (ou ajustar conforme projeto)

### Tamanhos e Pesos

| Nome | Tamanho | Peso | Uso |
|------|---------|------|-----|
| **Heading 1** | 32px | 700 (Bold) | Títulos principais |
| **Heading 2** | 24px | 700 (Bold) | Subtítulos, cabeçalhos de seção |
| **Heading 3** | 18px | 600 (Semibold) | Títulos de cards, modais |
| **Body Large** | 16px | 400 (Regular) | Texto principal em cards |
| **Body Regular** | 14px | 400 (Regular) | Texto padrão, labels |
| **Body Small** | 12px | 400 (Regular) | Textos auxiliares, timestamps |
| **Caption** | 11px | 400 (Regular) | Informações muito pequenas |

### Cores de Texto
- **Texto Principal**: `#1F2937` (Cinza Escuro)
- **Texto Secundário**: `#6B7280` (Cinza Médio)
- **Texto Invertido** (sobre fundo azul): `#FFFFFF` (Branco)

---

## 3. Espaçamento (Grid 4px)

| Tamanho | Valor | Uso |
|---------|-------|-----|
| **xs** | 4px | Espaçamentos muito pequenos |
| **sm** | 8px | Espaçamentos pequenos |
| **md** | 16px | Espaçamento padrão |
| **lg** | 24px | Espaçamentos grandes |
| **xl** | 32px | Espaçamentos muito grandes |
| **2xl** | 48px | Espaçamentos entre seções |

---

## 4. Componentes Base

### 4.1 Botões

#### Botão Primário
```
Estilo: Fundo #2563EB, Texto Branco
Padding: 12px 24px
Altura: 44px (aproximado)
Border-radius: 6px
Font-size: 14px
Font-weight: 600
Estado Hover: Fundo #1D4ED8 (mais escuro)
Estado Active: Fundo #1E40AF (ainda mais escuro)
```

#### Botão Secundário
```
Estilo: Fundo Transparente, Texto Preto, Borda #1F2937
Padding: 12px 24px
Altura: 44px (aproximado)
Border-radius: 6px
Font-size: 14px
Font-weight: 600
Estado Hover: Fundo #F3F4F6
```

#### Botão Desativado
```
Estilo: Fundo #D1D5DB, Texto #9CA3AF
Padding: 12px 24px
Cursor: not-allowed
```

### 4.2 Inputs

```
Altura: 40px
Padding: 10px 12px
Border: 1px solid #D1D5DB
Border-radius: 6px
Font-size: 14px
Cor Fundo: #FFFFFF
Cor Texto: #1F2937
Placeholder: #9CA3AF (Cinza)

Estado Focus:
- Border-color: #2563EB
- Box-shadow: 0 0 0 3px rgba(37, 99, 235, 0.1)

Estado Error:
- Border-color: #EF4444
```

### 4.3 Cards

```
Fundo: #FFFFFF
Border: 1px solid #E5E7EB ou sem borda
Border-radius: 8px
Padding: 16px ou 20px
Box-shadow: 0 1px 3px rgba(0, 0, 0, 0.1)
```

### 4.4 Avatares

```
Tamanho: 40px (pequeno), 48px (médio), 64px (grande)
Border-radius: 50% (circular)
Font-size: 16px (para iniciais)
Font-weight: 600
Usar cores diferentes da paleta para cada usuário

Indicador de Status:
- Tamanho: 12px x 12px
- Posição: Canto inferior direito
- Cores: Verde (online), Vermelho (offline), Laranja (away)
- Border: 2px branco
```

### 4.5 Badges/Chips

```
Padding: 4px 12px
Border-radius: 12px (arredondado)
Font-size: 12px
Font-weight: 500
Exemplos: Status, Tags, Labels
```

### 4.6 Notificação/Toast

```
Fundo: Varia conforme tipo (Sucesso: verde, Erro: vermelho, Info: azul)
Cor Texto: Branco
Padding: 16px 20px
Border-radius: 6px
Min-width: 300px
Box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15)
```

---

## 5. Layout

### Estrutura Geral
- **Sidebar/Menu**: Esquerda (se mobile, colapsável)
- **Content Area**: Centro/Direita
- **Responsividade**: 
  - Desktop: Layout full
  - Tablet: Sidebar colapsada
  - Mobile: Menu hamburger

### Tamanhos de Container
- **Desktop**: Max-width 1400px
- **Tablet**: Max-width 1024px
- **Mobile**: Full-width com padding

### Gaps e Margens Padrão
- Entre elementos: 16px (md)
- Entre seções: 32px (xl) ou 48px (2xl)
- Padding de página: 24px (lg)
- Padding de cards: 16px-20px (md-lg)

---

## 6. Ícones e Ilustrações

### Ícone de Usuário
- Tamanho padrão: 24x24px
- Stroke: 2px
- Cor: Seguir cor do contexto

### Avatares com Iniciais
- Quando não houver imagem, usar iniciais do nome
- Duas primeiras letras (ex: "JD" para João da Silva)
- Usando cores da paleta de avatares

### Ilustração Principal
- Personagem Talkie em diferentes situações (bem-vindo, conversa, etc.)
- Balão amarelo (#FBBF24) como elemento recorrente

---

## 7. Estados Interativos

### Hover
- Mudança de cor ou background leve
- Cursor pointer em elementos clicáveis
- Transição suave (0.2s)

### Focus
- Outline de 2px em cor primária (azul)
- Para acessibilidade

### Disabled
- Opacidade 50% ou cinza desaturado
- Cursor not-allowed

### Loading
- Spinner azul (#2563EB)
- Skeleton screens em cinza claro (#E5E7EB)

---

## 8. Breakpoints (Responsive)

```
Mobile: < 640px
Tablet: 640px - 1024px
Desktop: > 1024px
```

### Ajustes por Breakpoint
- **Mobile**: Fonte reduzida (-2px), padding menor, layout stack vertical
- **Tablet**: Fonte normal, padding médio, layout em colunas
- **Desktop**: Fonte normal, padding completo, layout full

---

## 9. Animações e Transições

- **Transição Padrão**: 200ms (0.2s) ease-in-out
- **Entrada de Modal/Toast**: 300ms fade-in
- **Saída de Modal/Toast**: 200ms fade-out
- **Hover de Botão**: 200ms background-color
- **Loading Spinner**: Rotação infinita 1s linear

---

## 10. Exemplos de Uso

### Página de Login
1. Container centralizado
2. Logo/Título (Heading 1)
3. Formulário com inputs e espaçamento md
4. Botão primário full-width
5. Link secundário para registro

### Página de Chat
1. Sidebar com lista de conversas (cards)
2. Area central com mensagens (timeline)
3. Input na base com botão de envio
4. Notificações no topo

### Página de Perfil/Configurações
1. Avatar grande (64px)
2. Dados do usuário (Heading 2)
3. Seções com cards
4. Botões de ação (primário/secundário)

---

## 11. Acessibilidade

- Todos os inputs devem ter labels associadas
- Cores não devem ser único indicador (usar ícones ou texto adicional)
- Contraste mínimo de 4.5:1 para texto
- Focus visível em todos os elementos interativos
- Atributos ARIA quando necessário (aria-label, aria-describedby, etc.)

---

## 12. Notas Importantes

- Manter consistência visual em todas as páginas
- Usar apenas cores, fontes e componentes definidos aqui
- Respeitar espaçamento e padding
- Testar responsividade em todos os breakpoints
- Garantir acessibilidade e usabilidade
- Animações devem ser suaves e não distrativas

# Frontend (Vue 3 + TypeScript)

- Scaffold básico com Vite + Vue 3 + TypeScript
- Pinia e Vue Router (estado e rotas)
- Estrutura: `src/components`, `src/pages`, `src/services`

Como executar (frontend):

1. cd frontend
2. npm install
3. npm run dev

## Design tokens

O projeto usa um conjunto pequeno de design tokens definidos em `src/styles/tokens.css`.

- Cores: variáveis `--color-*` (ex: `--color-primary`, `--color-accent`, `--color-card`).
- Tamanhos: `--size-container-max`, `--size-container-padding`.
- Sombras: `--shadow-subtle`.

Os tokens são usados via `var(--...)` e também expostos ao Tailwind (via `tailwind.config.cjs`) para manter consistência. O tema escuro é ativado adicionando a classe `dark` no `html` ou no `body`.

Exemplo rápido: use `background-color: var(--color-card)` em componentes ou classes Tailwind como `text-primary` (mapeado para a variável no config).

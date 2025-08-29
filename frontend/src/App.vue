<template>
  <div class="min-h-screen bg-gray-50">
    <!-- link para pular para o conteúdo principal (a11y) -->
    <a href="#main-content" class="skip-link sr-only focus:not-sr-only">
      Pular para o conteúdo
    </a>

    <Layout>
      <router-view />
    </Layout>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import Layout from "./components/Layout.vue";

/**
 * Componente raiz da aplicação frontend.
 * Usa `Layout` global que contém header, navegação responsiva e container.
 */
export default defineComponent({
  name: "App",
  components: { Layout },
});
</script>

<style>
/* Mantém qualquer estilo global mínimo. Tailwind cuida do restante */
html,
body,
#app {
  height: 100%;
}
/* estilos para o link de pular conteúdo e foco visível consistente */
.skip-link {
  position: absolute;
  left: 1rem;
  top: 0.5rem;
  background: white;
  color: #111827; /* gray-900 */
  padding: 0.25rem 0.5rem;
  border-radius: 0.25rem;
  z-index: 60;
  box-shadow: 0 1px 2px rgba(0, 0, 0, 0.05);
}

/* utilitário: remove visualmente, mas deixa visível quando em foco */
.sr-only {
  position: absolute;
  width: 1px;
  height: 1px;
  padding: 0;
  margin: -1px;
  overflow: hidden;
  clip: rect(0, 0, 0, 0);
  white-space: nowrap;
  border: 0;
}
.focus\:not-sr-only:focus {
  position: static;
  width: auto;
  height: auto;
  margin: 0;
  overflow: visible;
  clip: auto;
  white-space: normal;
}

/* foco consistente para elementos interativos quando usando teclado */
button:focus,
a:focus,
input:focus,
select:focus,
textarea:focus {
  outline: 3px solid rgba(59, 130, 246, 0.45);
  outline-offset: 2px;
}
</style>

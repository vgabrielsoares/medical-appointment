<template>
  <router-view />
</template>

<script lang="ts">
import { defineComponent } from "vue";

/**
 * Componente raiz da aplicação frontend.
 * Responsabilidade: renderizar a rota atual via <router-view>.
 */
export default defineComponent({
  name: "App",
});
</script>

<style>
/* estilos mínimos até integração com Tailwind */
html,
body,
#app {
  height: 100%;
  margin: 0;
  font-family: Inter, system-ui, -apple-system, "Segoe UI", Roboto,
    "Helvetica Neue", Arial;
}
</style>

<template>
  <div>
    <NavBar />

    <main id="main-content" class="py-8" role="main">
      <div class="app-container">
        <slot />
      </div>
    </main>

    <footer class="mt-8 py-6 text-center text-sm text-gray-500">
      <div class="app-container">&copy; {{ year }} — Agendamento Médico</div>
    </footer>

    <!-- Toasts globais -->
    <UiToast />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import NavBar from "./NavBar.vue";
import { UiToast } from "./ui";

export default defineComponent({
  name: "Layout",
  components: { NavBar, UiToast },
  data() {
    return { year: new Date().getFullYear() };
  },
});
</script>

<style scoped>
/* Pequeno espaçamento do footer para não colar no conteúdo */
</style>

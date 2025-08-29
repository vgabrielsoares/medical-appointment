<template>
  <div class="min-h-screen bg-gray-50">
    <header class="bg-white shadow-sm">
      <div class="app-container flex items-center justify-between h-16">
        <div class="flex items-center gap-4">
          <router-link to="/" class="text-lg font-semibold text-primary">
            Agendamento Médico
          </router-link>
        </div>
        <nav>
          <router-link
            to="/login"
            class="text-sm text-gray-600 hover:text-accent mr-4"
            >Login</router-link
          >
          <router-link
            to="/patient"
            class="text-sm text-gray-600 hover:text-accent mr-4"
            >Paciente</router-link
          >
          <router-link
            to="/doctor"
            class="text-sm text-gray-600 hover:text-accent"
            >Médico</router-link
          >
        </nav>
      </div>
    </header>

    <main class="py-8">
      <div class="app-container">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

/**
 * Componente raiz da aplicação frontend.
 * Renderiza header mínimo e centraliza conteúdo com `.app-container`.
 */
export default defineComponent({
  name: "App",
});
</script>

<style>
/* Mantém qualquer estilo global mínimo. Tailwind cuida do restante */
html,
body,
#app {
  height: 100%;
}
</style>

<template>
  <div class="app-container">
    <div class="card">
      <h1 class="text-2xl font-bold">Agendamento Médico</h1>
      <p class="mt-4">Aplicação demonstrativa — escolha sua rota:</p>
      <ul class="mt-4 space-y-2">
        <li><router-link to="/login">Entrar</router-link></li>
        <li><router-link to="/doctor">Área do médico</router-link></li>
        <li><router-link to="/patient">Área do paciente</router-link></li>
      </ul>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({ name: "Home" });
</script>

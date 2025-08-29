<template>
  <div class="app-container">
    <div class="card">
      <h2 class="text-xl font-semibold">Área do médico</h2>
      <p class="mt-2">Nesta área o médico poderá gerenciar seus horários.</p>
      <div class="mt-4">
        <router-link
          to="/doctor/slots"
          class="px-4 py-2 bg-blue-600 text-white rounded"
          >Gerenciar horários</router-link
        >
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({ name: "Doctor" });
</script>

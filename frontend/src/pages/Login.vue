<template>
  <div class="max-w-md mx-auto mt-12 p-6 bg-white rounded shadow">
    <h2 class="text-2xl font-semibold mb-4">Entrar</h2>

    <form @submit.prevent="onSubmit" class="space-y-4">
      <div>
        <label class="block text-sm font-medium">Email</label>
        <input
          v-model="email"
          type="email"
          required
          class="mt-1 block w-full border rounded px-3 py-2"
        />
      </div>

      <div>
        <label class="block text-sm font-medium">Senha</label>
        <input
          v-model="password"
          type="password"
          required
          class="mt-1 block w-full border rounded px-3 py-2"
        />
      </div>

      <div class="flex items-center justify-between">
        <button
          :disabled="loading"
          class="bg-blue-600 text-white px-4 py-2 rounded disabled:opacity-50"
          type="submit"
        >
          Entrar
        </button>
        <span v-if="loading" class="text-sm text-gray-500"
          >Autenticando...</span
        >
      </div>

      <p v-if="error" class="text-sm text-red-600">{{ error }}</p>

      <p class="text-xs text-gray-500">Use as credenciais seed do backend.</p>
    </form>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";

export default defineComponent({
  name: "Login",
  setup() {
    const email = ref("");
    const password = ref("");
    const loading = ref(false);
    const error = ref<string | null>(null);

    const router = useRouter();
    const auth = useAuthStore();

    async function onSubmit() {
      error.value = null;
      loading.value = true;
      try {
        const user = await auth.login(email.value, password.value);
        // Redireciona por role
        if (user.role === "ROLE_DOCTOR") {
          await router.push({ name: "doctor" });
        } else {
          await router.push({ name: "patient" });
        }
      } catch (e: any) {
        // Mensagem simples, sem vazar detalhes do servidor
        error.value = e?.response?.data?.message || "Credenciais inválidas";
      } finally {
        loading.value = false;
      }
    }

    return { email, password, loading, error, onSubmit };
  },
});
</script>

<style scoped>
body {
  background: #f3f4f6;
}
</style>

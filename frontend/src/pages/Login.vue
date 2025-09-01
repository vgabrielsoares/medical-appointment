<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900 flex items-center justify-center p-4"
  >
    <div class="w-full max-w-md">
      <!-- Logo Section -->
      <div class="text-center mb-8">
        <div
          class="inline-flex items-center justify-center w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl mb-4 shadow-xl shadow-blue-500/25"
        >
          <LockClosedIcon class="w-8 h-8 text-white" />
        </div>
        <h1
          class="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white"
        >
          Bem-vindo de volta
        </h1>
        <p class="text-gray-600 dark:text-gray-300 mt-2">
          Entre em sua conta para continuar
        </p>
      </div>

      <!-- Login Form -->
      <UiCard variant="glass" size="lg" class="backdrop-blur-xl">
        <form @submit.prevent="onSubmit" class="space-y-6">
          <!-- Email Field -->
          <div class="space-y-2">
            <label
              for="email"
              class="block text-sm font-medium text-gray-700 dark:text-gray-200"
            >
              Email
            </label>
            <div class="relative">
              <div
                class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"
              >
                <EnvelopeIcon class="h-5 w-5 text-gray-400" />
              </div>
              <UiInput
                id="email"
                v-model="email"
                type="email"
                required
                placeholder="seu@email.com"
                class="pl-10"
              />
            </div>
          </div>

          <!-- Password Field -->
          <div class="space-y-2">
            <label
              for="password"
              class="block text-sm font-medium text-gray-700 dark:text-gray-200"
            >
              Senha
            </label>
            <div class="relative">
              <div
                class="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none"
              >
                <LockClosedIcon class="h-5 w-5 text-gray-400" />
              </div>
              <UiInput
                id="password"
                v-model="password"
                :type="showPassword ? 'text' : 'password'"
                required
                placeholder="••••••••"
                class="pl-10 pr-10"
              />
              <button
                type="button"
                @click="showPassword = !showPassword"
                class="absolute inset-y-0 right-0 pr-3 flex items-center"
              >
                <EyeIcon
                  v-if="!showPassword"
                  class="h-5 w-5 text-gray-400 hover:text-gray-600"
                />
                <EyeSlashIcon
                  v-else
                  class="h-5 w-5 text-gray-400 hover:text-gray-600"
                />
              </button>
            </div>
          </div>

          <!-- Submit Button -->
          <UiButton
            type="submit"
            variant="primary"
            size="lg"
            :loading="loading"
            :icon="ArrowRightOnRectangleIcon"
            full-width
          >
            {{ loading ? "Entrando..." : "Entrar" }}
          </UiButton>

          <!-- Error Message -->
          <div v-if="error" class="rounded-lg bg-red-50 dark:bg-red-900/50 p-4">
            <div class="flex">
              <div class="flex-shrink-0">
                <ExclamationTriangleIcon class="h-5 w-5 text-red-400" />
              </div>
              <div class="ml-3">
                <p class="text-sm text-red-800 dark:text-red-200">
                  {{ error }}
                </p>
              </div>
            </div>
          </div>
        </form>

        <!-- Demo Credentials -->
        <div class="mt-6 pt-6 border-t border-gray-200 dark:border-gray-700">
          <div class="text-center">
            <p class="text-xs text-gray-500 dark:text-gray-400 mb-3">
              Credenciais de demonstração:
            </p>
            <div class="grid grid-cols-1 sm:grid-cols-2 gap-2 text-xs">
              <div class="bg-gray-50 dark:bg-gray-800 rounded-md p-2">
                <div class="font-medium text-gray-700 dark:text-gray-200">
                  Médico
                </div>
                <div class="text-gray-500 dark:text-gray-400">
                  doctor@example.com
                </div>
              </div>
              <div class="bg-gray-50 dark:bg-gray-800 rounded-md p-2">
                <div class="font-medium text-gray-700 dark:text-gray-200">
                  Paciente
                </div>
                <div class="text-gray-500 dark:text-gray-400">
                  patient@example.com
                </div>
              </div>
            </div>
          </div>
        </div>
      </UiCard>

      <!-- Back to Home -->
      <div class="text-center mt-6">
        <router-link
          to="/"
          class="inline-flex items-center gap-2 text-sm text-gray-600 dark:text-gray-300 hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
        >
          <ArrowLeftIcon class="w-4 h-4" />
          Voltar ao início
        </router-link>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import { useRouter } from "vue-router";
import {
  LockClosedIcon,
  EnvelopeIcon,
  EyeIcon,
  EyeSlashIcon,
  ArrowRightOnRectangleIcon,
  ArrowLeftIcon,
  ExclamationTriangleIcon,
} from "@heroicons/vue/24/solid";
import { UiButton, UiCard, UiInput } from "../components/ui";
import { useAuthStore } from "../stores/auth";

export default defineComponent({
  name: "Login",
  components: {
    UiButton,
    UiCard,
    UiInput,
    LockClosedIcon,
    EnvelopeIcon,
    EyeIcon,
    EyeSlashIcon,
    ArrowRightOnRectangleIcon,
    ArrowLeftIcon,
    ExclamationTriangleIcon,
  },
  setup() {
    const router = useRouter();
    const auth = useAuthStore();

    const email = ref("");
    const password = ref("");
    const loading = ref(false);
    const error = ref("");
    const showPassword = ref(false);

    const onSubmit = async () => {
      if (loading.value) return;

      loading.value = true;
      error.value = "";

      try {
        await auth.login(email.value, password.value);

        // Redirect based on role
        if (auth.user?.role === "ROLE_DOCTOR") {
          router.push("/doctor");
        } else {
          router.push("/patient");
        }
      } catch (e: any) {
        error.value =
          e?.response?.data?.message || e.message || "Erro ao fazer login.";
      } finally {
        loading.value = false;
      }
    };

    return {
      email,
      password,
      loading,
      error,
      showPassword,
      onSubmit,
      ArrowRightOnRectangleIcon,
    };
  },
});
</script>

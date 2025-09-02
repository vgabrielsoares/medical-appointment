<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <div class="app-container py-6 sm:py-8">
      <div class="mb-8">
        <h1
          class="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-3"
        >
          <div
            class="w-10 h-10 bg-gradient-to-r from-purple-600 to-pink-600 rounded-xl flex items-center justify-center"
          >
            <CogIcon class="w-6 h-6 text-white" />
          </div>
          Configurações
        </h1>
        <p class="text-gray-600 dark:text-gray-300 mt-2">
          Personalize sua experiência no sistema
        </p>
      </div>

      <div class="max-w-4xl mx-auto space-y-6">
        <UiCard variant="elevated" size="lg">
          <template #header>
            <span>Tema da Interface</span>
          </template>

          <div class="space-y-4">
            <div class="grid grid-cols-1 sm:grid-cols-3 gap-3">
              <button
                @click="setTheme('light')"
                class="p-4 rounded-xl border-2 transition-all hover:shadow-md"
                :class="
                  theme.pref === 'light'
                    ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/30 text-purple-900 dark:text-purple-100'
                    : 'border-gray-200 dark:border-gray-700 text-gray-900 dark:text-gray-100'
                "
              >
                <div class="flex items-center gap-3">
                  <SunIcon class="w-6 h-6 text-yellow-500" />
                  <span>Claro</span>
                </div>
              </button>

              <button
                @click="setTheme('dark')"
                class="p-4 rounded-xl border-2 transition-all hover:shadow-md"
                :class="
                  theme.pref === 'dark'
                    ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/30 text-purple-900 dark:text-purple-100'
                    : 'border-gray-200 dark:border-gray-700 text-gray-900 dark:text-gray-100'
                "
              >
                <div class="flex items-center gap-3">
                  <MoonIcon class="w-6 h-6 text-purple-500" />
                  <span>Escuro</span>
                </div>
              </button>

              <button
                @click="setTheme('system')"
                class="p-4 rounded-xl border-2 transition-all hover:shadow-md"
                :class="
                  theme.pref === 'system'
                    ? 'border-purple-500 bg-purple-50 dark:bg-purple-900/30 text-purple-900 dark:text-purple-100'
                    : 'border-gray-200 dark:border-gray-700 text-gray-900 dark:text-gray-100'
                "
              >
                <div class="flex items-center gap-3">
                  <ComputerDesktopIcon class="w-6 h-6 text-gray-600" />
                  <span>Sistema</span>
                </div>
              </button>
            </div>
          </div>
        </UiCard>

        <UiCard variant="elevated" size="lg">
          <template #header>
            <span>Notificações</span>
          </template>

          <div class="space-y-4">
            <div class="flex items-center justify-between">
              <div>
                <h4 class="font-medium text-gray-900 dark:text-white">
                  Notificações de Agendamento
                </h4>
                <p class="text-sm text-gray-500">
                  Receba alertas sobre novos agendamentos
                </p>
              </div>
              <input
                type="checkbox"
                v-model="settings.notifications.appointments"
                @change="saveSettings"
                class="w-5 h-5 text-purple-600 rounded"
              />
            </div>

            <div class="flex items-center justify-between">
              <div>
                <h4 class="font-medium text-gray-900 dark:text-white">
                  Lembretes por Email
                </h4>
                <p class="text-sm text-gray-500">
                  Receba lembretes de consultas por email
                </p>
              </div>
              <input
                type="checkbox"
                v-model="settings.notifications.email"
                @change="saveSettings"
                class="w-5 h-5 text-purple-600 rounded"
              />
            </div>
          </div>
        </UiCard>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, reactive } from "vue";
import {
  CogIcon,
  SunIcon,
  MoonIcon,
  ComputerDesktopIcon,
} from "@heroicons/vue/24/solid";
import { UiCard } from "../components/ui";
import { useThemeStore } from "../stores/theme";

export default defineComponent({
  name: "Settings",
  components: {
    UiCard,
    CogIcon,
    SunIcon,
    MoonIcon,
    ComputerDesktopIcon,
  },
  setup() {
    const theme = useThemeStore();

    const settings = reactive({
      notifications: {
        appointments: true,
        email: true,
      },
    });

    const setTheme = (newTheme: "light" | "dark" | "system") => {
      theme.set(newTheme);
    };

    const saveSettings = () => {
      console.log("Configurações salvas:", settings);
    };

    return {
      theme,
      settings,
      setTheme,
      saveSettings,
    };
  },
});
</script>

<template>
  <div
    class="min-h-screen bg-gradient-to-br from-blue-50 via-white to-purple-50 dark:from-gray-900 dark:via-gray-800 dark:to-gray-900"
  >
    <!-- Hero Section -->
    <div class="relative overflow-hidden">
      <!-- Background Pattern -->
      <div class="absolute inset-0 opacity-10 dark:opacity-5">
        <svg
          class="absolute inset-0 h-full w-full"
          fill="currentColor"
          viewBox="0 0 100 100"
        >
          <defs>
            <pattern
              id="grid"
              width="10"
              height="10"
              patternUnits="userSpaceOnUse"
            >
              <circle cx="5" cy="5" r="0.5" />
            </pattern>
          </defs>
          <rect width="100" height="100" fill="url(#grid)" />
        </svg>
      </div>

      <!-- Content -->
      <div class="relative app-container py-12 sm:py-20 lg:py-24">
        <div class="text-center">
          <!-- Logo/Icon -->
          <div class="flex justify-center mb-6">
            <div class="relative">
              <div
                class="w-20 h-20 sm:w-24 sm:h-24 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl flex items-center justify-center shadow-xl shadow-blue-500/25 transform hover:rotate-12 hover:scale-105 transition-transform duration-300"
              >
                <CalendarDaysIcon
                  class="w-10 h-10 sm:w-12 sm:h-12 text-white"
                />
              </div>
              <div
                class="absolute -top-2 -right-2 w-4 h-4 bg-green-500 rounded-full border-2 border-white dark:border-gray-800"
              ></div>
            </div>
          </div>

          <!-- Title -->
          <h1
            class="text-3xl sm:text-4xl lg:text-6xl font-bold text-gray-900 dark:text-white mb-4"
          >
            <span
              class="bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent"
            >
              Agendamento
            </span>
            <br />
            <span class="text-gray-700 dark:text-gray-200">Médico</span>
          </h1>

          <!-- Subtitle -->
          <p
            class="text-lg sm:text-xl text-gray-600 dark:text-gray-300 mb-8 max-w-2xl mx-auto"
          >
            Plataforma moderna e intuitiva para gerenciar consultas médicas.
            <span class="hidden sm:inline"
              >Conectando médicos e pacientes de forma eficiente.</span
            >
          </p>

          <!-- CTA Buttons -->
          <div
            class="flex flex-col sm:flex-row gap-4 justify-center items-center mb-12"
          >
            <UiButton
              variant="primary"
              size="lg"
              :icon="UserIcon"
              @click="handlePatientClick"
              class="w-full sm:w-auto"
            >
              Sou Paciente
            </UiButton>

            <UiButton
              variant="secondary"
              size="lg"
              :icon="UserGroupIcon"
              @click="handleDoctorClick"
              class="w-full sm:w-auto"
            >
              Sou Médico
            </UiButton>
          </div>
        </div>

        <!-- Feature Cards -->
        <div
          class="grid grid-cols-1 md:grid-cols-3 gap-6 lg:gap-8 mt-16 sm:mt-20"
        >
          <UiCard
            v-for="feature in features"
            :key="feature.title"
            variant="glass"
            size="lg"
            hover
            class="text-center group"
          >
            <div class="flex flex-col items-center">
              <div
                class="w-12 h-12 bg-gradient-to-r from-blue-500 to-purple-600 rounded-xl flex items-center justify-center mb-4 group-hover:scale-110 transition-transform duration-300"
              >
                <component :is="feature.icon" class="w-6 h-6 text-white" />
              </div>
              <h3
                class="text-lg font-semibold text-gray-900 dark:text-white mb-2"
              >
                {{ feature.title }}
              </h3>
              <p class="text-gray-600 dark:text-gray-300 text-sm">
                {{ feature.description }}
              </p>
            </div>
          </UiCard>
        </div>

        <!-- Quick Access Links -->
        <div class="mt-16 sm:mt-20">
          <UiCard variant="elevated" size="lg">
            <div class="text-center mb-6">
              <h2
                class="text-xl sm:text-2xl font-semibold text-gray-900 dark:text-white mb-2"
              >
                Acesso Rápido
              </h2>
              <p class="text-gray-600 dark:text-gray-300">
                Escolha sua área para começar
              </p>
            </div>

            <div class="grid grid-cols-1 sm:grid-cols-3 gap-4">
              <router-link
                to="/login"
                class="group flex items-center gap-3 p-4 rounded-xl bg-gray-50 dark:bg-gray-700 hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-all duration-200 hover:scale-105"
              >
                <div
                  class="w-10 h-10 bg-blue-100 dark:bg-blue-900/50 rounded-lg flex items-center justify-center group-hover:bg-blue-200 dark:group-hover:bg-blue-800 transition-colors"
                >
                  <ArrowRightOnRectangleIcon
                    class="w-5 h-5 text-blue-600 dark:text-blue-400"
                  />
                </div>
                <div>
                  <div class="font-medium text-gray-900 dark:text-white">
                    Entrar
                  </div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">
                    Acesse sua conta
                  </div>
                </div>
              </router-link>

              <div
                @click="handlePatientClick"
                class="group flex items-center gap-3 p-4 rounded-xl bg-gray-50 dark:bg-gray-700 hover:bg-green-50 dark:hover:bg-green-900/20 transition-all duration-200 hover:scale-105 cursor-pointer"
              >
                <div
                  class="w-10 h-10 bg-green-100 dark:bg-green-900/50 rounded-lg flex items-center justify-center group-hover:bg-green-200 dark:group-hover:bg-green-800 transition-colors"
                >
                  <UserIcon
                    class="w-5 h-5 text-green-600 dark:text-green-400"
                  />
                </div>
                <div>
                  <div class="font-medium text-gray-900 dark:text-white">
                    Paciente
                  </div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">
                    Agendar consulta
                  </div>
                </div>
              </div>

              <div
                @click="handleDoctorClick"
                class="group flex items-center gap-3 p-4 rounded-xl bg-gray-50 dark:bg-gray-700 hover:bg-purple-50 dark:hover:bg-purple-900/20 transition-all duration-200 hover:scale-105 cursor-pointer"
              >
                <div
                  class="w-10 h-10 bg-purple-100 dark:bg-purple-900/50 rounded-lg flex items-center justify-center group-hover:bg-purple-200 dark:group-hover:bg-purple-800 transition-colors"
                >
                  <UserGroupIcon
                    class="w-5 h-5 text-purple-600 dark:text-purple-400"
                  />
                </div>
                <div>
                  <div class="font-medium text-gray-900 dark:text-white">
                    Médico
                  </div>
                  <div class="text-sm text-gray-500 dark:text-gray-400">
                    Gerenciar agenda
                  </div>
                </div>
              </div>
            </div>
          </UiCard>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import { useRouter } from "vue-router";
import {
  CalendarDaysIcon,
  UserIcon,
  UserGroupIcon,
  ArrowRightOnRectangleIcon,
  ClockIcon,
  ShieldCheckIcon,
  DevicePhoneMobileIcon,
} from "@heroicons/vue/24/solid";
import { UiButton, UiCard } from "../components/ui";
import { useAuthStore } from "../stores/auth";

export default defineComponent({
  name: "Home",
  components: {
    UiButton,
    UiCard,
    CalendarDaysIcon,
    UserIcon,
    UserGroupIcon,
    ArrowRightOnRectangleIcon,
    ClockIcon,
    ShieldCheckIcon,
    DevicePhoneMobileIcon,
  },
  setup() {
    const router = useRouter();
    const auth = useAuthStore();

    const features = [
      {
        title: "Agendamento Fácil",
        description:
          "Interface intuitiva para marcar e gerenciar consultas de forma rápida e eficiente.",
        icon: CalendarDaysIcon,
      },
      {
        title: "Seguro e Confiável",
        description:
          "Seus dados médicos protegidos com as melhores práticas de segurança.",
        icon: ShieldCheckIcon,
      },
      {
        title: "Acesso Mobile",
        description:
          "Disponível em qualquer dispositivo, a qualquer hora e lugar.",
        icon: DevicePhoneMobileIcon,
      },
    ];

    const handlePatientClick = () => {
      if (auth.isAuthenticated) {
        if (auth.isPatient) {
          router.push("/patient");
        } else {
          // Se é médico tentando acessar área de paciente, redirecionar para área do médico
          router.push("/doctor");
        }
      } else {
        router.push("/login");
      }
    };

    const handleDoctorClick = () => {
      if (auth.isAuthenticated) {
        if (auth.isDoctor) {
          router.push("/doctor");
        } else {
          // Se é paciente tentando acessar área de médico, redirecionar para área do paciente
          router.push("/patient");
        }
      } else {
        router.push("/login");
      }
    };

    return {
      features,
      UserIcon,
      UserGroupIcon,
      handlePatientClick,
      handleDoctorClick,
    };
  },
});
</script>

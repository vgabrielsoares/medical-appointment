<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <div class="app-container py-6 sm:py-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div
          class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
        >
          <div>
            <h1
              class="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-3"
            >
              <div
                class="w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl flex items-center justify-center"
              >
                <UserGroupIcon class="w-6 h-6 text-white" />
              </div>
              Área do Médico
            </h1>
            <p class="text-gray-600 dark:text-gray-300 mt-2">
              Gerencie sua agenda e horários de atendimento
            </p>
          </div>

          <div class="flex items-center gap-3">
            <UiBadge variant="success" size="md" :icon="CheckCircleIcon">
              Online
            </UiBadge>
          </div>
        </div>
      </div>

      <!-- Quick Stats -->
      <div
        class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-6 mb-8"
      >
        <UiCard variant="elevated" size="md" class="text-center">
          <div class="flex flex-col items-center">
            <div
              class="w-12 h-12 bg-blue-100 dark:bg-blue-900/50 rounded-xl flex items-center justify-center mb-3"
            >
              <CalendarDaysIcon
                class="w-6 h-6 text-blue-600 dark:text-blue-400"
              />
            </div>
            <div class="text-2xl font-bold text-gray-900 dark:text-white">
              24
            </div>
            <div class="text-sm text-gray-600 dark:text-gray-300">
              Consultas Esta Semana
            </div>
          </div>
        </UiCard>

        <UiCard variant="elevated" size="md" class="text-center">
          <div class="flex flex-col items-center">
            <div
              class="w-12 h-12 bg-green-100 dark:bg-green-900/50 rounded-xl flex items-center justify-center mb-3"
            >
              <ClockIcon class="w-6 h-6 text-green-600 dark:text-green-400" />
            </div>
            <div class="text-2xl font-bold text-gray-900 dark:text-white">
              8
            </div>
            <div class="text-sm text-gray-600 dark:text-gray-300">
              Horários Hoje
            </div>
          </div>
        </UiCard>

        <UiCard variant="elevated" size="md" class="text-center">
          <div class="flex flex-col items-center">
            <div
              class="w-12 h-12 bg-purple-100 dark:bg-purple-900/50 rounded-xl flex items-center justify-center mb-3"
            >
              <UserIcon class="w-6 h-6 text-purple-600 dark:text-purple-400" />
            </div>
            <div class="text-2xl font-bold text-gray-900 dark:text-white">
              156
            </div>
            <div class="text-sm text-gray-600 dark:text-gray-300">
              Pacientes Ativos
            </div>
          </div>
        </UiCard>

        <UiCard variant="elevated" size="md" class="text-center">
          <div class="flex flex-col items-center">
            <div
              class="w-12 h-12 bg-amber-100 dark:bg-amber-900/50 rounded-xl flex items-center justify-center mb-3"
            >
              <ChartBarIcon
                class="w-6 h-6 text-amber-600 dark:text-amber-400"
              />
            </div>
            <div class="text-2xl font-bold text-gray-900 dark:text-white">
              92%
            </div>
            <div class="text-sm text-gray-600 dark:text-gray-300">
              Taxa de Ocupação
            </div>
          </div>
        </UiCard>
      </div>

      <!-- Main Actions -->
      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 sm:gap-8 mb-8">
        <!-- Manage Schedule Card -->
        <UiCard
          variant="glass"
          size="lg"
          hover
          class="group cursor-pointer"
          @click="$router.push('/doctor/slots')"
        >
          <div class="flex items-start gap-4">
            <div
              class="w-16 h-16 bg-gradient-to-r from-blue-500 to-blue-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300"
            >
              <CalendarDaysIcon class="w-8 h-8 text-white" />
            </div>
            <div class="flex-1">
              <h3
                class="text-xl font-semibold text-gray-900 dark:text-white mb-2"
              >
                Gerenciar Horários
              </h3>
              <p class="text-gray-600 dark:text-gray-300 mb-4">
                Crie, edite e organize seus horários de atendimento. Configure
                sua disponibilidade para a semana.
              </p>
              <div
                class="flex items-center gap-2 text-blue-600 dark:text-blue-400 font-medium"
              >
                <span>Acessar agenda</span>
                <ArrowRightIcon
                  class="w-4 h-4 group-hover:translate-x-1 transition-transform"
                />
              </div>
            </div>
          </div>
        </UiCard>

        <!-- Patient Management Card -->
        <UiCard variant="glass" size="lg" hover class="group cursor-pointer">
          <div class="flex items-start gap-4">
            <div
              class="w-16 h-16 bg-gradient-to-r from-purple-500 to-purple-600 rounded-2xl flex items-center justify-center group-hover:scale-110 transition-transform duration-300"
            >
              <UserGroupIcon class="w-8 h-8 text-white" />
            </div>
            <div class="flex-1">
              <h3
                class="text-xl font-semibold text-gray-900 dark:text-white mb-2"
              >
                Gerenciar Pacientes
              </h3>
              <p class="text-gray-600 dark:text-gray-300 mb-4">
                Visualize informações dos pacientes, histórico de consultas e
                agendamentos futuros.
              </p>
              <div
                class="flex items-center gap-2 text-purple-600 dark:text-purple-400 font-medium"
              >
                <span>Ver pacientes</span>
                <ArrowRightIcon
                  class="w-4 h-4 group-hover:translate-x-1 transition-transform"
                />
              </div>
            </div>
          </div>
        </UiCard>
      </div>

      <!-- Quick Actions -->
      <UiCard variant="elevated" size="lg">
        <template #header>
          <div class="flex items-center gap-3">
            <div
              class="w-8 h-8 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center"
            >
              <BoltIcon class="w-5 h-5 text-gray-600 dark:text-gray-300" />
            </div>
            <span>Ações Rápidas</span>
          </div>
        </template>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
          <UiButton
            variant="secondary"
            size="lg"
            :icon="PlusIcon"
            full-width
            @click="$router.push('/doctor/slots')"
          >
            Novo Horário
          </UiButton>

          <UiButton variant="secondary" size="lg" :icon="EyeIcon" full-width>
            Ver Agenda
          </UiButton>

          <UiButton
            variant="secondary"
            size="lg"
            :icon="DocumentTextIcon"
            full-width
          >
            Relatórios
          </UiButton>

          <UiButton variant="secondary" size="lg" :icon="CogIcon" full-width>
            Configurações
          </UiButton>
        </div>
      </UiCard>

      <!-- Recent Activity -->
      <div class="mt-8">
        <UiCard variant="elevated" size="lg">
          <template #header>
            <div class="flex items-center justify-between">
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 bg-gray-100 dark:bg-gray-700 rounded-lg flex items-center justify-center"
                >
                  <ClockIcon class="w-5 h-5 text-gray-600 dark:text-gray-300" />
                </div>
                <span>Atividade Recente</span>
              </div>
              <UiButton variant="ghost" size="sm"> Ver todas </UiButton>
            </div>
          </template>

          <div class="space-y-4">
            <div
              v-for="i in 3"
              :key="i"
              class="flex items-center gap-4 p-4 rounded-xl bg-gray-50 dark:bg-gray-700/50"
            >
              <UiAvatar :name="`Paciente ${i}`" size="md" status="online" />
              <div class="flex-1">
                <div class="font-medium text-gray-900 dark:text-white">
                  Consulta agendada - Paciente {{ i }}
                </div>
                <div class="text-sm text-gray-600 dark:text-gray-300">
                  {{ new Date().toLocaleDateString("pt-BR") }} às 14:{{
                    30 + i * 15
                  }}
                </div>
              </div>
              <UiBadge variant="success" size="sm"> Confirmado </UiBadge>
            </div>
          </div>
        </UiCard>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";
import {
  UserGroupIcon,
  CalendarDaysIcon,
  ClockIcon,
  UserIcon,
  ChartBarIcon,
  ArrowRightIcon,
  PlusIcon,
  EyeIcon,
  DocumentTextIcon,
  CogIcon,
  BoltIcon,
  CheckCircleIcon,
} from "@heroicons/vue/24/solid";
import { UiButton, UiCard, UiAvatar, UiBadge } from "../components/ui";

export default defineComponent({
  name: "Doctor",
  components: {
    UiButton,
    UiCard,
    UiAvatar,
    UiBadge,
    UserGroupIcon,
    CalendarDaysIcon,
    ClockIcon,
    UserIcon,
    ChartBarIcon,
    ArrowRightIcon,
    PlusIcon,
    EyeIcon,
    DocumentTextIcon,
    CogIcon,
    BoltIcon,
    CheckCircleIcon,
  },
  setup() {
    return {
      CheckCircleIcon,
      PlusIcon,
      EyeIcon,
      DocumentTextIcon,
      CogIcon,
    };
  },
});
</script>

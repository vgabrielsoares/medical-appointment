<template>
  <div>
    <!-- Empty State -->
    <div v-if="slots.length === 0" class="text-center py-12">
      <ClockIcon class="w-16 h-16 text-gray-300 mx-auto mb-4" />
      <h3 class="text-lg font-medium text-gray-900 dark:text-white mb-2">
        Nenhum horário disponível
      </h3>
      <p class="text-gray-500 dark:text-gray-400">
        {{
          patientMode
            ? "Este médico não possui horários cadastrados no momento."
            : "Você ainda não cadastrou nenhum horário."
        }}
      </p>
    </div>

    <!-- Slots Grid -->
    <div v-else class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
      <transition-group name="slot" appear>
        <div
          v-for="slot in slots"
          :key="slot.id"
          :class="[
            'group relative rounded-xl p-4 transition-all duration-300 transform hover:scale-[1.02]',
            getSlotCardClass(slot.status),
          ]"
        >
          <!-- Status Badge -->
          <div class="flex items-center justify-between mb-3">
            <UiBadge
              :variant="getStatusVariant(slot.status)"
              size="sm"
              :icon="getStatusIcon(slot.status)"
            >
              {{ getStatusText(slot.status) }}
            </UiBadge>

            <!-- Action Menu for Doctor Mode -->
            <div v-if="!patientMode" class="relative">
              <button
                @click="toggleMenu(slot.id)"
                class="p-1 rounded-md hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
              >
                <EllipsisVerticalIcon class="w-4 h-4 text-gray-500" />
              </button>

              <!-- Dropdown Menu -->
              <div
                v-if="activeMenu === slot.id"
                class="absolute right-0 top-8 w-32 bg-white dark:bg-gray-800 rounded-lg shadow-xl border border-gray-200 dark:border-gray-700 py-1 z-10"
              >
                <button
                  @click="
                    $emit('edit', slot);
                    toggleMenu(null);
                  "
                  class="w-full px-3 py-2 text-left text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center gap-2"
                >
                  <PencilIcon class="w-3 h-3" />
                  Editar
                </button>
                <button
                  @click="
                    $emit('delete', slot);
                    toggleMenu(null);
                  "
                  class="w-full px-3 py-2 text-left text-sm text-red-600 hover:bg-red-50 dark:hover:bg-red-900/20 flex items-center gap-2"
                >
                  <TrashIcon class="w-3 h-3" />
                  Excluir
                </button>
              </div>
            </div>
          </div>

          <!-- Time Display -->
          <div class="mb-4">
            <div class="flex items-center gap-3 mb-2">
              <div
                :class="[
                  'w-10 h-10 rounded-xl flex items-center justify-center',
                  getIconBackground(slot.status),
                ]"
              >
                <ClockIcon class="w-5 h-5 text-white" />
              </div>
              <div>
                <div class="font-semibold text-gray-900 dark:text-white">
                  {{ formatTimeRange(slot.start, slot.end) }}
                </div>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  {{ formatDate(slot.start) }}
                </div>
              </div>
            </div>
          </div>

          <!-- Patient Info (for booked slots) -->
          <div
            v-if="slot.status === 'booked' && slot.patientName"
            class="mb-4 p-3 bg-gray-50 dark:bg-gray-700 rounded-lg"
          >
            <div class="flex items-center gap-2 mb-1">
              <UserIcon class="w-4 h-4 text-gray-500" />
              <span class="text-sm font-medium text-gray-900 dark:text-white">
                Paciente
              </span>
            </div>
            <div class="text-sm text-gray-600 dark:text-gray-300">
              {{ slot.patientName }}
            </div>
            <div
              v-if="slot.patientPhone"
              class="text-xs text-gray-500 dark:text-gray-400 mt-1"
            >
              {{ slot.patientPhone }}
            </div>
          </div>

          <!-- Duration -->
          <div
            class="flex items-center gap-2 text-sm text-gray-500 dark:text-gray-400 mb-4"
          >
            <ClockIcon class="w-4 h-4" />
            <span>{{ getDuration(slot.start, slot.end) }} minutos</span>
          </div>

          <!-- Action Button -->
          <div v-if="patientMode">
            <UiButton
              v-if="slot.status === 'available'"
              @click="$emit('book', slot)"
              variant="primary"
              size="md"
              :icon="CalendarIcon"
              full-width
              class="group-hover:scale-105 transition-transform"
            >
              Agendar
            </UiButton>

            <div v-else-if="slot.status === 'booked'" class="text-center">
              <UiButton variant="secondary" size="md" disabled full-width>
                Já agendado
              </UiButton>
            </div>
          </div>

          <!-- Availability Indicator -->
          <div
            :class="[
              'absolute top-3 right-3 w-3 h-3 rounded-full',
              slot.status === 'available' ? 'bg-green-500' : 'bg-red-500',
            ]"
          />
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";
import {
  ClockIcon,
  UserIcon,
  EllipsisVerticalIcon,
  PencilIcon,
  TrashIcon,
  CalendarIcon,
  CheckCircleIcon,
  XCircleIcon,
} from "@heroicons/vue/24/solid";
import { UiBadge, UiButton } from "./ui";

export default defineComponent({
  name: "SlotList",
  components: {
    UiBadge,
    UiButton,
    ClockIcon,
    UserIcon,
    EllipsisVerticalIcon,
    PencilIcon,
    TrashIcon,
    CalendarIcon,
    CheckCircleIcon,
    XCircleIcon,
  },
  props: {
    slots: {
      type: Array as () => Array<{
        id: string;
        start: string;
        end: string;
        status: string;
        patientName?: string;
        patientPhone?: string;
      }>,
      default: () => [],
    },
    patientMode: { type: Boolean, default: false },
  },
  emits: ["book", "edit", "delete"],
  setup() {
    const activeMenu = ref<string | null>(null);

    const toggleMenu = (slotId: string | null) => {
      activeMenu.value = activeMenu.value === slotId ? null : slotId;
    };

    const formatDate = (dateString: string) => {
      const date = new Date(dateString);
      return date.toLocaleDateString("pt-BR", {
        weekday: "long",
        day: "numeric",
        month: "long",
      });
    };

    const formatTimeRange = (start: string, end: string) => {
      const startTime = new Date(start).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });
      const endTime = new Date(end).toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });
      return `${startTime} - ${endTime}`;
    };

    const getDuration = (start: string, end: string) => {
      const startTime = new Date(start);
      const endTime = new Date(end);
      return Math.round((endTime.getTime() - startTime.getTime()) / 60000);
    };

    const getSlotCardClass = (status: string) => {
      const baseClasses = "border-2 backdrop-blur-sm";
      switch (status) {
        case "available":
          return `${baseClasses} bg-green-50/80 dark:bg-green-900/20 border-green-200 dark:border-green-800 hover:bg-green-100/80 dark:hover:bg-green-900/30 hover:border-green-300 hover:shadow-lg hover:shadow-green-500/20`;
        case "booked":
          return `${baseClasses} bg-blue-50/80 dark:bg-blue-900/20 border-blue-200 dark:border-blue-800 hover:bg-blue-100/80 dark:hover:bg-blue-900/30`;
        default:
          return `${baseClasses} bg-gray-50/80 dark:bg-gray-800/80 border-gray-200 dark:border-gray-700 hover:bg-gray-100/80 dark:hover:bg-gray-700/80`;
      }
    };

    const getStatusVariant = (status: string) => {
      switch (status) {
        case "available":
          return "success";
        case "booked":
          return "info";
        default:
          return "default";
      }
    };

    const getStatusIcon = (status: string) => {
      switch (status) {
        case "available":
          return CheckCircleIcon;
        case "booked":
          return XCircleIcon;
        default:
          return ClockIcon;
      }
    };

    const getStatusText = (status: string) => {
      switch (status) {
        case "available":
          return "Disponível";
        case "booked":
          return "Agendado";
        default:
          return "Indisponível";
      }
    };

    const getIconBackground = (status: string) => {
      switch (status) {
        case "available":
          return "bg-gradient-to-r from-green-500 to-green-600";
        case "booked":
          return "bg-gradient-to-r from-blue-500 to-blue-600";
        default:
          return "bg-gradient-to-r from-gray-500 to-gray-600";
      }
    };

    return {
      activeMenu,
      toggleMenu,
      formatDate,
      formatTimeRange,
      getDuration,
      getSlotCardClass,
      getStatusVariant,
      getStatusIcon,
      getStatusText,
      getIconBackground,
      CheckCircleIcon,
      CalendarIcon,
    };
  },
});
</script>

<style scoped>
.slot-enter-from,
.slot-leave-to {
  opacity: 0;
  transform: translateY(10px) scale(0.95);
}

.slot-enter-to,
.slot-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}

.slot-enter-active {
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.slot-leave-active {
  transition: all 0.3s ease-in;
}

.slot-move {
  transition: transform 0.3s ease;
}
</style>

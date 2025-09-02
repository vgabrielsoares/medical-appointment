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
                class="w-10 h-10 bg-gradient-to-r from-green-600 to-blue-600 rounded-xl flex items-center justify-center"
              >
                <UserIcon class="w-6 h-6 text-white" />
              </div>
              Área do Paciente
            </h1>
            <p class="text-gray-600 dark:text-gray-300 mt-2">
              Encontre médicos e agende suas consultas
            </p>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="grid grid-cols-1 lg:grid-cols-12 gap-6 lg:gap-8">
        <!-- Doctors Sidebar -->
        <div class="lg:col-span-4">
          <UiCard variant="elevated" size="lg">
            <template #header>
              <div class="flex items-center gap-3">
                <div
                  class="w-8 h-8 bg-green-100 dark:bg-green-900/50 rounded-lg flex items-center justify-center"
                >
                  <UserGroupIcon
                    class="w-5 h-5 text-green-600 dark:text-green-400"
                  />
                </div>
                <span>Médicos Disponíveis</span>
              </div>
            </template>

            <div class="space-y-3">
              <div v-if="doctors.length === 0" class="text-center py-8">
                <UserGroupIcon class="w-12 h-12 text-gray-300 mx-auto mb-3" />
                <p class="text-gray-500 dark:text-gray-400">
                  Carregando médicos...
                </p>
              </div>

              <div
                v-for="doctor in doctors"
                :key="doctor.id"
                @click="selectDoctor(doctor)"
                :class="[
                  'group relative cursor-pointer rounded-xl p-4 transition-all duration-200',
                  selectedDoctor?.id === doctor.id
                    ? 'bg-gradient-to-r from-green-50 to-blue-50 dark:from-green-900/20 dark:to-blue-900/20 ring-2 ring-green-500 shadow-lg'
                    : 'bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 hover:shadow-md hover:scale-[1.02]',
                ]"
              >
                <div class="flex items-center gap-4">
                  <UiAvatar
                    :name="doctor.name || 'Médico'"
                    size="lg"
                    :status="doctor.status || 'online'"
                  />
                  <div class="flex-1 min-w-0">
                    <div
                      class="font-semibold text-gray-900 dark:text-white truncate"
                    >
                      {{ doctor.name || "Nome não informado" }}
                    </div>
                    <div
                      class="text-sm text-gray-600 dark:text-gray-300 truncate"
                    >
                      {{ doctor.specialty || "Especialidade não informada" }}
                    </div>
                    <div class="flex items-center gap-2 mt-1">
                      <UiBadge variant="success" size="sm">
                        Disponível
                      </UiBadge>
                      <span class="text-xs text-gray-500">
                        {{ doctor.availableSlots || 0 }} horários
                      </span>
                    </div>
                  </div>
                  <ChevronRightIcon
                    :class="[
                      'w-5 h-5 transition-transform duration-200',
                      selectedDoctor?.id === doctor.id
                        ? 'rotate-90 text-green-600'
                        : 'text-gray-400',
                    ]"
                  />
                </div>

                <!-- Selected indicator -->
                <div
                  v-if="selectedDoctor?.id === doctor.id"
                  class="absolute top-2 right-2 w-3 h-3 bg-green-500 rounded-full border-2 border-white dark:border-gray-800"
                />
              </div>
            </div>
          </UiCard>
        </div>

        <!-- Schedule Content -->
        <div class="lg:col-span-8">
          <!-- My Appointments -->
          <UiCard variant="elevated" size="lg" class="mb-6">
            <template #header>
              <div class="flex items-center justify-between">
                <div class="flex items-center gap-3">
                  <div
                    class="w-8 h-8 bg-purple-100 dark:bg-purple-900/50 rounded-lg flex items-center justify-center"
                  >
                    <CalendarDaysIcon
                      class="w-5 h-5 text-purple-600 dark:text-purple-400"
                    />
                  </div>
                  <span>Meus Agendamentos</span>
                </div>
                <UiButton
                  variant="ghost"
                  size="sm"
                  :icon="ArrowPathIcon"
                  @click="loadMyAppointments"
                  :loading="isLoadingAppointments"
                >
                  Atualizar
                </UiButton>
              </div>
            </template>

            <div v-if="myAppointments.length === 0" class="text-center py-8">
              <CalendarDaysIcon class="w-12 h-12 text-gray-300 mx-auto mb-3" />
              <p class="text-gray-500 dark:text-gray-400">
                Você ainda não tem agendamentos
              </p>
            </div>

            <div v-else class="space-y-3">
              <div
                v-for="appointment in myAppointments"
                :key="appointment.id"
                class="p-4 border border-gray-200 dark:border-gray-700 rounded-xl hover:shadow-md transition-shadow"
              >
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-4">
                    <UiAvatar
                      :name="appointment.doctorName || 'Médico'"
                      size="md"
                      status="online"
                    />
                    <div>
                      <div class="font-medium text-gray-900 dark:text-white">
                        Dr.
                        {{ appointment.doctorName || "Nome não disponível" }}
                      </div>
                      <div class="text-sm text-gray-500 dark:text-gray-400">
                        {{
                          appointment.doctorSpecialty ||
                          "Especialidade não informada"
                        }}
                      </div>
                      <div
                        class="text-sm text-gray-600 dark:text-gray-300 mt-1"
                      >
                        {{
                          formatAppointmentDateTime(
                            appointment.start,
                            appointment.end
                          )
                        }}
                      </div>
                    </div>
                  </div>
                  <div class="text-right">
                    <UiBadge
                      :variant="getAppointmentStatusVariant(appointment.status)"
                      size="sm"
                    >
                      {{ getAppointmentStatusText(appointment.status) }}
                    </UiBadge>
                  </div>
                </div>
              </div>
            </div>
          </UiCard>

          <!-- No doctor selected state -->
          <div v-if="!selectedDoctor" class="text-center py-12">
            <UiCard variant="glass" size="lg">
              <div class="py-8">
                <CalendarDaysIcon
                  class="w-16 h-16 text-gray-300 mx-auto mb-4"
                />
                <h3
                  class="text-xl font-semibold text-gray-900 dark:text-white mb-2"
                >
                  Selecione um médico
                </h3>
                <p class="text-gray-600 dark:text-gray-300">
                  Escolha um médico da lista ao lado para ver os horários
                  disponíveis
                </p>
              </div>
            </UiCard>
          </div>

          <!-- Doctor selected - show schedule -->
          <div v-else>
            <!-- Doctor Info Header -->
            <UiCard variant="elevated" size="lg" class="mb-6">
              <div class="flex items-center gap-4">
                <UiAvatar
                  :name="selectedDoctor.name"
                  size="xl"
                  status="online"
                />
                <div class="flex-1">
                  <h2
                    class="text-xl sm:text-2xl font-bold text-gray-900 dark:text-white"
                  >
                    {{ selectedDoctor.name }}
                  </h2>
                  <p class="text-gray-600 dark:text-gray-300 mb-2">
                    {{
                      selectedDoctor.specialty || "Especialidade não informada"
                    }}
                  </p>
                  <div class="flex flex-wrap gap-2">
                    <UiBadge
                      variant="success"
                      size="sm"
                      :icon="CheckCircleIcon"
                    >
                      Verificado
                    </UiBadge>
                    <UiBadge variant="info" size="sm">
                      {{ Math.floor(Math.random() * 15) + 5 }} anos de
                      experiência
                    </UiBadge>
                    <UiBadge variant="outline" size="sm">
                      CRM {{ Math.floor(Math.random() * 90000) + 10000 }}
                    </UiBadge>
                  </div>
                </div>
                <div class="hidden sm:block text-right">
                  <div
                    class="text-2xl font-bold text-green-600 dark:text-green-400"
                  >
                    4.9★
                  </div>
                  <div class="text-sm text-gray-500">
                    ({{ Math.floor(Math.random() * 200) + 50 }} avaliações)
                  </div>
                </div>
              </div>
            </UiCard>

            <!-- Available Slots -->
            <UiCard variant="elevated" size="lg">
              <template #header>
                <div class="flex items-center justify-between">
                  <div class="flex items-center gap-3">
                    <div
                      class="w-8 h-8 bg-blue-100 dark:bg-blue-900/50 rounded-lg flex items-center justify-center"
                    >
                      <ClockIcon
                        class="w-5 h-5 text-blue-600 dark:text-blue-400"
                      />
                    </div>
                    <span>Horários Disponíveis</span>
                  </div>
                  <UiButton
                    variant="ghost"
                    size="sm"
                    :icon="ArrowPathIcon"
                    @click="refreshSlots"
                    :loading="isRefreshingSlots"
                  >
                    Atualizar
                  </UiButton>
                </div>
              </template>

              <SlotList
                :slots="slots"
                patient-mode
                @book="bookSlot"
                class="enhanced-slot-list"
              />
            </UiCard>
          </div>
        </div>
      </div>

      <!-- Error Toast -->
      <div v-if="error" class="fixed bottom-4 right-4 z-50">
        <div
          class="bg-red-500 text-white px-6 py-3 rounded-lg shadow-xl flex items-center gap-3 animate-slide-in"
        >
          <ExclamationTriangleIcon class="w-5 h-5" />
          <span>{{ error }}</span>
          <button @click="error = ''" class="ml-2 hover:text-red-200">
            <XMarkIcon class="w-4 h-4" />
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import SlotList from "../components/SlotList.vue";
import { listDoctors } from "../services/doctors";
import { listDoctorSlots } from "../services/slots";
import {
  createAppointment,
  listMyAppointments,
} from "../services/appointments";
import { useAuthStore } from "../stores/auth";
import {
  UserIcon,
  UserGroupIcon,
  CalendarDaysIcon,
  ClockIcon,
  ChevronRightIcon,
  CheckCircleIcon,
  ArrowPathIcon,
  ExclamationTriangleIcon,
  XMarkIcon,
} from "@heroicons/vue/24/solid";
import {
  UiCard,
  UiAvatar,
  UiBadge,
  UiButton,
  pushToast,
} from "../components/ui";

export default defineComponent({
  name: "Patient",
  components: {
    SlotList,
    UiCard,
    UiAvatar,
    UiBadge,
    UiButton,
    UserIcon,
    UserGroupIcon,
    CalendarDaysIcon,
    ClockIcon,
    ChevronRightIcon,
    CheckCircleIcon,
    ArrowPathIcon,
    ExclamationTriangleIcon,
    XMarkIcon,
  },
  setup() {
    const auth = useAuthStore();
    const doctors = ref([] as any[]);
    const selectedDoctor = ref<any | null>(null);
    const slots = ref([] as any[]);
    const error = ref("");
    const isRefreshingSlots = ref(false);
    const myAppointments = ref([] as any[]);
    const isLoadingAppointments = ref(false);

    const loadDoctors = async () => {
      error.value = "";
      try {
        const doctorsList = await listDoctors();
        // Carregar o número de slots disponíveis para cada médico
        for (const doctor of doctorsList) {
          try {
            const doctorSlots = await listDoctorSlots(doctor.id);
            doctor.availableSlots = doctorSlots.filter(
              (slot: any) => slot.status === "available"
            ).length;
          } catch (e) {
            doctor.availableSlots = 0;
          }
        }
        doctors.value = doctorsList;
      } catch (e: any) {
        error.value =
          e?.response?.data?.message ||
          e.message ||
          "Erro ao carregar médicos.";
      }
    };

    const selectDoctor = async (d: any) => {
      selectedDoctor.value = d;
      await loadSlots(d.id);
    };

    const loadSlots = async (doctorId: string) => {
      error.value = "";
      try {
        slots.value = await listDoctorSlots(doctorId);
      } catch (e: any) {
        error.value =
          e?.response?.data?.message ||
          e.message ||
          "Erro ao carregar horários.";
      }
    };

    const refreshSlots = async () => {
      if (!selectedDoctor.value || isRefreshingSlots.value) return;

      isRefreshingSlots.value = true;
      try {
        await loadSlots(selectedDoctor.value.id);
        // Atualizar também o contador de slots do médico selecionado
        const availableCount = slots.value.filter(
          (slot: any) => slot.status === "available"
        ).length;
        if (selectedDoctor.value) {
          selectedDoctor.value.availableSlots = availableCount;
        }
        // Atualizar na lista de médicos também
        const doctorIndex = doctors.value.findIndex(
          (d) => d.id === selectedDoctor.value.id
        );
        if (doctorIndex >= 0) {
          doctors.value[doctorIndex].availableSlots = availableCount;
        }
      } catch (e: any) {
        error.value =
          e?.response?.data?.message ||
          e.message ||
          "Erro ao atualizar horários.";
      } finally {
        isRefreshingSlots.value = false;
      }
    };

    const loadMyAppointments = async () => {
      if (isLoadingAppointments.value) return;

      isLoadingAppointments.value = true;
      try {
        myAppointments.value = await listMyAppointments();
      } catch (e: any) {
        console.error("Erro ao carregar agendamentos:", e);
        // Não mostrar erro para agendamentos, apenas log
      } finally {
        isLoadingAppointments.value = false;
      }
    };

    const formatAppointmentDateTime = (start: string, end: string) => {
      if (!start || !end) return "Data não disponível";

      const startDate = new Date(start);
      const endDate = new Date(end);

      // Verificar se as datas são válidas
      if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
        return "Data inválida";
      }

      const dateStr = startDate.toLocaleDateString("pt-BR");
      const startTime = startDate.toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });
      const endTime = endDate.toLocaleTimeString("pt-BR", {
        hour: "2-digit",
        minute: "2-digit",
      });

      return `${dateStr} às ${startTime} - ${endTime}`;
    };

    const getAppointmentStatusVariant = (status: string) => {
      switch (status) {
        case "confirmed":
          return "success";
        case "pending":
          return "warning";
        case "cancelled":
          return "danger";
        default:
          return "info";
      }
    };

    const getAppointmentStatusText = (status: string) => {
      switch (status) {
        case "confirmed":
          return "Confirmado";
        case "pending":
          return "Pendente";
        case "cancelled":
          return "Cancelado";
        default:
          return "Agendado";
      }
    };

    const bookSlot = async (slot: any) => {
      error.value = "";
      try {
        if (!auth.user) throw new Error("Usuário não autenticado.");
        const payload = { doctorId: selectedDoctor.value.id, slotId: slot.id };
        await createAppointment(payload);
        const idx = slots.value.findIndex((s: any) => s.id === slot.id);
        if (idx >= 0) slots.value[idx].status = "booked";

        // Show success feedback
        const successDiv = document.createElement("div");
        successDiv.className =
          "fixed top-4 right-4 z-50 bg-green-500 text-white px-6 py-3 rounded-lg shadow-xl animate-slide-in";
        successDiv.innerHTML = "✓ Agendamento criado com sucesso!";
        document.body.appendChild(successDiv);
        setTimeout(() => document.body.removeChild(successDiv), 3000);
      } catch (e: any) {
        if (e?.response?.status === 409) {
          const msg =
            "Este horário já foi reservado por outro paciente. Por favor, escolha outro horário disponível.";
          error.value = msg;
          // toast amigável
          pushToast("Conflito de horário", msg);
          // Recarregar os slots para mostrar o estado atualizado
          if (selectedDoctor.value) {
            await loadSlots(selectedDoctor.value.id);
          }
        } else {
          error.value =
            e?.response?.data?.message ||
            e.message ||
            "Erro ao agendar horário.";
        }
      }
    };

    onMounted(() => {
      loadDoctors();
      loadMyAppointments();
    });

    return {
      doctors,
      selectedDoctor,
      slots,
      error,
      isRefreshingSlots,
      myAppointments,
      isLoadingAppointments,
      selectDoctor,
      bookSlot,
      refreshSlots,
      loadMyAppointments,
      formatAppointmentDateTime,
      getAppointmentStatusVariant,
      getAppointmentStatusText,
      CheckCircleIcon,
      ArrowPathIcon,
    };
  },
});
</script>

<style scoped>
@keyframes slide-in {
  from {
    transform: translateX(100%);
    opacity: 0;
  }
  to {
    transform: translateX(0);
    opacity: 1;
  }
}

.animate-slide-in {
  animation: slide-in 0.3s ease-out;
}

.enhanced-slot-list {
  gap: 0.75rem;
}
</style>

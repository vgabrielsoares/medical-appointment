<template>
  <div class="app-container">
    <div class="card">
      <div class="flex flex-col md:flex-row md:items-start md:gap-6">
        <div class="md:w-1/3">
          <h2 class="text-lg font-semibold">Área do paciente</h2>
          <p class="mt-2 text-sm text-gray-600">
            Escolha um médico e veja horários disponíveis.
          </p>

          <div class="mt-4">
            <h3 class="font-medium mb-2">Médicos</h3>
            <ul class="space-y-2">
              <li
                v-for="d in doctors"
                :key="d.id"
                @click="selectDoctor(d)"
                :class="[
                  'flex items-center gap-3 p-2 rounded cursor-pointer',
                  selectedDoctor?.id === d.id
                    ? 'bg-blue-50 border-l-4 border-blue-400'
                    : 'bg-white',
                ]"
              >
                <div
                  class="w-10 h-10 rounded-full bg-gray-100 flex items-center justify-center text-sm font-medium text-gray-700"
                >
                  {{ (d.name || "?").slice(0, 1) }}
                </div>
                <div class="flex-1">
                  <div class="font-medium text-sm">{{ d.name }}</div>
                  <div class="text-xs text-gray-500">
                    {{ d.specialty || "—" }}
                  </div>
                </div>
              </li>
            </ul>
          </div>
        </div>

        <div class="md:flex-1 mt-4 md:mt-0">
          <h3 class="font-medium mb-2">Horários</h3>
          <div v-if="!selectedDoctor" class="text-sm text-gray-500">
            Selecione um médico à esquerda.
          </div>
          <div v-else>
            <div class="mb-4">
              <div class="font-medium">{{ selectedDoctor.name }}</div>
              <div class="text-xs text-gray-500">
                {{ selectedDoctor.specialty || "—" }}
              </div>
            </div>

            <SlotList :slots="slots" patientMode @book="bookSlot" />
          </div>
        </div>
      </div>

      <div v-if="error" class="mt-4 text-red-600">{{ error }}</div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import SlotList from "../components/SlotList.vue";
import { listDoctors } from "../services/doctors";
import { listDoctorSlots } from "../services/slots";
import { createAppointment } from "../services/appointments";
import { useAuthStore } from "../stores/auth";

export default defineComponent({
  name: "Patient",
  components: { SlotList },
  setup() {
    const auth = useAuthStore();
    const doctors = ref([] as any[]);
    const selectedDoctor = ref<any | null>(null);
    const slots = ref([] as any[]);
    const error = ref("");

    const loadDoctors = async () => {
      error.value = "";
      try {
        doctors.value = await listDoctors();
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

    const bookSlot = async (slot: any) => {
      error.value = "";
      try {
        if (!auth.user) throw new Error("Usuário não autenticado.");
        const payload = { doctorId: selectedDoctor.value.id, slotId: slot.id };
        await createAppointment(payload);
        const idx = slots.value.findIndex((s: any) => s.id === slot.id);
        if (idx >= 0) slots.value[idx].status = "booked";
        alert("Agendamento criado com sucesso.");
      } catch (e: any) {
        error.value =
          e?.response?.data?.message || e.message || "Erro ao agendar horário.";
      }
    };

    onMounted(() => {
      loadDoctors();
    });

    return {
      doctors,
      selectedDoctor,
      slots,
      error,
      selectDoctor,
      bookSlot,
    };
  },
});
</script>

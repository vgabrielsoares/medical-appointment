<template>
  <div class="p-6">
    <h2 class="text-xl font-semibold">Área do paciente</h2>
    <p class="mt-2">
      Aqui o paciente poderá ver médicos disponíveis e agendar horários.
    </p>
    <div class="mt-6 grid grid-cols-1 md:grid-cols-3 gap-6">
      <div class="md:col-span-1">
        <h3 class="font-medium mb-2">Médicos</h3>
        <ul class="space-y-2">
          <li
            v-for="d in doctors"
            :key="d.id"
            @click="selectDoctor(d)"
            :class="[
              'p-3 rounded cursor-pointer',
              selectedDoctor?.id === d.id
                ? 'bg-blue-50 border-l-4 border-blue-400'
                : 'bg-white',
            ]"
          >
            <div class="font-medium">{{ d.name }}</div>
            <div class="text-xs text-gray-500">{{ d.specialty || "—" }}</div>
          </li>
        </ul>
      </div>

      <div class="md:col-span-2">
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
        // atualizar lista de slots localmente: marcar como booked
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

<template>
  <div class="app-container space-y-4">
    <h2 class="text-2xl font-semibold">Meus horários</h2>

    <div class="grid grid-cols-1 md:grid-cols-3 gap-4">
      <div class="md:col-span-2">
        <div class="card">
          <SlotList :slots="slots" @edit="onEdit" @delete="onDelete" />
        </div>
      </div>

      <div>
        <div class="card">
          <div v-if="editing">
            <h3 class="font-medium mb-2">Editar horário</h3>
            <SlotForm
              :modelValue="editing"
              @save="saveEdit"
              @cancel="cancelEdit"
            />
          </div>
          <div v-else>
            <h3 class="font-medium mb-2">Criar novo horário</h3>
            <SlotForm @save="createSlot" @cancel="noop" />
          </div>
        </div>
      </div>
    </div>

    <div v-if="error" class="text-red-600">{{ error }}</div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted } from "vue";
import SlotList from "../components/SlotList.vue";
import SlotForm from "../components/SlotForm.vue";
import { useRouter } from "vue-router";
import { useAuthStore } from "../stores/auth";
import {
  listDoctorSlots,
  createDoctorSlot,
  updateDoctorSlot,
  deleteDoctorSlot,
} from "../services/slots";
import { pushToast } from "../components/ui";
import { getMyDoctor } from "../services/doctors";

export default defineComponent({
  name: "DoctorSlots",
  components: { SlotList, SlotForm },
  setup() {
    const auth = useAuthStore();
    const router = useRouter();
    const slots = ref([] as any[]);
    const editing = ref<any | null>(null);
    const error = ref("");

    const load = async () => {
      error.value = "";
      try {
        // somente médicos podem acessar esta página
        if (!auth.user || auth.user.role !== "ROLE_DOCTOR") {
          router.replace({ name: "login" });
          return;
        }
        // obtemos o doctorId real vinculado ao usuário autenticado
        const doctor = await getMyDoctor();
        if (!doctor || !doctor.id)
          throw new Error("ID do médico não encontrado.");
        const res = await listDoctorSlots(doctor.id);
        slots.value = res;
      } catch (e: any) {
        error.value =
          e?.response?.data?.message ||
          e.message ||
          "Erro ao carregar horários.";
      }
    };

    const createSlot = async (payload: any) => {
      error.value = "";
      try {
        const doctor = await getMyDoctor();
        if (!doctor || !doctor.id)
          throw new Error("ID do médico não encontrado.");
        const created = await createDoctorSlot(doctor.id, payload);
        slots.value.unshift(created);
      } catch (e: any) {
        // Tratamento específico para conflito 409 (horário já existente)
        if (e?.response?.status === 409) {
          const msg =
            "Este horário conflita com um horário existente. Verifique a sua agenda e tente outro horário.";
          error.value = msg;
          pushToast("Conflito de horário", msg);
          // Recarregar lista para refletir estado atual
          try {
            await load();
          } catch (err) {
            // ignorar falhas no reload para não sobrescrever a mensagem principal
          }
        } else {
          error.value =
            e?.response?.data?.message || e.message || "Erro ao criar horário.";
        }
      }
    };

    const onEdit = (slot: any) => {
      editing.value = slot;
    };
    const cancelEdit = () => {
      editing.value = null;
    };

    const saveEdit = async (payload: any) => {
      if (!editing.value) return;
      error.value = "";
      try {
        const doctor = await getMyDoctor();
        const updated = await updateDoctorSlot(
          doctor!.id,
          editing.value.id,
          payload
        );
        // substituir na lista
        const idx = slots.value.findIndex((s: any) => s.id === updated.id);
        if (idx >= 0) slots.value.splice(idx, 1, updated);
        editing.value = null;
      } catch (e: any) {
        error.value =
          e?.response?.data?.message ||
          e.message ||
          "Erro ao atualizar horário.";
      }
    };

    const onDelete = async (slot: any) => {
      if (!confirm("Confirma exclusão deste horário?")) return;
      error.value = "";
      try {
        const doctor = await getMyDoctor();
        await deleteDoctorSlot(doctor!.id, slot.id);
        slots.value = slots.value.filter((s: any) => s.id !== slot.id);
      } catch (e: any) {
        error.value =
          e?.response?.data?.message || e.message || "Erro ao excluir horário.";
      }
    };

    onMounted(() => {
      load();
    });

    return {
      slots,
      editing,
      error,
      createSlot,
      onEdit,
      onDelete,
      saveEdit,
      cancelEdit,
      noop: () => {},
    };
  },
});
</script>

<style scoped></style>

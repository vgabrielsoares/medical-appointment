<template>
  <form
    @submit.prevent="onSubmit"
    class="space-y-3 bg-white p-4 rounded shadow-sm"
  >
    <div>
      <label class="block text-sm font-medium text-gray-700">Início</label>
      <input
        v-model="start"
        type="datetime-local"
        class="mt-1 block w-full border rounded p-2"
      />
    </div>

    <div>
      <label class="block text-sm font-medium text-gray-700">Fim</label>
      <input
        v-model="end"
        type="datetime-local"
        class="mt-1 block w-full border rounded p-2"
      />
    </div>

    <div v-if="error" class="text-red-600 text-sm">{{ error }}</div>

    <div class="flex items-center gap-2">
      <button type="submit" class="px-4 py-2 bg-green-600 text-white rounded">
        Salvar
      </button>
      <button
        type="button"
        @click="$emit('cancel')"
        class="px-4 py-2 bg-gray-200 rounded"
      >
        Cancelar
      </button>
    </div>
  </form>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";

export default defineComponent({
  name: "SlotForm",
  props: {
    modelValue: { type: Object as PropType<any>, default: null },
  },
  emits: ["save", "cancel"],
  data() {
    return {
      start: this.modelValue?.start ? this.isoLocal(this.modelValue.start) : "",
      end: this.modelValue?.end ? this.isoLocal(this.modelValue.end) : "",
      error: "",
    };
  },
  methods: {
    isoLocal(iso: string) {
      // converte ISO para valor compatível com input datetime-local (sem timezone)
      const d = new Date(iso);
      const tzOffset = d.getTimezoneOffset() * 60000;
      const local = new Date(d.getTime() - tzOffset);
      return local.toISOString().slice(0, 16);
    },
    toIsoLocal(value: string) {
      if (!value) return "";
      // assume value como 'YYYY-MM-DDTHH:mm'
      const asDate = new Date(value);
      return asDate.toISOString();
    },
    onSubmit() {
      this.error = "";
      if (!this.start || !this.end) {
        this.error = "Preencha início e fim.";
        return;
      }
      const s = new Date(this.start);
      const e = new Date(this.end);
      if (s >= e) {
        this.error = "Início deve ser anterior ao fim.";
        return;
      }
      this.$emit("save", {
        start: this.toIsoLocal(this.start),
        end: this.toIsoLocal(this.end),
      });
    },
  },
});
</script>

<style scoped></style>

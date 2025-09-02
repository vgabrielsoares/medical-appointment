<template>
  <form @submit.prevent="onSubmit" class="space-y-3">
    <div>
      <UiInput v-model="start" type="datetime-local" label="Início" />
    </div>

    <div>
      <UiInput v-model="end" type="datetime-local" label="Fim" />
    </div>

    <div
      v-if="error"
      class="text-red-600 text-sm"
      role="alert"
      aria-live="assertive"
    >
      {{ error }}
    </div>

    <div class="flex items-center gap-2">
      <UiButton type="submit" variant="primary" ariaLabel="Salvar horário"
        >Salvar</UiButton
      >
      <UiButton
        type="button"
        variant="secondary"
        ariaLabel="Cancelar"
        @click="$emit('cancel')"
        >Cancelar</UiButton
      >
    </div>
  </form>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";
import { UiInput } from "./ui";
import { UiButton } from "./ui";

export default defineComponent({
  name: "SlotForm",
  components: { UiInput, UiButton },
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

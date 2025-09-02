<template>
  <div class="flex flex-col">
    <label
      v-if="label"
      :for="inputId"
      class="mb-1 text-sm text-gray-900 dark:text-gray-100"
      >{{ label }}</label
    >
    <input
      :id="inputId"
      :type="type"
      v-bind="$attrs"
      :class="inputClass"
      :value="modelValue"
      @input="onInput"
      :aria-label="(label || ($attrs as any)['aria-label'] || '') as string"
      :aria-describedby="hint ? `${inputId}-hint` : undefined"
    />
    <p v-if="hint" :id="`${inputId}-hint`" class="mt-1 text-xs text-gray-500">
      {{ hint }}
    </p>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiInput",
  props: {
    modelValue: { type: [String, Number], default: "" },
    label: { type: String, default: "" },
    hint: { type: String, default: "" },
    id: { type: String, default: "" },
    type: {
      type: String as () =>
        | "text"
        | "email"
        | "password"
        | "datetime-local"
        | "date",
      default: "text",
    },
  },
  emits: ["update:modelValue"],
  setup(props, { emit }) {
    // gera um id quando não informado para associar o label corretamente
    const generatedId = `input-${Math.random().toString(36).slice(2, 9)}`;
    const inputId = computed(() =>
      props.id && props.id.length ? props.id : generatedId
    );

    const inputClass = computed(
      () =>
        "w-full border border-gray-200 rounded-md px-3 py-2 focus:outline-none focus:ring-2 focus:ring-accent text-gray-900 bg-white dark:bg-gray-700 dark:text-gray-100 placeholder-gray-400"
    );
    function onInput(e: Event) {
      const target = e.target as HTMLInputElement | null;
      if (!target) return;
      emit("update:modelValue", target.value);
    }
    return { inputClass, onInput, inputId };
  },
});
</script>

<style scoped>
/* Default: texto escuro em themes claros */
input[type="datetime-local"] {
  color: #0f172a !important;
  /* text-gray-900 */
}

/* Aplicar a pseudo-partes do datetime-local para herdarem a cor atual */
input[type="datetime-local"]::-webkit-datetime-edit,
input[type="datetime-local"]::-webkit-datetime-edit-text,
input[type="datetime-local"]::-webkit-datetime-edit-field,
input[type="datetime-local"]::-webkit-datetime-edit-hour-field,
input[type="datetime-local"]::-webkit-datetime-edit-minute-field,
input[type="datetime-local"]::-webkit-datetime-edit-day-field,
input[type="datetime-local"]::-webkit-datetime-edit-month-field,
input[type="datetime-local"]::-webkit-datetime-edit-year-field {
  color: inherit !important;
}

/* Dark mode: usar texto claro para boa legibilidade */
.dark input[type="datetime-local"],
.dark input[type="datetime-local"]::-webkit-datetime-edit,
.dark input[type="datetime-local"]::-webkit-datetime-edit-text,
.dark input[type="datetime-local"]::-webkit-datetime-edit-field {
  color: #f3f4f6 !important;
  /* text-gray-100 */
}

/* Calendar picker icon (Chrome/WebKit) - ajustar contraste no dark mode */
input[type="datetime-local"]::-webkit-calendar-picker-indicator {
  filter: none;
}

.dark input[type="datetime-local"]::-webkit-calendar-picker-indicator {
  /* forçar ícone claro em dark mode */
  filter: brightness(0) invert(1) !important;
}
</style>

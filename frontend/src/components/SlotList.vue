<template>
  <div>
    <div v-if="slots.length === 0" class="text-sm text-gray-500">
      Nenhum horário cadastrado.
    </div>
    <ul class="space-y-2 mt-4">
      <li
        v-for="s in slots"
        :key="s.id"
        class="flex items-center justify-between p-3 bg-white rounded shadow-sm"
      >
        <div>
          <div class="font-medium">{{ formatInterval(s.start, s.end) }}</div>
          <div class="text-xs text-gray-500">Status: {{ s.status }}</div>
        </div>
        <div class="flex items-center gap-2">
          <button
            @click="$emit('edit', s)"
            class="px-3 py-1 bg-blue-600 text-white rounded text-sm"
          >
            Editar
          </button>
          <button
            @click="$emit('delete', s)"
            class="px-3 py-1 bg-red-600 text-white rounded text-sm"
          >
            Excluir
          </button>
        </div>
      </li>
    </ul>
  </div>
</template>

<script lang="ts">
import { defineComponent, PropType } from "vue";

export default defineComponent({
  name: "SlotList",
  props: {
    slots: { type: Array as PropType<Array<any>>, default: () => [] },
  },
  methods: {
    formatInterval(start: string, end: string) {
      try {
        const s = new Date(start);
        const e = new Date(end);
        return `${s.toLocaleString()} — ${e.toLocaleString()}`;
      } catch (e) {
        return `${start} — ${end}`;
      }
    },
  },
});
</script>

<style scoped></style>

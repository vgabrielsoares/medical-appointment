<template>
  <div class="fixed bottom-6 right-6 z-50 flex flex-col gap-2">
    <transition-group name="toast" tag="div">
      <div
        v-for="t in toasts"
        :key="t.id"
        class="min-w-[220px] bg-white border rounded-md p-3 shadow-md transform-gpu"
      >
        <div class="font-medium text-sm">{{ t.title }}</div>
        <div class="text-xs text-gray-600 mt-1">{{ t.message }}</div>
      </div>
    </transition-group>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref } from "vue";

type Toast = { id: string; title: string; message: string; timeout?: number };

const toasts = ref<Toast[]>([]);

export function pushToast(title: string, message: string, timeout = 4000) {
  const id = Math.random().toString(36).slice(2, 9);
  const t = { id, title, message, timeout };
  toasts.value.push(t);
  setTimeout(() => {
    toasts.value = toasts.value.filter((x) => x.id !== id);
  }, timeout);
}

export default defineComponent({
  name: "UiToast",
  setup() {
    return { toasts };
  },
});
</script>

<style scoped>
.toast-enter-active,
.toast-leave-active {
  transition: opacity var(--motion-fast, 180ms) ease,
    transform var(--motion-fast, 180ms) cubic-bezier(0.2, 0.8, 0.2, 1);
}
.toast-enter-from {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}
.toast-enter-to {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.toast-leave-from {
  opacity: 1;
  transform: translateY(0) scale(1);
}
.toast-leave-to {
  opacity: 0;
  transform: translateY(8px) scale(0.98);
}

@media (prefers-reduced-motion: reduce) {
  .toast-enter-active,
  .toast-leave-active {
    transition: none !important;
  }
}
</style>

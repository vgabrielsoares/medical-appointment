<template>
  <div
    :class="[
      'bg-card p-3 md:p-4 rounded-md shadow-subtle transition-transform duration-150 ease-out hover:-translate-y-0.5 hover:shadow-md',
      className,
    ]"
    role="region"
  >
    <slot />
  </div>
</template>

<script lang="ts">
import { defineComponent } from "vue";

export default defineComponent({
  name: "UiCard",
  props: {
    className: { type: [String, Array, Object], default: "" },
  },
});
</script>

<style scoped></style>

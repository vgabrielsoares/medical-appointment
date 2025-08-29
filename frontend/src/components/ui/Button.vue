<template>
  <button
    :type="type"
    :class="buttonClass"
    :disabled="disabled"
    :aria-disabled="disabled ? 'true' : 'false'"
    :aria-label="ariaLabel || undefined"
    :tabindex="disabled ? -1 : 0"
    @click="$emit('click', $event)"
  >
    <slot />
  </button>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiButton",
  props: {
    variant: {
      type: String as () => "primary" | "secondary" | "ghost",
      default: "primary",
    },
    size: { type: String as () => "sm" | "md" | "lg", default: "md" },
    type: {
      type: String as () => "button" | "submit" | "reset",
      default: "button",
    },
    disabled: { type: Boolean, default: false },
    ariaLabel: { type: String, default: "" },
  },
  emits: ["click"],
  setup(props) {
    const base =
      "inline-flex items-center justify-center rounded-md font-medium focus:outline-none focus:ring-2 focus:ring-offset-2 focus-visible:ring-4 transition-transform duration-150 ease-out transform";

    const sizes: Record<string, string> = {
      sm: "px-2 py-1 text-xs md:px-3 md:py-1.5 md:text-sm",
      md: "px-3 py-1.5 text-sm md:px-4 md:py-2 md:text-sm",
      lg: "px-4 py-2 text-sm md:px-5 md:py-3 md:text-base",
    };

    const variants: Record<string, string> = {
      primary:
        "bg-primary text-white hover:bg-primary-600 focus:ring-primary-600",
      secondary:
        "bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 focus:ring-accent",
      ghost: "bg-transparent text-primary hover:bg-gray-100 focus:ring-accent",
    };

    const disabledClass = "opacity-50 cursor-not-allowed";

    const interactionClasses =
      "hover:translate-y-0 hover:shadow-sm active:scale-95 disabled:active:scale-100";

    const buttonClass = computed(() => {
      return [
        base,
        sizes[props.size],
        variants[props.variant],
        interactionClasses,
        props.disabled ? disabledClass : "",
      ].join(" ");
    });

    return { buttonClass };
  },
});
</script>

<style scoped></style>

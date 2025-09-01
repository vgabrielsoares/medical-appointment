<template>
  <button
    :type="type"
    :class="buttonClass"
    :disabled="disabled || loading"
    :aria-disabled="disabled || loading ? 'true' : 'false'"
    :aria-label="ariaLabel || undefined"
    :tabindex="disabled || loading ? -1 : 0"
    @click="$emit('click', $event)"
  >
    <span v-if="loading" class="mr-2">
      <svg class="animate-spin h-4 w-4" viewBox="0 0 24 24" fill="none">
        <circle
          class="opacity-25"
          cx="12"
          cy="12"
          r="10"
          stroke="currentColor"
          stroke-width="4"
        />
        <path
          class="opacity-75"
          fill="currentColor"
          d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
        />
      </svg>
    </span>
    <component v-if="icon && !loading" :is="icon" :class="iconClass" />
    <slot />
  </button>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiButton",
  props: {
    variant: {
      type: String as () =>
        | "primary"
        | "secondary"
        | "ghost"
        | "danger"
        | "success",
      default: "primary",
    },
    size: {
      type: String as () => "xs" | "sm" | "md" | "lg" | "xl",
      default: "md",
    },
    type: {
      type: String as () => "button" | "submit" | "reset",
      default: "button",
    },
    disabled: { type: Boolean, default: false },
    loading: { type: Boolean, default: false },
    ariaLabel: { type: String, default: "" },
    icon: { type: Object, default: null },
    fullWidth: { type: Boolean, default: false },
  },
  emits: ["click"],
  setup(props) {
    const base =
      "inline-flex items-center justify-center font-medium focus:outline-none focus:ring-2 focus:ring-offset-2 focus-visible:ring-4 transition-all duration-200 ease-in-out transform hover:scale-[1.02] active:scale-[0.98] disabled:transform-none disabled:cursor-not-allowed";

    const sizes: Record<string, string> = {
      xs: "px-2 py-1 text-xs rounded-md gap-1",
      sm: "px-3 py-1.5 text-xs rounded-md gap-1.5 sm:text-sm",
      md: "px-4 py-2 text-sm rounded-lg gap-2 sm:px-5 sm:py-2.5",
      lg: "px-5 py-2.5 text-sm rounded-lg gap-2 sm:px-6 sm:py-3 sm:text-base",
      xl: "px-6 py-3 text-base rounded-xl gap-2.5 sm:px-8 sm:py-4 sm:text-lg",
    };

    const variants: Record<string, string> = {
      primary:
        "bg-gradient-to-r from-blue-600 to-blue-700 text-white shadow-lg shadow-blue-500/25 hover:shadow-xl hover:shadow-blue-500/40 focus:ring-blue-500 disabled:from-gray-400 disabled:to-gray-400 disabled:shadow-none",
      secondary:
        "bg-white text-gray-700 border border-gray-300 shadow-sm hover:bg-gray-50 hover:shadow-md focus:ring-blue-500 dark:bg-gray-800 dark:text-gray-200 dark:border-gray-600 dark:hover:bg-gray-700 disabled:bg-gray-100 disabled:text-gray-400",
      ghost:
        "bg-transparent text-gray-700 hover:bg-gray-100 hover:text-gray-900 focus:ring-gray-500 dark:text-gray-200 dark:hover:bg-gray-800 disabled:text-gray-400 disabled:hover:bg-transparent",
      danger:
        "bg-gradient-to-r from-red-600 to-red-700 text-white shadow-lg shadow-red-500/25 hover:shadow-xl hover:shadow-red-500/40 focus:ring-red-500 disabled:from-gray-400 disabled:to-gray-400 disabled:shadow-none",
      success:
        "bg-gradient-to-r from-green-600 to-green-700 text-white shadow-lg shadow-green-500/25 hover:shadow-xl hover:shadow-green-500/40 focus:ring-green-500 disabled:from-gray-400 disabled:to-gray-400 disabled:shadow-none",
    };

    const disabledClass = "opacity-50 cursor-not-allowed";

    const interactionClasses =
      "hover:translate-y-0 hover:shadow-sm active:scale-95 disabled:active:scale-100";

    const iconClass = computed(() => {
      const sizeMap: Record<string, string> = {
        xs: "w-3 h-3",
        sm: "w-4 h-4",
        md: "w-4 h-4",
        lg: "w-5 h-5",
        xl: "w-6 h-6",
      };
      return sizeMap[props.size] || "w-4 h-4";
    });

    const buttonClass = computed(() => {
      const classes = [
        base,
        sizes[props.size],
        variants[props.variant],
        interactionClasses,
        props.disabled || props.loading ? disabledClass : "",
      ];

      if (props.fullWidth) {
        classes.push("w-full");
      }

      return classes.join(" ");
    });

    return { buttonClass, iconClass };
  },
});
</script>

<style scoped></style>

<template>
  <div :class="cardClass">
    <div
      v-if="header || $slots.header"
      class="px-4 py-3 border-b border-gray-200 dark:border-gray-700 sm:px-6"
    >
      <slot name="header">
        <h3
          v-if="header"
          class="text-base font-semibold text-gray-900 dark:text-gray-100"
        >
          {{ header }}
        </h3>
      </slot>
    </div>

    <div :class="bodyClass">
      <slot />
    </div>

    <div
      v-if="$slots.footer"
      class="px-4 py-3 border-t border-gray-200 dark:border-gray-700 bg-gray-50 dark:bg-gray-800/50 sm:px-6"
    >
      <slot name="footer" />
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiCard",
  props: {
    variant: {
      type: String as () => "default" | "elevated" | "outlined" | "glass",
      default: "default",
    },
    size: {
      type: String as () => "sm" | "md" | "lg",
      default: "md",
    },
    header: {
      type: String,
      default: "",
    },
    noPadding: {
      type: Boolean,
      default: false,
    },
    hover: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const baseClass = "bg-white dark:bg-gray-800 transition-all duration-200";

    const variants: Record<string, string> = {
      default: "border border-gray-200 dark:border-gray-700 shadow-sm",
      elevated: "shadow-lg shadow-gray-100 dark:shadow-gray-900/20",
      outlined: "border-2 border-gray-300 dark:border-gray-600",
      glass:
        "backdrop-blur-md bg-white/80 dark:bg-gray-800/80 border border-gray-200/50 dark:border-gray-700/50",
    };

    const sizes: Record<string, string> = {
      sm: "rounded-lg",
      md: "rounded-xl",
      lg: "rounded-2xl",
    };

    const hoverEffects = "hover:shadow-xl hover:-translate-y-1 cursor-pointer";

    const cardClass = computed(() => {
      const classes = [baseClass, variants[props.variant], sizes[props.size]];

      if (props.hover) {
        classes.push(hoverEffects);
      }

      return classes.join(" ");
    });

    const bodyClass = computed(() => {
      if (props.noPadding) return "";

      const paddingMap: Record<string, string> = {
        sm: "p-3 sm:p-4",
        md: "p-4 sm:p-6",
        lg: "p-6 sm:p-8",
      };

      return paddingMap[props.size] || paddingMap.md;
    });

    return { cardClass, bodyClass };
  },
});
</script>

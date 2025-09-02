<template>
  <span :class="badgeClass">
    <component v-if="icon" :is="icon" class="w-3 h-3" />
    <slot />
  </span>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiBadge",
  props: {
    variant: {
      type: String as () =>
        | "default"
        | "success"
        | "warning"
        | "danger"
        | "info"
        | "outline",
      default: "default",
    },
    size: {
      type: String as () => "sm" | "md" | "lg",
      default: "md",
    },
    icon: {
      type: Object,
      default: null,
    },
    pill: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const base =
      "inline-flex items-center font-medium transition-all duration-200";

    const sizes: Record<string, string> = {
      sm: "px-2 py-0.5 text-xs gap-1",
      md: "px-2.5 py-1 text-sm gap-1.5",
      lg: "px-3 py-1.5 text-sm gap-2",
    };

    const variants: Record<string, string> = {
      default: "bg-gray-100 text-gray-800 dark:bg-gray-700 dark:text-gray-200",
      success:
        "bg-green-100 text-green-800 dark:bg-green-900/50 dark:text-green-200",
      warning:
        "bg-yellow-100 text-yellow-800 dark:bg-yellow-900/50 dark:text-yellow-200",
      danger: "bg-red-100 text-red-800 dark:bg-red-900/50 dark:text-red-200",
      info: "bg-blue-100 text-blue-800 dark:bg-blue-900/50 dark:text-blue-200",
      outline:
        "border border-gray-300 text-gray-700 dark:border-gray-600 dark:text-gray-200",
    };

    const badgeClass = computed(() => {
      const classes = [base, sizes[props.size], variants[props.variant]];

      if (props.pill) {
        classes.push("rounded-full");
      } else {
        classes.push("rounded-md");
      }

      return classes.join(" ");
    });

    return { badgeClass };
  },
});
</script>

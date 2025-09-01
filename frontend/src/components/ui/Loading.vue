<template>
  <div :class="wrapperClass">
    <div :class="spinnerClass">
      <div
        class="animate-spin rounded-full border-2 border-t-transparent"
        :class="sizeClass"
      />
    </div>
    <div v-if="text" :class="textClass">
      {{ text }}
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, computed } from "vue";

export default defineComponent({
  name: "UiLoading",
  props: {
    size: {
      type: String as () => "sm" | "md" | "lg" | "xl",
      default: "md",
    },
    text: {
      type: String,
      default: "",
    },
    variant: {
      type: String as () => "primary" | "white" | "gray",
      default: "primary",
    },
    fullPage: {
      type: Boolean,
      default: false,
    },
  },
  setup(props) {
    const sizes: Record<string, string> = {
      sm: "w-4 h-4",
      md: "w-6 h-6",
      lg: "w-8 h-8",
      xl: "w-12 h-12",
    };

    const variants: Record<string, string> = {
      primary: "border-blue-600",
      white: "border-white",
      gray: "border-gray-400",
    };

    const wrapperClass = computed(() => {
      if (props.fullPage) {
        return "fixed inset-0 bg-white/80 dark:bg-gray-900/80 backdrop-blur-sm flex items-center justify-center z-50";
      }
      return "flex items-center justify-center gap-3";
    });

    const spinnerClass = computed(() => {
      return "flex items-center justify-center";
    });

    const sizeClass = computed(() => {
      return `${sizes[props.size]} ${variants[props.variant]}`;
    });

    const textClass = computed(() => {
      const baseClass = "text-gray-600 dark:text-gray-300 font-medium";
      const sizeMap: Record<string, string> = {
        sm: "text-sm",
        md: "text-base",
        lg: "text-lg",
        xl: "text-xl",
      };
      return `${baseClass} ${sizeMap[props.size]}`;
    });

    return {
      wrapperClass,
      spinnerClass,
      sizeClass,
      textClass,
    };
  },
});
</script>

<template>
  <div :class="avatarClass">
    <img
      v-if="src"
      :src="src"
      :alt="alt || name"
      class="w-full h-full object-cover"
      @error="handleImageError"
    />
    <span v-else-if="name" :class="textClass">
      {{ initials }}
    </span>
    <UserIcon v-else class="w-1/2 h-1/2 text-gray-400" />

    <div v-if="status" :class="statusClass" />
  </div>
</template>

<script lang="ts">
import { defineComponent, computed, ref } from "vue";
import { UserIcon } from "@heroicons/vue/24/solid";

export default defineComponent({
  name: "UiAvatar",
  components: { UserIcon },
  props: {
    src: {
      type: String,
      default: "",
    },
    name: {
      type: String,
      default: "",
    },
    alt: {
      type: String,
      default: "",
    },
    size: {
      type: String as () => "xs" | "sm" | "md" | "lg" | "xl" | "2xl",
      default: "md",
    },
    status: {
      type: String as () => "online" | "offline" | "busy" | "away" | "",
      default: "",
    },
    variant: {
      type: String as () => "circle" | "rounded" | "square",
      default: "circle",
    },
  },
  setup(props) {
    const imageError = ref(false);

    const sizes: Record<string, string> = {
      xs: "w-6 h-6",
      sm: "w-8 h-8",
      md: "w-10 h-10",
      lg: "w-12 h-12",
      xl: "w-16 h-16",
      "2xl": "w-20 h-20",
    };

    const textSizes: Record<string, string> = {
      xs: "text-xs",
      sm: "text-sm",
      md: "text-sm",
      lg: "text-base",
      xl: "text-lg",
      "2xl": "text-xl",
    };

    const variants: Record<string, string> = {
      circle: "rounded-full",
      rounded: "rounded-lg",
      square: "rounded-none",
    };

    const statusSizes: Record<string, string> = {
      xs: "w-1.5 h-1.5 -top-0.5 -right-0.5",
      sm: "w-2 h-2 -top-0.5 -right-0.5",
      md: "w-2.5 h-2.5 -top-1 -right-1",
      lg: "w-3 h-3 -top-1 -right-1",
      xl: "w-4 h-4 -top-1.5 -right-1.5",
      "2xl": "w-5 h-5 -top-2 -right-2",
    };

    const statusColors: Record<string, string> = {
      online: "bg-green-500",
      offline: "bg-gray-400",
      busy: "bg-red-500",
      away: "bg-yellow-500",
    };

    const initials = computed(() => {
      if (!props.name) return "";
      return props.name
        .split(" ")
        .map((word) => word.charAt(0))
        .join("")
        .slice(0, 2)
        .toUpperCase();
    });

    const avatarClass = computed(() => {
      return [
        "relative inline-flex items-center justify-center bg-gradient-to-br from-blue-500 to-purple-600 text-white font-medium overflow-hidden",
        sizes[props.size],
        variants[props.variant],
      ].join(" ");
    });

    const textClass = computed(() => {
      return textSizes[props.size];
    });

    const statusClass = computed(() => {
      if (!props.status) return "";
      return [
        "absolute rounded-full border-2 border-white dark:border-gray-800",
        statusSizes[props.size],
        statusColors[props.status],
      ].join(" ");
    });

    const handleImageError = () => {
      imageError.value = true;
    };

    return {
      initials,
      avatarClass,
      textClass,
      statusClass,
      handleImageError,
    };
  },
});
</script>

<template>
  <header class="bg-white shadow-sm">
    <div class="app-container flex items-center justify-between h-16">
      <div class="flex items-center gap-4">
        <router-link
          to="/"
          class="flex items-center gap-3 text-lg font-semibold text-primary"
        >
          <svg
            class="w-8 h-8 rounded-full bg-accent text-white p-1"
            viewBox="0 0 24 24"
            fill="none"
          >
            <path d="M12 2L15 8H9L12 2Z" fill="currentColor" />
          </svg>
          <span class="hidden sm:inline">Agendamento Médico</span>
        </router-link>
      </div>

      <!-- desktop nav -->
      <nav class="hidden md:flex items-center gap-4">
        <router-link
          v-if="!isAuthenticated"
          to="/login"
          class="text-sm text-gray-600 hover:text-accent"
          >Login</router-link
        >
        <router-link
          v-if="!isAuthenticated"
          to="/patient"
          class="text-sm text-gray-600 hover:text-accent"
          >Paciente</router-link
        >
        <router-link
          v-if="!isAuthenticated"
          to="/doctor"
          class="text-sm text-gray-600 hover:text-accent"
          >Médico</router-link
        >

        <!-- theme toggle -->
        <button
          @click="toggleTheme"
          :aria-pressed="isDark()"
          class="p-2 rounded-md flex items-center justify-center text-sm text-gray-600 hover:text-accent focus:outline-none"
          title="Alternar tema claro/escuro"
        >
          <component :is="isDark() ? MoonIcon : SunIcon" class="w-5 h-5" />
        </button>

        <div v-if="isAuthenticated" class="relative" ref="userMenuRef">
          <button
            @click="toggleUserMenu"
            class="flex items-center gap-2 text-sm text-gray-700 hover:text-accent focus:outline-none"
            aria-haspopup="true"
            :aria-expanded="showUserMenu"
            role="button"
          >
            <UserCircleIcon
              class="w-7 h-7 text-gray-400 bg-gray-100 rounded-full p-1"
            />
            <span>{{ displayName }}</span>
            <ChevronDownIcon class="w-4 h-4 text-gray-500" />
          </button>

          <div
            v-if="showUserMenu"
            class="absolute right-0 mt-2 w-40 bg-white border rounded-md shadow-md py-1 z-20"
          >
            <router-link
              to="/profile"
              class="block px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
              >Perfil</router-link
            >
            <button
              @click="onLogout"
              class="w-full text-left px-3 py-2 text-sm text-gray-700 hover:bg-gray-100"
            >
              Sair
            </button>
          </div>
        </div>
      </nav>

      <!-- mobile hamburger -->
      <div class="md:hidden flex items-center">
        <button
          @click="toggleMobileMenu"
          class="p-2 rounded-md focus:outline-none"
          aria-label="Abrir menu mobile"
          :aria-expanded="mobileOpen"
        >
          <component :is="mobileOpen ? XMarkIcon : Bars3Icon" class="w-6 h-6" />
        </button>
      </div>
    </div>

    <!-- menu mobile -->
    <div
      v-if="mobileOpen"
      class="md:hidden bg-white dark:bg-gray-900 border-b"
      role="menu"
    >
      <div class="app-container py-3 flex flex-col gap-2">
        <router-link
          to="/"
          @click="closeMobile"
          class="flex items-center gap-2 p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-800"
        >
          <HomeIcon class="w-5 h-5 text-gray-600" />
          <span class="text-sm">Início</span>
        </router-link>
        <router-link
          to="/patient"
          @click="closeMobile"
          class="flex items-center gap-2 p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-800"
        >
          <UsersIcon class="w-5 h-5 text-gray-600" />
          <span class="text-sm">Paciente</span>
        </router-link>
        <router-link
          to="/doctor"
          @click="closeMobile"
          class="flex items-center gap-2 p-2 rounded hover:bg-gray-50 dark:hover:bg-gray-800"
        >
          <ClipboardDocumentIcon class="w-5 h-5 text-gray-600" />
          <span class="text-sm">Médico</span>
        </router-link>

        <div v-if="isAuthenticated" class="pt-2 border-t">
          <router-link
            to="/profile"
            @click="closeMobile"
            class="block p-2 text-sm"
            >Perfil</router-link
          >
          <button
            @click="toggleTheme"
            class="w-full text-left p-2 flex items-center gap-2 text-sm"
          >
            Alternar tema
          </button>
          <button @click="mobileLogout" class="w-full text-left p-2 text-sm">
            Sair
          </button>
        </div>
      </div>
    </div>
  </header>
</template>

<script lang="ts">
import { defineComponent, ref, onMounted, onBeforeUnmount } from "vue";
import { useAuthStore } from "../stores/auth";
import { useThemeStore } from "../stores/theme";
import {
  SunIcon,
  MoonIcon,
  Bars3Icon,
  XMarkIcon,
  UserCircleIcon,
  ChevronDownIcon,
  HomeIcon,
  UsersIcon,
  ClipboardDocumentIcon,
} from "@heroicons/vue/24/outline";

export default defineComponent({
  name: "NavBar",
  setup() {
    const mobileOpen = ref(false);
    const showUserMenu = ref(false);
    const userMenuRef = ref<HTMLElement | null>(null);

    const auth = useAuthStore();
    const theme = useThemeStore();

    function toggleMobileMenu() {
      mobileOpen.value = !mobileOpen.value;
    }
    function closeMobile() {
      mobileOpen.value = false;
    }
    function toggleUserMenu() {
      showUserMenu.value = !showUserMenu.value;
    }
    function onLogout() {
      auth.logout();
      window.location.href = "#/login";
    }
    function mobileLogout() {
      onLogout();
      closeMobile();
    }

    // fecha menus ao clicar fora e com ESC
    function onDocumentClick(e: MouseEvent) {
      if (!userMenuRef.value) return;
      const el = userMenuRef.value as HTMLElement;
      if (!el.contains(e.target as Node)) showUserMenu.value = false;
    }

    function onDocumentKey(e: KeyboardEvent) {
      if (e.key === "Escape") {
        showUserMenu.value = false;
        mobileOpen.value = false;
      }
    }

    onMounted(() => {
      document.addEventListener("click", onDocumentClick);
      document.addEventListener("keydown", onDocumentKey);
    });
    onBeforeUnmount(() => {
      document.removeEventListener("click", onDocumentClick);
      document.removeEventListener("keydown", onDocumentKey);
    });

    function closeUserMenu() {
      showUserMenu.value = false;
    }

    const isAuthenticated = auth.isAuthenticated;
    const displayName = auth.user ? auth.user.id?.slice(0, 8) : "Usuário";

    function toggleTheme() {
      theme.toggle();
    }

    function isDark() {
      return theme.isDark;
    }

    return {
      mobileOpen,
      toggleMobileMenu,
      closeMobile,
      isAuthenticated,
      displayName,
      showUserMenu,
      toggleUserMenu,
      onLogout,
      userMenuRef,
      mobileLogout,
      closeUserMenu,
      toggleTheme,
      isDark,
      SunIcon,
      MoonIcon,
      Bars3Icon,
      XMarkIcon,
      UserCircleIcon,
      ChevronDownIcon,
      HomeIcon,
      UsersIcon,
      ClipboardDocumentIcon,
    };
  },
});
</script>

<style scoped>
/* pequenas correções de acessibilidade visual */
.app-container :global(.focus\:outline-none:focus) {
  outline: 2px solid transparent;
}
</style>

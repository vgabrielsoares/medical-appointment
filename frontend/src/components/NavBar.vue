<template>
  <header
    class="sticky top-0 z-50 bg-white/80 dark:bg-gray-900/80 backdrop-blur-xl border-b border-gray-200 dark:border-gray-700"
  >
    <div class="app-container">
      <div class="flex items-center justify-between h-16">
        <!-- Logo -->
        <div class="flex items-center gap-4">
          <router-link
            to="/"
            class="group flex items-center gap-3 text-lg font-bold text-gray-900 dark:text-white hover:text-blue-600 dark:hover:text-blue-400 transition-colors"
          >
            <div class="relative">
              <div
                class="w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl flex items-center justify-center shadow-lg group-hover:shadow-xl group-hover:scale-105 transition-all duration-300"
              >
                <CalendarDaysIcon class="w-6 h-6 text-white" />
              </div>
              <div
                class="absolute -top-1 -right-1 w-3 h-3 bg-green-500 rounded-full border-2 border-white dark:border-gray-900 animate-pulse"
              ></div>
            </div>
            <span
              class="hidden sm:inline bg-gradient-to-r from-blue-600 to-purple-600 bg-clip-text text-transparent"
            >
              MedBook
            </span>
          </router-link>
        </div>

        <!-- Desktop Navigation -->
        <nav class="hidden md:flex items-center gap-1">
          <router-link v-if="!isAuthenticated" to="/login" class="nav-link">
            <ArrowRightOnRectangleIcon class="w-4 h-4" />
            <span>Login</span>
          </router-link>

          <router-link v-if="!isAuthenticated" to="/patient" class="nav-link">
            <UserIcon class="w-4 h-4" />
            <span>Paciente</span>
          </router-link>

          <router-link v-if="!isAuthenticated" to="/doctor" class="nav-link">
            <UserGroupIcon class="w-4 h-4" />
            <span>Médico</span>
          </router-link>

          <!-- Theme Toggle -->
          <button
            @click="toggleTheme"
            class="nav-button group"
            :title="isDark() ? 'Ativar tema claro' : 'Ativar tema escuro'"
          >
            <component
              :is="isDark() ? SunIcon : MoonIcon"
              class="w-5 h-5 group-hover:rotate-12 transition-transform duration-300"
            />
          </button>

          <!-- User Menu -->
          <div v-if="isAuthenticated" class="relative ml-2" ref="userMenuRef">
            <button
              @click="toggleUserMenu"
              class="flex items-center gap-2 px-3 py-2 rounded-xl bg-gray-100 dark:bg-gray-800 hover:bg-gray-200 dark:hover:bg-gray-700 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
              :aria-expanded="showUserMenu"
            >
              <UiAvatar :name="displayName" size="sm" status="online" />
              <span
                class="hidden sm:block text-sm font-medium text-gray-900 dark:text-white"
              >
                {{ displayName }}
              </span>
              <ChevronDownIcon
                :class="[
                  'w-4 h-4 text-gray-500 transition-transform duration-200',
                  showUserMenu ? 'rotate-180' : '',
                ]"
              />
            </button>

            <!-- Dropdown Menu -->
            <div
              v-if="showUserMenu"
              class="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-xl shadow-xl border border-gray-200 dark:border-gray-700 py-2 z-20"
            >
              <div
                class="px-4 py-2 border-b border-gray-200 dark:border-gray-700"
              >
                <div class="text-sm font-medium text-gray-900 dark:text-white">
                  {{ displayName }}
                </div>
                <div class="text-xs text-gray-500 dark:text-gray-400">
                  {{ auth.user?.email }}
                </div>
              </div>

              <router-link
                to="/profile"
                class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                @click="showUserMenu = false"
              >
                <UserCircleIcon class="w-4 h-4" />
                Perfil
              </router-link>

              <router-link
                to="/settings"
                class="flex items-center gap-3 px-4 py-2 text-sm text-gray-700 dark:text-gray-200 hover:bg-gray-100 dark:hover:bg-gray-700 transition-colors"
                @click="showUserMenu = false"
              >
                <CogIcon class="w-4 h-4" />
                Configurações
              </router-link>

              <div
                class="border-t border-gray-200 dark:border-gray-700 mt-2 pt-2"
              >
                <button
                  @click="onLogout"
                  class="flex items-center gap-3 w-full px-4 py-2 text-sm text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 transition-colors"
                >
                  <ArrowLeftOnRectangleIcon class="w-4 h-4" />
                  Sair
                </button>
              </div>
            </div>
          </div>
        </nav>

        <!-- Mobile Menu Button -->
        <div class="md:hidden flex items-center gap-2">
          <!-- Theme Toggle Mobile -->
          <button
            @click="toggleTheme"
            class="nav-button"
            :title="isDark() ? 'Ativar tema claro' : 'Ativar tema escuro'"
          >
            <component :is="isDark() ? SunIcon : MoonIcon" class="w-5 h-5" />
          </button>

          <!-- Hamburger Menu -->
          <button
            @click="toggleMobileMenu"
            class="nav-button"
            :aria-expanded="mobileOpen"
          >
            <component
              :is="mobileOpen ? XMarkIcon : Bars3Icon"
              class="w-6 h-6"
            />
          </button>
        </div>
      </div>

      <!-- Mobile Menu -->
      <div
        v-if="mobileOpen"
        class="md:hidden border-t border-gray-200 dark:border-gray-700 bg-white/95 dark:bg-gray-900/95 backdrop-blur-xl"
      >
        <div class="px-4 py-4 space-y-2">
          <router-link
            v-if="!isAuthenticated"
            to="/login"
            class="mobile-nav-link"
            @click="mobileOpen = false"
          >
            <ArrowRightOnRectangleIcon class="w-5 h-5" />
            <span>Login</span>
          </router-link>

          <router-link
            v-if="!isAuthenticated"
            to="/patient"
            class="mobile-nav-link"
            @click="mobileOpen = false"
          >
            <UserIcon class="w-5 h-5" />
            <span>Área do Paciente</span>
          </router-link>

          <router-link
            v-if="!isAuthenticated"
            to="/doctor"
            class="mobile-nav-link"
            @click="mobileOpen = false"
          >
            <UserGroupIcon class="w-5 h-5" />
            <span>Área do Médico</span>
          </router-link>

          <!-- Mobile User Menu -->
          <div
            v-if="isAuthenticated"
            class="pt-4 border-t border-gray-200 dark:border-gray-700"
          >
            <div
              class="flex items-center gap-3 px-4 py-3 bg-gray-50 dark:bg-gray-800 rounded-xl mb-3"
            >
              <UiAvatar :name="displayName" size="md" status="online" />
              <div>
                <div class="font-medium text-gray-900 dark:text-white">
                  {{ displayName }}
                </div>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  {{ auth.user?.email }}
                </div>
              </div>
            </div>

            <router-link
              to="/profile"
              class="mobile-nav-link"
              @click="mobileOpen = false"
            >
              <UserCircleIcon class="w-5 h-5" />
              <span>Perfil</span>
            </router-link>

            <router-link
              to="/settings"
              class="mobile-nav-link"
              @click="mobileOpen = false"
            >
              <CogIcon class="w-5 h-5" />
              <span>Configurações</span>
            </router-link>

            <button
              @click="onLogout"
              class="w-full flex items-center gap-3 px-4 py-3 text-red-600 dark:text-red-400 hover:bg-red-50 dark:hover:bg-red-900/20 rounded-xl transition-colors"
            >
              <ArrowLeftOnRectangleIcon class="w-5 h-5" />
              <span>Sair</span>
            </button>
          </div>
        </div>
      </div>
    </div>
  </header>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import {
  CalendarDaysIcon,
  ArrowRightOnRectangleIcon,
  ArrowLeftOnRectangleIcon,
  UserIcon,
  UserGroupIcon,
  UserCircleIcon,
  ChevronDownIcon,
  Bars3Icon,
  XMarkIcon,
  SunIcon,
  MoonIcon,
  CogIcon,
} from "@heroicons/vue/24/solid";
import { UiAvatar } from "./ui";
import { useAuthStore } from "../stores/auth";
import { useThemeStore } from "../stores/theme";

export default defineComponent({
  name: "NavBar",
  components: {
    UiAvatar,
    CalendarDaysIcon,
    ArrowRightOnRectangleIcon,
    ArrowLeftOnRectangleIcon,
    UserIcon,
    UserGroupIcon,
    UserCircleIcon,
    ChevronDownIcon,
    Bars3Icon,
    XMarkIcon,
    SunIcon,
    MoonIcon,
    CogIcon,
  },
  setup() {
    const router = useRouter();
    const auth = useAuthStore();
    const theme = useThemeStore();

    const showUserMenu = ref(false);
    const mobileOpen = ref(false);
    const userMenuRef = ref<HTMLElement | null>(null);

    const isAuthenticated = computed(() => auth.isAuthenticated);

    const displayName = computed(() => {
      return auth.user?.id || "Usuário";
    });

    const toggleTheme = () => {
      theme.toggle();
    };

    const isDark = () => {
      return theme.isDark;
    };

    const toggleUserMenu = () => {
      showUserMenu.value = !showUserMenu.value;
    };

    const toggleMobileMenu = () => {
      mobileOpen.value = !mobileOpen.value;
    };

    const onLogout = async () => {
      try {
        await auth.logout();
        showUserMenu.value = false;
        mobileOpen.value = false;
        router.push("/");
      } catch (error) {
        console.error("Erro ao fazer logout:", error);
      }
    };

    const handleClickOutside = (event: Event) => {
      if (
        userMenuRef.value &&
        !userMenuRef.value.contains(event.target as Node)
      ) {
        showUserMenu.value = false;
      }
    };

    onMounted(() => {
      document.addEventListener("click", handleClickOutside);
    });

    onUnmounted(() => {
      document.removeEventListener("click", handleClickOutside);
    });

    return {
      auth,
      showUserMenu,
      mobileOpen,
      userMenuRef,
      isAuthenticated,
      displayName,
      toggleTheme,
      isDark,
      toggleUserMenu,
      toggleMobileMenu,
      onLogout,
      SunIcon,
      MoonIcon,
      Bars3Icon,
      XMarkIcon,
    };
  },
});
</script>

<style scoped>
.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.5rem 0.75rem;
  border-radius: 0.5rem;
  font-size: 0.875rem;
  font-weight: 500;
  color: rgb(75 85 99);
  transition: all 0.2s;
}

.nav-link:hover {
  color: rgb(17 24 39);
  background-color: rgb(243 244 246);
}

.dark .nav-link {
  color: rgb(209 213 219);
}

.dark .nav-link:hover {
  color: white;
  background-color: rgb(31 41 55);
}

.nav-button {
  padding: 0.5rem;
  border-radius: 0.5rem;
  color: rgb(75 85 99);
  transition: all 0.2s;
}

.nav-button:hover {
  color: rgb(17 24 39);
  background-color: rgb(243 244 246);
}

.dark .nav-button {
  color: rgb(209 213 219);
}

.dark .nav-button:hover {
  color: white;
  background-color: rgb(31 41 55);
}

.mobile-nav-link {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  width: 100%;
  padding: 0.75rem 1rem;
  color: rgb(55 65 81);
  border-radius: 0.75rem;
  transition: colors 0.2s;
}

.mobile-nav-link:hover {
  background-color: rgb(243 244 246);
}

.dark .mobile-nav-link {
  color: rgb(229 231 235);
}

.dark .mobile-nav-link:hover {
  background-color: rgb(31 41 55);
}

.router-link-active.nav-link {
  color: rgb(37 99 235);
  background-color: rgb(219 234 254);
}

.dark .router-link-active.nav-link {
  color: rgb(96 165 250);
  background-color: rgba(37, 99, 235, 0.2);
}

.router-link-active.mobile-nav-link {
  color: rgb(37 99 235);
  background-color: rgb(219 234 254);
}

.dark .router-link-active.mobile-nav-link {
  color: rgb(96 165 250);
  background-color: rgba(37, 99, 235, 0.2);
}
</style>

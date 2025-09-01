<template>
  <div
    class="min-h-screen bg-gray-50 dark:bg-gray-900 flex items-center justify-center p-4"
  >
    <div class="w-full max-w-md">
      <!-- Logo e Título -->
      <div class="text-center mb-8">
        <div class="flex justify-center mb-4">
          <div
            class="w-16 h-16 bg-gradient-to-r from-blue-600 to-purple-600 rounded-2xl flex items-center justify-center shadow-xl"
          >
            <CalendarDaysIcon class="w-8 h-8 text-white" />
          </div>
        </div>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-2">
          Criar Conta
        </h1>
        <p class="text-gray-600 dark:text-gray-400">
          Junte-se ao MedBook e gerencie seus agendamentos
        </p>
      </div>

      <!-- Formulário de Registro -->
      <UiCard variant="elevated" size="lg">
        <form @submit.prevent="handleRegister" class="space-y-6">
          <!-- Nome -->
          <div>
            <label
              for="name"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Nome Completo *
            </label>
            <UiInput
              id="name"
              v-model="form.name"
              type="text"
              placeholder="Digite seu nome completo"
              required
              :disabled="isLoading"
            />
          </div>

          <!-- Email -->
          <div>
            <label
              for="email"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Email *
            </label>
            <UiInput
              id="email"
              v-model="form.email"
              type="email"
              placeholder="Digite seu email"
              required
              :disabled="isLoading"
            />
            <p
              v-if="!isEmailValid(form.email || '') && form.email.trim() !== ''"
              class="mt-1 text-xs text-red-600 dark:text-red-400"
            >
              Email inválido
            </p>
            <p
              v-else-if="checkingEmail"
              class="mt-1 text-xs text-gray-600 dark:text-gray-400"
            >
              Verificando email...
            </p>
            <p
              v-else-if="emailExists"
              class="mt-1 text-xs text-red-600 dark:text-red-400"
            >
              Este email já está em uso
            </p>
            <p
              v-else-if="emailCheckError"
              class="mt-1 text-xs text-yellow-600 dark:text-yellow-400"
            >
              {{ emailCheckError }}
            </p>
          </div>

          <!-- Senha -->
          <div>
            <label
              for="password"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Senha *
            </label>
            <UiInput
              id="password"
              v-model="form.password"
              type="password"
              placeholder="Digite sua senha"
              required
              :disabled="isLoading"
            />
          </div>

          <!-- Confirmar Senha -->
          <div>
            <label
              for="confirmPassword"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Confirmar Senha *
            </label>
            <UiInput
              id="confirmPassword"
              v-model="form.confirmPassword"
              type="password"
              placeholder="Confirme sua senha"
              required
              :disabled="isLoading"
            />
            <p
              v-if="
                form.confirmPassword && form.password !== form.confirmPassword
              "
              class="mt-1 text-xs text-red-600 dark:text-red-400"
            >
              As senhas não coincidem
            </p>
          </div>

          <!-- Tipo de Usuário -->
          <div>
            <label
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-3"
            >
              Tipo de Conta *
            </label>
            <div class="grid grid-cols-2 gap-3">
              <button
                type="button"
                @click="form.userType = 'ROLE_PATIENT'"
                :class="[
                  'p-4 rounded-xl border-2 transition-all duration-200',
                  form.userType === 'ROLE_PATIENT'
                    ? 'border-green-500 bg-green-50 dark:bg-green-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600',
                ]"
                :disabled="isLoading"
              >
                <div class="flex flex-col items-center gap-2">
                  <UserIcon
                    class="w-8 h-8 text-green-600 dark:text-green-400"
                  />
                  <span class="font-medium text-gray-900 dark:text-white"
                    >Paciente</span
                  >
                  <span class="text-xs text-gray-500">Agendar consultas</span>
                </div>
              </button>

              <button
                type="button"
                @click="form.userType = 'ROLE_DOCTOR'"
                :class="[
                  'p-4 rounded-xl border-2 transition-all duration-200',
                  form.userType === 'ROLE_DOCTOR'
                    ? 'border-blue-500 bg-blue-50 dark:bg-blue-900/20'
                    : 'border-gray-200 dark:border-gray-700 hover:border-gray-300 dark:hover:border-gray-600',
                ]"
                :disabled="isLoading"
              >
                <div class="flex flex-col items-center gap-2">
                  <UserGroupIcon
                    class="w-8 h-8 text-blue-600 dark:text-blue-400"
                  />
                  <span class="font-medium text-gray-900 dark:text-white"
                    >Médico</span
                  >
                  <span class="text-xs text-gray-500">Gerenciar agenda</span>
                </div>
              </button>
            </div>
          </div>

          <!-- Especialidade (apenas para médicos) -->
          <div v-if="form.userType === 'ROLE_DOCTOR'">
            <label
              for="specialty"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Especialidade *
            </label>
            <UiInput
              id="specialty"
              v-model="form.specialty"
              type="text"
              placeholder="Ex: Cardiologia, Pediatria, etc."
              required
              :disabled="isLoading"
            />
          </div>

          <!-- Telefone -->
          <div>
            <label
              for="phone"
              class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
            >
              Telefone
            </label>
            <UiInput
              id="phone"
              :modelValue="form.phone"
              @update:modelValue="onPhoneInput"
              type="text"
              placeholder="(11) 99999-9999"
              :disabled="isLoading"
              @blur="formatPhoneInput"
            />
            <p
              v-if="form.phone && !isPhoneValid(form.phone)"
              class="mt-1 text-xs text-red-600 dark:text-red-400"
            >
              Telefone inválido. Use (11) 99999-9999
            </p>
          </div>

          <!-- Erro -->
          <div
            v-if="error"
            class="p-3 bg-red-50 dark:bg-red-900/20 border border-red-200 dark:border-red-800 rounded-lg"
          >
            <p class="text-sm text-red-600 dark:text-red-400">
              {{ error }}
            </p>
          </div>

          <!-- Botão de Registro -->
          <UiButton
            type="submit"
            variant="primary"
            size="lg"
            :loading="isLoading"
            :disabled="!isFormValid"
            class="w-full"
          >
            <UserPlusIcon class="w-5 h-5 mr-2" />
            Criar Conta
          </UiButton>

          <!-- Link para Login -->
          <div class="text-center pt-4">
            <p class="text-sm text-gray-600 dark:text-gray-400">
              Já tem uma conta?
              <router-link
                to="/login"
                class="font-medium text-blue-600 hover:text-blue-500 dark:text-blue-400 dark:hover:text-blue-300"
              >
                Faça login
              </router-link>
            </p>
          </div>
        </form>
      </UiCard>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, watch } from "vue";
import { useRouter } from "vue-router";
import {
  CalendarDaysIcon,
  UserIcon,
  UserGroupIcon,
  UserPlusIcon,
} from "@heroicons/vue/24/solid";
import { UiCard, UiInput, UiButton } from "../components/ui";
import api from "../services/api";
import { useAuthStore } from "../stores/auth";
import {
  isEmailValid as utilIsEmailValid,
  formatPhoneProgressive,
  formatPhoneFinal,
  isPhoneValid as utilIsPhoneValid,
  sanitizeDigits,
} from "../utils/formatters";

interface RegisterForm {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  userType: "ROLE_PATIENT" | "ROLE_DOCTOR" | "";
  specialty?: string;
  phone?: string;
}

export default defineComponent({
  name: "Register",
  components: {
    UiCard,
    UiInput,
    UiButton,
    CalendarDaysIcon,
    UserIcon,
    UserGroupIcon,
    UserPlusIcon,
  },
  setup() {
    const router = useRouter();
    const auth = useAuthStore();

    const isLoading = ref(false);
    const error = ref("");
    const emailExists = ref(false);
    const checkingEmail = ref(false);
    const emailCheckError = ref("");

    const form = ref<RegisterForm>({
      name: "",
      email: "",
      password: "",
      confirmPassword: "",
      userType: "",
      specialty: "",
      phone: "",
    });

    const isFormValid = computed(() => {
      return (
        form.value.name.trim() !== "" &&
        form.value.email.trim() !== "" &&
        form.value.password.trim() !== "" &&
        form.value.confirmPassword.trim() !== "" &&
        form.value.userType !== "" &&
        form.value.password === form.value.confirmPassword &&
        (form.value.userType !== "ROLE_DOCTOR" ||
          form.value.specialty?.trim() !== "") &&
        // email não pode já existir
        !emailExists.value &&
        // telefone, se preenchido, deve ser válido
        (form.value.phone?.trim() === "" ||
          isPhoneValid(form.value.phone || ""))
      );
    });

    const isEmailValid = utilIsEmailValid;
    const isPhoneValid = utilIsPhoneValid;

    // debounce para checar email existente
    let emailTimer: number | undefined;
    watch(
      () => form.value.email,
      (newVal) => {
        emailExists.value = false;
        emailCheckError.value = "";
        if (emailTimer) clearTimeout(emailTimer);
        const val = (newVal || "").trim();
        if (!isEmailValid(val)) {
          checkingEmail.value = false;
          return;
        }
        checkingEmail.value = true;
        emailTimer = window.setTimeout(async () => {
          try {
            const res = await api.get("/auth/check-email", {
              params: { email: val },
            });
            emailExists.value = !!res.data?.exists;
          } catch (e: any) {
            emailCheckError.value =
              e.response?.data?.message || "Não foi possível verificar o email";
            emailExists.value = false;
          } finally {
            checkingEmail.value = false;
          }
        }, 600);
      }
    );

    const handleRegister = async () => {
      if (!isFormValid.value || isLoading.value) {
        return;
      }

      if (form.value.password !== form.value.confirmPassword) {
        error.value = "As senhas não coincidem";
        return;
      }

      if (!isEmailValid(form.value.email || "")) {
        error.value = "Email inválido";
        return;
      }

      if (form.value.phone && !isPhoneValid(form.value.phone)) {
        error.value = "Telefone inválido. Use o formato (11) 99999-9999";
        return;
      }

      try {
        isLoading.value = true;
        error.value = "";

        const payload = {
          name: form.value.name.trim(),
          email: form.value.email.trim(),
          password: form.value.password,
          role: form.value.userType,
          ...(form.value.userType === "ROLE_DOCTOR" && {
            specialty: form.value.specialty?.trim(),
          }),
          ...(form.value.phone && { phone: form.value.phone.trim() }),
        };

        const response = await api.post("/auth/register", payload);

        // Se o registro incluir login automático
        if (response.data.token) {
          auth.setToken(response.data.token);
          auth.setUser(response.data.user);

          // Redirecionar baseado no tipo de usuário
          if (response.data.user.role === "ROLE_DOCTOR") {
            router.push("/doctor");
          } else {
            router.push("/patient");
          }
        } else {
          // Se não incluir login automático, redirecionar para login
          router.push("/login");
        }
      } catch (err: any) {
        console.error("Erro no registro:", err);
        error.value =
          err.response?.data?.message ||
          "Erro ao criar conta. Tente novamente.";
      } finally {
        isLoading.value = false;
      }
    };

    // Formata telefone no blur
    function formatPhoneInput() {
      form.value.phone = formatPhoneFinal(form.value.phone || "");
    }

    // Handler progressivo para o input do telefone enquanto o usuário digita.
    function onPhoneInput(val: string) {
      form.value.phone = formatPhoneProgressive(val || "");
    }

    return {
      form,
      isLoading,
      error,
      isFormValid,
      emailExists,
      checkingEmail,
      emailCheckError,
      handleRegister,
      isPhoneValid,
      isEmailValid,
      formatPhoneInput,
      onPhoneInput,
    };
  },
});
</script>

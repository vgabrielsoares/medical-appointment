<template>
  <div class="min-h-screen bg-gray-50 dark:bg-gray-900">
    <div class="app-container py-6 sm:py-8">
      <!-- Header Section -->
      <div class="mb-8">
        <div
          class="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4"
        >
          <div>
            <h1
              class="text-2xl sm:text-3xl font-bold text-gray-900 dark:text-white flex items-center gap-3"
            >
              <div
                class="w-10 h-10 bg-gradient-to-r from-blue-600 to-purple-600 rounded-xl flex items-center justify-center"
              >
                <UserCircleIcon class="w-6 h-6 text-white" />
              </div>
              Meu Perfil
            </h1>
            <p class="text-gray-600 dark:text-gray-300 mt-2">
              Gerencie suas informações pessoais
            </p>
          </div>
        </div>
      </div>

      <!-- Main Content -->
      <div class="max-w-4xl mx-auto">
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">
          <!-- Profile Card -->
          <div class="lg:col-span-1">
            <UiCard variant="elevated" size="lg">
              <div class="text-center">
                <div class="mb-6">
                  <UiAvatar
                    :name="displayName"
                    size="2xl"
                    status="online"
                    class="mx-auto"
                  />
                </div>
                <h2
                  class="text-xl font-bold text-gray-900 dark:text-white mb-1"
                >
                  {{ displayName }}
                </h2>
                <p class="text-gray-500 dark:text-gray-400 mb-2">
                  {{ auth.user?.email }}
                </p>
                <UiBadge
                  :variant="auth.isDoctor ? 'info' : 'success'"
                  class="mb-4"
                >
                  {{ auth.isDoctor ? "Médico" : "Paciente" }}
                </UiBadge>
                <div class="text-sm text-gray-500 dark:text-gray-400">
                  Membro desde {{ memberSince }}
                </div>
              </div>
            </UiCard>
          </div>

          <!-- Profile Form -->
          <div class="lg:col-span-2">
            <UiCard variant="elevated" size="lg">
              <template #header>
                <div class="flex items-center justify-between">
                  <h3
                    class="text-lg font-semibold text-gray-900 dark:text-white"
                  >
                    Informações Pessoais
                  </h3>
                  <UiButton
                    v-if="!isEditing"
                    variant="secondary"
                    size="sm"
                    @click="startEditing"
                  >
                    <PencilIcon class="w-4 h-4 mr-2" />
                    Editar
                  </UiButton>
                </div>
              </template>

              <form @submit.prevent="saveProfile" class="space-y-6">
                <!-- Nome -->
                <div>
                  <label
                    for="name"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Nome Completo
                  </label>
                  <UiInput
                    id="name"
                    v-model="profileForm.name"
                    :disabled="!isEditing"
                    placeholder="Digite seu nome completo"
                    required
                  />
                </div>

                <!-- Email -->
                <div>
                  <label
                    for="email"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Email
                  </label>
                  <UiInput
                    id="email"
                    v-model="profileForm.email"
                    type="email"
                    :disabled="!isEditing"
                    placeholder="Digite seu email"
                    required
                  />
                </div>

                <!-- Especialidade (só para médicos) -->
                <div v-if="auth.isDoctor">
                  <label
                    for="specialty"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Especialidade
                  </label>
                  <UiInput
                    id="specialty"
                    v-model="profileForm.specialty"
                    :disabled="!isEditing"
                    placeholder="Digite sua especialidade"
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
                    v-model="profileForm.phone"
                    :disabled="!isEditing"
                    placeholder="Digite seu telefone"
                  />
                </div>

                <!-- Data de Nascimento -->
                <div>
                  <label
                    for="birthDate"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Data de Nascimento
                  </label>
                  <UiInput
                    id="birthDate"
                    v-model="profileForm.birthDate"
                    type="date"
                    :disabled="!isEditing"
                    class="[&::-webkit-calendar-picker-indicator]:dark:invert"
                  />
                </div>

                <!-- Botões de Ação -->
                <div v-if="isEditing" class="flex gap-3 pt-4">
                  <UiButton
                    type="submit"
                    variant="primary"
                    :loading="isSaving"
                    class="flex-1"
                  >
                    <CheckIcon class="w-4 h-4 mr-2" />
                    Salvar Alterações
                  </UiButton>
                  <UiButton
                    type="button"
                    variant="secondary"
                    @click="cancelEditing"
                    :disabled="isSaving"
                  >
                    <XMarkIcon class="w-4 h-4 mr-2" />
                    Cancelar
                  </UiButton>
                </div>
              </form>
            </UiCard>

            <!-- Alteração de Senha -->
            <UiCard variant="elevated" size="lg" class="mt-6">
              <template #header>
                <h3 class="text-lg font-semibold text-gray-900 dark:text-white">
                  Alterar Senha
                </h3>
              </template>

              <form @submit.prevent="changePassword" class="space-y-6">
                <div>
                  <label
                    for="currentPassword"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Senha Atual
                  </label>
                  <UiInput
                    id="currentPassword"
                    v-model="passwordForm.currentPassword"
                    type="password"
                    placeholder="Digite sua senha atual"
                    required
                  />
                </div>

                <div>
                  <label
                    for="newPassword"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Nova Senha
                  </label>
                  <UiInput
                    id="newPassword"
                    v-model="passwordForm.newPassword"
                    type="password"
                    placeholder="Digite sua nova senha"
                    required
                  />
                </div>

                <div>
                  <label
                    for="confirmPassword"
                    class="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2"
                  >
                    Confirmar Nova Senha
                  </label>
                  <UiInput
                    id="confirmPassword"
                    v-model="passwordForm.confirmPassword"
                    type="password"
                    placeholder="Confirme sua nova senha"
                    required
                  />
                </div>

                <UiButton
                  type="submit"
                  variant="primary"
                  :loading="isChangingPassword"
                  class="w-full"
                >
                  <KeyIcon class="w-4 h-4 mr-2" />
                  Alterar Senha
                </UiButton>
              </form>
            </UiCard>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script lang="ts">
import { defineComponent, ref, computed, onMounted } from "vue";
import {
  UserCircleIcon,
  PencilIcon,
  CheckIcon,
  XMarkIcon,
  KeyIcon,
} from "@heroicons/vue/24/solid";
import { UiCard, UiButton, UiInput, UiAvatar, UiBadge } from "../components/ui";
import { useAuthStore } from "../stores/auth";
import api from "../services/api";

interface ProfileForm {
  name: string;
  email: string;
  specialty?: string;
  phone?: string;
  birthDate?: string;
}

interface PasswordForm {
  currentPassword: string;
  newPassword: string;
  confirmPassword: string;
}

export default defineComponent({
  name: "Profile",
  components: {
    UiCard,
    UiButton,
    UiInput,
    UiAvatar,
    UiBadge,
    UserCircleIcon,
    PencilIcon,
    CheckIcon,
    XMarkIcon,
    KeyIcon,
  },
  setup() {
    const auth = useAuthStore();

    const isEditing = ref(false);
    const isSaving = ref(false);
    const isChangingPassword = ref(false);

    const profileForm = ref<ProfileForm>({
      name: "",
      email: "",
      specialty: "",
      phone: "",
      birthDate: "",
    });

    const passwordForm = ref<PasswordForm>({
      currentPassword: "",
      newPassword: "",
      confirmPassword: "",
    });

    const originalProfileData = ref<ProfileForm>({
      name: "",
      email: "",
      specialty: "",
      phone: "",
      birthDate: "",
    });

    const displayName = computed(() => auth.displayName);

    const memberSince = computed(() => {
      // Placeholder
      return "Janeiro 2025";
    });

    const startEditing = () => {
      isEditing.value = true;
      // Salvar dados originais para poder cancelar
      originalProfileData.value = { ...profileForm.value };
    };

    const cancelEditing = () => {
      isEditing.value = false;
      // Restaurar dados originais
      profileForm.value = { ...originalProfileData.value };
    };

    const saveProfile = async () => {
      if (isSaving.value) return;

      try {
        isSaving.value = true;

        // TODO: Implementar chamada para API
        await api.patch("/api/users/profile", profileForm.value);

        // Atualizar dados na store
        if (auth.user) {
          auth.user.name = profileForm.value.name;
          auth.user.email = profileForm.value.email;
          auth.setUser(auth.user);
        }

        isEditing.value = false;

        // TODO: Mostrar toast de sucesso
        console.log("Perfil atualizado com sucesso");
      } catch (error) {
        console.error("Erro ao atualizar perfil:", error);
        // TODO: Mostrar toast de erro
      } finally {
        isSaving.value = false;
      }
    };

    const changePassword = async () => {
      if (isChangingPassword.value) return;

      if (
        passwordForm.value.newPassword !== passwordForm.value.confirmPassword
      ) {
        // TODO: Mostrar toast de erro
        console.error("Senhas não coincidem");
        return;
      }

      try {
        isChangingPassword.value = true;

        // TODO: Implementar chamada para API
        await api.put("/api/users/change-password", {
          currentPassword: passwordForm.value.currentPassword,
          newPassword: passwordForm.value.newPassword,
        });

        // Limpar formulário
        passwordForm.value = {
          currentPassword: "",
          newPassword: "",
          confirmPassword: "",
        };

        // TODO: Mostrar toast de sucesso
        console.log("Senha alterada com sucesso");
      } catch (error) {
        console.error("Erro ao alterar senha:", error);
        // TODO: Mostrar toast de erro
      } finally {
        isChangingPassword.value = false;
      }
    };

    const loadProfile = async () => {
      try {
        // TODO: Implementar chamada para API para carregar dados do perfil
        // const response = await api.get('/api/users/profile');
        // profileForm.value = response.data;

        // Por enquanto, usar dados da store
        if (auth.user) {
          profileForm.value.name = auth.user.name || "";
          profileForm.value.email = auth.user.email || "";
        }
      } catch (error) {
        console.error("Erro ao carregar perfil:", error);
      }
    };

    onMounted(() => {
      loadProfile();
    });

    return {
      auth,
      isEditing,
      isSaving,
      isChangingPassword,
      profileForm,
      passwordForm,
      displayName,
      memberSince,
      startEditing,
      cancelEditing,
      saveProfile,
      changePassword,
    };
  },
});
</script>

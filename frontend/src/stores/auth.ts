import { defineStore } from "pinia";
import api from "../services/api";

/**
 * Store de autenticação (Pinia)
 * Responsabilidade: guardar token JWT e configurar header Authorization na instância axios.
 * Pré-condição: token JWT recebido do backend (endpoint /api/auth/login).
 */
export const useAuthStore = defineStore("auth", {
  state: () => ({ token: "" as string }),
  actions: {
    /**
     * Define o token localmente e atualiza o header Authorization do axios.
     * @param t token JWT (string) ou string vazia para limpar.
     */
    setToken(t: string) {
      this.token = t;
      api.defaults.headers.common.Authorization = t ? `Bearer ${t}` : "";
    },
  },
});

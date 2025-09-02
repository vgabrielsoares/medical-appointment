import { defineStore } from "pinia";
import api, { registerAuthHandlers } from "../services/api";

/**
 * Tipagem do usuário autenticado retornado pelo backend.
 */
export interface AuthUser {
  id: string;
  role: string; // 'ROLE_DOCTOR' | 'ROLE_PATIENT'
  name?: string; // nome do médico ou paciente
  email?: string; // email do usuário
}

/**
 * Store de autenticação (Pinia)
 * Responsabilidade:
 * - armazenar token e dados do usuário autenticado
 * - fornecer actions para login/logout
 * - expor helper getToken usado pelos interceptors em `services/api`
 *
 * A store mantém o token em localStorage para persistência simples entre reloads.
 */
export const useAuthStore = defineStore("auth", {
  state: () => ({
    token: (localStorage.getItem("ma_token") || "") as string,
    user:
      (JSON.parse(
        localStorage.getItem("ma_user") || "null"
      ) as AuthUser | null) || null,
  }),
  getters: {
    isAuthenticated: (state) => !!state.token,
    getUser: (state) => state.user,
    isDoctor: (state) => state.user?.role === "ROLE_DOCTOR",
    isPatient: (state) => state.user?.role === "ROLE_PATIENT",
    displayName: (state) => state.user?.name || state.user?.id || "Usuário",
  },
  actions: {
    setToken(t: string) {
      this.token = t;
      if (t) localStorage.setItem("ma_token", t);
      else localStorage.removeItem("ma_token");
    },
    setUser(u: AuthUser | null) {
      this.user = u;
      if (u) localStorage.setItem("ma_user", JSON.stringify(u));
      else localStorage.removeItem("ma_user");
    },
    getToken() {
      return this.token || localStorage.getItem("ma_token") || "";
    },
    logout() {
      this.setToken("");
      this.setUser(null);
    },

    /**
     * Realiza login contra o backend e popula token + user na store.
     * @returns AuthUser em caso de sucesso
     * @throws erro do axios em caso de falha
     */
    async login(email: string, password: string) {
      const res = await api.post("/auth/login", { email, password });
      // contrato esperado: { token: string, user?: { id, role } }
      const token = res.data?.token as string | undefined;
      if (!token) throw new Error("invalid auth response");

      // salva o token primeiro
      this.setToken(token);

      // se o backend não devolveu o objeto `user`, extraímos do JWT (claim `sub` e `role`)
      let user = res.data?.user as AuthUser | undefined;
      if (!user) {
        try {
          const parts = token.split(".");
          if (parts.length >= 2) {
            const payload = JSON.parse(atob(parts[1]));
            user = { id: payload.sub, role: payload.role } as AuthUser;
          }
        } catch (e) {
          // falha ao decodificar token — manter user como null
          user = undefined;
        }
      }

      this.setUser(user || null);
      return user || null;
    },
  },
});

// Registrar handlers de auth para o módulo api.
// Mantemos a responsabilidade do redirecionamento fora da store, o consumidor passa o onUnauthorized.
export function attachDefaultAuthHandlers(onUnauthorized: () => void) {
  const store = useAuthStore();
  registerAuthHandlers({
    getToken: () => store.getToken(),
    onUnauthorized: () => {
      store.logout();
      onUnauthorized();
    },
  });
}

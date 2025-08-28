import { defineStore } from "pinia";
import { registerAuthHandlers } from "../services/api";

/**
 * Store de autenticação (Pinia)
 * Responsabilidade: gerenciar token JWT em memória/localStorage e expor helpers.
 * A store não manipula diretamente headers do axios, o `services/api` registra
 * interceptors que consultam o token via getToken.
 */
export const useAuthStore = defineStore("auth", {
  state: () => ({ token: (localStorage.getItem("ma_token") || "") as string }),
  actions: {
    setToken(t: string) {
      this.token = t;
      if (t) localStorage.setItem("ma_token", t);
      else localStorage.removeItem("ma_token");
    },
    getToken() {
      return this.token || localStorage.getItem("ma_token") || "";
    },
    logout() {
      this.setToken("");
    },
  },
});

// Registrar handlers de auth para o módulo api.
// Fazemos aqui para manter a store como fonte da verdade do token,
// e delegar o comportamento de 401 (logout/redirecionamento) para quem usar a app.
// A store exportada não sabe como redirecionar, isso será registrado em `main.ts`.
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

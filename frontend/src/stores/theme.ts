import { defineStore } from "pinia";

export type Theme = "light" | "dark" | "system";

const STORAGE_KEY = "ma:theme";

/**
 * Theme store responsável por gerenciar o tema (light/dark/system) e persistência.
 * - Guarda preferência do usuário em localStorage
 * - Aplica classe `dark` no elemento root quando apropriado
 */
export const useThemeStore = defineStore("theme", {
  state: () => ({
    pref: (localStorage.getItem(STORAGE_KEY) as Theme) || ("system" as Theme),
  }),
  getters: {
    isDark(state): boolean {
      if (state.pref === "system") {
        return (
          window.matchMedia &&
          window.matchMedia("(prefers-color-scheme: dark)").matches
        );
      }
      return state.pref === "dark";
    },
  },
  actions: {
    set(pref: Theme) {
      this.pref = pref;
      try {
        localStorage.setItem(STORAGE_KEY, pref);
      } catch (e) {
        // se localStorage não estiver disponível, ignoramos
      }
      this.apply();
    },
    toggle() {
      const next = this.isDark ? "light" : "dark";
      this.set(next as Theme);
    },
    apply() {
      const root = document.documentElement;
      if (this.isDark) root.classList.add("dark");
      else root.classList.remove("dark");
    },
    init() {
      // aplica inicialmente sem alterar preferência salvo
      this.apply();

      // observa mudança do sistema se modo 'system' for selecionado
      if (window.matchMedia) {
        const mq = window.matchMedia("(prefers-color-scheme: dark)");
        const handler = () => {
          if (this.pref === "system") this.apply();
        };
        mq.addEventListener?.("change", handler);
      }
    },
  },
});

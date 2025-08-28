import { createApp } from "vue";
import { createPinia } from "pinia";
import App from "./App.vue";
import router from "./router";

import "./styles.css";

// Ponto de entrada da aplicação frontend
// Registra Pinia (store) e o Vue Router
const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount("#app");

// registra handlers de autenticação que anexam token e tratam 401.
// import dinamicamente para evitar ciclo de import entre store <-> api
import("./stores/auth").then(({ attachDefaultAuthHandlers }) => {
  // onUnauthorized: limpa estado e redireciona para /login
  attachDefaultAuthHandlers(() => {
    // Se o router já estiver pronto, usamos push. caso contrário, use location
    try {
      router.push({ name: "login" });
    } catch (e) {
      window.location.href = "/#/login";
    }
  });
});

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

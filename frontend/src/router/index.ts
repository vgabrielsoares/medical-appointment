import { createRouter, createWebHistory } from "vue-router";
import Home from "../pages/Home.vue";
import Login from "../pages/Login.vue";
import Doctor from "../pages/Doctor.vue";
import DoctorSlots from "../pages/DoctorSlots.vue";
import Patient from "../pages/Patient.vue";

// Definição das rotas principais da aplicação
const routes = [
  { path: "/", name: "home", component: Home },
  { path: "/login", name: "login", component: Login },
  { path: "/doctor", name: "doctor", component: Doctor },
  { path: "/doctor/slots", name: "doctor-slots", component: DoctorSlots },
  { path: "/patient", name: "patient", component: Patient },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;

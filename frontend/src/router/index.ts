import { createRouter, createWebHistory } from "vue-router";
import { useAuthStore } from "../stores/auth";
import Home from "../pages/Home.vue";
import Login from "../pages/Login.vue";
import Doctor from "../pages/Doctor.vue";
import DoctorSlots from "../pages/DoctorSlots.vue";
import Patient from "../pages/Patient.vue";

// Definição das rotas principais da aplicação
const routes = [
  { path: "/", name: "home", component: Home },
  { path: "/login", name: "login", component: Login },
  {
    path: "/doctor",
    name: "doctor",
    component: Doctor,
    meta: { requiresAuth: true, requiresRole: "ROLE_DOCTOR" },
  },
  {
    path: "/doctor/slots",
    name: "doctor-slots",
    component: DoctorSlots,
    meta: { requiresAuth: true, requiresRole: "ROLE_DOCTOR" },
  },
  {
    path: "/patient",
    name: "patient",
    component: Patient,
    meta: { requiresAuth: true, requiresRole: "ROLE_PATIENT" },
  },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// Guard de autenticação e autorização
router.beforeEach((to, from, next) => {
  const authStore = useAuthStore();

  // Se a rota requer autenticação
  if (to.meta.requiresAuth && !authStore.isAuthenticated) {
    next("/login");
    return;
  }

  // Se a rota requer uma role específica
  if (to.meta.requiresRole && authStore.user?.role !== to.meta.requiresRole) {
    // Redirecionar para a área apropriada baseada na role do usuário
    if (authStore.isDoctor) {
      next("/doctor");
    } else if (authStore.isPatient) {
      next("/patient");
    } else {
      next("/login");
    }
    return;
  }

  next();
});

export default router;

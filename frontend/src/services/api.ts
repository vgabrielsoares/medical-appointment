import axios from "axios";

// Base URL da API. pode ser sobrescrito por variável de ambiente VITE_API_BASE_URL
const baseURL =
  import.meta.env.VITE_API_BASE_URL || "http://localhost:8080/api";

// Instância axios centralizada para reutilização nas chamadas à API
const api = axios.create({ baseURL });

// Nota: interceptors (p.ex. anexar token Authorization ou tratar 401) são adicionados pela store
// de autenticação para manter separação de responsabilidades e facilitar testes.

export default api;

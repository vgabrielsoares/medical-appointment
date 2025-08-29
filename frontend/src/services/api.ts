import axios from "axios";

// Base URL da API. pode ser sobrescrito por variável de ambiente VITE_API_BASE_URL
// cast para any para evitar erro em tempo de compilação quando o ambiente não tiver tipagem de ImportMeta
const baseURL =
  (import.meta as any)?.env?.VITE_API_BASE_URL || "http://localhost:8080/api";

// Instância axios centralizada para reutilização nas chamadas à API
const api = axios.create({ baseURL });

// Expor função para registrar um handler de autenticação.
// Isso evita dependência direta entre a store (Pinia) e este módulo,
// permitindo testes mais fáceis e evitando ciclos de import.
// - getToken(): deve retornar a string do token atual (ou '')
// - onUnauthorized(): chamado quando uma resposta 401 é detectada (p.ex. limpar store e redirecionar)
export function registerAuthHandlers({
  getToken,
  onUnauthorized,
}: {
  getToken: () => string;
  onUnauthorized: () => void;
}) {
  // Request interceptor: anexa Authorization quando houver token
  api.interceptors.request.use(
    (config) => {
      try {
        const token = getToken();
        if (token) {
          // os tipos de headers do axios podem variar. fazemos cast seguro
          const headers: any = config.headers || {};
          headers.Authorization = `Bearer ${token}`;
          config.headers = headers;
        }
      } catch (e) {
        // se getToken lançar, não bloquear a requisição, apenas seguir sem header
      }
      return config;
    },
    (err) => Promise.reject(err)
  );

  // Response interceptor: captura 401 e delega ação (logout/redirecionamento)
  api.interceptors.response.use(
    (res) => res,
    (error) => {
      const status = error?.response?.status;
      if (status === 401) {
        try {
          onUnauthorized();
        } catch (e) {
          // engole erros do handler para não mascarar o erro original
        }
      }
      return Promise.reject(error);
    }
  );
}

export default api;

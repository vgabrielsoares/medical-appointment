// Mock simples de api para testes. Exporta uma função post que pode ser stubada.
export default {
  post: async (url: string, body: any) => {
    // default fallback
    return { data: {} };
  },
};

export const registerAuthHandlers = () => {};

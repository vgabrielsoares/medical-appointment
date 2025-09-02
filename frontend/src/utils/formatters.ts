// Helpers para validação e máscara de email/telefone
// Número máximo de dígitos permitidos para telefone (DD + número, sem símbolos)
export const PHONE_MAX_DIGITS = 11;
export const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
export function isEmailValid(email: string) {
  return emailRegex.test((email || "").trim());
}

// Remove tudo que não for dígito e retorna no máximo `max` dígitos.
export function sanitizeDigits(input: string, max = PHONE_MAX_DIGITS) {
  if (!input) return "";
  const digits = input.replace(/\D+/g, "");
  // manter os primeiros `max` dígitos — evita manter código de país longo
  return digits.length > max ? digits.slice(0, max) : digits;
}

// Formata progressivamente enquanto o usuário digita.
// Recebe o valor bruto (pode conter caracteres) e retorna a string mascarada.
export function formatPhoneProgressive(value: string) {
  const digits = sanitizeDigits(value, PHONE_MAX_DIGITS);
  if (!digits) return "";
  if (digits.length <= 2) return `(${digits}`;
  if (digits.length <= 6) return `(${digits.slice(0, 2)}) ${digits.slice(2)}`;
  if (digits.length <= 10)
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(6)}`;
  return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
}

// Formata no blur para saída consistente
export function formatPhoneFinal(value: string) {
  const digits = sanitizeDigits(value, PHONE_MAX_DIGITS);
  if (!digits) return "";
  if (digits.length <= 10) {
    return `(${digits.slice(0, 2)}) ${digits.slice(2, 6)}-${digits.slice(
      6,
      10
    )}`;
  }
  return `(${digits.slice(0, 2)}) ${digits.slice(2, 7)}-${digits.slice(7, 11)}`;
}

// Validação final de telefone
export function isPhoneValid(phone: string) {
  const p = (phone || "").trim();
  if (!p) return true; // opcional
  const re = /^\(\d{2}\) \d{4,5}-\d{4}$/;
  return re.test(p);
}

export default {
  isEmailValid,
  sanitizeDigits,
  formatPhoneProgressive,
  formatPhoneFinal,
  PHONE_MAX_DIGITS,
  isPhoneValid,
};

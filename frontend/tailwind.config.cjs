/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["./index.html", "./src/**/*.{vue,js,ts,jsx,tsx}"],
  darkMode: "class",
  theme: {
    extend: {
      colors: {
        primary: "var(--color-primary)",
        "primary-600": "var(--color-primary-600)",
        accent: "var(--color-accent)",
        muted: "var(--color-muted)",
        bg: "var(--color-bg)",
        card: "var(--color-card)",
      },
      fontFamily: {
        sans: ["Inter", "ui-sans-serif", "system-ui"],
      },
      spacing: {
        "container-padding": "1rem",
      },
      borderRadius: {
        sm: "0.375rem",
        md: "0.5rem",
        lg: "0.75rem",
      },
      boxShadow: {
        subtle: "var(--shadow-subtle)",
      },
      maxWidth: {
        app: "var(--size-container-max)",
      },
    },
  },
  plugins: [],
};

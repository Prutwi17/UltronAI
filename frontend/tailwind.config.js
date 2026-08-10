/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          dark: "#0b0f19",
          card: "#111827",
          accent: "#7c3aed",
          hover: "#6d28d9",
        }
      }
    },
  },
  plugins: [],
}

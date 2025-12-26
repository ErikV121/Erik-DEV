/** @type {import('tailwindcss').Config} */
module.exports = {
  content: ["../resources/templates/**/*.{html,js}"],
    theme: {
        extend: {
            fontFamily: {
                'serif': ['Georgia', 'serif'],
            }
        }
    },
  plugins: [],
}


/** @type {import('tailwindcss').Config} */
export default {
  content: [
    './index.html',
    './src/**/*.{js,ts,vue}',
  ],
  plugins: [
    require('@tailwindcss/typography'),
  ],
}

import type { Config } from 'tailwindcss'

export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        black: {
          900: '#1A1A1A',
          800: '#333333',
          700: '#4D4D4D',
          500: '#808080',
          100: '#E5E5E5',
          50: '#F2F2F2',
        },
        brand: {
          blue: '#007AFF',
          navy: '#4667AC',
          logo: '#343A40',
        },
      },
      fontFamily: {
        poppins: ['Poppins', 'sans-serif'],
        roboto: ['Roboto', 'sans-serif'],
        logo: ['Inter', 'sans-serif'],
      },
      letterSpacing: {
        logo: '-0.96px',
        wide03: '0.3px',
      },
    },
  },
  plugins: [],
} satisfies Config

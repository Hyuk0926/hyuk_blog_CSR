/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./index.html",
    "./src/**/*.{vue,js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      typography: {
        DEFAULT: {
          css: {
            maxWidth: 'none',
            color: '#374151',
            a: {
              color: '#3b82f6',
              '&:hover': {
                color: '#2563eb',
              },
            },
            h1: {
              color: '#111827',
            },
            h2: {
              color: '#111827',
            },
            h3: {
              color: '#111827',
            },
            h4: {
              color: '#111827',
            },
            h5: {
              color: '#111827',
            },
            h6: {
              color: '#111827',
            },
            strong: {
              color: '#111827',
            },
            code: {
              color: '#111827',
              backgroundColor: '#f3f4f6',
              padding: '0.125rem 0.25rem',
              borderRadius: '0.25rem',
              fontWeight: '400',
            },
            pre: {
              backgroundColor: '#1f2937',
              color: '#f9fafb',
              overflow: 'auto',
              borderRadius: '0.5rem',
              padding: '1rem',
            },
            'pre code': {
              backgroundColor: 'transparent',
              padding: '0',
              borderRadius: '0',
              color: 'inherit',
            },
            blockquote: {
              borderLeftColor: '#d1d5db',
              backgroundColor: '#f9fafb',
              padding: '1rem',
              borderRadius: '0.5rem',
            },
            table: {
              borderCollapse: 'collapse',
              width: '100%',
            },
            th: {
              backgroundColor: '#f9fafb',
              borderBottom: '1px solid #d1d5db',
              padding: '0.5rem 0.75rem',
              textAlign: 'left',
            },
            td: {
              borderBottom: '1px solid #e5e7eb',
              padding: '0.5rem 0.75rem',
            },
          },
        },
        invert: {
          css: {
            color: '#d1d5db',
            a: {
              color: '#60a5fa',
              '&:hover': {
                color: '#93c5fd',
              },
            },
            h1: {
              color: '#f9fafb',
            },
            h2: {
              color: '#f9fafb',
            },
            h3: {
              color: '#f9fafb',
            },
            h4: {
              color: '#f9fafb',
            },
            h5: {
              color: '#f9fafb',
            },
            h6: {
              color: '#f9fafb',
            },
            strong: {
              color: '#f9fafb',
            },
            code: {
              color: '#f9fafb',
              backgroundColor: '#374151',
            },
            pre: {
              backgroundColor: '#111827',
              color: '#f9fafb',
            },
            blockquote: {
              borderLeftColor: '#4b5563',
              backgroundColor: '#374151',
            },
            th: {
              backgroundColor: '#374151',
              borderBottom: '1px solid #4b5563',
            },
            td: {
              borderBottom: '1px solid #4b5563',
            },
          },
        },
      },
    },
  },
  plugins: [
    require('@tailwindcss/typography'),
  ],
}

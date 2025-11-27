// Tailwind CSS Configuration for eTMA Handler
// High-contrast, accessible design following WCAG 2.3 AAA guidelines

const plugin = require("tailwindcss/plugin")

module.exports = {
  content: [
    "./js/**/*.js",
    "../lib/etma_handler_web.ex",
    "../lib/etma_handler_web/**/*.*ex"
  ],
  theme: {
    extend: {
      colors: {
        // The "Slate" palette for high-contrast UI backgrounds
        slate: {
          50: '#f8fafc',
          100: '#f1f5f9',
          200: '#e2e8f0',
          300: '#cbd5e1',
          400: '#94a3b8',
          500: '#64748b',
          600: '#475569',
          700: '#334155',
          800: '#1e293b',
          900: '#0f172a',
        },
        // Open University brand blue (approximate)
        ou_blue: {
          DEFAULT: '#1d4ed8',
          light: '#3b82f6',
          dark: '#1e40af',
        },
        // Status colors
        status: {
          draft: '#eab308',
          sent: '#22c55e',
          unmarked: '#64748b',
          error: '#ef4444',
        }
      },
      fontFamily: {
        // System font stack for UI (fast loading)
        sans: [
          'Inter',
          'ui-sans-serif',
          'system-ui',
          '-apple-system',
          'BlinkMacSystemFont',
          'Segoe UI',
          'Roboto',
          'Helvetica Neue',
          'Arial',
          'sans-serif',
        ],
        // Monospace for marks input (clear numbers)
        mono: [
          'JetBrains Mono',
          'ui-monospace',
          'SFMono-Regular',
          'Menlo',
          'Monaco',
          'Consolas',
          'Liberation Mono',
          'Courier New',
          'monospace',
        ],
        // Serif for document preview (academic feel)
        serif: [
          'Merriweather',
          'ui-serif',
          'Georgia',
          'Cambria',
          'Times New Roman',
          'Times',
          'serif',
        ],
      },
      // Enhanced shadow for "paper" effect
      boxShadow: {
        'paper': '0 1px 3px 0 rgba(0, 0, 0, 0.1), 0 1px 2px -1px rgba(0, 0, 0, 0.1)',
        'paper-lg': '0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -4px rgba(0, 0, 0, 0.1)',
      },
      // Animation for save indicator
      animation: {
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
      },
      // Ensure minimum tap target size (44px) for accessibility
      minWidth: {
        'tap': '44px',
      },
      minHeight: {
        'tap': '44px',
      },
    },
  },
  plugins: [
    // Official Tailwind Forms plugin
    require("@tailwindcss/forms"),

    // Phoenix-specific variants for LiveView states
    plugin(({ addVariant }) => {
      addVariant("phx-no-feedback", [".phx-no-feedback&", ".phx-no-feedback &"])
      addVariant("phx-click-loading", [".phx-click-loading&", ".phx-click-loading &"])
      addVariant("phx-submit-loading", [".phx-submit-loading&", ".phx-submit-loading &"])
      addVariant("phx-change-loading", [".phx-change-loading&", ".phx-change-loading &"])
    }),

    // Custom utilities for eTMA
    plugin(({ addUtilities }) => {
      addUtilities({
        '.scrollbar-thin': {
          'scrollbar-width': 'thin',
        },
        '.scrollbar-none': {
          'scrollbar-width': 'none',
          '&::-webkit-scrollbar': {
            display: 'none',
          },
        },
      })
    }),
  ],
}

tailwind.config = {
    darkMode: 'class',
    theme: {
        extend: {
            colors: {
                'custom-light': '#706D54',
                'custom-medium': '#A08963',
                'custom-dark': '#C9B194',
                'custom-darker': '#3F4F44',
            },
            animation: {
                'fade-in': 'fadeIn 0.5s ease-in-out',
                'slide-up': 'slideUp 0.3s ease-out',
                'pulse-subtle': 'pulse 2s cubic-bezier(0.4, 0, 0.6, 1) infinite',
            },
            keyframes: {
                fadeIn: {
                    '0%': { opacity: '0' },
                    '100%': { opacity: '1' }
                },
                slideUp: {
                    '0%': { transform: 'translateY(10px)', opacity: '0' },
                    '100%': { transform: 'translateY(0)', opacity: '1' }
                }
            }
        }
    }
}

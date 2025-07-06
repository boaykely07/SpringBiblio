tailwind.config = {
    darkMode: 'class',
    theme: {
        extend: {
            colors: {
                'custom-light': '#F6F8D5',
                'custom-medium': '#98D2C0',
                'custom-dark': '#4F959D',
                'custom-darker': '#205781',
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

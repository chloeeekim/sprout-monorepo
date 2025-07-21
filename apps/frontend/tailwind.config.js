/** @type {import('tailwindcss').Config} */
export default {
    content: [
        "./index.html",
        "./src/**/*.{js,ts,jsx,tsx}",
    ],
    theme: {
        extend: {
            colors: {
                // 배경색
                'sprout-background': '#F8F5F2',
                // 기본 텍스트
                'sprout-text': '#333333',
                // 강조색
                'sprout-accent': '#5A8B72',
                // 강조색 dark
                'sprout-accent-dark': '#4a725e',
            },
        },
    },
    plugins: [],
}
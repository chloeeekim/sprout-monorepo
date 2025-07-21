import React from "react";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: 'primary' | 'secondary';
}

const Button: React.FC<ButtonProps> = ({ children, variant = 'primary', className, ...props}) => {
    const baseStyle = "font-bold py-2 px-4 rounded focus:outline-none focus:shadow-outline";
    const primaryStyle = "bg-sprout-accent hover:bg-sprout-accent-dark text-white";
    const secondaryStyle = "bg-gray-200 hover:bg-gray-300 text-sprout-text";
    const style = variant === 'primary' ? primaryStyle : secondaryStyle;

    return (
        <button className={`${baseStyle} ${style} ${className || ''}`}
                {...props}
        >
            {children}
        </button>
    );
};

export default Button;
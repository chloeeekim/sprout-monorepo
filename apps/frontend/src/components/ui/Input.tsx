import React from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
    label?: string;
}

const Input: React.FC<InputProps> = ({label, id, ...props}) => {
    const inputId = id || props.name;
    return (
        <div className="mb-4">
            {label && (
                <label htmlFor={inputId} className="block text-sprout-text text-sm font-bold mb-2">
                    {label}
                </label>
            )}
            <input id={inputId} className="shadow appearance-none border rounded w-full py-2 px-3 text-sprout-text leading-tight
            focus:outline-none focus:shadow-outline focus:border-sprout-accent"
                   {...props}
            />
        </div>
    );
};

export default Input;
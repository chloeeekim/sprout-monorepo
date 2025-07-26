import React from "react";

interface AuthLayoutProps {
    children: React.ReactNode;
    title: String;
}

const AuthLayout: React.FC<AuthLayoutProps> = ({ children, title }) => {
    return (
        <div className="min-h-screen flex items-center justify-center bg-sprout-background p-4">
            <div className="bg-white p-8 rounded-lg shadow-lg max-w-md w-full">
                <h2 className="text-2xl font-bold text-sprout-text text-center mb-6">
                    {title}
                </h2>
                {children}
            </div>
        </div>
    );
};

export default AuthLayout;
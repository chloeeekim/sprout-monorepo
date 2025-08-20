import React from "react";

interface TopBarProps {
    children: React.ReactNode;
}

const TopBar: React.FC<TopBarProps> = ({ children }) => {
    return (
        <div className="sticky top-0 h-10 w-full flex flex-row justify-between items-center px-4">
            {children}
        </div>
    );
};

export default TopBar;
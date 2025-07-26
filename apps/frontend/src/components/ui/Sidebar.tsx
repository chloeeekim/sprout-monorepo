import React from "react";
import UserMenu from "./UserMenu";
import NavMenu from "./NavMenu";
import NewNoteButton from "./NewNoteButton";

const Sidebar: React.FC = () => {
    return (
        <div className="h-screen w-64 bg-gray-50 border-r border-gray-200 flex flex-col p-2">
            <div className="flex-shrink-0">
                <UserMenu />
            </div>
            <nav className="flex-grow">
                <NavMenu />
            </nav>
            <div className="flex-shrink-0 pb-2">
                <NewNoteButton />
            </div>
        </div>
    );
};

export default Sidebar;
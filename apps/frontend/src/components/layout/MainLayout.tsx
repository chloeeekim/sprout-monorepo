import React, {useState} from "react";
import Sidebar from "../ui/Sidebar";
import SearchModal from "../ui/SearchModal";

interface MainLayoutProps {
    children: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
    const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
    const openSearchModal = () => setIsSearchModalOpen(true);
    const closeSearchModal = () => setIsSearchModalOpen(false);

    return (
        <div className="flex h-screen bg-white text-gry-800">
            <Sidebar onSearchClick={openSearchModal} />
            <main className="flex-1 overflow-y-auto">
                <div className="p-8">
                    {children}
                </div>
            </main>
            <SearchModal isOpen={isSearchModalOpen} onClose={closeSearchModal} />
        </div>
    );
};

export default MainLayout;
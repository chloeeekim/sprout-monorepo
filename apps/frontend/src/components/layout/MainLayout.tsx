import React from "react";

interface MainLayoutProps {
    children: React.ReactNode;
}

const MainLayout: React.FC<MainLayoutProps> = ({ children }) => {
    return (
        <div className="min-h-screen bg-sprout-background text-sprout-text">
            {/* 상단 네비게이션 바 */}
            <header className="bg-white shadow-sm">
                <div className="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex justify-between items-center">
                    <h1 className="text-2xl font-bold text-sprout-accent">Sprout</h1>
                    {/* TODO: 사용자 프로필, 로그아웃 버튼 등 */}
                </div>
            </header>

            {/* 메인 컨텐츠 */}
            <main>
                <div className="max-w-7xl mx-auto py-6 sm:px-6 lg:px-8">
                    {children}
                </div>
            </main>
        </div>
    );
};

export default MainLayout;
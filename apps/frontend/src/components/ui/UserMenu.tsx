import React from "react";

const UserMenu: React.FC = () => {
    // TODO: 실제 사용자 정보로 교체
    const userName = 'Username';

    return (
        <div className="p-2 mb-4">
            <div className="flex items-center p-2 rounded-lg hover:bg-gray-100 cursor-pointer">
                {/* TODO: 실제 사용자 프로필로 교체 */}
                <div className="w-6 h-6 bg-green-200 rounded-full flex items-center justify-center mr-2 flex-shirink-0">
                    <span className="text-sm font-bold text-green-700">
                        {userName.charAt(0)}
                    </span>
                </div>
                <span className="font-semibold text-sm text-sprout-text block">
                    {userName}'s Sprout
                </span>
            </div>
        </div>
    );
};

export default UserMenu;
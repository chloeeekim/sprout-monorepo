import React from "react";
import { Search, StickyNote, GitFork, Shuffle, Tag } from "lucide-react";
import { useNavigate } from "react-router-dom";

interface NavMenuProps {
    onSearchClick: () => void;
}

const NavMenu: React.FC<NavMenuProps> = ({ onSearchClick }) => {
    const navigate = useNavigate();

    const onAllNotesClick = () => {
        navigate("/notes");
    }

    // TODO: 실제 라우팅 구현
    const menuItems = [
        { icon: <Search size={16} className="text-gray-500" />, name: "검색", action: onSearchClick },
        { icon: <StickyNote size={16} className="text-gray-500" />, name: "모든 노트", action: onAllNotesClick},
        { icon: <GitFork size={16} className="text-gray-500" />, name: "지식 그래프" },
        { icon: <Shuffle size={16} className="text-gray-500" />, name: "랜덤 노트 탐색" },
    ];

    return (
        <div className="px-2">
            {menuItems.map((item, index) => (
                <button key={index} onClick={item.action} className="flex items-center p-2 text-sm w-full cursor-pointer text-gray-600 rounded-lg hover:bg-gray-100">
                    <div className="mr-3">{item.icon}</div>
                    <span>{item.name}</span>
                </button>
            ))}
            <div className="mt-6">
                <h3 className="px-2 mb-2 text-xs font-semibold text-gray-400">TAGS</h3>
                {/* TODO: 태그 목록 동적 렌더링 */}
                <a href="#" className="flex items-center p-2 text-sm text-gray-600 rounded-lg hover:bg-gray-100">
                    <div className="mr-3">
                        <Tag size={16} className="text-gray-500" />
                    </div>
                    <span>#프로젝트</span>
                </a>
                <a href="#" className="flex items-center p-2 text-sm text-gray-600 rounded-lg hover:bg-gray-100">
                    <div className="mr-3">
                        <Tag size={16} className="text-gray-500" />
                    </div>
                    <span>#아이디어</span>
                </a>
            </div>
        </div>
    );
};

export default NavMenu;
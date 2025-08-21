import React, { useState, useEffect } from "react";
import { Search, StickyNote, GitFork, Shuffle, Tag } from "lucide-react";
import {Link, useNavigate} from "react-router-dom";
import apiClient from "../../lib/apiClient";
import { TagListResponse } from "@sprout/shared-types/tag";
import {FolderList} from "./FolderList";
import { useFolderStore } from "../../stores/folderStore";
import {TagList} from "@/components/ui/TagList";

interface NavMenuProps {
    onSearchClick: () => void;
}

const NavMenu: React.FC<NavMenuProps> = ({ onSearchClick }) => {
    const [tags, setTags] = useState<TagListResponse[]>([]);
    const navigate = useNavigate();
    const { unselectFolder } = useFolderStore();

    const onAllNotesClick = () => {
        unselectFolder();
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
                <FolderList />
            </div>
            <div className="mt-6">
                <TagList />
            </div>
        </div>
    );
};

export default NavMenu;
import React, { useState, useEffect } from "react";
import { Search, StickyNote, GitFork, Shuffle, Tag } from "lucide-react";
import {Link, useNavigate} from "react-router-dom";
import apiClient from "../../lib/apiClient";
import { TagListResponse } from "@sprout/shared-types/tag";

interface NavMenuProps {
    onSearchClick: () => void;
}

const NavMenu: React.FC<NavMenuProps> = ({ onSearchClick }) => {
    const [tags, setTags] = useState<TagListResponse[]>([]);
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

    useEffect(() => {
        const fetchTags = async () => {
            const response = await apiClient.get(`/api/tags`);
            setTags(response.data.data);
        };

        fetchTags();
    }, []);

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
                {tags.map((tag) => (
                    <a href={`/notes/tags/${tag.tagName}`} key={tag.id} className="flex items-center p-2 text-sm text-gray-600 rounded-lg hover:bg-gray-100">
                        <div className="mr-3">
                            <Tag size={16} className="text-gray-500" />
                        </div>
                        <span>#{tag.tagName}</span>
                    </a>
                ))}
            </div>
        </div>
    );
};

export default NavMenu;
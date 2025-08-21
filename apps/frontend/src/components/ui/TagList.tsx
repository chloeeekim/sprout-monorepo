import React, { useEffect, useState } from "react";
import { useTagStore } from "../../stores/tagStore";
import {Tag as TagIcon, Trash2} from "lucide-react";
import {useFolderStore} from "@/stores/folderStore";

export const TagList = () => {
    const { tags, selectedTagId, fetchTags, selectTag, removeTag } = useTagStore();
    const { unselectFolder } = useFolderStore();

    useEffect(() => {
        fetchTags();
    }, [fetchTags]);

    const handleClickTag = (id: string) => {
        unselectFolder();
        selectTag(id);
    }

    return (
        <div>
            <h3 className="px-2 mb-2 text-xs font-semibold uppercase text-gray-400">Tags</h3>
            <ul>
                {tags.map((tag) => (
                    <li key={tag.id} className={`group flex items-center rounded-md text-sm ${selectedTagId === tag.id ? 'bg-gray-100' : 'hover:bg-gray-100'}`}>
                        <div className="flex items-center justify-between w-full cursor-pointer px-2 py-1"
                             onClick={() => handleClickTag(tag.id)}>
                            <div className="flex items-center truncate">
                                <TagIcon size={16} className="mr-3 flex-shrink-0 text-gray-500" />
                                <span className="truncate text-gray-600">{tag.name}</span>
                            </div>
                            <div className="flex items-center justify-end h-7 pl-2">
                                <div className="hidden items-center opacity-0 group-hover:flex group-hover:opacity-100 transition-opacity">
                                    <button onClick={(e) => {e.stopPropagation(); removeTag(tag.id);} }
                                            className="p-1 hover:bg-gray-200 rounded cursor-pointer">
                                        <Trash2 size={16} className="text-gray-600" />
                                    </button>
                                </div>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};
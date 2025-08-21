import React, { useEffect, useState } from "react";
import { useTagStore } from "../../stores/tagStore";
import { Tag as TagIcon } from "lucide-react";
import {useFolderStore} from "@/stores/folderStore";

export const TagList = () => {
    const { tags, selectedTagId, fetchTags, selectTag } = useTagStore();
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
                        <div className="flex items-center w-full cursor-pointer p-2"
                             onClick={() => handleClickTag(tag.id)}>
                            <div className="flex items-center truncate">
                                <TagIcon size={16} className="mr-3 flex-shrink-0 text-gray-500" />
                                <span className="truncate text-gray-600">{tag.name}</span>
                            </div>
                        </div>
                    </li>
                ))}
            </ul>
        </div>
    );
};
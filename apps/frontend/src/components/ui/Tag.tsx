import React from "react";
import {useTagStore} from "@/stores/tagStore";

interface TagProps {
    name: string;
    id: string;
}

const Tag: React.FC<TagProps> = ({ name, id }) => {
    const { selectTag } = useTagStore();

    const handleClick = (id: string) => {
        selectTag(id);
    }

    return (
        <div className="bg-gray-200 hover:bg-amber-300 text-gray-700 px-2 py-1 rounded-full text-sm no-underline"
             onClick={() => handleClick(id)} >
            {name}
        </div>
    );
};

export default Tag;
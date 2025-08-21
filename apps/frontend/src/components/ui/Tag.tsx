import React from "react";
import { Link } from "react-router-dom";

interface TagProps {
    name: String;
}

const Tag: React.FC<TagProps> = ({ name }) => {
    return (
        <Link to={`/notes/tags/${encodeURIComponent(name)}`} className="bg-gray-200 hover:bg-amber-300 text-gray-700
        px-2 py-1 rounded-full text-sm no-underline" >
            {name}
        </Link>
    );
};

export default Tag;
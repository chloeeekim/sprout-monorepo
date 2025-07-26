import React from "react";
import { Plus } from 'lucide-react';
import {Link} from "react-router-dom";

const NewNoteButton: React.FC = () => {
    return (
        <Link to="/notes/new" className="w-full flex items-center p-2 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-100">
            <Plus size={16} className="mr-3" />
            <span>새 노트</span>
        </Link>
    );
};

export default NewNoteButton;
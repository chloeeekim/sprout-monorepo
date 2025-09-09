import React from "react";
import { useNavigate } from "react-router-dom";
import { Plus } from "lucide-react";
import apiClient from "../../lib/apiClient";

const NewNoteButton: React.FC = () => {
    const navigate = useNavigate();

    const handleCreate = async () => {
        try {
            const newNote = {
                title: "Untitled",
                content: null,
                tags: [],
                folderId: null
            };

            const response = await apiClient.post("/api/notes", newNote);
            const noteId = response.data.data.id;
            navigate(`/notes/${noteId}`, { state: { data: response.data.data }});
        } catch (err) {
            console.error("Error creating note: ", err);
            alert("노트 생성에 실패하였습니다. 다시 시도해주세요.");
        }
    };

    return (
        <button className="w-full flex items-center p-2 text-sm font-medium text-gray-600 rounded-lg hover:bg-gray-100 cursor-pointer"
                onClick={handleCreate}>
            <Plus size={16} className="mr-3 text-gray-500" />
            <span>새 노트</span>
        </button>
    );
};

export default NewNoteButton;
import React from "react";
import { Note } from "@sprout/shared-types";
import { Star } from "lucide-react";
import Tag from "./Tag";

interface NoteCardProps {
    note: Note;
    onToggleFavorite: (id: string, isFavorite: boolean) => void;
}

const NoteCard: React.FC<NoteCardProps & { onClick?: () => void }> = ({ note, onToggleFavorite, onClick }) => {
    const handleFavoriteClick = (e: React.MouseEvent) => {
        e.stopPropagation(); // 카드 전체 클릭 방지
        onToggleFavorite(note.id, !note.isFavorite);
    };

    const handleTagClick = (e: React.MouseEvent) => {
        e.stopPropagation(); // 카드 전체 클릭 방지
    }

    return (
        <div onClick={onClick} className="bg-white p-6 rounded-lg shadow-md hover:shadow-lg transition-shadow cursor-pointer border border-gray-200">
            <div className="flex justify-between items-start mb-2">
                <h2 className="text-xl font-bold text-sprout-accent">{note.title}</h2>
                <button onClick={handleFavoriteClick} className="text-gray-400 hover:text-yellow-500">
                    <Star size={20} className={note.isFavorite ? 'text-yello-400 fill-current' : ''} />
                </button>
            </div>
            <p className="text-sprout-text h-24 overflow-hidden text-ellipsis">
                {note.content}
            </p>
            <div className="mt-4 flex flex-wrap gap-2" onClick={handleTagClick}>
                {note.tags.map((tag) => (
                    <Tag name={tag} key={tag} />
                ))}
            </div>
        </div>
    );
};

export default NoteCard;
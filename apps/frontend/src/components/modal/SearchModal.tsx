import React, { useState, useEffect } from "react";
import { Link } from "react-router-dom";
import { Search, Loader2 } from "lucide-react";
import apiClient from "../../lib/apiClient";
import { Note } from "@sprout/shared-types";

interface SearchModalProps {
    isOpen: boolean;
    onClose: () => void;
}

const SearchModal: React.FC<SearchModalProps> = ({ isOpen, onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [searchResults, setSearchResults] = useState<Note[]>([]);

    // Debouncing을 위한 useEffect
    useEffect(() => {
        if (!searchTerm.trim()) {
            setSearchResults([]);
            setIsLoading(false);
            return;
        }

        setIsLoading(true);

        // 500ms 이후에 검색을 실행하는 타이머 설정
        const delayDebounceFn = setTimeout(async () => {
            try {
                const response = await apiClient.get(`/api/notes?keyword=${searchTerm}`);
                setSearchResults(response.data.data.content);
            } catch (err) {
                console.error("Search failed: ", err);
                setSearchResults([]);
            } finally {
                setIsLoading(false);
            }
        }, 500); // 500ms 지연

        // searchTerm 변경 시 이전 타이머 취소하고 새 타이머 설정
        return () => clearTimeout(delayDebounceFn);
    }, [searchTerm]);

    useEffect(() => {
        if (!isOpen) {
            setSearchTerm('');
            setSearchResults([]);
            setIsLoading(false);
        }
    }, [isOpen]);

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/[var(--bg-opacity)] [--bg-opacity:20%] flex justify-center items-start pt-20 z-50" onClick={onClose}>
            <div className="bg-white rounded-lg shadow-xl w-full max-w-lg border border-gray-200" onClick={(e) => e.stopPropagation()} >
                <div className="p-4 border-b border-gray-200">
                    <div className="flex items-center">
                        <Search size={20} className="text-gray-400 mr-3" />
                        <input type="text" placeholder="노트 검색..." value={searchTerm}
                               onChange={(e) => setSearchTerm(e.target.value)}
                               className="w-full focus:outline-none text-sm" autoFocus />
                    </div>
                </div>
                <div className="p-4 h-96 overflow-y-auto">
                    {isLoading ? (
                        <div className="flex justify-center items-center h-full">
                            <Loader2 className="animate-spin text-gray-400" />
                        </div>
                    ) : searchResults.length > 0 ? (
                        <ul>
                            {searchResults.map((note) => (
                                <li key={note.id} className="p-2 hover:bg-gray-100 cursor-pointer rounded-md">
                                    <Link to={`/notes/${note.id}`} onClick={onClose}>
                                        <p className="font-semibold">{note.title}</p>
                                        <p className="text-sm text-gray-600 truncate">{note.content}</p>
                                    </Link>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <div className="text-center text-gray-500">
                            {searchTerm ? `"${searchTerm}"에 대한 검색 결과가 없습니다.` : `검색어를 입력하세요.`}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SearchModal;
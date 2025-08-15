import React, {useState, useEffect, useRef, useCallback} from "react";
import { Link } from "react-router-dom";
import { Search, Loader2, Star } from "lucide-react";
import apiClient from "../../lib/apiClient";
import { Note } from "@sprout/shared-types";
import { useNavigate } from "react-router-dom";
import { debounce } from "lodash-es";
import dayjs from "dayjs";

interface SearchModalProps {
    isOpen: boolean;
    onClose: () => void;
}

const SearchModal: React.FC<SearchModalProps> = ({ isOpen, onClose }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [searchResults, setSearchResults] = useState<Note[]>([]);
    const [recentSearches, setRecentSearches] = useState<string[]>([]);

    const inputRef = useRef<HTMLInputElement>(null);
    const navigate = useNavigate();

    useEffect(() => {
        if (isOpen && inputRef.current) {
            inputRef.current.focus();
        } else if (!isOpen) {
            // 모달이 닫힐 때 상태 초기화
            setSearchTerm('');
            setSearchResults([]);
            setIsLoading(false);
        }
    }, [isOpen]);

    useEffect(() => {
        const storedSearches = localStorage.getItem("recentSearches");
        if (storedSearches) {
            setRecentSearches(JSON.parse(storedSearches));
        }
    }, []);

    const addRecentSearch = (search: string) => {
        if (search) {
            const newSearches = [search, ...recentSearches.filter(s => s !== search)].slice(0, 10);
            setRecentSearches(newSearches);
            localStorage.setItem("recentSearches", JSON.stringify(newSearches));
        }
    };

    const executeSearch = async (search: string) => {
        if (!search.trim()) {
            setSearchResults([]);
            setIsLoading(false);
            return;
        }

        setIsLoading(true);
        try {
            const response = await apiClient.get(`/api/notes?keyword=${search}`);
            setSearchResults(response.data.data.content);
        } catch (err) {
            console.error("Search failed: ", err);
            setSearchResults([]);
        } finally {
            setIsLoading(false);
        }
    };

    const debouncedSearch = useCallback(debounce(executeSearch, 500), []);

    const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const newSearchTerm = e.target.value;
        setSearchTerm(newSearchTerm);
        debouncedSearch(newSearchTerm);
    };

    const onSearchItemClick = (noteId: string) => {
        addRecentSearch(searchTerm);
        onClose();
        navigate(`/notes/${noteId}`);
    };

    const handleRecentSearchClick = (search: string) => {
        setSearchTerm(search);
        executeSearch(search);
    }

    const formatUpdatedAt = (date: string) => {
        const updatedAt = dayjs(date);
        return updatedAt.format("YYYY-MM-DD") + " · " + updatedAt.fromNow();
    };

    if (!isOpen) return null;

    return (
        <div className="fixed inset-0 bg-black/[var(--bg-opacity)] [--bg-opacity:20%] flex justify-center items-start pt-20 z-50" onClick={onClose}>
            <div className="bg-white rounded-lg shadow-xl w-full max-w-lg border border-gray-200" onClick={(e) => e.stopPropagation()} >
                <div className="p-4 border-b border-gray-200">
                    <div className="flex items-center">
                        <Search size={20} className="text-gray-400 mr-3" />
                        <input type="text" placeholder="노트 검색..." value={searchTerm}
                               onChange={handleChange}
                               className="w-full focus:outline-none text-sm"
                               autoFocus
                               ref={inputRef}
                        />
                    </div>
                </div>
                <div className="p-4 h-96 overflow-y-auto items-center">
                    {isLoading ? (
                        <div className="flex justify-center items-center h-full">
                            <Loader2 className="animate-spin text-gray-400" />
                        </div>
                    ) : searchResults.length > 0 ? (
                        <ul>
                            {searchResults.map((note) => (
                                <li key={note.id} className="p-3 hover:bg-gray-100 cursor-pointer rounded-md"
                                    onMouseDown={(e) => e.preventDefault()}
                                    onClick={() => onSearchItemClick(note.id)}
                                >
                                    <div className="flex justify-between items-center mb-1">
                                        <div className="flex-grow min-w-0">
                                            <div className="flex items-center mr-3">
                                                <p className="font-semibold text-base truncate">{note.title}</p>
                                                {note.isFavorite && <Star size={14} className="text-yellow-500 fill-yellow-500 ml-2 flex-shrink-0" />}
                                            </div>
                                        </div>
                                        <span className="text-xs text-gray-400 flex-shrink-0">{formatUpdatedAt(note.updatedAt)}</span>
                                    </div>
                                    <p className="text-sm text-gray-600 truncate">{note.content}</p>
                                </li>
                            ))}
                        </ul>
                    ) : (
                        <div className="text-center text-gray-500 mt-6">
                            {searchTerm.trim() !== "" ? `"${searchTerm}"에 대한 검색 결과가 없습니다.` : recentSearches.length > 0 ? (
                                <div>
                                    <h3 className="text-sm font-semibold text-gray-600">최근에 이런 내용을 검색하셨어요.</h3>
                                    <div className="flex flex-wrap gap-2 justify-center mt-2">
                                        {recentSearches.map((search, index) => (
                                            <div key={index} onClick={() => handleRecentSearchClick(search)}
                                                className="cursor-pointer px-2 py-1 border border-gray-200 hover:bg-gray-100 rounded-full text-sm no-underline">
                                                {search}
                                            </div>
                                        ))}
                                    </div>
                                </div>
                            ) : "검색어를 입력하세요."}
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
};

export default SearchModal;
import React, { useEffect, useState } from "react";
import MainLayout from "../../components/layout/MainLayout";
import {Note, NoteListResponse} from '@sprout/shared-types';
import apiClient from "../../lib/apiClient";
import NoteCard from "../../components/ui/NoteCard";
import { Link, useNavigate } from "react-router-dom";
import Button from "../../components/ui/Button";
import InfiniteScroll from "react-infinite-scroll-component";
import { useFolderStore } from "../../stores/folderStore";
import TopBar from "../../components/ui/TopBar";
import {useTagStore} from "../../stores/tagStore";
import {Folder, Folder as FolderIcon, Tag as TagIcon} from "lucide-react";

const NoteListPage: React.FC = () => {
    const [notes, setNotes] = useState<Note[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [hasNext, setHasNext] = useState(true);
    const [lastUpdatedAt, setLastUpdatedAt] = useState<string | null>(null);
    const [lastId, setLastId] = useState<string | null>(null);

    const navigate = useNavigate();
    const { selectedFolderId, selectedFolderName, unselectFolder } = useFolderStore();
    const { selectedTagId, selectedTagName, unselectTag } = useTagStore();

    const PAGE_SIZE = 20;

    useEffect(() => {
        fetchNotes(true);
    }, [selectedFolderId, selectedTagId]);

    const fetchNotes = async (initialLoad: boolean) => {
        if (loading || (!initialLoad && !hasNext)) return; // 더 이상 불러올 노트가 없으면 중단

        setLoading(true);
        setError(null);
        try {
            const queryParams = new URLSearchParams();
            queryParams.append("size", PAGE_SIZE.toString());

            if (selectedFolderId) {
                queryParams.append("folderId", selectedFolderId);
            }
            if (selectedTagId) {
                queryParams.append("tagId", selectedTagId);
            }
            if (!initialLoad && lastUpdatedAt && lastId) {
                queryParams.append("lastUpdatedAt", lastUpdatedAt);
                queryParams.append("lastId", lastId);
            }

            const response = await apiClient.get(`/api/notes?${queryParams.toString()}`)
            const content: Array<NoteListResponse> = response.data.data.content;

            setNotes((prevNotes) => initialLoad ? content : [...prevNotes, ...content]);
            setHasNext(!response.data.data.last);

            if (content.length > 0) {
                setLastUpdatedAt(content[content.length - 1].updatedAt);
                setLastId(content[content.length - 1].id);
            } else if (initialLoad) {
                setLastUpdatedAt(null);
                setLastId(null);
            }
        } catch (err) {
            setError("노트를 불러오는 데 실패했습니다.");
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    const handleToggleFavorite = async (id: string, isFavorite: boolean) => {
        try {
            await apiClient.post(`/api/notes/${id}/favorite`);
            setNotes(notes.map(note =>
                note.id === id ? { ...note, isFavorite: isFavorite } : note
            ));
        } catch (err) {
            alert("즐겨찾기 상태 변경에 실패했습니다.");
            console.error(err);
        }
    }

    const showAllNotes = () => {
        unselectFolder();
        unselectTag();
    }

    return (
        <MainLayout>
            <TopBar>
                <div className="flex flex-row gap-1 items-center text-gray-600">
                    <div className="hover:bg-gray-100 rounded p-1 cursor-pointer"
                            onClick={showAllNotes}>
                        <span>모든 노트</span>
                    </div>
                    {selectedFolderId && (
                        <div className="flex flex-row gap-1 items-center">
                            <span>/</span>
                            <div className="hover:bg-gray-100 rounded p-1 cursor-pointer">
                                <div className="flex items-center">
                                    <FolderIcon size={16} className="mr-1 flex-shrink-0 text-gray-500" />
                                    <span>{selectedFolderName}</span>
                                </div>
                            </div>
                        </div>
                    )}
                    {selectedTagId && (
                        <div className="flex flex-row gap-1 items-center">
                            <span>/</span>
                            <div className="hover:bg-gray-100 rounded p-1 cursor-pointer">
                                <div className="flex items-center">
                                    <TagIcon size={16} className="mr-1 flex-shrink-0 text-gray-500" />
                                    <span>{selectedTagName}</span>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </TopBar>

            {loading && <p>로딩 중...</p>}
            {error && <p className="text-red-500">{error}</p> }

            <div id="scrollableDiv" className="pt-5 h-screen-minus-10 overflow-y-auto">
                <InfiniteScroll
                    next={() => fetchNotes(false)}
                    hasMore={hasNext}
                    loader={<div className="loader" key={0}>로딩 중...</div>}
                    dataLength={notes.length}
                    scrollableTarget="scrollableDiv"
                >
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 p-8 pt-2">
                        {notes.map((note) => (
                            <NoteCard key={note.id} note={note} onToggleFavorite={handleToggleFavorite}
                                      onClick={() => navigate(`/notes/${note.id}`)} />
                        ))}
                    </div>
                </InfiniteScroll>
            </div>
        </MainLayout>
    );
};

export default NoteListPage;
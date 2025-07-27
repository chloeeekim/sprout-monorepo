import React, { useEffect, useState } from "react";
import MainLayout from "../../components/layout/MainLayout";
import {Note, NoteListResponse} from '@sprout/shared-types';
import apiClient from "../../lib/apiClient";
import NoteCard from "../../components/ui/NoteCard";
import { Link, useNavigate, useLocation } from "react-router-dom";
import Button from "../../components/ui/Button";
import Input from "../../components/ui/Input";
import InfiniteScroll from "react-infinite-scroll-component";
import {Slice} from "@sprout/shared-types/slice";
import {List} from "lucide-react";

const NoteListPage: React.FC = () => {
    const [notes, setNotes] = useState<Note[]>([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [searchKeyword, setSearchKeyword] = useState<string>('');
    const [currentSearchQuery, setCurrentSearchQuery] = useState<string>('');
    const [hasNext, setHasNext] = useState(true);
    const [lastUpdatedAt, setLastUpdatedAt] = useState<string | null>(null);
    const [lastId, setLastId] = useState<string | null>(null);

    const navigate = useNavigate();
    const location = useLocation();

    const PAGE_SIZE = 20;

    useEffect(() => {
        const queryParams = new URLSearchParams(location.search);
        const keywordFromUrl = queryParams.get('keyword');
        if (keywordFromUrl) {
            setSearchKeyword(keywordFromUrl);
        }
    }, [location.search]);

    useEffect(() => {
        fetchNotes(true);
    }, []);

    const fetchNotes = async (initialLoad: boolean) => {
        if (loading || (!initialLoad && !hasNext)) return; // 더 이상 불러올 노트가 없으면 중단

        setLoading(true);
        setError(null);
        try {
            const queryParams = new URLSearchParams();
            queryParams.append("size", PAGE_SIZE.toString());
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

    const handleSearch = () => {
        setCurrentSearchQuery(searchKeyword);
        const queryParams = new URLSearchParams();
        if (searchKeyword) {
            queryParams.append('keyword', searchKeyword);
        }
        navigate(`/notes?${queryParams.toString()}`)
    }

    return (
        <MainLayout>
            <div className="h-12 flex justify-between items-center m-8 mb-6">
                <h1 className="text-3xl font-bold text-sprout-text">내 노트</h1>
                <div className="flex items-center gap-2">
                    <Input
                        type="text"
                        placeholder="검색어를 입력하세요."
                        value={searchKeyword}
                        onChange={(e) => setSearchKeyword(e.target.value)}
                        />
                    <Button variant="primary" onClick={handleSearch}>
                        검색
                    </Button>
                    <Link to="/notes/new">
                        <Button variant="primary">새 노트 작성</Button>
                    </Link>
                </div>
            </div>

            {loading && <p>로딩 중...</p>}
            {error && <p className="text-red-500">{error}</p> }

            <div id="scrollableDiv" className="h-screen-minus-27 overflow-y-auto">
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
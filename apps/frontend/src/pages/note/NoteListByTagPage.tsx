import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import MainLayout from "../../components/layout/MainLayout";
import { Note } from "@sprout/shared-types";
import apiClient from "../../lib/apiClient";
import NoteCard from "../../components/ui/NoteCard";
import Button from "../../components/ui/Button";

const NoteListByTagPage: React.FC = () => {
    const [notes, setNotes] = useState<Note[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { tagName } = useParams<{ tagName: string }>();
    const navigate = useNavigate();

    useEffect(() => {
        if (!tagName) {
            setError("Tag name is not provided.");
            setLoading(false);
            return;
        }

        const fetchNotesByTag = async () => {
            try {
                // TODO: 백에드 API에 태그 필터링 요청
            } catch (err) {
                setError(`'${tagName}' 태그를 가진 노트를 불러오는데 실패했습니다.`);
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchNotesByTag();
    }, [tagName]);

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

    return (
        <MainLayout>
            <div className="flex justify-between items-center mb-6">
                <h1 className="text-3xl font-bold text-sprout-text">태그: ${tagName}</h1>
                <Link to="/notes/new">
                    <Button variant="primary">새 노트 작성</Button>
                </Link>
            </div>

            {loading && <p>로딩 중...</p>}
            {error && <p className="text-red-500">{error}</p> }

            {!loading && !error && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {notes.map((note) => (
                        <NoteCard note={note} onToggleFavorite={handleToggleFavorite}
                                  onClick={() => navigate(`/notes/${note.id}`)} />
                    ))}
                </div>
            )}
            <div className="mt-8">
                <Button variant="secondary" onClick={() => navigate("/notes")}>
                    전체 노트 목록으로 돌아가기
                </Button>
            </div>
        </MainLayout>
    );
};

export default NoteListByTagPage;
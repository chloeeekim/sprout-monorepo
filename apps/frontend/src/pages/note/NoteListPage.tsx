import React, { useEffect, useState } from "react";
import MainLayout from "../../components/layout/MainLayout";
import { Note } from '@sprout/shared-types';
import apiClient from "../../lib/apiClient";
import NoteCard from "../../components/ui/NoteCard";
import { Link, useNavigate } from "react-router-dom";
import Button from "../../components/ui/Button";

const NoteListPage: React.FC = () => {
    const [notes, setNotes] = useState<Note[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchNotes = async () => {
            try {
                const response = await apiClient.get('/api/notes');
                setNotes(response.data.data);
            } catch (err) {
                setError("노트를 불러오는 데 실패했습니다.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchNotes();
    }, []);

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
                <h1 className="text-3xl font-bold text-sprout-text">내 노트</h1>
                <Link to="/notes/new">
                    <Button variant="primary">새 노트 작성</Button>
                </Link>
            </div>

            {loading && <p>로딩 중...</p>}
            {error && <p className="text-red-500">{error}</p> }

            {!loading && !error && (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {notes.map((note) => (
                        <NoteCard key={note.id} note={note} onToggleFavorite={handleToggleFavorite}
                                  onClick={() => navigate(`/notes/${note.id}`)} />
                    ))}
                </div>
            )}
        </MainLayout>
    );
};

export default NoteListPage;
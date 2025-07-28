import React, { useEffect, useState } from "react";
import { useParams, useNavigate, Link } from "react-router-dom";
import ReactMarkdown from "react-markdown";
import { Star } from "lucide-react";
import apiClient from '../../lib/apiClient';
import { Note } from '@sprout/shared-types';
import MainLayout from "../../components/layout/MainLayout";
import Button from "../../components/ui/Button";
import Tag from "../../components/ui/Tag"
import dayjs from "dayjs";
import relativeTime from "dayjs/plugin/relativeTime";
import "dayjs/locale/ko";

dayjs.extend(relativeTime);
dayjs.locale("ko");

const NoteDetailPage: React.FC = () => {
    const [note, setNote] = useState<Note | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();

    useEffect(() => {
        if (!id) {
            setError("Note ID is not provided.");
            setLoading(false);
            return;
        }

        const fetchNote = async () => {
            try {
                const response = await apiClient.get(`/api/notes/${id}`, id);
                console.log(`response: `, response);
                console.log(typeof response.data.data.updatedAt)
                setNote(response.data.data);
            } catch (err) {
                setError("노트를 불러오는 데 실패했습니다.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        };

        fetchNote();
    }, [id]);

    const handleDelete = async () => {
        if (!id) return;

        if (window.confirm("정말로 이 노트를 삭제하시겠습니까?")) {
            try {
                await apiClient.delete(`/api/notes/${id}`);
                alert("노트가 성공적으로 삭제되었습니다.");
                navigate('/notes');
            } catch (err) {
                alert("노트 삭제에 실패했습니다. 다시 시도해주세요.");
                console.error("Error delete note: ", err);
            }
        }
    };

    const handleToggleFavorite = async () => {
        if (!note || !id) return;

        const originalNote = note;
        const updatedNote = { ...note, isFavorite: !note.isFavorite };
        setNote(updatedNote); // UI 즉시 업데이트

        try {
            await apiClient.post(`/api/notes/${id}/favorite`);
        } catch (err) {
            alert("즐겨찾기 상태 변경에 실패했습니다.");
            setNote(originalNote); // 에러 발생 시 원래 상태로 복구
            console.error(err);
        }
    }

    const formatUpdatedAt = (date: string) => {
        const updatedAt = dayjs(date);
        return updatedAt.format("YYYY-MM-DD HH:mm") + " · " + updatedAt.fromNow();
    };

    if (loading) {
        return <MainLayout><p>로딩 중...</p></MainLayout>;
    }

    if (error) {
        return <MainLayout><p className="text-red-500">{error}</p> </MainLayout>;
    }

    if (!note) {
        return <MainLayout><p>노트를 찾을 수 없습니다.</p></MainLayout>;
    }

    return (
        <MainLayout>
            <div className="p-4">
                <div className="flex justify-between items-center mb-4">
                    <div className="flex items-center gap-4">
                        <h1 className="text-3xl font-bold text-sprout-text">{note.title}</h1>
                        <button onClick={handleToggleFavorite} className="text-gray-400 hover:text-yellow-500 focus:outline-none">
                            <Star size={28} fill={note.isFavorite ? 'currentColor' : 'none'} />
                        </button>
                    </div>
                    <div className="flex gap-2">
                        <Link to={`/notes/${note.id}/edit`}>
                            <Button variant="primary">수정</Button>
                        </Link>
                        <Button variant="secondary" onClick={handleDelete}>
                            삭제
                        </Button>
                    </div>
                </div>
                <div className="text-sm text-gray-500 mb-4">
                    최종 수정: {formatUpdatedAt(note.updatedAt)}
                </div>
                <div className="prose max-w-none text-sprout-text mb-6">
                    <ReactMarkdown>{note.content || ''}</ReactMarkdown>
                </div>
                <div className="flex flex-wrap gap-2">
                    {note.tags.map((tag, index) => (
                        <Tag key={index} name={tag} />
                    ))}
                </div>
                <div className="mt-8">
                    <Button variant="secondary" onClick={() => navigate("/notes")}>
                        목록으로 돌아가기
                    </Button>
                </div>
            </div>
        </MainLayout>
    );
};

export default NoteDetailPage;

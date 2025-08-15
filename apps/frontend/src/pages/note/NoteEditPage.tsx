import React, {use, useEffect, useState} from "react";
import { useParams, useNavigate } from "react-router-dom";
import apiClient  from "../../lib/apiClient";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import MainLayout from "../../components/layout/MainLayout";

const NoteEditPage: React.FC = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');
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
                const response = await apiClient.get(`/api/notes/${id}`);
                const note = response.data.data;

                setTitle(note.title);
                setContent(note.content || '');
                setTags(note.tags.join(', '));
            } catch (err) {
                setError("노트를 불러오는 데 실패했습니다.");
                console.error(err);
            } finally {
                setLoading(false);
            }
        }

        fetchNote();
    }, [id]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (!id) return;

        try {
            const updatedNote = {
                title,
                content,
                tags: tags.split(',').map(tag => tag.trim()).filter(tag => tag)
            };
            await apiClient.post(`/api/notes/${id}`, updatedNote);
            alert("노트가 성공적으로 수정되었습니다.");
            navigate(`/notes/${id}`);
        } catch (err) {
            alert("노트 수정에 실패했습니다.");
            console.error("Error update note: ", err);
        }
    };

    if (loading) {
        return <MainLayout><p>로딩 중...</p></MainLayout>;
    }

    if (error) {
        return <MainLayout><p className="text-red-500">{error}</p> </MainLayout>;
    }

    return (
        <MainLayout>
            <div className="container mx-auto p-8">
                <h1 className="text-2xl font-bold mb-4">Edit Note</h1>
                <form onSubmit={handleSubmit}>
                    <Input
                        label="Title"
                        value={title}
                        onChange={(e) => setTitle(e.target.value)}
                        placeholder="Enter title"
                        required
                        />
                    <div className="mb-4">
                        <label htmlFor="content" className="block text-sprout-text text-sm font-bold mb-2">Content</label>
                        <textarea
                            id="content"
                            value={content}
                            onChange={(e) => setContent(e.target.value)}
                            placeholder="Enter content"
                            className="shadow appearance-none border rounded w-full py-2 px-3 text-sprout-text leading-tight
                            focus:outline-none focus:shadow-outline focus:border-sprout-accent h-48"
                            required
                            />
                        <Input
                            label="Tags (comma sepearted)"
                            value={tags}
                            onChange={(e) => setTags(e.target.value)}
                            placeholder="e.g., 회의록, 아이디어, 일기"
                            />
                        <div className="flex items-center justify-between mt-4">
                            <Button type="submit" variant="primary">
                                Save Changes
                            </Button>
                            <Button type="button" variant="secondary" onClick={() => navigate(`/notes/${id}`)}>
                                Cancel
                            </Button>
                        </div>
                    </div>
                </form>
            </div>
        </MainLayout>
    );
};

export default NoteEditPage;
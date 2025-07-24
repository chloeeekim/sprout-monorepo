import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import apiClient from "../../lib/apiClient";

const NoteCreatePage: React.FC = () => {
    const [title, setTitle] = useState('');
    const [content, setContent] = useState('');
    const [tags, setTags] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            const newNote = {
                title,
                content,
                tags: tags.split(',').map(tag => tag.trim()).filter(tag => tag)
            };
            await apiClient.post("/api/notes", newNote);
            alert("노트가 성공적으로 생성되었습니다.");
            navigate("/notes");
        } catch (err) {
            console.error("Error creating note: ", err);
            alert("노트 생성에 실패하였습니다. 다시 시도해주세요.");
        }
    };

    return (
        <div className="container mx-auto p-4">
            <h1 className="text-2xl font-bold mb-4">Create New Note</h1>
            <form onSubmit={handleSubmit}>
                <Input
                    label="Title"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    placeholder="제목"
                    required
                    />
                <div className="mb-4">
                    <label htmlFor="content" className="block text-sprout-text text-sm font-bold mb-2">Content</label>
                    <textarea
                        id="content"
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                        placeholder="내용을 입력하세요."
                        className="shadow appearance-none border rounded w-full py-2 px-3 text-sprout-text leading-tight
                        focus:outline-none focus:shadow-outline focus:border-sprout-accent h-48"
                        required
                        />
                </div>
                <Input
                    label="Tags (comma separated)"
                    value={tags}
                    onChange={(e) => setTags(e.target.value)}
                    placeholder="e.g., 회의록, 아이디어, 일기"
                    />
                <div className="flex items-center justify-between">
                    <Button type="submit" variant="primary">
                        Save Note
                    </Button>
                    <Button type="button" variant="secondary" onClick={() => navigate("/notes")}>
                        Cancel
                    </Button>
                </div>
            </form>
        </div>
    );
};

export default NoteCreatePage;
import React, {useCallback, useEffect, useMemo, useRef, useState} from "react";
import {useLocation, useNavigate, Link, useParams} from "react-router-dom";
import ReactMarkdown from "react-markdown";
import {Note, NoteUpdateRequest, NoteUpdateResponse, Tag} from "@sprout/shared-types";
import apiClient from "../../lib/apiClient";
import MainLayout from "../../components/layout/MainLayout";
import LineSkeleton from "../../components/ui/LineSkeleton";
import TopBar from "../../components/ui/TopBar";
import {Star, Trash2, Copy} from "lucide-react";
import clsx from "clsx";
import {useFolderStore} from "../../stores/folderStore";
import SingleSelect from "../../components/ui/SingleSelect";
import MultiSelect from "../../components/ui/MultiSelect";
import {useTagStore} from "../../stores/tagStore";
import useFormattedTime from "../../hooks/useFormattedTime";
import { debounce } from "lodash-es";
import {copyNote, deleteNote, getNoteById, toggleIsFavorite, updateNote} from "../../lib/noteApi";
import {TagDetailResponse} from "@sprout/shared-types/tag";
import {buildPartialUpdate} from "../../utils/buildPartialUpdate";

const NotePage: React.FC = () => {
    const location = useLocation();
    const [note, setNote] = useState<Note<TagDetailResponse[]> | null>(null);

    const [initial, setInitial] = useState<NoteUpdateRequest | null>(null);
    const [updated, setUpdated] = useState<NoteUpdateRequest | null>(null);

    const [updatedAt, setUpdatedAt] = useState('');
    const [isFavorite, setIsFavorite] = useState(false);

    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const { id } = useParams<{ id: string }>();

    const { folders } = useFolderStore();
    const { tags, addTag } = useTagStore();

    const formatted = useFormattedTime(updatedAt);
    const isInitializing = useRef(true);

    // form 값 업데이트
    const updateField = useCallback(<K extends keyof NoteUpdateRequest>(
        key: K,
        value: NoteUpdateRequest[K]
    ) => {
        setUpdated(prev => ({ ...prev, [key]: value }));
    }, []);

    useEffect(() => {
        const fetchNote = async () => {
            if (id) {
                try {
                    const response = await getNoteById(id);
                    setNote(response);
                } catch (err) {
                    setError("노트를 불러오는 데 실패했습니다.");
                    console.error(err);
                } finally {
                    setLoading(false);
                }
            }
        }

        if (location.state) {
            setNote(location.state.data);
        } else {
            fetchNote();
        }
    }, [id]);

    useEffect(() => {
        if (note && isInitializing.current) {
            const data = {
                title: note.title,
                content: note.content,
                tags: note.tags.map(t => t.id),
                folderId: note.folderId
            };
            setInitial(data);
            setUpdated(data);

            setUpdatedAt(note.updatedAt);
            setIsFavorite(note.isFavorite);
            setLoading(false);

            setTimeout(() => {
                isInitializing.current = false;
            }, 0);
        }
    }, [note]);

    const executeUpdate = useCallback (async () => {
        if (!id) return;

        const payload = buildPartialUpdate(initial, updated);
        if (Object.keys(payload).length === 0) return; // 변경사항이 없는 경우 요청 안 함

        try {
            const response = await updateNote(id, payload);

            if (response) {
                setUpdatedAt(response.updatedAt);
                setInitial(updated);
            }
        } catch (err) {
            console.error("Failed save note: ", err);
        }
    }, [id, updated]);

    const debouncedUpdate = useMemo(() => debounce(executeUpdate, 500), [executeUpdate]);

    useEffect(() => {
        if (isInitializing.current) return;

        debouncedUpdate();
    }, [updated, debouncedUpdate]);

    // 컴포넌트 언마운트 시 pending 요청 취소
    useEffect(() => {
        return () => {
            debouncedUpdate.cancel();
        };
    }, [debouncedUpdate]);

    const handleDelete = async () => {
        if (!note || !id) return;

        if (window.confirm("정말로 이 노트를 삭제하시겠습니까?")) {
            try {
                await deleteNote(id);
                alert("노트가 성공적으로 삭제되었습니다.");
                navigate('/notes');
            } catch (err) {
                alert("노트 삭제에 실패했습니다. 다시 시도해주세요.");
                console.log("Error delete note: ", err);
            }
        }
    };

    const handleToggleFavorite = async () => {
        if (!note || !id) return;

        setIsFavorite(!isFavorite);
        try {
            await toggleIsFavorite(id);
        } catch (err) {
            alert("즐겨찾기 상태 변경에 실패했습니다.");
            setIsFavorite(!isFavorite);
            console.error(err);
        }
    };

    const handleCopy = async () => {
        if (!note || !id) return;

        try {
            const newNote = await copyNote(id);

            navigate(`/notes/${newNote.id}`, { state: { data: newNote }});
        } catch (err) {
            console.error("Error copy note: ", err);
            alert("노트 복제에 실패하였습니다. 다시 시도해주세요.");
        }
    };

    const handleCreateTag = async (name: string) => {
        if (name.trim()) {
            await addTag(name.trim());
        }
    }

    return (
      <MainLayout>
          <TopBar>
              <span className="text-gray-600">{updated?.title ?? ""}</span>
              <div className="flex flex-row gap-1 items-center">
                  <button className={clsx("p-1 hover:bg-gray-100 rounded cursor-pointer", isFavorite ? "text-yellow-500" : "text-gray-500") }
                        onClick={handleToggleFavorite}>
                      <Star size={20} strokeWidth={1.5} fill={isFavorite ? 'currentColor' : 'none'} />
                  </button>
                  <button className="p-1 text-gray-500 hover:bg-gray-100 rounded cursor-pointer"
                        onClick={handleCopy}>
                      <Copy size={20} strokeWidth={1.5} />
                  </button>
                  <button className="p-1 text-gray-500 hover:bg-gray-100 rounded cursor-pointer"
                        onClick={handleDelete}>
                      <Trash2 size={20} strokeWidth={1.5} />
                  </button>
              </div>
          </TopBar>
        <div className="text-gray-800 container mx-auto p-8">
          <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8">
            {/* Title Section */}
            <div className="mb-6">
                {loading ? (
                    <LineSkeleton width="w-80" height="h-10" rounded="rounded-lg" />
                ) : (
                    <input
                        type="text"
                        value={updated?.title ?? ""}
                        onChange={(e) => updateField("title", e.target.value)}
                        placeholder="Untitled"
                        className="w-full text-4xl font-semibold text-gray-800 border-none focus:outline-none px-0"
                    />
                )}
            </div>

            <hr className="mb-6 text-gray-200" />

            {/* Meta Info Section */}
              {loading ? (
                  <div className="flex flex-col gap-6 my-7">
                      <LineSkeleton width="w-60" height="h-5" rounded="rounded-lg" />
                      <LineSkeleton width="w-60" height="h-5" rounded="rounded-lg" />
                      <LineSkeleton width="w-60" height="h-5" rounded="rounded-lg" />
                  </div>
              ) : (
                  <div className="flex flex-col gap-1 text-sm">
                      <div className="flex items-start flex-row gap-2">
                          <span className="text-gray-500 w-28 mt-2">Last Updated</span>
                          <div className="flex-1">
                              <div className="p-2 w-full items-center text-gray-700">
                                  {formatted}
                              </div>
                          </div>
                      </div>
                      <div className="flex items-start flex-row gap-2">
                          <span className="text-gray-500 w-28 mt-2">Folder</span>
                          <SingleSelect options={folders.map(f => ({ value: f.id, label: f.name }))}
                                        value={updated?.folderId ?? null} onChange={(val) => updateField("folderId", val)}
                                        placeholder="Add Folder" />
                      </div>
                      <div className="flex items-start flex-row gap-2">
                          <span className="text-gray-500 w-28 mt-2">Tags</span>
                          <MultiSelect options={tags.map(t => ({ value: t.id, label: t.name }))}
                                       selected={updated?.tags ?? []} onChange={(val) => updateField("tags", val)}
                                       onCreate={handleCreateTag}
                                       placeholder="Add Tags" />
                      </div>
                  </div>
              )}

            <hr className="my-6 text-gray-200" />

            {/* Content Section */}
            <div>
                {loading ? (
                    <div className="flex flex-col gap-1">
                        <LineSkeleton height="h-6" rounded="rounded-lg" />
                        <LineSkeleton height="h-6" rounded="rounded-lg" />
                    </div>
                ) : (
                    <textarea
                        value={updated?.content ?? ""}
                        onChange={(e) => updateField("content", e.target.value)}
                        placeholder="오늘 어떤 생각을 하셨나요?"
                        className="w-full h-screen-minus-91 text-base text-gray-800 border-none focus:outline-none px-0 resize-none"
                    />
                )}
            </div>
          </div>
        </div>
      </MainLayout>
    );
};

export default NotePage;
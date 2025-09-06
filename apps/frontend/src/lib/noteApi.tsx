import apiClient from "./apiClient";
import {
    Slice,
    Note,
    NoteListResponse,
    NoteUpdateRequest,
    NoteUpdateResponse,
    NoteDetailResponse, NoteSimpleResponse
} from "@sprout/shared-types";

export const getNotes = async (
    initialLoad: boolean,
    lastUpdatedAt: string | null,
    lastId: string | null,
    tagId: string | null,
    keyword: string | null,
    folderId: string | null,
    size: number
): Promise<Slice<Note[]>> => {
    const queryParams = new URLSearchParams();
    queryParams.append("size", size.toString());

    if (folderId) {
        queryParams.append("folderId", folderId);
    }
    if (tagId) {
        queryParams.append("tagId", tagId);
    }
    if (keyword) {
        queryParams.append("keyword", keyword);
    }
    if (!initialLoad && lastUpdatedAt && lastId) {
        queryParams.append("lastUpdatedAt", lastUpdatedAt);
        queryParams.append("lastId", lastId);
    }

    const response = await apiClient.get(`/api/notes?${queryParams.toString()}`);
    return response.data.data;
};

export const getAllNotes = async (): Promise<NoteSimpleResponse[]> => {
    const response = await apiClient.get(`/api/notes/all`);
    return response.data.data;
};

export const getNoteById = async (id: string): Promise<Note> => {
    const response = await apiClient.get(`/api/notes/${id}`);
    return response.data.data;
};

export const getRandomNote = async (): Promise<NoteDetailResponse> => {
    const response = await apiClient.get(`/api/notes/random`);
    return response.data.data;
}

export const createNote = async (): Promise<Note> => {
    const newNote = {
        title: "Untitled",
        content: null,
        tags: [],
        folderId: null
    };

    const response = await apiClient.post("/api/notes", newNote);
    return response.data.data;
};

export const copyNote = async (id: string): Promise<Note> => {
    const response = await apiClient.post(`/api/notes/${id}/copy`);
    return response.data.data;
};

export const updateNote = async (id: string, request: Partial<NoteUpdateRequest>): Promise<NoteUpdateResponse> => {
    const response = await apiClient.patch(`/api/notes/${id}`, request);
    return response.data.data;
}

export const toggleIsFavorite = async (id: string): Promise<Note> => {
    const response = await apiClient.post(`/api/notes/${id}/favorite`);
    return response.data.data;
};

export const deleteNote = async (id: string): Promise<void> => {
    await apiClient.delete(`/api/notes/${id}`);
};

export const getSimilarNotes = async (id: string): Promise<NoteListResponse[]> => {
    const response = await apiClient.get(`/api/notes/${id}/similar`);
    return response.data.data;
};
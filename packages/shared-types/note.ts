import {TagDetailResponse} from "./tag";

export interface Note<TTags> {
    id: string;
    title: string;
    content: string | null;
    isFavorite: boolean;
    tags: TTags;
    createdAt: string; // ISO 8601 format string
    updatedAt: string; // ISO 8601 format string
    folderId: string | null;
}

// request types
export type NoteCreateRequest = Pick<Note<string[]>, "title" | "content" | "tags" | "folderId">;
export type NoteUpdateRequest = Partial<Pick<Note<string[]>, "title" | "content" | "tags" | "folderId">>;

// response types
export type NoteCreateResponse = Note<TagDetailResponse[]>;
export type NoteUpdateResponse = Note<TagDetailResponse[]>;
export type NoteDetailResponse = Note<TagDetailResponse[]>;
export type NoteListResponse = Note<TagDetailResponse[]>;
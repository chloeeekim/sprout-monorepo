export interface Note {
    id: string;
    title: string;
    content: string | null;
    isFavorite: boolean;
    tags: string[];
    createdAt: string; // ISO 8601 format string
    updatedAt: string; // ISO 8601 format string
    folderId: string | null;
}

// request types
export type NoteCreateRequest = Pick<Note, 'title' | 'content' | 'tags'> & { folderId?: string };
export type NoteUpdateRequest = Pick<Note, 'title' | 'content' | 'tags'> & { folderId?: string };

// response types
export type NoteCreateResponse = Note;
export type NoteUpdateResponse = Note;
export type NoteDetailResponse = Note;
export type NoteListResponse = Note;
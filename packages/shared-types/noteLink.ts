export interface NoteLink {
    source: string;
    target: string;
    label: string | null;
    direction: string;
}

// response types
export type NoteLinkResponse = NoteLink;
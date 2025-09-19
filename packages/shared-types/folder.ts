export interface Folder {
    id: string;
    name: string;
    count: number;
}

export type FolderCreateResponse = Folder;
export type FolderUpdateResponse = Folder;
export type FolderListResponse = Folder;
import {create} from "zustand";
import { Folder } from "@sprout/shared-types";
import { getFolders, createFolder, updateFolder, deleteFolder } from "../lib/folderApi";
import {parseAstAsync} from "vite";

interface FolderState {
    folders: Folder[];
    selectedFolderId: string | null;
    isLoading: boolean;
    error: Error | null;
    fetchFolders: () => Promise<void>;
    addFolder: (name: string) => Promise<void>;
    editFolder: (id: string, name: string) => Promise<void>;
    removeFolder: (id: string) => Promise<void>;
    selectFolder: (id: string | null) => void;
}

export const useFolderStore = create<FolderState>((set) => ({
    folders: [],
    selectedFolderId: null,
    isLoading: false,
    error: null,
    fetchFolders: async () => {
        set({ isLoading: true, error: null });
        try {
            const folders = await getFolders();
            set({ folders, isLoading: false });
        } catch (error) {
            set({ error: error as Error, isLoading: false });
        }
    },
    addFolder: async (name: string) => {
        try {
            const newFolder = await createFolder(name);
            set((state) => ({ folders: [...state.folders, newFolder] }));
        } catch (error) {
            console.error("Failed to add folder: ", error);
        }
    },
    editFolder: async (id: string, name: string) => {
        try {
            const updatedFolder = await updateFolder(id, name);
            set((state) => ({
                folders: state.folders.map((f) => (f.id === id ? updatedFolder : f)),
            }));
        } catch (error) {
            console.error("Failed to edit folder: ", error);
        }
    },
    removeFolder: async (id: string) => {
        try {
            await deleteFolder(id);
            set((state) => ({
                folders: state.folders.filter((f) => f.id !== id),
            }));
        } catch (error) {
            console.error("Failed to delete folder: ", error);
        }
    },
    selectFolder: (id: string | null) => {
        set({ selectedFolderId: id });
    },
}));
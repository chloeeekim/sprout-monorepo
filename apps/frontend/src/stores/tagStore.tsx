import {create} from "zustand";
import type { Tag } from '@sprout/shared-types';
import {createTag, getTags, deleteTag} from "@/lib/tagApi";

interface TagState {
    tags: Tag[];
    selectedTagId: string | null;
    selectedTagName: string | null;
    isLoading: boolean;
    error: Error | null;
    fetchTags: () => Promise<void>;
    addTag: (name: string) => Promise<void>;
    removeTag: (id: string) => Promise<void>;
    selectTag: (id: string | null) => void;
    unselectTag: () => void;
}

export const useTagStore = create<TagState>((set, get) => ({
    tags: [],
    selectedTagId: null,
    selectedTagName: null,
    isLoading: false,
    error: null,
    fetchTags: async () => {
        set({ isLoading: true, error: null });
        try {
            const tags = await getTags();
            set({ tags, isLoading: false });
        } catch (err) {
            set({ error: err as Error, isLoading: false });
        }
    },
    addTag: async (name: string) => {
        try {
            const newTag = await createTag(name);
            set((state) => ({ tags: [...state.tags, newTag] }));
        } catch (err) {
            console.error("Failed to add tag: ", err);
        }
    },
    removeTag: async (id: string) => {
        try {
            await deleteTag(id);
            set((state) => ({
                tags: state.tags.filter((t) => t.id !== id),
            }));
        } catch (err) {
            console.error("Failed to delete tag: ", err);
        }
    },
    selectTag: (id: string | null) => {
        const tagName = get().tags.find((t) => t.id === id)?.name;
        set({ selectedTagId: id, selectedTagName: tagName });
    },
    unselectTag: () => {
        set({ selectedTagId: null, selectedTagName: null });
    },
}));
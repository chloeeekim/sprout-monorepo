import apiClient from "./apiClient";
import { Tag } from "@sprout/shared-types";

export const getTags = async (): Promise<Tag[]> => {
    const response = await apiClient.get('/api/tags');
    return response.data.data;
};

export const createTag = async (name: string): Promise<Tag> => {
    const response = await apiClient.post('/api/tags', { name });
    return response.data.data;
};

export const deleteTag = async (id: string): Promise<void> => {
    await apiClient.delete(`/api/tags/${id}`);
};
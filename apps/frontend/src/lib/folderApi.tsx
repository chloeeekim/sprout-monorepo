import apiClient from "./apiClient";
import type { Folder } from '@sprout/shared-types';

export const getFolders = async (): Promise<Folder[]> => {
    const response = await apiClient.get('/api/folders');
    return response.data.data;
};

export const createFolder = async (name: string): Promise<Folder> => {
    const response = await apiClient.post('/api/folders', { name });
    return response.data.data;
};

export const updateFolder = async (id: string, name: string): Promise<Folder> => {
    const response = await apiClient.post(`/api/folders/${id}`, { name });
    return response.data.data;
};

export const deleteFolder = async (id: string): Promise<void> => {
    await apiClient.delete(`/api/folders/${id}`);
};
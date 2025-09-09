import apiClient from "./apiClient";
import type { NoteLinkResponse } from '@sprout/shared-types/noteLink';

export const getAllNoteLinks = async (): Promise<NoteLinkResponse> => {
    const response = await apiClient.get(`/api/links`);
    return response.data.data;
}
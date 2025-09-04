import axios from "axios";

const apiClient = axios.create({
    baseURL: import.meta.env.VITE_API_BASE_URL || ''
});

// 요청 인터셉터 : 모든 요청이 보내지기 전에 특정 작업을 수행하도록 설정
apiClient.interceptors.request.use(
    (config) => {
        // localStorage에서 accessToken을 가져옴
        const token = localStorage.getItem('accessToken');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => {
        return Promise.reject(error);
    }
);

export default apiClient;
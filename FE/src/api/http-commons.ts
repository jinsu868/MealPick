import axios from "axios";

const API_BASE_URL = `${import.meta.env.VITE_API_URL}/api/v1/`;

const navigateToLogin = () => {
    localStorage.removeItem("accessToken"); // 만료된 토큰 삭제
    window.location.href = "/login"; // 강제 리디렉트
};

const localAxios = () => {
    const instance = axios.create({
        baseURL: API_BASE_URL,
        withCredentials: true,
        headers: {
            "Content-type": "application/json;charset=utf-8",
            "Access-Control-Allow-Origin": "*",
            crossDomain: true,
        },
    });

    instance.interceptors.request.use(
        (config) => {
            const token = localStorage.getItem("accessToken");
            if (token) {
                config.headers.Authorization = `Bearer ${token}`;
            }
            return config;
        },
        (error) => Promise.reject(error),
    );

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response) {
                const { status } = error.response;
                if (status === 401 || status === 403) {
                    console.log("액세스 토큰 만료됨, 로그인 페이지로 이동");
                    navigateToLogin();
                }
            }
            return Promise.reject(error);
        },
    );

    return instance;
};

const multiPartAxios = () => {
    const instance = axios.create({
        baseURL: API_BASE_URL,
        withCredentials: true,
        headers: {
            "Content-Type": "multipart/form-data",
        },
    });

    instance.interceptors.request.use((config) => {
        const token = localStorage.getItem("accessToken");
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    });

    instance.interceptors.response.use(
        (response) => response,
        (error) => {
            if (error.response) {
                const { status } = error.response;
                if (status === 401 || status === 403) {
                    console.log("액세스 토큰 만료됨, 로그인 페이지로 이동");
                    navigateToLogin();
                }
            }
            return Promise.reject(error);
        },
    );

    return instance;
};

export { localAxios, multiPartAxios };

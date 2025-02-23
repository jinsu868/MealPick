import { localAxios } from "./http-commons";
import { handleAxiosError } from "../utils/axiosErrorHandler";
import SignupRequest from "../types/SignupT";

const axiosInstance = localAxios();

export const kakaoLogin = async (code: string) => {
    try {
        console.log("code", code);
        const response = await axiosInstance.post(
            "/auth/login/kakao",
            {
                code,
            },
            {
                headers: {
                    "Content-Type": "application/json",
                },
            },
        );
        console.log(response.data);
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Kakao Login Failed");
    }
};

export const signup = async (data: SignupRequest) => {
    try {
        console.log(data);
        const response = await axiosInstance.post("/auth/sign-up", data);
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Signup Failed");
    }
};

export const logout = async () => {
    try {
        const response = await axiosInstance.post("/auth/logout");
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Logout Failed");
    }
};

/**
 * 이메일·비밀번호 로그인
 * @param email
 * @param password
 * @returns { user: User; token: string } 형태를 가정
 */
export const emailLogin = async (email: string, password: string) => {
    try {
        const response = await axiosInstance.post("/auth/login", {
            email,
            password,
        });
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Email Login Failed");
    }
};

// eslint-disable-next-line @typescript-eslint/no-explicit-any
export const EditUserInfo = async (data: any) => {
    try {
        const response = await axiosInstance.put("/user/update", {
            data,
        });
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Edit Info Failed");
    }
};

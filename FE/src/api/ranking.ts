import { localAxios } from "./http-commons";
import { handleAxiosError } from "@/utils/axiosErrorHandler";

export interface TrendTagMonthResponse {
    tagName: string;
    postCount: number;
}

const axiosInstance = localAxios();

export const getRanking = async () => {
    try {
        const response = await axiosInstance.get("/rankings");
        console.log("asdf", response.data);
        return response.data;
    } catch (error) {
        handleAxiosError(error, "Kakao Login Failed");
    }
};

export const getMonthlyTrend = async (): Promise<TrendTagMonthResponse[]> => {
    try {
        const response = await axiosInstance.get("/tags/trending");
        console.log("요청받은 태그 데이터", response.data);
        return response.data;
    } catch (error) {
        console.error("태그별 월간 게시글 수 조회 실패:", error);
        throw error;
    }
};

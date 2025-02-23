import { localAxios } from "./http-commons";

const axiosInstance = localAxios();

export const getMontlyPostCountInTag = async (
    tagName: string,
    period?: "MONTH" | "QUARTER" | "YEAR",
) => {
    try {
        const response = await axiosInstance.get(`/tags/${tagName}/chart`, {
            params: period ? { period } : {},
        });
        console.log("요청받은 태그 데이터", response.data);
        return response.data;
    } catch (error) {
        console.error("태그별 월간 게시글 수 조회 실패:", error);
        throw error;
    }
};

export interface TagSearchResponse {
    tagName: string;
}

export const searchTags = async (
    keyword: string,
): Promise<TagSearchResponse[]> => {
    try {
        const response = await axiosInstance.get<TagSearchResponse[]>(
            `/tags/search/${keyword}`,
        );
        console.log("태그 검색 결과", response.data);
        return response.data;
    } catch (error) {
        console.error("태그 검색 실패:", error);
        throw error;
    }
};

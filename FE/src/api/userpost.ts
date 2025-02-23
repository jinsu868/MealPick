import { localAxios } from "./http-commons";
import Scrap, { getScrapDefault, ScrapDefault } from "@/types/Scrap";
import {
    UserPostDetail,
    getUserPostDetailDefault,
} from "@/types/UserPostDetail";

const axiosInstance = localAxios();

export const getUserPosts = async (
    userId: number | null,
    isMyPage: boolean,
    pageToken?: string,
    foodTag?: string,
    mealTime?: string,
): Promise<{ content: UserPostDetail[]; nextPageToken: string | null }> => {
    if (!userId) {
        console.error("❌ userId가 없음");
        return { content: [], nextPageToken: null };
    }

    try {
        const requestUrl = isMyPage ? "posts/me" : `posts/member/${userId}`;
        console.log(`📢 getUserPosts 요청: ${requestUrl}`);

        const response = await axiosInstance.get<{
            pageToken?: string | null;
            data: UserPostDetail[];
            hasNext: boolean;
        }>(requestUrl, {
            params: {
                pageToken: pageToken ?? null,
                foodTag: foodTag ?? null,
                mealTime: mealTime ?? null,
            },
        });

        console.log("✅ 사용자 게시글 데이터 수신:", response.data);

        return {
            content: response.data.data.map((post) =>
                getUserPostDetailDefault(post),
            ), // ✅ 타입 적용
            nextPageToken: response.data.pageToken ?? null,
        };
    } catch (error) {
        console.error("❌ 사용자 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

/* ✅ 내가 스크랩한 게시글 리스트 요청 */
export const getScrappedPosts = async (
    pageToken?: string,
): Promise<{ content: ScrapDefault[]; nextPageToken: string | null }> => {
    try {
        const response = await axiosInstance.get<{
            content?: Scrap[];
            nextPageToken?: string | null;
        }>("scrabs", {
            params: {
                pageToken: pageToken ?? null,
            },
        });

        console.log("scrap posts", response.data);

        return {
            content: (response.data ?? []).map((scrap) =>
                getScrapDefault(scrap),
            ),
            nextPageToken: response.data?.nextPageToken ?? null,
        };
    } catch (error) {
        console.error("스크랩한 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

import { localAxios, multiPartAxios } from "./http-commons";
import { PostImageInfo } from "@/types/PostImageInfo";
import PostDetail, {
    getPostDetailDefault,
    PostDetailDefault,
} from "@/types/PostDetail";
import { FoodTag } from "@/types/FoodTag";
const axiosInstance = localAxios();
const multiPartAxiosInstance = multiPartAxios();

export interface PostUpdateRequest {
    title?: string;
    content?: string;
    mealTime?: string;
    foodTag?: string;
    tags?: string[];
}

/* 게시글 생성 */
export const createPost = async (
    title: string,
    content: string,
    mealTime: string,
    foodTag: string,
    tags: string[],
    images: File[],
    latitude: number,
    longitude: number,
) => {
    const formData = new FormData();

    const postCreateRequest = {
        title,
        content,
        mealTime,
        foodTag,
        tags,
        latitude,
        longitude,
        requestTime: new Date().toLocaleString("sv-SE").replace(" ", "T"),
    };
    console.log(
        "requestTime",
        new Date().toLocaleString("sv-SE").replace(" ", "T"),
    );
    console.log("좌표", latitude, longitude);
    console.log("mealTime", mealTime);

    formData.append(
        "postCreateRequest",
        new Blob([JSON.stringify(postCreateRequest)], {
            type: "application/json; charset=UTF-8",
        }),
    );

    images.forEach((image) => {
        formData.append("images", image);
    });

    try {
        const response = await multiPartAxiosInstance.post("posts", formData);
        console.log("게시글 생성 성공");
        return response.data;
    } catch (error) {
        console.error("게시글 생성 중 오류 발생:", error);
        throw error;
    }
};

export const deletePost = async (postId: number) => {
    try {
        const response = await axiosInstance.delete(`posts/${postId}`);
        console.log("게시글 삭제 성공");
        return response.data;
    } catch (error) {
        console.error("게시글 삭제 중 오류 발생:", error);
        throw error;
    }
};

/* 게시글 저장 */
export const createScrab = async (postId: number) => {
    try {
        const response = await axiosInstance.post(`posts/${postId}/scrab`);
        console.log("게시글 스크랩 성공", postId);
        return response.data;
    } catch (error) {
        console.error("게시글 스크랩 중 오류 발생:", error);
        throw error;
    }
};

/* 게시글 저장 취소 */
export const deleteScrab = async (postId: number) => {
    try {
        const response = await axiosInstance.delete(`scrabs/${postId}`);
        console.log("게시글 스크랩 취소 성공");
        return response.data;
    } catch (error) {
        console.error("게시글 스크랩 취소 중 오류 발생:", error);
        throw error;
    }
};

/* 게시글 좋아요 */
export const createPostLike = async (postId: number) => {
    try {
        const response = await axiosInstance.post(`/posts/${postId}/like`);
        console.log("게시글 좋아요 성공");
        return response.data;
    } catch (error) {
        console.error("게시글 좋아요 중 에러 발생", error);
        throw error;
    }
};

/* 게시글 좋아요 취소 */
export const deletePostLike = async (postId: number) => {
    try {
        const response = await axiosInstance.delete(
            `/posts/${postId}/post-like`,
        );
        console.log("게시글 좋아요 취소 성공");
        return response.data;
    } catch (error) {
        console.error("게시글 좋아요 취소 중 에러 발생", error);
        throw error;
    }
};

/* 게시글 리스트 요청 */
export const getPosts = async (
    pageToken?: string,
    tagName?: string,
): Promise<{ content: PostDetailDefault[]; pageToken: string | null }> => {
    try {
        const response = await axiosInstance.get<{
            data?: PostDetail[];
            pageToken?: string | null;
        }>("posts", {
            params: {
                pageToken: pageToken || undefined,
                tagName: tagName || undefined,
            },
        });
        console.log("게시글 목록 요청 성공:", response.data);

        return {
            content: (response.data.data ?? []).map((post) =>
                getPostDetailDefault(post),
            ),
            pageToken: response.data.pageToken ?? null,
        };
    } catch (error) {
        console.error("게시글 목록 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 게시글 수정 */
export const updatePost = async (
    postId: number,
    postData: PostUpdateRequest,
): Promise<void> => {
    try {
        await axiosInstance.patch(`/posts/${postId}`, postData);
        console.log("게시글 수정 성공");
    } catch (error) {
        console.error("게시글 수정 중 오류 발생:", error);
        throw error;
    }
};

/* 게시글 사진 요청 */
export const getNextPostImages = async (
    postId: number,
): Promise<PostImageInfo[]> => {
    try {
        const response = await axiosInstance.get<PostImageInfo[]>(
            `posts/${postId}`,
        );
        console.log("게시글 사진 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("게시글 사진 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 개별 게시글 상세 요청 */
export const getPostDetail = async (
    postId: number,
): Promise<PostDetailDefault> => {
    try {
        const response = await axiosInstance.get<PostDetail>(`posts/${postId}`);
        console.log("게시글 상세 요청 성공:", response.data);
        return getPostDetailDefault(response.data);
    } catch (error) {
        console.error("게시글 상세 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 아점저 PIC 개수 요청청 */
export interface MyData {
    mealTime: string;
    count: number;
}

export const getMyData = async (): Promise<MyData[]> => {
    try {
        const response = await axiosInstance.get<MyData[]>(`posts/me/data`);
        console.log("마이 데이터 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("마이 데이터 요청 중 오류 발생:", error);
        throw error;
    }
};

export const getOtherData = async (memberId: number): Promise<MyData[]> => {
    try {
        const response = await axiosInstance.get<MyData[]>(
            `posts/${memberId}/data`,
        );
        console.log("다른 사람 데이터 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("다른 사람 데이터 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 내 근처 게시글 요청 */
export interface NearByPost {
    postId: number;
    postImage: string;
}

export const getNearByPosts = async (
    longitude: number,
    latitude: number,
): Promise<NearByPost[]> => {
    try {
        console.log("보내는 좌표", longitude, latitude);
        const response = await axiosInstance.get<NearByPost[]>(
            `posts/nearby?longitude=${longitude}&latitude=${latitude}`,
        );
        console.log("주변 게시글 요청 성공:", response);
        return response.data;
    } catch (error) {
        console.error("주변 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 내 앨범 요청 */
export interface Album {
    foodTag: FoodTag;
    lastCreatedAt: string;
    postCount: number;
    postImages: string[];
}

export const getMyPostAlbums = async (): Promise<Album[]> => {
    try {
        const response = await axiosInstance.get<Album[]>(`posts/me/album`);
        console.log("마이 앨범 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("마이 앨범 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 내 게시글 목록 요청(앨범 상세) */
export const getMyFoodTagPosts = async (
    foodTag: string,
): Promise<PostDetailDefault[]> => {
    try {
        const response = await axiosInstance.get<{
            data: PostDetail[];
            hasNext: boolean;
            pageToken: string | null;
        }>(`posts/me/${foodTag}`);
        console.log("마이 음식 태그 게시글 요청 성공:", response.data);
        return response.data.data.map((post) => getPostDetailDefault(post));
    } catch (error) {
        console.error("마이 음식 태그 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 상대 앨범 요청 */
export const getOtherPostAlbums = async (
    memberId: number,
): Promise<Album[]> => {
    try {
        const response = await axiosInstance.get<Album[]>(
            `posts/${memberId}/album`,
        );
        console.log("상대 앨범 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("상대 앨범 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 상대 게시글 목록 요청(앨범 상세) */
export const getOtherFoodTagPosts = async (
    foodTag: string,
    memberId: number,
): Promise<PostDetailDefault[]> => {
    try {
        const response = await axiosInstance.get<{
            data: PostDetail[];
            hasNext: boolean;
            pageToken: string | null;
        }>(`posts/${foodTag}/${memberId}`);
        console.log("상대 음식 태그 게시글 요청 성공:", response.data);
        return response.data.data.map((post) => getPostDetailDefault(post));
    } catch (error) {
        console.error("상대 음식 태그 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

export interface TagPost {
    postId: number;
    image: string;
}

/* 마이 태그 게시글 요청 */
export const getMyTagPosts = async (
    tagName: string,
    pageToken?: string,
): Promise<{ data: TagPost[]; pageToken?: string }> => {
    try {
        const response = await axiosInstance.get<{
            data: TagPost[];
            hasNext: boolean;
            pageToken: string;
        }>(
            `posts/tag/${tagName}/me?${pageToken ? `pageToken=${pageToken}` : ""}`,
        );
        console.log("마이 태그 게시글 요청 성공:", response.data);
        return response.data;
    } catch (error) {
        console.error("마이 태그 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

/* 다른사람 태그 게시글 요청 */
export const getOtherMembersTagPosts = async (
    tagName: string,
): Promise<{ data: PostDetailDefault[]; pageToken?: string }> => {
    try {
        const response = await axiosInstance.get<{
            data: PostDetailDefault[];
            hasNext: boolean;
            pageToken: string;
        }>(`posts/tag/${tagName}`);
        console.log("다른 사람 태그 게시글 요청 성공", response.data);
        return response.data;
    } catch (error) {
        console.error("다른 사람 태그 게시글 요청 중 오류 발생:", error);
        throw error;
    }
};

export const getTagExist = async (
    tagName: string,
): Promise<{ isExist: boolean; tagName: string }> => {
    try {
        const response = await axiosInstance.get(`posts/search/${tagName}`);
        console.log("태그 존재 여부 요청 성공", response.data);
        return response.data.isExist;
    } catch (error) {
        console.error("태그 존재 여부 요청 중 오류 발생:", error);
        throw error;
    }
};

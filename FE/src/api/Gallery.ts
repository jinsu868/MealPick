import { localAxios } from "./http-commons";

const axiosInstance = localAxios();

// 갤러리 아이템 타입 - 일단 대충 지정 나중에 수정 예정
export interface GalleryItem {
    id: number;
    imageUrl: string;
    title: string;
    createdAt: string;
}

// 갤러리 리스트 응답 타입
interface GalleryResponse {
    gallery: GalleryItem[];
}

// 특정 유저의 갤러리 목록 가져오기
export const getUserGallery = async (
    userId?: number,
): Promise<GalleryItem[]> => {
    try {
        const response = await axiosInstance.get<GalleryResponse>(
            userId ? `gallery/user/${userId}` : "gallery/me",
        );
        return response.data.gallery;
    } catch (error) {
        console.error("갤러리 데이터를 불러오는 데 실패했습니다:", error);
        throw new Error("갤러리 불러오기 실패");
    }
};

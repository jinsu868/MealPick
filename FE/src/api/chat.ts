// api/chat.ts
import { localAxios } from "./http-commons";
import { handleAxiosError } from "../utils/axiosErrorHandler";

const axiosInstance = localAxios();

export const fetchChatRooms = async (pageToken?: string | null) => {
    try {
        // 요청할 쿼리 파라미터 설정
        const params: Record<string, string> = {};
        if (pageToken) {
            params.pageToken = pageToken;
        }

        // Axios를 사용하여 GET 요청 (params 적용)
        const response = await axiosInstance.get("/chat-rooms", { params });

        // JSON 데이터를 콘솔에 출력
        console.log(pageToken, "📢 채팅방 응답 데이터:", response.data);

        return response.data;
    } catch (error) {
        console.error("❌ API 요청 오류:", error);
        throw error;
    }
};

export const fetchChatMessages = async (
    chatRoomId: string,
    pageToken?: string | null,
) => {
    try {
        // 요청할 URL을 동적으로 설정
        const url = `rooms/${chatRoomId}`;

        // 요청할 쿼리 파라미터 설정
        const params: Record<string, string> = {};
        if (pageToken) {
            params.pageToken = pageToken;
        }

        // Axios를 사용하여 GET 요청 (params 적용)
        const response = await axiosInstance.get(url, { params });

        // JSON 데이터를 콘솔에 출력
        console.log(`📢 채팅방(${chatRoomId}) 응답 데이터:`, response.data);

        return response.data;
    } catch (error) {
        console.error("❌ API 요청 오류:", error);
        throw error;
    }
};

/**
 * 새로운 채팅방 생성 API 요청
 * @param name 채팅방 이름
 * @param partnerId 상대방 ID
 * @returns 생성된 채팅방 정보 반환
 */
export const createChatRoom = async (name: string, partnerId: number) => {
    try {
        const response = await axiosInstance.post("chat-rooms", {
            name,
            partnerId,
        });

        console.log("🎉 채팅방 생성 성공:", response.data);
        return response.data; // 생성된 채팅방 데이터 반환
    } catch (error) {
        console.error("❌ 채팅방 생성 실패:", error);
        throw error.response.data;
    }
};

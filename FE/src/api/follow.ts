import { localAxios } from "./http-commons";
import {
    FollowRequestListItem,
    FollowUser,
    FollowStatusResponse,
} from "@/types/Follow";

const axiosInstance = localAxios();

/**
 * 팔로우 요청 보내기
 * @param recipientId - 팔로우할 사용자 ID
 */
export const sendFollowRequest = async (
    recipientId: number,
): Promise<boolean> => {
    try {
        console.log("📡 [API 요청] 팔로우 요청 전송:", recipientId);
        const response = await axiosInstance.post("/follow/request", {
            recipientId,
        });

        console.log("✅ [API 응답] 팔로우 요청 성공:", response.data);
        return true; // ✅ 성공 시 true 반환
    } catch (error: any) {
        console.error("❌ 팔로우 요청 실패:", error);
        if (error.response) {
            console.error("🛑 서버 응답:", error.response.data);
        }
        return false; // ✅ 실패 시 false 반환
    }
};

/**
 * 언팔로우 요청 보내기
 * @param recipientId - 언팔로우할 사용자 ID
 */
export const sendUnfollowRequest = async (
    recipientId: number,
): Promise<void> => {
    try {
        const response = await axiosInstance.delete(`/follow/${recipientId}`);
        console.log("🚫 언팔로우 요청 성공 - Response Data:", response.data);
    } catch (error) {
        console.error("❌ 언팔로우 요청 실패:", error);
        throw new Error("언팔로우 요청 중 오류가 발생했습니다.");
    }
};

/**
 * 팔로우 요청 목록 조회 (대기 중인 요청)
 */
export const getPendingFollowRequests = async (): Promise<FollowUser[]> => {
    try {
        const response = await axiosInstance.get<{
            data: FollowRequestListItem[];
        }>("/follow/stand-by");

        console.log("📌 [API 응답] 대기 중인 팔로우 요청:", response.data.data);

        return response.data.data.map((user) => ({
            followingId: user.followerId,
            nickName: user.nickName ?? "닉네임 없음",
            imageUrl: user.imageUrl ?? "",
        }));
    } catch (error) {
        console.error("❌ 대기 중인 팔로우 요청 조회 실패:", error);
        return [];
    }
};

/**
 * ✅ 팔로우 요청 수락
 */
export const acceptFollowRequest = async (
    requesterId: number,
): Promise<void> => {
    try {
        const response = await axiosInstance.patch("/follow/request/handle", {
            requesterId: requesterId,
            status: "ACCEPT",
        });
        console.log("✅ 팔로우 요청 수락 성공:", response.data);
    } catch (error) {
        console.error("❌ 팔로우 요청 수락 실패:", error);
    }
};

/**
 * ✅ 팔로우 요청 거절
 */
export const rejectFollowRequest = async (
    requesterId: number,
): Promise<void> => {
    try {
        const response = await axiosInstance.patch("/follow/request/handle", {
            recipientId: requesterId,
            status: "REJECT",
        });
        console.log(`🚫 팔로우 요청 거절 성공 - Response Data:`, response.data);
    } catch (error) {
        console.error("❌ 팔로우 요청 거절 실패:", error);
    }
};

/**
 * 팔로워 목록 조회
 */
export const getFollowers = async (): Promise<FollowUser[]> => {
    try {
        const response = await axiosInstance.get<{
            data: {
                followerId: number;
                nickName: string;
                imageUrl?: string;
            }[];
        }>("/follow/me/follower");
        console.log("📌 팔로워 목록 조회 성공 - Response Data:", response.data);
        return response.data.data.map((user) => ({
            followingId: user.followerId,
            nickName: user.nickName,
            imageUrl: user.imageUrl || "",
        }));
    } catch (error) {
        console.error("❌ 팔로워 목록 조회 실패:", error);
        return [];
    }
};

/**
 * 팔로잉 목록 조회
 */
export const getFollowing = async (): Promise<FollowUser[]> => {
    try {
        const response = await axiosInstance.get<{
            data: {
                followerId: number;
                nickName: string;
                imageUrl?: string;
            }[];
        }>("/follow/me/following");
        console.log("📌 팔로잉 목록 조회 성공 - Response Data:", response.data);
        return response.data.data.map((user) => ({
            followingId: user.followerId,
            nickName: user.nickName,
            imageUrl: user.imageUrl || "",
        }));
    } catch (error) {
        console.error("❌ 팔로잉 목록 조회 실패:", error);
        return [];
    }
};

export const getFollowStatus = async (
    someoneId: number,
): Promise<FollowStatusResponse | null> => {
    try {
        const response = await axiosInstance.get(`/follow/${someoneId}`);

        console.log("📌 개별 팔로우 상태 조회 성공 - Response Data:", response);

        // ✅ 응답 구조 확인
        if (!response || !response.data) {
            console.warn("⚠️ getFollowStatus 응답이 올바르지 않음", response);
            return null;
        }

        // ✅ response.data가 객체인지 확인 후 반환
        return response.data as FollowStatusResponse;
    } catch (error) {
        console.error("❌ 개별 팔로우 상태 조회 실패:", error);
        return null;
    }
};

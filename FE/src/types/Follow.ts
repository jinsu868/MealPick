export interface FollowRequestListItem {
    followerId: number;
    nickName: string;
    imageUrl?: string;
    status: "STAND_BY" | "ACCEPT" | "REJECT";
}

export interface FollowRequestAction {
    requestId: number;
    requesterId: number; // 요청 보낸 사용자 ID
    recipientId: number; // 요청 받은 사용자 ID
    status: "STAND_BY" | "ACCEPT" | "REJECT"; // 요청 상태
}

export interface FollowUser {
    followingId: number;
    nickName: string;
    imageUrl?: string;
}

export interface FollowStatusResponse {
    requesterId: number;
    recipientId: number;
    status: "STAND_BY" | "ACCEPT" | "REJECT";
}

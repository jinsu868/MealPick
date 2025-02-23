import { localAxios, multiPartAxios } from "./http-commons";
import { AxiosError } from "axios";
import { MemberDetail, otherMemberDetail } from "@/types/MemberDetail";

const axiosInstance = localAxios();

/** 내 정보 조회 */
export const getMyProfile = async (): Promise<MemberDetail> => {
    try {
        const { data } = await axiosInstance.get("members/me");
        localStorage.setItem("userId", data.id);
        return {
            id: data.id ?? null,
            name: data.name ?? "이름 없음",
            nickname: data.nickname ?? "닉네임 없음",
            profileImage: data.profileImage || null,
            email: data.email || "이메일 없음",
            noticeCheck: data.noticeCheck ?? false,
            darkModeCheck: data.darkModeCheck ?? false,
            alias: data.alias,
        };
    } catch (error) {
        return handleError(error, "내 정보를 불러올 수 없습니다.");
    }
};

/** 다른 사람 프로필 조회 */
export const getUserProfile = async (
    memberId: number,
): Promise<otherMemberDetail> => {
    try {
        const { data } = await axiosInstance.get(`members/${memberId}`);
        return {
            memberId: data.memberId ?? null,
            name: data.name ?? "이름 없음",
            nickName: data.nickName ?? "닉네임 없음",
            profileImage: data.profileImage || null,
            alias: data.alias ?? "별명 없음",
        };
    } catch (error) {
        return handleError(error, "사용자 정보를 불러올 수 없습니다.");
    }
};

/** 프로필 업데이트 */
export const updateMemberProfile = async (
    profile: Partial<MemberDetail>,
    file?: File,
) => {
    try {
        const formData = new FormData();
        formData.append(
            "socialMemberUpdateRequest",
            new Blob([JSON.stringify(profile)], { type: "application/json" }),
        );
        if (file) formData.append("profileImage", file, file.name);

        await multiPartAxios().patch("/members/social", formData, {
            headers: { "Content-Type": "multipart/form-data" },
        });

        console.log("✅ 프로필 업데이트 성공");
    } catch (error) {
        return handleError(error, "프로필을 업데이트할 수 없습니다.");
    }
};

/** 사용자 Alias 저장 */
export const updateUserAlias = async (alias: string): Promise<boolean> => {
    try {
        await axiosInstance.post("/members/alias", { alias });
        console.log("✅ 사용자 별명 저장 성공:", alias);
        return true;
    } catch (error) {
        console.error("❌ 사용자 별명 저장 실패:", error);
        return false;
    }
};

/** 공통 오류 핸들러 */
const handleError = (error: unknown, defaultMessage: string) => {
    if (error instanceof AxiosError) {
        console.error("❌ 오류:", error.response?.data || error.message);
        throw new Error(error.response?.data || defaultMessage);
    }
    throw new Error(defaultMessage);
};

export interface SearchedMember {
    memberId: number;
    name: string;
    nickname: string;
    profileImageUrl: string;
    isFollowed: boolean;
}

export const searchMembers = async (
    keyword: string,
): Promise<SearchedMember[]> => {
    try {
        const response = await axiosInstance.get<SearchedMember[]>(
            `members/search/${keyword}`,
        );
        console.log("멤버 검색 결과:", response.data);
        return response.data;
    } catch (error) {
        return handleError(error, "멤버 검색에 실패했습니다.");
    }
};

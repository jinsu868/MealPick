export interface MemberDetail {
    id?: number | null;
    name?: string | null;
    nickname: string;
    profileImage?: string | null;
    email?: string | null;
    noticeCheck?: boolean | null;
    darkModeCheck?: boolean | null;
    alias: string | null;
}

export interface otherMemberDetail {
    memberId: number | null;
    name: string | null;
    nickName: string;
    profileImage?: string | null;
    alias: string | null;
}

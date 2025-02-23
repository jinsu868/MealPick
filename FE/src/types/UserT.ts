export default interface UserProfileT {
    memberId?: number | null;
    name?: string | null;
    nickname: string;
    profileImage?: string | null;
    email?: string | null;
    noticeCheck?: boolean | null;
    darkModeCheck?: boolean | null;
}

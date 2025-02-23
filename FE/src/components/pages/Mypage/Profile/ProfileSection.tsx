import { useEffect, useState } from "react";
import { getMyProfile, getUserProfile } from "@/api/user";
import { MemberDetail, otherMemberDetail } from "@/types/MemberDetail";
import MyProfile from "./MyProfile";
import UserProfile from "./UserProfile";

interface ProfileSectionProps {
    id?: number;
    isMyPage: boolean;
}

export default function ProfileSection({ id, isMyPage }: ProfileSectionProps) {
    const [profile, setProfile] = useState<
        MemberDetail | otherMemberDetail | null
    >(null);

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const data = isMyPage
                    ? await getMyProfile()
                    : await getUserProfile(id!);
                setProfile(data);
            } catch (error) {
                console.error("❌ 프로필 가져오기 실패:", error);
            }
        };
        fetchProfile();
    }, [id, isMyPage]);

    return (
        <div className="w-full max-w-[600px]">
            {isMyPage && profile ? (
                <MyProfile
                    profile={profile as MemberDetail}
                    setProfile={setProfile}
                />
            ) : (
                profile &&
                id && <UserProfile profile={profile as otherMemberDetail} />
            )}
        </div>
    );
}

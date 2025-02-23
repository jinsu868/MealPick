import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import ProfileSection from "@/components/pages/Mypage/Profile/ProfileSection";
import GallerySection from "@/components/pages/Mypage/Gallery/GallerySection";
import { getMyProfile } from "@/api/user";

export default function UserPage() {
    const { id } = useParams<{ id: string }>();
    const userId = id ? Number(id) : undefined;
    const navigate = useNavigate();
    const [isMyPage, setIsMyPage] = useState(false);

    useEffect(() => {
        const fetchMyProfile = async () => {
            try {
                const profile = await getMyProfile();
                if (!profile?.id) {
                    console.error("❌ 내 프로필 정보를 가져올 수 없음");
                    return;
                }

                console.log("✅ 내 ID:", profile.id, "| 요청된 ID:", userId);

                if (userId === undefined || profile.id === userId) {
                    setIsMyPage(true);
                    if (userId !== profile.id) {
                        navigate(`/user/${profile.id}`, { replace: true }); // ✅ 내 ID로 리다이렉트
                    }
                } else {
                    setIsMyPage(false);
                }
            } catch (error) {
                console.error("❌ 사용자 정보 확인 실패:", error);
            }
        };

        fetchMyProfile();
    }, [userId, navigate]);

    return (
        <div className="w-screen max-w-[600px] p-4 h-screen">
            <ProfileSection id={userId} isMyPage={isMyPage} />
            <GallerySection id={userId} isMyPage={isMyPage} />
        </div>
    );
}

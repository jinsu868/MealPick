import { useEffect, useState } from "react";
import { otherMemberDetail } from "@/types/MemberDetail";
import { FollowStatusResponse } from "@/types/Follow";
import { FaUserCircle } from "react-icons/fa";
import { Button } from "@/components/ui/button";
import { FiUserPlus, FiClock, FiArrowLeft } from "react-icons/fi";
import { RiUserUnfollowLine } from "react-icons/ri";
import StartChatButton from "./StartChatButton";
import MealtimeSlotMachine from "./MealtimeSlotMachine.tsx";
import { useNavigate } from "react-router-dom";
import {
    getFollowStatus,
    sendFollowRequest,
    sendUnfollowRequest,
} from "@/api/follow";

interface UserProfileProps {
    profile: otherMemberDetail;
}

export default function UserProfile({ profile }: UserProfileProps) {
    const userId: number = profile.memberId ?? 0;
    const navigate = useNavigate();

    /* ✅ UI 상태 */
    const [followStatus, setFollowStatus] = useState<
        "팔로우" | "수락 대기 중" | "언팔로우"
    >("팔로우");
    const [isSlotMachineOpen, setSlotMachineOpen] = useState<boolean>(false);
    const isButtonDisabled = followStatus === "수락 대기 중";

    /* ✅ API 데이터 상태 */
    const [mealtimeResult, setMealtimeResult] = useState<string>(
        profile.alias || "별명 없음",
    );
    const [mealtimeColor, setMealtimeColor] = useState<string>("text-gray-500");

    /* ✅ 팔로우 상태 조회 */
    const fetchFollowStatus = async () => {
        if (!userId) return;

        try {
            console.log("📌 [API 호출] getFollowStatus() 실행 중...", userId);

            const response: FollowStatusResponse | null =
                await getFollowStatus(userId);
            console.log("📌 [API 응답] getFollowStatus():", response);

            if (!response) {
                console.log("⚠️ 응답 없음, 기본값 '팔로우' 설정");
                setFollowStatus("팔로우");
                return;
            }

            console.log("🔍 받은 status 값:", response.status);

            // ✅ 상태값만 보고 버튼 상태 결정
            let newStatus: "팔로우" | "수락 대기 중" | "언팔로우" = "팔로우";
            if (response.status === "ACCEPT") {
                newStatus = "언팔로우"; // 이미 팔로우 중
            } else if (response.status === "STAND_BY") {
                newStatus = "수락 대기 중"; // 요청 보냄
            }

            setFollowStatus(newStatus);
            console.log(`✅ 적용될 followStatus: ${newStatus}`);
        } catch (error) {
            console.error("❌ getFollowStatus() API 호출 실패:", error);
        }
    };

    /* ✅ followStatus 변경 감지 */
    useEffect(() => {
        console.log(`🔄 [상태 변경 감지] followStatus: ${followStatus}`);
    }, [followStatus]);

    /* ✅ 페이지 로드 시 상태 자동 불러오기 */
    useEffect(() => {
        fetchFollowStatus();
        window.addEventListener("focus", fetchFollowStatus);
        return () => {
            window.removeEventListener("focus", fetchFollowStatus);
        };
    }, [userId]);

    /* ✅ 팔로우 / 언팔로우 토글 */
    const handleFollowToggle = async () => {
        if (!userId || isButtonDisabled) return;

        try {
            let newStatus: "팔로우" | "수락 대기 중" | "언팔로우" = "팔로우";

            if (followStatus === "언팔로우") {
                console.log("🚫 언팔로우 요청:", userId);
                await sendUnfollowRequest(userId);
                newStatus = "팔로우"; // 언팔로우 후 기본 상태
            } else if (followStatus === "팔로우") {
                console.log("➕ 팔로우 요청:", userId);
                const success = await sendFollowRequest(userId);
                if (!success) return;
                newStatus = "수락 대기 중"; // 팔로우 요청 후 대기 상태
            }

            console.log(`🔄 즉시 반영되는 followStatus: ${newStatus}`);
            setFollowStatus(newStatus); // ✅ 상태 즉시 반영
            await fetchFollowStatus(); // ✅ 최신 상태 API 재조회
        } catch (error) {
            console.error("❌ 팔로우/언팔로우 요청 실패:", error);
        }
    };

    return (
        <div className="w-full flex flex-col items-center">
            <div className="w-full flex justify-start px-4 mb-2">
                <button
                    className="bg-white dark:bg-gray-600 border border-gray-400 rounded-full p-2 hover:bg-gray-100"
                    onClick={() => navigate(-1)}
                >
                    <FiArrowLeft
                        size={20}
                        className="text-gray-700 dark:text-gray-200"
                    />
                </button>
            </div>
            <div className="relative w-full flex flex-col mt-2 items-center text-center p-6 bg-white dark:bg-gray-700 dark:text-gray-200 shadow-[inset_0_0px_50px_rgba(80,100,120,0.1)] rounded-3xl">
                {/* ✅ 프로필 이미지 */}
                {profile.profileImage ? (
                    <img
                        src={profile.profileImage}
                        className="w-24 h-24 rounded-full object-cover border border-gray-300 dark:border-gray-600"
                        alt="프로필"
                    />
                ) : (
                    <FaUserCircle className="text-gray-400 dark:text-gray-500 w-24 h-24" />
                )}

                <h2 className="text-lg font-medium text-gray-900 dark:text-gray-100 mt-3 mb-2">
                    {profile.nickName || "닉네임 없음"}
                </h2>

                {/* ✅ Picname (Mealtime 결과) */}
                {mealtimeResult && (
                    <p
                        className={`mt-2 text-sm font-semibold ${mealtimeColor}`}
                    >
                        {mealtimeResult}
                    </p>
                )}

                {/* ✅ MealtimeSlotMachine 적용 */}
                <MealtimeSlotMachine
                    isOpen={isSlotMachineOpen}
                    onClose={() => setSlotMachineOpen(false)}
                    nickname={profile.nickName}
                    onResultUpdate={(result, color) => {
                        setMealtimeResult(result);
                        setMealtimeColor(color);
                    }}
                />

                <div className="flex gap-2 mt-3">
                    <Button
                        className={`flex items-center gap-2 px-4 py-2 rounded-full shadow-none ${
                            followStatus === "언팔로우"
                                ? "bg-gray-300 dark:bg-gray-600 text-black dark:text-white"
                                : followStatus === "수락 대기 중"
                                  ? "bg-yellow-500 text-white"
                                  : "bg-black dark:bg-gray-800 text-white"
                        } ${isButtonDisabled ? "opacity-50 cursor-not-allowed" : ""}`}
                        onClick={handleFollowToggle}
                        disabled={isButtonDisabled} // ✅ 버튼 비활성화
                    >
                        {followStatus === "언팔로우" ? (
                            <RiUserUnfollowLine size={16} />
                        ) : followStatus === "수락 대기 중" ? (
                            <FiClock size={16} />
                        ) : (
                            <FiUserPlus size={16} />
                        )}
                        {followStatus}
                    </Button>

                    <StartChatButton
                        name={profile.name}
                        memberId={profile.memberId ?? 0}
                    />
                </div>
            </div>
        </div>
    );
}

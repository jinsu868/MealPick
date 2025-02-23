import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { Dialog, DialogContent, DialogHeader } from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { getMyProfile, updateUserAlias } from "@/api/user";
import { getMyData } from "@/api/post"; // ✅ 여기서 count 가져오기
import { MealTime } from "@/types/MealTime";

interface MealtimeSlotMachineProps {
    isOpen: boolean;
    onClose: () => void;
    nickname: string;
    onResultUpdate: (result: string, color: string) => void;
}

/* ✅ 별명 리스트 (랜덤 적용) */
const availableAliases = [
    "헌터",
    "마스터",
    "식도락가",
    "미식가",
    "연구가",
    "덕후",
    "마니아",
    "애호가",
    "러버",
    "중독자",
];

const getNextAlias = () => {
    return availableAliases[
        Math.floor(Math.random() * availableAliases.length)
    ];
};

/* ✅ FoodTag 한글 변환 */
const foodTagMap: Record<string, string> = {
    KOREAN: "한식",
    WESTERN: "양식",
    JAPANESE: "일식",
    CHINESE: "중식",
    BUNSIK: "분식",
    ASIAN: "아시안",
    FASTFOOD: "패스트푸드",
    CHICKEN: "치킨",
    PIZZA: "피자",
    DESSERT: "디저트",
    NONE: "없음",
};

export default function MealtimeSlotMachine({
    isOpen,
    onClose,
    nickname,
    onResultUpdate,
}: MealtimeSlotMachineProps) {
    const navigate = useNavigate();

    /* ✅ 상태 초기화 */
    const [rollingMealtime, setRollingMealtime] = useState<MealTime>(
        MealTime.LUNCH,
    );
    const [rollingFoodTag, setRollingFoodTag] = useState<string>(
        foodTagMap["KOREAN"],
    );
    const [rollingAlias, setRollingAlias] = useState<string>("로딩 중...");

    const [finalMealtimeFoodTag, setFinalMealtimeFoodTag] =
        useState<string>("");
    const [finalAlias, setFinalAlias] = useState<string>("");
    const [hasUpload, setHasUpload] = useState<boolean>(false); // ✅ 업로드 여부 기본값 false
    const [isRolling, setIsRolling] = useState<boolean>(true);

    useEffect(() => {
        if (isRolling) {
            console.log("🎰 슬롯머신이 작동 중...");
        }
    }, [isRolling]);

    useEffect(() => {
        if (!isOpen) return;

        let interval: NodeJS.Timeout;
        let timeout: NodeJS.Timeout;

        const fetchUserData = async () => {
            setIsRolling(true);
            setFinalMealtimeFoodTag("");
            setFinalAlias("");
            setRollingAlias("로딩 중...");

            /* ✅ 게시물 개수 확인 */
            try {
                const myData = await getMyData();
                const totalPosts = myData.reduce(
                    (sum, item) => sum + item.count,
                    0,
                );
                setHasUpload(totalPosts > 0); // ✅ 개수가 0이면 false, 1개 이상이면 true
            } catch (error) {
                console.error("❌ 게시물 개수 확인 실패:", error);
                setHasUpload(false);
            }

            /* 🎰 애니메이션 유지 */
            interval = setInterval(() => {
                setRollingMealtime(
                    Object.values(MealTime).filter((m) => m !== MealTime.NONE)[
                        Math.floor(Math.random() * 3)
                    ],
                );
                setRollingFoodTag(
                    foodTagMap[
                        Object.keys(foodTagMap)[
                            Math.floor(
                                Math.random() *
                                    (Object.keys(foodTagMap).length - 1),
                            )
                        ] as keyof typeof foodTagMap
                    ],
                );
                setRollingAlias(getNextAlias());
            }, 100);

            try {
                /* ✅ 기존 별명 여부와 관계없이 새로운 별명 생성 */
                const newAlias = getNextAlias();

                /* ✅ 애니메이션 값 사용 */
                let mealtimeFoodTag = `${rollingMealtime} ${rollingFoodTag}`;
                const profile = await getMyProfile();
                if (profile.alias && profile.alias !== "??? 형") {
                    const aliasParts = profile.alias.split(" ");
                    mealtimeFoodTag =
                        aliasParts.slice(0, 2).join(" ") || mealtimeFoodTag;
                }

                /* ✅ 2초 후 최종 결과 표시 */
                timeout = setTimeout(() => {
                    setFinalMealtimeFoodTag(mealtimeFoodTag);
                    setFinalAlias(newAlias);
                    onResultUpdate(
                        `${mealtimeFoodTag} [${newAlias}]`,
                        "text-orange-400",
                    );

                    /* ✅ 별명을 서버에 업데이트 */
                    updateUserAlias(newAlias);
                }, 2000);
            } catch (error) {
                console.error("❌ 사용자 데이터 가져오기 실패:", error);
                setRollingAlias("오류 발생");
            }
        };

        fetchUserData();

        return () => {
            clearInterval(interval);
            clearTimeout(timeout);
        };
    }, [isOpen]);

    return (
        <Dialog open={isOpen} onOpenChange={onClose}>
            <DialogContent className="max-w-md p-6 bg-white text-black dark:bg-gray-700 dark:text-gray-200 rounded-xl shadow-lg">
                <DialogHeader>
                    <h2 className="text-center text-lg font-bold">
                        {nickname}님의 picname은?
                    </h2>
                </DialogHeader>

                {hasUpload ? (
                    <div className="flex justify-center items-center text-xl font-bold py-4 bg-white rounded-md shadow-inner w-full">
                        {finalAlias ? ( // ✅ 2초 후 최종 결과 표시
                            <span>
                                {finalMealtimeFoodTag} [{finalAlias}]
                            </span>
                        ) : (
                            <span className="animate-pulse">
                                {rollingMealtime} 형 {rollingFoodTag}{" "}
                                {rollingAlias}
                            </span>
                        )}
                    </div>
                ) : (
                    <div className="text-center text-gray-500 dark:text-gray-200 mt-4">
                        <p>📸 업로드된 사진이 없어요!</p>
                        <Button
                            onClick={() => navigate("/post")}
                            className="mt-2 w-full bg-blue-500 text-white font-bold rounded-lg"
                        >
                            📷 업로드하러 가기
                        </Button>
                    </div>
                )}

                {/* ✅ 닫기 버튼 */}
                <Button
                    onClick={onClose}
                    className="mt-6 w-full bg-gray-700 hover:bg-gray-800 text-white rounded-lg"
                >
                    닫기
                </Button>
            </DialogContent>
        </Dialog>
    );
}

import { useEffect, useState } from "react";
import { getFollowers, getFollowing } from "@/api/follow";
import { IoClose } from "react-icons/io5";
import { FaUserCircle } from "react-icons/fa";
import { useNavigate } from "react-router-dom";

import { FollowUser } from "@/types/Follow";

interface FollowListProps {
    isFollowersView: boolean;
    onClose: () => void;
}

export default function FollowList({
    isFollowersView,
    onClose,
}: FollowListProps) {
    const [list, setList] = useState<FollowUser[]>([]);
    const [loading, setLoading] = useState(true);
    const navigate = useNavigate();

    // ✅ requestId → followingId 변환하는 함수
    const transformResponse = (data: any[]): FollowUser[] => {
        return data.map((user) => ({
            followingId: user.requestId ?? user.followingId, // ✅ requestId가 있으면 변환, 없으면 유지
            nickName: user.nickName,
            imageUrl: user.imageUrl,
        }));
    };

    useEffect(() => {
        const fetchFollowList = async () => {
            setLoading(true);
            try {
                // ✅ API 호출 후 데이터 가져오기
                const response = isFollowersView
                    ? await getFollowers()
                    : await getFollowing();

                console.log("🔍 API 응답 데이터:", response); // ✅ 응답 확인

                if (response && Array.isArray(response)) {
                    console.log("✅ 리스트가 설정됨:", response); // ✅ 데이터 확인

                    // ✅ requestId를 followingId로 변환 후 저장
                    setList(transformResponse(response)); // ✅ 변환된 데이터 저장
                } else {
                    console.error("❌ API 응답이 배열이 아님:", response);
                    setList([]);
                }
            } catch (error) {
                console.error("❌ 팔로우 목록 조회 실패:", error);
                setList([]);
            } finally {
                setLoading(false);
            }
        };

        fetchFollowList();
    }, [isFollowersView]);

    const handleGotoMember = (memberId?: number) => {
        if (!memberId || isNaN(memberId)) {
            console.error("❌ Invalid memberId:", memberId);
            return; // ❌ `undefined`일 경우 navigate 실행 안 함
        }

        console.log("🔍 Navigating to user:", memberId);
        navigate(`/user/${memberId}`);
        onClose();
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-20">
            <div className="bg-white dark:bg-gray-700 dark:text-gray-200 p-6 rounded-lg max-w-md w-full shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">
                        {isFollowersView ? "팔로워" : "팔로잉"}
                    </h2>
                    <IoClose
                        className="cursor-pointer text-2xl text-gray-500 hover:text-gray-800"
                        onClick={onClose}
                    />
                </div>
                <hr />
                <br />
                {loading ? (
                    <p className="text-center text-gray-500">불러오는 중...</p>
                ) : list.length > 0 ? (
                    <ul className="max-h-60 overflow-y-auto space-y-4">
                        {list.map((user) => (
                            <li
                                key={user.followingId}
                                className="flex items-center justify-between"
                            >
                                <div className="flex items-center gap-3">
                                    {user.imageUrl ? (
                                        <img
                                            src={user.imageUrl}
                                            className="w-10 h-10 rounded-full object-cover cursor-pointer"
                                            alt={user.nickName}
                                            onClick={() =>
                                                handleGotoMember(
                                                    user.followingId,
                                                )
                                            }
                                        />
                                    ) : (
                                        <FaUserCircle
                                            className="text-gray-400 w-10 h-10 cursor-pointer"
                                            onClick={() =>
                                                handleGotoMember(
                                                    user.followingId,
                                                )
                                            }
                                        />
                                    )}
                                    <p
                                        className="text-gray-800 cursor-pointer hover:underline"
                                        onClick={() =>
                                            handleGotoMember(user.followingId)
                                        }
                                    >
                                        {user.nickName}
                                    </p>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500 text-center">
                        목록이 없습니다.
                    </p>
                )}
            </div>
        </div>
    );
}

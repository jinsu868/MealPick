import { useEffect, useState } from "react";
import {
    getPendingFollowRequests,
    acceptFollowRequest,
    rejectFollowRequest,
} from "@/api/follow";
import { IoClose } from "react-icons/io5";
import { FaUserCircle } from "react-icons/fa";
import { Button } from "@/components/ui/button";
import { FollowUser } from "@/types/Follow";

interface FollowRequestsProps {
    onClose: () => void;
    onAccept: (followerId: number) => void;
}

export default function FollowRequests({
    onClose,
    onAccept,
}: FollowRequestsProps) {
    const [requests, setRequests] = useState<FollowUser[]>([]);
    const [loading, setLoading] = useState(true);

    const fetchFollowRequests = async () => {
        setLoading(true);
        try {
            const data = await getPendingFollowRequests();
            console.log("📌 [모달 열기] 대기 중인 팔로우 요청:", data);
            setRequests(data);
        } catch (error) {
            console.error("❌ 팔로우 요청 목록 조회 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchFollowRequests();
    }, []);

    const handleAccept = async (followerId: number) => {
        console.log("✅ 팔로우 요청 수락:", followerId);
        await acceptFollowRequest(followerId);
        setRequests((prev) =>
            prev.filter((req) => req.followingId !== followerId),
        );
        onAccept(followerId);
    };

    const handleReject = async (followerId: number) => {
        await rejectFollowRequest(followerId);
        setRequests((prev) =>
            prev.filter((req) => req.followingId !== followerId),
        );
    };

    return (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-20">
            <div className="bg-white dark:bg-gray-700 dark:text-gray-200 p-6 rounded-lg max-w-md w-full shadow-lg">
                <div className="flex justify-between items-center mb-4">
                    <h2 className="text-lg font-semibold">팔로우 요청</h2>
                    <IoClose
                        className="cursor-pointer text-2xl text-gray-500 hover:text-gray-800"
                        onClick={onClose}
                    />
                </div>
                <hr />
                <br />
                {loading ? (
                    <p className="text-center text-gray-500">불러오는 중...</p>
                ) : requests.length > 0 ? (
                    <ul className="max-h-60 overflow-y-auto space-y-4">
                        {requests.map((user, idx) => (
                            <li
                                key={idx}
                                className="flex items-center justify-between"
                            >
                                <div className="flex items-center gap-3">
                                    {user.imageUrl ? (
                                        <img
                                            src={user.imageUrl}
                                            className="w-10 h-10 rounded-full object-cover"
                                            alt={user.nickName}
                                        />
                                    ) : (
                                        <FaUserCircle className="text-gray-400 w-10 h-10" />
                                    )}
                                    <p className="text-gray-800">
                                        {user.nickName}
                                    </p>
                                </div>
                                <div className="flex gap-2">
                                    <Button
                                        className="bg-green-500 text-white px-3 py-1 text-sm"
                                        onClick={() =>
                                            handleAccept(user.followingId)
                                        }
                                    >
                                        수락
                                    </Button>

                                    <Button
                                        className="bg-red-500 text-white px-3 py-1 text-sm"
                                        onClick={() =>
                                            handleReject(user.followingId)
                                        }
                                    >
                                        거절
                                    </Button>
                                </div>
                            </li>
                        ))}
                    </ul>
                ) : (
                    <p className="text-gray-500 text-center">
                        받은 요청이 없습니다.
                    </p>
                )}
            </div>
        </div>
    );
}

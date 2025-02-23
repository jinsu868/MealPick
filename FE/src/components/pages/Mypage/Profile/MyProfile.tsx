import { useEffect, useState } from "react";
import { MemberDetail } from "@/types/MemberDetail";
import EditProfile from "./EditProfile";
import FollowList from "./FollowList";
import FollowRequests from "./FollowRequests";
import { FaUserCircle } from "react-icons/fa";
import { Button } from "@/components/ui/button";
import { FiBell, FiMenu, FiArrowLeft } from "react-icons/fi";
import { MdPersonSearch } from "react-icons/md";
import {
    getFollowers,
    getFollowing,
    getPendingFollowRequests,
    acceptFollowRequest,
} from "@/api/follow";
import MealtimeSlotMachine from "./MealtimeSlotMachine.tsx";
import {
    updateUserAlias,
    getMyProfile,
    searchMembers,
    SearchedMember,
} from "@/api/user";
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { logout } from "@/redux/slices/userSlice.ts";
import {
    Dialog,
    DialogContent,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";

interface MyProfileProps {
    profile: MemberDetail;
    setProfile: (profile: MemberDetail) => void;
}

// Debounce function to limit API calls
const debounce = (func: (...args: any[]) => void, delay: number) => {
    let timeoutId: NodeJS.Timeout;
    return (...args: any[]) => {
        clearTimeout(timeoutId);
        timeoutId = setTimeout(() => func(...args), delay);
    };
};

export default function MyProfile({ profile, setProfile }: MyProfileProps) {
    const dispatch = useDispatch();
    const userId = profile.id ?? 0;
    const navigate = useNavigate();

    /* ✅ UI 상태 */
    const [editModalOpen, setEditModalOpen] = useState(false);
    const [followListOpen, setFollowListOpen] = useState(false);
    const [isFollowersView, setIsFollowersView] = useState(true);
    const [followRequestsOpen, setFollowRequestsOpen] = useState(false);
    const [isSlotMachineOpen, setSlotMachineOpen] = useState(false);
    const [isMenuOpen, setIsMenuOpen] = useState(false);
    const [isSearchModalOpen, setIsSearchModalOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");
    const [searchResults, setSearchResults] = useState<SearchedMember[]>([]);

    const [pendingRequestCount, setPendingRequestCount] = useState(0);
    const [followerCount, setFollowerCount] = useState(0);
    const [followingCount, setFollowingCount] = useState(0);
    const [mealtimeResult, setMealtimeResult] = useState<string>("별명 없음");
    const [mealtimeColor, setMealtimeColor] = useState("text-gray-500");

    useEffect(() => {
        const fetchProfile = async () => {
            try {
                const updatedProfile = await getMyProfile();
                setMealtimeResult(updatedProfile.alias ?? "");
                setProfile(updatedProfile);
            } catch (error) {
                console.error("별명 가져오기 실패:", error);
            }
        };

        fetchProfile();
    }, []);

    const fetchFollowStatus = async () => {
        if (!userId) return;

        try {
            const [followers, following, pendingRequests] = await Promise.all([
                getFollowers(),
                getFollowing(),
                getPendingFollowRequests(),
            ]);

            setFollowerCount(followers.length);
            setFollowingCount(following.length);
            setPendingRequestCount(pendingRequests?.length ?? 0);
        } catch (error) {
            console.error("❌ [MyProfile] 팔로우 상태 조회 실패:", error);
        }
    };

    useEffect(() => {
        fetchFollowStatus();
        const handleFocus = () => fetchFollowStatus();
        window.addEventListener("focus", handleFocus);

        return () => {
            window.removeEventListener("focus", handleFocus);
        };
    }, [userId]);

    useEffect(() => {
        if (followListOpen || followRequestsOpen) {
            fetchFollowStatus();
        }
    }, [followListOpen, followRequestsOpen]);

    const handleAcceptFollow = async (requesterId: number) => {
        await acceptFollowRequest(requesterId);
        fetchFollowStatus();
    };

    const formatPicname = (picname: string): string => {
        const words = picname.split(" ");
        if (words.length <= 3) {
            return picname;
        }
        const aliasMatch = picname.match(/\[(.*?)\]/);
        let formattedPicname = aliasMatch ? aliasMatch[0] : picname;
        formattedPicname = formattedPicname.replace(/^\[+/, "").trim();
        return formattedPicname;
    };

    const savePicname = async (newPicname: string) => {
        if (newPicname === mealtimeResult) return;
        try {
            const formattedPicname = formatPicname(newPicname);
            const success = await updateUserAlias(formattedPicname);
            if (success) {
                const updatedProfile = await getMyProfile();
                setMealtimeResult(
                    formatPicname(updatedProfile.alias ?? "별명 없음"),
                );
                setProfile(updatedProfile);
            }
        } catch (error) {
            console.error("❌ Picname 저장 실패:", error);
        }
    };

    const handleLogout = () => {
        localStorage.removeItem("accessToken");
        dispatch(logout());
        navigate("/main");
    };

    const handleMemberSearch = async (query: string) => {
        if (!query.trim()) {
            setSearchResults([]);
            return;
        }

        try {
            const results = await searchMembers(query);
            setSearchResults(results);
        } catch (error) {
            console.error("멤버 검색 실패:", error);
        }
    };

    // Debounced search function
    const debouncedSearch = debounce(handleMemberSearch, 300);

    const handleGotoMember = (memberId: number) => {
        //navigate(`/user/${memberId}`);
        window.location.assign(`/user/${memberId}`);
        setIsSearchModalOpen(false);
    };

    return (
        <div className="w-full flex flex-col items-center">
            {/* ✅ 카드 외부 버튼 영역 */}
            <div className="w-full flex justify-between items-center px-6 mb-4">
                {/* 뒤로가기 버튼 (왼쪽) */}
                <button
                    className="bg-white dark:bg-gray-600 border border-gray-400 rounded-full p-2 hover:bg-gray-100"
                    onClick={() => navigate(-1)}
                >
                    <FiArrowLeft
                        size={20}
                        className="text-gray-700 dark:text-gray-200"
                    />
                </button>

                {/* 오른쪽 그룹 (알림 버튼 + 햄버거 메뉴) */}
                <div className="flex items-center gap-2">
                    {/* 알림 버튼 */}
                    <button
                        className={`relative bg-white dark:bg-gray-600 border border-gray-400 rounded-full p-2 ${
                            pendingRequestCount > 0
                                ? "bg-red-100 border-red-500"
                                : "hover:bg-gray-100"
                        }`}
                        onClick={() => setFollowRequestsOpen(true)}
                    >
                        <FiBell
                            size={20}
                            className="text-gray-700 dark:text-gray-200"
                        />
                        {pendingRequestCount > 0 && (
                            <span className="absolute -top-1.5 -right-1.5 bg-red-500 text-white text-xs font-bold rounded-full px-2 py-0.5">
                                {pendingRequestCount}
                            </span>
                        )}
                    </button>

                    {/* 햄버거 메뉴 버튼 */}
                    <div className="relative">
                        <button
                            className="bg-white dark:bg-gray-600 border border-gray-400 rounded-full p-2 hover:bg-gray-100"
                            onClick={() => setIsMenuOpen(!isMenuOpen)}
                        >
                            <FiMenu
                                size={20}
                                className="text-gray-700 dark:text-gray-200"
                            />
                        </button>

                        {/* ✅ 햄버거 메뉴 드롭다운 (프로필 수정 포함) */}
                        {isMenuOpen && (
                            <div className="absolute top-12 right-0 bg-white dark:bg-gray-600 border border-gray-300 dark:border-gray-500 rounded-lg z-10 transition-all duration-300 min-w-[160px] flex flex-col shadow-md">
                                <div
                                    className="px-5 py-3 cursor-pointer hover:bg-gray-100 dark:hover:bg-gray-500 text-center whitespace-nowrap"
                                    onClick={() => {
                                        setEditModalOpen(true);
                                        setIsMenuOpen(false);
                                    }}
                                >
                                    프로필 수정
                                </div>
                                <div className="border-t border-gray-300 dark:border-gray-500" />
                                <div
                                    className="px-5 py-3 cursor-pointer hover:bg-red-400 dark:hover:bg-red-500 text-center text-red-600 dark:text-red-300 whitespace-nowrap"
                                    onClick={() => {
                                        handleLogout();
                                        setIsMenuOpen(false);
                                    }}
                                >
                                    로그아웃
                                </div>
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <div className="relative w-full flex flex-col mt-2 items-center text-center p-6 bg-white dark:bg-gray-400 dark:text-white shadow-[inset_0_0px_50px_rgba(80,100,120,0.1)] rounded-3xl">
                {profile.profileImage ? (
                    <img
                        src={profile.profileImage}
                        className="w-24 h-24 rounded-full object-cover border border-gray-300"
                        alt="프로필"
                    />
                ) : (
                    <FaUserCircle className="text-gray-400 dark:text-gray-200 w-24 h-24" />
                )}

                <h2 className="text-lg font-medium text-gray-900 mt-3">
                    {profile.nickname || "닉네임 없음"}
                </h2>

                {!mealtimeResult || mealtimeResult === "별명 없음" ? (
                    <Button
                        onClick={() => setSlotMachineOpen(true)}
                        className="mt-2 mb-2 bg-white text-blue-500  border-gray-400 dark:bg-slate-500 dark:text-gray-200 font-semibold border rounded-full px-4 py-2"
                    >
                        Picname 확인하기
                    </Button>
                ) : (
                    <p
                        className={`mt-2 text-sm font-semibold ${mealtimeColor} cursor-pointer`}
                        onClick={() => setSlotMachineOpen(true)}
                    >
                        {mealtimeResult}
                    </p>
                )}

                <MealtimeSlotMachine
                    isOpen={isSlotMachineOpen}
                    onClose={() => setSlotMachineOpen(false)}
                    nickname={profile.nickname}
                    onResultUpdate={(result, color) => {
                        savePicname(result);
                        setMealtimeColor(color);
                    }}
                />

                <div className="mt-2 text-gray-500 dark:text-white text-sm flex justify-center gap-4">
                    <span
                        className="cursor-pointer hover:text-black"
                        onClick={() => {
                            setFollowListOpen(true);
                            setIsFollowersView(true);
                        }}
                    >
                        팔로워{" "}
                        <span className="font-bold">{followerCount}</span>
                    </span>
                    <span className="text-gray-400 dark:text-white">|</span>
                    <span
                        className="cursor-pointer hover:text-black"
                        onClick={() => {
                            setFollowListOpen(true);
                            setIsFollowersView(false);
                        }}
                    >
                        팔로잉{" "}
                        <span className="font-bold">{followingCount}</span>
                    </span>
                </div>

                <div
                    className="mt-2 bg-gray-300 w-12 h-8 flex justify-center items-center rounded-full cursor-pointer"
                    onClick={() => setIsSearchModalOpen(true)}
                >
                    <MdPersonSearch className="w-6 h-6 text-gray-600" />
                </div>

                <Dialog
                    open={isSearchModalOpen}
                    onOpenChange={setIsSearchModalOpen}
                >
                    <DialogContent className="dark:bg-slate-700">
                        <DialogHeader>
                            <DialogTitle className="text-center">
                                멤버 검색
                            </DialogTitle>
                        </DialogHeader>
                        <div className="flex flex-col gap-4">
                            {/* 검색창 */}
                            <div className="flex items-center gap-2">
                                <input
                                    type="text"
                                    value={searchQuery}
                                    onChange={(e) => {
                                        setSearchQuery(e.target.value);
                                        debouncedSearch(e.target.value); // Debounced search
                                    }}
                                    placeholder="이름 또는 닉네임을 입력하세요"
                                    className="w-full px-4 py-2 border border-gray-300 rounded-full focus:outline-none focus:ring-2 dark:text-gray-800 focus:ring-blue-500"
                                />
                            </div>

                            {/* 검색 결과 */}
                            <div className="max-h-64 overflow-y-auto">
                                {searchResults.map((member) => (
                                    <div
                                        key={member.memberId}
                                        className="flex items-center gap-4 p-2 hover:bg-gray-100 dark:hover:bg-gray-600 rounded-lg cursor-pointer"
                                        onClick={() =>
                                            handleGotoMember(member.memberId)
                                        }
                                    >
                                        {/* 프로필 이미지 */}
                                        <div className="w-10 h-10 rounded-full overflow-hidden">
                                            {member.profileImageUrl ? (
                                                <img
                                                    src={member.profileImageUrl}
                                                    alt={member.nickname}
                                                    className="w-full h-full object-cover"
                                                    onError={(e) => {
                                                        e.currentTarget.src =
                                                            "";
                                                        e.currentTarget.className =
                                                            "w-full h-full bg-gray-300";
                                                    }}
                                                />
                                            ) : (
                                                <div className="w-full h-full bg-gray-300"></div>
                                            )}
                                        </div>

                                        {/* 닉네임 및 이름 */}
                                        <div className="flex flex-col">
                                            <span className="font-medium">
                                                {member.nickname}
                                            </span>
                                            <span className="text-sm text-gray-500 dark:text-gray-400">
                                                {member.name}
                                            </span>
                                            {member.isFollowed && (
                                                <span className="text-sm font-light text-gray-500">
                                                    팔로우 중
                                                </span>
                                            )}
                                        </div>
                                    </div>
                                ))}
                            </div>
                        </div>
                    </DialogContent>
                </Dialog>

                {editModalOpen && (
                    <EditProfile
                        profile={profile}
                        setProfile={setProfile}
                        onClose={() => setEditModalOpen(false)}
                    />
                )}

                {followRequestsOpen && (
                    <FollowRequests
                        onClose={() => setFollowRequestsOpen(false)}
                        onAccept={handleAcceptFollow}
                    />
                )}

                {followListOpen && (
                    <FollowList
                        isFollowersView={isFollowersView}
                        onClose={() => setFollowListOpen(false)}
                    />
                )}
            </div>
        </div>
    );
}

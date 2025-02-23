import { CiDark, CiBrightnessUp } from "react-icons/ci";
import { useEffect, useState } from "react";
import { useDispatch, useSelector } from "react-redux";
import { themeActions } from "@/redux/slices/themeSlice";
import Toggle from "@/components/commons/ToggleBtn";
import UserProfile from "./UserProfile";
import { Link, useNavigate } from "react-router-dom";
import { RootState } from "@/redux/store";
import { getMyProfile } from "@/api/user";
import { MemberDetail } from "@/types/MemberDetail";

export default function Header() {
    const dispatch = useDispatch();
    const navigate = useNavigate();
    const [isDark, setIsDark] = useState(false);
    const isLoggedIn = useSelector((state: RootState) => state.user.isLoggedIn);
    const profile = useSelector((state: RootState) => state.user.profile);

    const [MyProfile, setMyProfile] = useState<MemberDetail | null>(null);
    console.log("상태 변경됨");

    const handleDarkMode = () => {
        dispatch(isDark ? themeActions.lightMode() : themeActions.darkMode());
        setIsDark(!isDark);
    };

    const fetchMyProfile = async () => {
        const myProfile = await getMyProfile();
        console.log("프로필 사진 불러옴", myProfile.profileImage);
        setMyProfile(myProfile);
    };

    useEffect(() => {
        fetchMyProfile();
    }, [profile]);

    return (
        <>
            {/* 헤더 */}
            <header className="fixed top-0 left-0 right-0 mx-auto max-w-[600px] w-full h-[52px] flex items-center justify-between px-4 bg-white dark:bg-gray-900 z-10">
                {/* 로고 */}
                <Link
                    to="/main"
                    className="text-lg font-bold text-[#1F1F1F] dark:text-white"
                >
                    MeaLPIC
                </Link>

                {/* 중앙 정렬된 Filter 버튼 */}
                {/* <div className="absolute left-1/2 transform -translate-x-1/2">
                    <button
                        onClick={() => setIsPopupOpen(!isPopupOpen)}
                        className="flex items-center gap-2 text-sm py-1 px-3 text-[#1F1F1F] dark:text-white bg-transparent border-none focus:ring-0 focus:outline-none"
                    >
                        Filter
                    </button>
                </div> */}

                <div className="flex items-center gap-3">
                    {/* 🔆 다크모드 토글 스위치 */}
                    <Toggle
                        initialState={isDark}
                        size="md"
                        onIcon={<CiDark />}
                        offIcon={<CiBrightnessUp />}
                        onColor="bg-gray-700"
                        offColor="bg-yellow-500"
                        onToggle={handleDarkMode}
                    />

                    {/* 🔍 검색 버튼 */}
                    {/* <button
                        onClick={() => navigate("/search")}
                        className="p-1 bg-transparent hover:bg-transparent focus:ring-0 focus:outline-none border-none shadow-none transition-transform transform hover:scale-110"
                    >
                        <FaSearch
                            size={18}
                            className="text-[#1F1F1F] dark:text-white"
                        />
                    </button> */}

                    {isLoggedIn ? (
                        <UserProfile
                            id={MyProfile?.id ?? -1}
                            imageUrl={MyProfile?.profileImage ?? ""}
                            size={24}
                        />
                    ) : (
                        <button
                            onClick={() => navigate("/login")}
                            className="px-3 py-1 bg-blue-600 text-white rounded-full text-sm font-semibold hover:bg-blue-700 transition"
                        >
                            로그인
                        </button>
                    )}
                </div>
            </header>

            {/* 팝업 */}
            {/* <AnimatePresence>
                {isPopupOpen && (
                    <motion.div
                        className="fixed inset-0 z-40 flex items-end justify-center"
                        initial={{ opacity: 0 }}
                        animate={{ opacity: 1 }}
                        exit={{ opacity: 0 }}
                        transition={{ duration: 0.3, ease: "easeInOut" }}
                    > */}
            {/* 배경 */}
            {/* <motion.div
                            className="absolute bottom-0 w-[440px] h-full bg-black bg-opacity-30 backdrop-blur-sm"
                            initial={{ opacity: 0 }}
                            animate={{ opacity: 1 }}
                            exit={{ opacity: 0 }}
                            transition={{ duration: 0.3, ease: "easeInOut" }}
                            onClick={() => setIsPopupOpen(false)}
                        ></motion.div> */}

            {/* 팝업창 */}
            {/* <motion.div
                            className="relative w-[440px] h-[300px] bg-white dark:bg-gray-800 rounded-t-lg px-4 pb-10 z-50 shadow-lg"
                            initial={{ y: "100%", opacity: 0 }}
                            animate={{ y: "0%", opacity: 1 }}
                            exit={{ y: "100%", opacity: 0 }}
                            transition={{ duration: 0.3, ease: "easeInOut" }}
                        > */}
            {/* 닫기 버튼 */}
            {/* <button
                                onClick={() => setIsPopupOpen(false)}
                                className="absolute top-[-60px] left-1/2 -translate-x-1/2 bg-white dark:bg-gray-900 text-[#1F1F1F] dark:text-white rounded-full p-2.5 shadow-md border border-gray-300 dark:border-gray-600 hover:bg-gray-200 dark:hover:bg-gray-700 transition-all duration-200"
                            >
                                <X className="w-5 h-5" />
                            </button>
                        </motion.div>
                    </motion.div>
                )}
            </AnimatePresence> */}
        </>
    );
}

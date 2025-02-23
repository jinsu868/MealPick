import { MdFoodBank, MdLibraryAdd, MdLibraryBooks } from "react-icons/md";
import { NavLink, useNavigate } from "react-router-dom";
import { TbMessageCircleFilled } from "react-icons/tb";

export default function Footer() {
    const navigate = useNavigate();

    const isLoggedIn = () => !!localStorage.getItem("accessToken");

    return (
        <footer className="fixed bottom-0 w-screen max-w-[600px] bg-white dark:bg-slate-700 backdrop-blur-lg border border-gray-200 dark:border-gray-700 rounded-t-3xl">
            <ul className="flex justify-around items-center py-3">
                {/** 홈 */}
                <li>
                    <NavLink
                        to="/"
                        className={({ isActive }) =>
                            `flex flex-col items-center gap-1 transition-all ease-in-out ${
                                isActive
                                    ? "text-blue-500 dark:text-blue-500"
                                    : "text-gray-500 dark:text-gray-400"
                            }`
                        }
                    >
                        <MdFoodBank
                            size="32"
                            className="hover:text-blue-500 dark:hover:text-blue-500 transition"
                        />
                        <span className="text-xs">홈</span>
                    </NavLink>
                </li>

                {/** Pic! */}
                <li>
                    <NavLink
                        to="/main"
                        className={({ isActive }) =>
                            `flex flex-col items-center gap-1 transition-all ease-in-out ${
                                isActive
                                    ? "text-blue-500 dark:text-blue-500"
                                    : "text-gray-500 dark:text-gray-400"
                            }`
                        }
                    >
                        <MdLibraryBooks
                            size="32"
                            className="hover:text-blue-500 dark:hover:text-blue-500 transition"
                        />
                        <span className="text-xs">PICs!</span>
                    </NavLink>
                </li>

                {/** 게시글 추가 (로그인 필요) */}
                <li>
                    <button
                        onClick={() => {
                            if (isLoggedIn()) {
                                navigate("/post");
                            } else {
                                navigate("/login");
                            }
                        }}
                        className="flex flex-col items-center gap-1 text-gray-500 dark:text-gray-400 transition-all ease-in-out hover:text-blue-500 dark:hover:text-blue-500"
                    >
                        <MdLibraryAdd size="32" />
                        <span className="text-xs">PIC 생성</span>
                    </button>
                </li>

                {/** 채팅 (로그인 필요) */}
                <li>
                    <button
                        onClick={() => {
                            if (isLoggedIn()) {
                                navigate("/chat");
                            } else {
                                navigate("/login");
                            }
                        }}
                        className="flex flex-col items-center gap-1 text-gray-500 dark:text-gray-400 transition-all ease-in-out hover:text-blue-500 dark:hover:text-blue-500"
                    >
                        <TbMessageCircleFilled size="32" />
                        <span className="text-xs">채팅</span>
                    </button>
                </li>
            </ul>
        </footer>
    );
}

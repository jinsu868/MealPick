// ChatHeader.tsx

import { FiArrowLeft, FiMoreHorizontal } from "react-icons/fi";
import { useNavigate } from "react-router-dom";

export default function ChatHeader({ roomName }: ChatHeaderProps) {
    const navigate = useNavigate();
    return (
        <div className="flex items-center w-screen max-w-[600px] p-4 border-b bg-white dark:bg-slate-800 dark:text-white">
            <FiArrowLeft
                className="text-2xl cursor-pointer text-gray-600"
                onClick={() => navigate(-1)}
            />
            <div className="flex-1 text-center text-lg font-semibold">
                {roomName}
            </div>
            {/* <FiMoreHorizontal className="text-xl cursor-pointer text-gray-600" /> */}
        </div>
    );
}

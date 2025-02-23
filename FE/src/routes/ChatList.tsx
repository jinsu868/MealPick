import { fetchChatRooms } from "@/api/chat";
import { useWebSocket } from "@/contexts/WebSocketContext";
import { useEffect, useRef, useState } from "react";
import { Link } from "react-router-dom";

export interface ChatRoom {
    id: number;
    name: string;
    displayIdx: number; // 여기서는 상대방의 ID (예: followerId)
    senderName: string | null;
    lastContent: string;
    lastChatAt: string;
    yourImageUrl: string;
}

export interface ChatMessage {
    senderId: string;
    id: number;
    profileImageUrl: string;
    createdAt: string;
    content: string;
}

export default function ChatList() {
    const observerRef = useRef<HTMLDivElement>(null);
    const [chatRooms, setChatRooms] = useState<ChatRoom[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const { isConnected, subscribe, unsubscribe } = useWebSocket();

    // useEffect(() => {
    //     const memberId = localStorage.getItem("userId");
    //     console.log("멤아", memberId, isConnected);
    //     if (isConnected) {
    //         // 채팅방 목록 업데이트를 위한 토픽 구독
    //         subscribe(`/topic/rooms/${memberId}/member`, (response) => {
    //             // 새로운 메시지가 오면 채팅방 목록 업데이트
    //             console.log(response);
    //             setChatRooms(response);
    //         });
    //     }

    //     return () => {
    //         unsubscribe(`/topic/rooms/${memberId}/member`);
    //     };
    // }, [isConnected]);

    const loadMoreRooms = async () => {
        if (isLoading) return;

        setIsLoading(true);
        try {
            const response = await fetchChatRooms();
            setChatRooms(response);
        } catch (error) {
            console.error("Failed to load chat rooms:", error);
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        // 채팅방 목록이 이미 있다면 loadMoreRooms() 호출을 건너뜁니다.
        console.log(chatRooms);
        if (chatRooms.length === 0) {
            loadMoreRooms();
        }

        const memberId = localStorage.getItem("userId");
        // 구독을 바로 등록하고, 반환되는 함수(unsubscribe)를 정리(cleanup)로 사용합니다.
        const unsubscribeTopic = subscribe(
            `/topic/rooms/${memberId}/member`,
            (response) => {
                console.log(response);
                setChatRooms(response);
            },
        );

        return () => {
            unsubscribeTopic();
        };
    }, []);

    useEffect(() => {
        console.log("chatRooms 업데이트:", chatRooms);
    }, [chatRooms]);

    return (
        <div className="w-screen max-w-[600px] h-screen bg-white p-4 relative dark:bg-gray-800 dark:text-white">
            {/* chatHeader 영역 */}
            <div className="flex items-center justify-between p-5 border-b">
                {/* <div className="w-6"></div>
                <h2 className="text-xl font-bold">채팅</h2>
                <button
                    // onClick={handlePlusClick}
                    className="bg-transparent border-none outline-none p-0 m-0"
                >
                    <FiPlus className="w-5 h-5 hover:text-gray-600" />
                </button> */}
            </div>

            <ul>
                {chatRooms &&
                    chatRooms.map((room) => (
                        <li key={room.id}>
                            <Link
                                to={`/chat/${room.id}`}
                                state={{
                                    roomName: room.senderName
                                        ? room.senderName
                                        : room.name,
                                }}
                                className="text-b"
                            >
                                <div className="flex items-center gap-5 p-3 rounded-lg hover:bg-gray-400 cursor-pointer transition text-black dark:text-gray-100">
                                    {/* 추후 상대 프로필 이미지로 변경 요청 */}
                                    <img
                                        src={room.yourImageUrl}
                                        alt="상대방 프로필"
                                        className="w-10 h-10 rounded-full object-cover flex-shrink-0"
                                    />
                                    <div className="flex-1 text-left">
                                        <div className="text-sm font-medium">
                                            {room.senderName === null
                                                ? room.name
                                                : room.senderName}
                                        </div>
                                        <div className="text-sm text-gray-500 dark:text-gray-300 mt-1">
                                            <p className="break-all line-clamp-1 leading-none">
                                                {room.lastContent}
                                            </p>
                                        </div>
                                    </div>

                                    <div className="text-sm text-gray-500 dark:text-gray-300">
                                        {(() => {
                                            const now = new Date();
                                            const chatDate = new Date(
                                                room.lastChatAt,
                                            );

                                            const isSameYear =
                                                now.getFullYear() ===
                                                chatDate.getFullYear();
                                            const isSameMonth =
                                                now.getMonth() ===
                                                chatDate.getMonth();
                                            const isSameDay =
                                                now.getDate() ===
                                                chatDate.getDate();

                                            return isSameYear &&
                                                isSameMonth &&
                                                isSameDay
                                                ? chatDate.toLocaleTimeString(
                                                      "ko-KR",
                                                      {
                                                          hour: "2-digit",
                                                          minute: "2-digit",
                                                          hour12: true, // 오전/오후 표시
                                                      },
                                                  )
                                                : isSameYear
                                                  ? chatDate.toLocaleDateString(
                                                        "ko-KR",
                                                        {
                                                            month: "long",
                                                            day: "numeric",
                                                        },
                                                    )
                                                  : chatDate.toLocaleDateString(
                                                        "ko-KR",
                                                        {
                                                            year: "numeric",
                                                            month: "numeric",
                                                            day: "numeric",
                                                        },
                                                    );
                                        })()}
                                    </div>
                                </div>
                                <hr />
                            </Link>
                        </li>
                    ))}
            </ul>
            <div ref={observerRef}></div>
        </div>
    );
}

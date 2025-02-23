import React, { useEffect, useState, useRef, useMemo } from "react";
import { useLocation, useNavigate, useParams } from "react-router-dom";
import { Client } from "@stomp/stompjs";
import { fetchChatMessages } from "@/api/chat";
import { ChatMessage } from "./ChatList";
import ChatHeader from "@/components/pages/chat/ChatHeader";
import { useWebSocket } from "@/contexts/WebSocketContext";

export default function ChatRoom() {
    const { roomId } = useParams<{ roomId: string }>();
    const { isConnected, subscribe, unsubscribe, publish } = useWebSocket();
    const navigate = useNavigate();
    const location = useLocation();
    const roomName = location.state?.roomName;
    const userId = Number(localStorage.getItem("userId"));
    const [messages, setMessages] = useState<ChatMessage[]>([]);
    const [newMessage, setNewMessage] = useState("");
    const [pageToken, setPageToken] = useState<string | null>(null);
    const [hasNext, setHasNext] = useState<boolean>(true);
    const [isLoading, setIsLoading] = useState<boolean>(false);
    const [isInitialLoad, setIsInitialLoad] = useState(true);
    const [shouldScrollToBottom, setShouldScrollToBottom] = useState(false);

    // const stompClient = useRef<Client | null>(null);
    const messagesContainerRef = useRef<HTMLDivElement>(null);
    const loadingRef = useRef<HTMLDivElement>(null);

    // url 판별하기
    const isValidUrl = (url: string) => {
        const pattern =
            /^(https?:\/\/)?([\da-z\.-]+)\.([a-z\.]{2,6})([\/\w\.-]*)*\/?(\?[^\s#]*)?(#[^\s]*)?$/;
        return pattern.test(url);
    };

    const formatUrl = (url: string) => {
        return url.match(/^(https?:\/\/)/) ? url : `https://${url}`;
    };

    const scrollToBottom = () => {
        if (messagesContainerRef.current) {
            messagesContainerRef.current.scrollTop =
                messagesContainerRef.current.scrollHeight;
        }
    };

    const preserveScroll = () => {
        const container = messagesContainerRef.current;
        if (!container) return 0;
        return container.scrollHeight - container.scrollTop;
    };

    const restoreScroll = (previousHeight: number) => {
        const container = messagesContainerRef.current;
        if (!container) return;
        container.scrollTop = container.scrollHeight - previousHeight;
    };

    // 새 메시지가 추가될 때만 스크롤을 아래로 이동
    useEffect(() => {
        if (shouldScrollToBottom) {
            scrollToBottom();
            setShouldScrollToBottom(false);
        }
    }, [messages, shouldScrollToBottom]);

    const loadMessages = async () => {
        if (!roomId || isLoading || !hasNext) return;

        setIsLoading(true);
        const previousHeight = preserveScroll();

        try {
            const response = await fetchChatMessages(roomId, pageToken);
            const reversedNewMessages = [...response.data].reverse();
            setMessages((prev) => {
                const newUniqueMessages = reversedNewMessages.filter(
                    (newMsg) => !prev.some((msg) => msg.id === newMsg.id),
                );
                return [...newUniqueMessages, ...prev];
            });
            setPageToken(response.pageToken);
            setHasNext(response.hasNext);

            if (!isInitialLoad) {
                setTimeout(() => restoreScroll(previousHeight), 0);
            }
        } catch (error) {
            console.error("Failed to load messages:", error);
        } finally {
            setIsLoading(false);
            if (isInitialLoad) {
                setIsInitialLoad(false);
                setShouldScrollToBottom(true);
            }
        }
    };

    useEffect(() => {
        const observer = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && hasNext && !isLoading) {
                    loadMessages();
                }
            },
            { threshold: 0.1 },
        );

        if (loadingRef.current) {
            observer.observe(loadingRef.current);
        }

        return () => observer.disconnect();
    }, [hasNext, isLoading, pageToken]);

    // useEffect(() => {
    //     if (!token || !roomId) return;

    //     const client = new Client({
    //         brokerURL: "ws://i12a803.p.ssafy.io:8082/allibe-ws",
    //         connectHeaders: {
    //             Authorization: `Bearer ${token}`,
    //         },
    //         debug: (str) => {
    //             console.log("STOMP: " + str);
    //         },
    //         reconnectDelay: 5000,
    //         heartbeatIncoming: 4000,
    //         heartbeatOutgoing: 4000,
    //     });

    //     client.onConnect = () => {
    //         setIsConnected(true);
    //         client.subscribe(`/topic/rooms/${roomId}`, (message) => {
    //             const receivedMessage = JSON.parse(message.body);
    //             setMessages((prev) => {
    //                 if (prev.some((msg) => msg.id === receivedMessage.id)) {
    //                     return prev;
    //                 }
    //                 return [...prev, receivedMessage];
    //             });
    //             setShouldScrollToBottom(true);
    //         });
    //     };

    //     client.onDisconnect = () => {
    //         setIsConnected(false);
    //     };

    //     client.onStompError = (frame) => {
    //         console.error("STOMP error:", frame);
    //     };

    //     stompClient.current = client;
    //     client.activate();

    //     if (messages.length === 0) {
    //         loadMessages();
    //     }

    //     return () => {
    //         if (client.active) {
    //             client.deactivate();
    //         }
    //     };
    // }, [roomId, token]);

    useEffect(() => {
        if (roomId) {
            // roomId가 있을 때 바로 토픽 구독
            const unsubscribeTopic = subscribe(
                `/topic/rooms/${roomId}`,
                (message) => {
                    setMessages((prev) => {
                        if (prev.some((msg) => msg.id === message.id)) {
                            return prev;
                        }
                        return [...prev, message];
                    });
                    setShouldScrollToBottom(true);
                },
            );

            return () => {
                unsubscribeTopic();
            };
        }
    }, [roomId]);

    const handleSendMessage = (e: React.FormEvent) => {
        e.preventDefault();
        if (!newMessage.trim() || !isConnected || !roomId) return;

        const chatRequest = {
            content: newMessage,
            roomId: roomId,
        };

        publish("/app/message", JSON.stringify(chatRequest));
        setNewMessage("");
        setShouldScrollToBottom(true);
    };

    // 메시지 그룹화: 날짜별로만 그룹화
    const groupedMessagesByDate = useMemo(() => {
        const sortedMsgs = [...messages].sort(
            (a, b) =>
                new Date(a.createdAt).getTime() -
                new Date(b.createdAt).getTime(),
        );

        const groups: {
            date: string;
            messageGroups: {
                senderId: string;
                profileImageUrl: string;
                messages: ChatMessage[];
                createdAt: string;
            }[];
        }[] = [];

        sortedMsgs.forEach((msg) => {
            const msgDate = new Date(msg.createdAt).toLocaleDateString();

            // 해당 날짜의 그룹이 없으면 새로 생성
            if (
                groups.length === 0 ||
                groups[groups.length - 1].date !== msgDate
            ) {
                groups.push({
                    date: msgDate,
                    messageGroups: [
                        {
                            senderId: msg.senderId,
                            profileImageUrl: msg.profileImageUrl,
                            messages: [msg],
                        },
                    ],
                });
            } else {
                const currentDateGroup = groups[groups.length - 1];
                const lastMessageGroup =
                    currentDateGroup.messageGroups[
                        currentDateGroup.messageGroups.length - 1
                    ];

                // 같은 발신자의 메시지면 기존 그룹에 추가
                if (
                    lastMessageGroup &&
                    lastMessageGroup.senderId === msg.senderId &&
                    new Date(lastMessageGroup.createdAt).toLocaleTimeString(
                        [],
                        {
                            hour: "2-digit",
                            minute: "2-digit",
                        },
                    ) ===
                        new Date(msg.createdAt).toLocaleTimeString([], {
                            hour: "2-digit",
                            minute: "2-digit",
                        })
                ) {
                    lastMessageGroup.messages.push(msg);
                } else {
                    // 다른 발신자의 메시지면 새 그룹 생성
                    currentDateGroup.messageGroups.push({
                        senderId: msg.senderId,
                        profileImageUrl: msg.profileImageUrl,
                        messages: [msg],
                        createdAt: msg.createdAt,
                    });
                }
            }
        });
        return groups;
    }, [messages]);

    return (
        <div className="flex flex-col h-screen bg-white dark:bg-gray-800">
            <ChatHeader roomName={roomName} />
            <div
                ref={messagesContainerRef}
                className="flex-1 overflow-y-auto px-4 [&::-webkit-scrollbar]:hidden [-ms-overflow-style:none] [scrollbar-width:none]"
            >
                <div ref={loadingRef}>
                    {isLoading && (
                        <div className="text-center text-gray-500">
                            이전 메시지 로드 중...
                        </div>
                    )}
                </div>

                {groupedMessagesByDate.map((dateGroup, dateIndex) => (
                    <div key={dateIndex} className="mb-2">
                        {/* 날짜 헤더 */}
                        <div className="text-center text-gray-500 my-2 text-base">
                            {dateGroup.date}
                        </div>
                        {/* 해당 날짜의 메시지들 */}
                        {dateGroup.messageGroups.map(
                            (messageGroup, groupIndex) => (
                                <div
                                    key={groupIndex}
                                    className={`mb-2 flex items-start gap-2 ${
                                        messageGroup.senderId === userId
                                            ? "flex-row-reverse"
                                            : ""
                                    }`}
                                >
                                    {messageGroup.senderId !== userId && (
                                        <img
                                            src={messageGroup.profileImageUrl}
                                            alt="Profile"
                                            className="w-8 h-8 rounded-full"
                                            onClick={() =>
                                                navigate(
                                                    `/user/${messageGroup.senderId}`,
                                                )
                                            }
                                        />
                                    )}
                                    <div className="flex flex-col">
                                        {messageGroup.messages.map(
                                            (msg, msgIndex) => (
                                                <div
                                                    key={msg.id}
                                                    className={`flex items-end gap-2 max-w-[600px] ${
                                                        msg.senderId === userId
                                                            ? "flex-row-reverse"
                                                            : ""
                                                    }`}
                                                >
                                                    <div
                                                        className={`rounded-3xl px-3 py-2 mt-1 max-w-[250px] xs:max-w-[300px] break-all ${
                                                            messageGroup.senderId ===
                                                            userId
                                                                ? "bg-blue-500 text-white"
                                                                : "bg-gray-100"
                                                        }`}
                                                    >
                                                        <p
                                                            className={`text-left ${
                                                                messageGroup.senderId ===
                                                                userId
                                                                    ? "text-white"
                                                                    : "text-black"
                                                            }`}
                                                        >
                                                            {isValidUrl(
                                                                msg.content,
                                                            ) ? (
                                                                <a
                                                                    href={formatUrl(
                                                                        msg.content,
                                                                    )}
                                                                    className={`${
                                                                        messageGroup.senderId ===
                                                                        userId
                                                                            ? "text-white"
                                                                            : localStorage.getItem(
                                                                                    "darkMode",
                                                                                )
                                                                              ? "text-black"
                                                                              : "text-white"
                                                                    } underline`}
                                                                >
                                                                    {
                                                                        msg.content
                                                                    }
                                                                </a>
                                                            ) : (
                                                                msg.content
                                                            )}
                                                        </p>
                                                    </div>
                                                    {messageGroup.messages
                                                        .length -
                                                        1 ===
                                                    msgIndex ? (
                                                        <span className="text-xs text-gray-500 whitespace-nowrap">
                                                            {new Date(
                                                                msg.createdAt,
                                                            ).toLocaleTimeString(
                                                                [],
                                                                {
                                                                    hour: "2-digit",
                                                                    minute: "2-digit",
                                                                },
                                                            )}
                                                        </span>
                                                    ) : (
                                                        <></>
                                                    )}
                                                </div>
                                            ),
                                        )}
                                    </div>
                                </div>
                            ),
                        )}
                    </div>
                ))}
            </div>

            <form onSubmit={handleSendMessage} className="p-4 border-t">
                <div className="flex gap-2">
                    <input
                        type="text"
                        value={newMessage}
                        onChange={(e) => setNewMessage(e.target.value)}
                        placeholder="메시지를 입력하세요..."
                        className="flex-1 p-2 border rounded-lg dark:text-black"
                        disabled={!isConnected}
                    />
                    <button
                        type="submit"
                        className="px-4 py-2 bg-blue-500 text-white rounded-lg hover:bg-blue-600 disabled:bg-gray-400"
                        disabled={!isConnected}
                    >
                        전송
                    </button>
                </div>
            </form>
        </div>
    );
}

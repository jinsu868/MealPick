import React, {
    createContext,
    useContext,
    useEffect,
    useRef,
    useState,
    useCallback,
} from "react";
import { Client } from "@stomp/stompjs";

// 전역 상태를 관리할 변수들
let stompClient: Client | null = null;
let connectedPages = new Set<string>();
let pendingSubscriptions: Map<
    string,
    Array<(client: Client) => void>
> = new Map();

interface WebSocketContextType {
    client: Client | null;
    isConnected: boolean;
    subscribe: (topic: string, callback: (message: any) => void) => () => void;
    unsubscribe: (topic: string) => void;
    publish: (destination: string, body: any) => void;
}

const WebSocketContext = createContext<WebSocketContextType | null>(null);

// 웹소켓 관리 함수들
function connectWebSocket(): Client | null {
    const token = localStorage.getItem("accessToken");
    if (!token) return null;

    if (!stompClient) {
        stompClient = new Client({
            brokerURL: "wss://i12a803.p.ssafy.io/allibe-ws",
            connectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            disconnectHeaders: {
                Authorization: `Bearer ${token}`,
            },
            debug: (str) => {
                console.log("STOMP: " + str);
            },
            reconnectDelay: 3000,
            heartbeatOutgoing: 3000,
        });

        // 연결 성공 시 대기 중인 구독 처리
        stompClient.onConnect = () => {
            console.log("WebSocket Connected");

            // 대기 중인 모든 구독 처리
            pendingSubscriptions.forEach((callbacks, topic) => {
                callbacks.forEach((callback) => {
                    if (stompClient) callback(stompClient);
                });
            });
            pendingSubscriptions.clear();
        };

        stompClient.activate();
    }
    return stompClient;
}

function disconnectWebSocket(): void {
    if (connectedPages.size === 0 && stompClient && stompClient.active) {
        stompClient.deactivate();
        stompClient = null;
        console.log("WebSocket Disconnected");
    }
}

function registerPage(pageId: string): void {
    connectedPages.add(pageId);
    connectWebSocket();
}

function unregisterPage(pageId: string): void {
    connectedPages.delete(pageId);
    if (connectedPages.size === 0) {
        disconnectWebSocket();
    }
}

interface WebSocketProviderProps {
    children: React.ReactNode;
    pageId?: string;
}

export const WebSocketProvider: React.FC<WebSocketProviderProps> = ({
    children,
    pageId,
}) => {
    const [isConnected, setIsConnected] = useState(false);
    const pageIdRef = useRef<string>(
        pageId || `page-${Math.random().toString(36).substr(2, 9)}`,
    );
    const subscriptionsRef = useRef<Map<string, { id: string }>>(new Map());

    useEffect(() => {
        // 페이지 등록
        registerPage(pageIdRef.current);

        // 토큰 확인
        const token = localStorage.getItem("accessToken");
        if (!token) return;

        // 스톰프 클라이언트 이벤트 핸들러 설정
        if (stompClient) {
            const originalOnConnect = stompClient.onConnect;

            stompClient.onConnect = function () {
                if (originalOnConnect) originalOnConnect.apply(this, arguments);
                setIsConnected(true);
            };

            stompClient.onDisconnect = () => {
                setIsConnected(false);
            };

            stompClient.onStompError = (frame) => {
                console.error("STOMP error:", frame);
            };

            // 이미 연결되어 있으면 상태 업데이트
            if (stompClient.connected) {
                setIsConnected(true);
            }
        }

        // 컴포넌트 언마운트 시 정리
        return () => {
            // 구독 해제
            subscriptionsRef.current.forEach((subscription, topic) => {
                if (stompClient) {
                    stompClient.unsubscribe(subscription.id);
                }
            });
            subscriptionsRef.current.clear();

            // 페이지 등록 해제
            unregisterPage(pageIdRef.current);
        };
    }, []);

    const subscribe = useCallback(
        (topic: string, callback: (message: any) => void) => {
            const token = localStorage.getItem("accessToken");
            if (!token) return () => {};

            const headers = {
                Authorization: `Bearer ${token}`,
            };

            // 구독 함수 정의
            const doSubscribe = (client: Client) => {
                if (subscriptionsRef.current.has(topic)) {
                    // 이미 구독 중인 토픽이면 새로 구독하지 않음
                    return;
                }

                const subscription = client.subscribe(
                    topic,
                    (message) => {
                        try {
                            const data = JSON.parse(message.body);
                            callback(data);
                        } catch (e) {
                            console.error("Error parsing message:", e);
                            callback(message.body);
                        }
                    },
                    headers,
                );

                subscriptionsRef.current.set(topic, { id: subscription.id });
            };

            // 이미 연결되어 있으면 바로 구독
            if (stompClient && isConnected) {
                doSubscribe(stompClient);
            } else {
                // 아직 연결되지 않았으면 대기 목록에 추가
                if (!pendingSubscriptions.has(topic)) {
                    pendingSubscriptions.set(topic, []);
                }
                pendingSubscriptions.get(topic)?.push(doSubscribe);
            }

            // 구독 해제 함수 반환
            return () => unsubscribe(topic);
        },
        [isConnected],
    );

    const unsubscribe = useCallback((topic: string) => {
        if (!stompClient) return;

        const subscription = subscriptionsRef.current.get(topic);
        if (subscription) {
            stompClient.unsubscribe(subscription.id);
            subscriptionsRef.current.delete(topic);
        }

        // 대기 중인 구독도 제거
        pendingSubscriptions.delete(topic);
    }, []);

    const publish = useCallback(
        (destination: string, body: any) => {
            if (!stompClient || !isConnected) return;

            const token = localStorage.getItem("accessToken");
            if (!token) return;

            stompClient.publish({
                destination,
                headers: {
                    Authorization: `Bearer ${token}`,
                },
                body: typeof body === "string" ? body : JSON.stringify(body),
            });
        },
        [isConnected],
    );

    const contextValue = {
        client: stompClient,
        isConnected,
        subscribe,
        unsubscribe,
        publish,
    };

    return (
        <WebSocketContext.Provider value={contextValue}>
            {children}
        </WebSocketContext.Provider>
    );
};

export const useWebSocket = () => {
    const context = useContext(WebSocketContext);
    if (!context) {
        throw new Error("useWebSocket must be used within a WebSocketProvider");
    }
    return context;
};

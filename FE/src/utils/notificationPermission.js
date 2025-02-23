import { getToken } from "firebase/messaging";
import { messaging } from "./initFirebase"; // messaging 가져오기
import { sendToken } from "@/api/fcm";

export async function handleAllowNotification() {
    try {
        const permission = await Notification.requestPermission();

        if (permission === "granted") {
            const token = await getToken(messaging, {
                vapidKey: import.meta.env.VITE_VAPID_KEY,
            });
            if (token) {
                // TODO: FCM token 백엔드로 보내기기
                console.log("✅ FCM 토큰:", token);
                sendToken(token);
            } else {
                alert("❌ 토큰을 가져올 수 없습니다. 권한을 확인하세요.");
            }
            return token;
        } else if (permission === "denied") {
            console.log("알림 권한이 차단되었습니다.");
        }
    } catch (error) {
        console.error("❌ 푸시 토큰 가져오기 실패:", error);
    }
}

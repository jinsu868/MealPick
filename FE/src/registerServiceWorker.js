import { initializeApp } from "firebase/app";

export function registerServiceWorker(firebaseConfig) {
    if ("serviceWorker" in navigator) {
        navigator.serviceWorker
            .register("/firebase-messaging-sw.js")
            .then((registration) => {
                console.log("✅ 서비스 워커 등록 완료:", registration.scope);

                // 환경 변수를 서비스 워커에 전달
                if (registration.active) {
                    registration.active.postMessage({
                        type: "SET_FIREBASE_CONFIG",
                        config: firebaseConfig,
                    });
                }
            })
            .catch((err) => {
                console.error("❌ 서비스 워커 등록 실패:", err);
            });
    }
}

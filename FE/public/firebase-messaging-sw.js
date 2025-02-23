importScripts(
    "https://www.gstatic.com/firebasejs/10.8.0/firebase-app-compat.js",
);
importScripts(
    "https://www.gstatic.com/firebasejs/10.8.0/firebase-messaging-compat.js",
);

// 앱이 실행될 때 권한 요청
self.addEventListener("DOMContentLoaded", async () => {
    // 알림 권한 요청
    if ("Notification" in window) {
        try {
            const permission = await Notification.requestPermission();
            console.log("알림 권한:", permission);
        } catch (error) {
            console.error("알림 권한 요청 실패:", error);
        }
    }

    // 위치 권한 요청
    if ("geolocation" in navigator) {
        try {
            await navigator.geolocation.getCurrentPosition(() => {
                console.log("위치 권한 승인됨");
            });
        } catch (error) {
            console.error("위치 권한 요청 실패:", error);
        }
    }
});

self.addEventListener("install", (event) => {
    console.log("fcm sw install..");
    self.skipWaiting();
});

self.addEventListener("activate", (event) => {
    console.log("✅ FCM 서비스 워커가 실행되었습니다.");
});

self.addEventListener("message", (event) => {
    if (event.data && event.data.type === "SET_FIREBASE_CONFIG") {
        self.firebaseConfig = event.data.config;
        firebase.initializeApp(self.firebaseConfig);
        self.messaging = firebase.messaging();
    }
});

self.addEventListener("push", function (e) {
    if (!e.data.json()) return;

    const resultData = e.data.json().notification;
    const notificationTitle = resultData.title;
    const notificationOptions = {
        body: resultData.body,
        icon: resultData.image, // 웹 푸시 이미지는 icon
        tag: resultData.tag,
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});

self.messaging?.onBackgroundMessage((payload) => {
    console.log("📩 백그라운드 메시지 수신:", payload);

    const notificationTitle = payload.notification?.title || "새로운 알림";
    const notificationOptions = {
        body: payload.notification?.body || "내용 없음",
        icon: payload.notification?.icon || "/default-icon.png",
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
    self.clients
        .matchAll({ type: "window", includeUncontrolled: true })
        .then((clients) => {
            clients.forEach((client) => {
                client.postMessage({
                    type: "PUSH_NOTIFICATION_RECEIVED",
                    payload,
                });
            });
        });
});

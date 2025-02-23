import { RefObject } from "react";

export const startCamera = async (
    videoRef: RefObject<HTMLVideoElement>,
    facingMode: "user" | "environment" = "environment",
): Promise<void> => {
    try {
        if (!videoRef.current) return;

        const constraints: MediaStreamConstraints = {
            video: { facingMode: { ideal: facingMode } }, // `ideal`로 변경하여 오류 방지
        };

        const stream: MediaStream =
            await navigator.mediaDevices.getUserMedia(constraints);
        videoRef.current.srcObject = stream;
    } catch (error) {
        console.error("카메라 접근 오류:", error);
        alert("카메라에 접근할 수 없습니다. 권한을 확인해주세요.");
    }
};

// `startCamera`와 `capturePhoto`를 모두 내보내도록 수정
export const capturePhoto = (
    videoRef: RefObject<HTMLVideoElement>,
    canvasRef: RefObject<HTMLCanvasElement>,
    callback: (image: string) => void,
    blurEnabled: boolean, // ✅ 블러 적용 여부 추가
    blurShape: "circle" | "square" = "circle", // ✅ 블러 형태 선택
) => {
    if (!videoRef.current) {
        console.error("❌ 카메라를 찾을 수 없음");
        return;
    }

    let canvas = canvasRef.current;
    if (!canvas) {
        canvas = document.createElement("canvas");
    }

    const video = videoRef.current;
    const context = canvas.getContext("2d");

    if (!context) {
        console.error("❌ 캔버스 컨텍스트를 찾을 수 없음");
        return;
    }

    // 캔버스 크기를 비디오 해상도에 맞춤
    canvas.width = video.videoWidth;
    canvas.height = video.videoHeight;
    context.drawImage(video, 0, 0, canvas.width, canvas.height);

    // ✅ 블러 적용 로직 추가
    if (blurEnabled) {
        context.fillStyle = "rgba(0, 0, 0, 0.5)"; // 블러 효과 색상
        context.filter = "blur(15px)";

        if (blurShape === "circle") {
            // ✅ 원형 블러 적용
            context.beginPath();
            context.arc(
                canvas.width / 2,
                canvas.height / 2,
                canvas.width * 0.35, // 중앙 원 크기
                0,
                Math.PI * 2,
            );
            context.clip();
        } else {
            // ✅ 사각형 블러 적용
            context.fillRect(0, 0, canvas.width, canvas.height);
            context.clearRect(
                canvas.width * 0.15,
                canvas.height * 0.15,
                canvas.width * 0.7,
                canvas.height * 0.7,
            );
        }
    }

    // 촬영된 이미지를 Base64로 변환하여 저장
    const imageData = canvas.toDataURL("image/png");
    callback(imageData);
};

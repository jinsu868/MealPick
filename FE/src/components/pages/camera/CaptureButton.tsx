import { useState } from "react";
import { capturePhoto } from "@/components/utils/cameraUtils";

interface Props {
    videoRef: React.RefObject<HTMLVideoElement>;
    canvasRef: React.RefObject<HTMLCanvasElement>;
    setPhotos: (data: {
        capturedImages: string[];
        capturedFiles: File[];
    }) => void;
    blurEnabled: boolean; // ✅ 블러 적용 여부 추가
    blurShape: "circle" | "square"; // ✅ 블러 모양 선택
}

export default function CaptureButton({
    videoRef,
    canvasRef,
    setPhotos,
    blurEnabled,
    blurShape,
}: Props) {
    const [recentPhoto, setRecentPhoto] = useState<string | null>(null);
    const [isVisible, setIsVisible] = useState(false);

    const base64ToFile = (base64: string, filename: string) => {
        const arr = base64.split(",");
        const mime = arr[0].match(/:(.*?);/)?.[1] || "image/png";
        const bstr = atob(arr[1]);
        let n = bstr.length;
        const u8arr = new Uint8Array(n);
        while (n--) {
            u8arr[n] = bstr.charCodeAt(n);
        }
        return new File([u8arr], filename, { type: mime });
    };

    const handleCapture = () => {
        capturePhoto(
            videoRef,
            canvasRef,
            (capturedImage: string) => {
                const file = base64ToFile(
                    capturedImage,
                    `photo_${Date.now()}.png`,
                );

                setRecentPhoto(capturedImage);
                setIsVisible(true);

                setPhotos({
                    capturedImages: [capturedImage],
                    capturedFiles: [file],
                });

                setTimeout(() => {
                    setIsVisible(false);
                }, 1000);
            },
            blurEnabled,
            blurShape,
        );
    };

    return (
        <div className="relative flex items-center space-x-4">
            <button
                onClick={handleCapture}
                className="w-16 h-16 rounded-full border-4 border-white bg-[#474F64] flex items-center justify-center"
            ></button>

            {recentPhoto && (
                <div
                    className={`absolute left-20 bottom-0 w-16 h-16 rounded-md overflow-hidden transition-all duration-500 ${
                        isVisible
                            ? "opacity-100 translate-y-0"
                            : "opacity-0 translate-y-10"
                    }`}
                >
                    <img
                        src={recentPhoto}
                        alt="Recent capture"
                        className="w-full h-full object-cover"
                    />
                </div>
            )}
        </div>
    );
}

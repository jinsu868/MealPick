import { useState, useRef, useEffect, RefObject } from "react";
import CameraView from "@/components/pages/camera/CameraView";
import SettingsPanel from "@/components/pages/camera/SettingsPanel";
import CaptureButton from "@/components/pages/camera/CaptureButton";
import { startCamera } from "@/components/utils/cameraUtils";

interface CameraPageProps {
    onCaptureImage: (data: { images: string[]; files: File[] }) => void;
    onBack: () => void;
}

export default function CameraPage({ onCaptureImage }: CameraPageProps) {
    const videoRef: RefObject<HTMLVideoElement> = useRef(null);
    const canvasRef: RefObject<HTMLCanvasElement> = useRef(null);
    const [photos, setPhotos] = useState<string[]>([]);
    const [gridEnabled, setGridEnabled] = useState(false);
    const [blurEnabled, setBlurEnabled] = useState(false);
    const [blurShape] = useState<"circle" | "square">("circle");

    useEffect(() => {
        startCamera(videoRef);
    }, []);

    const handleCapture = (data: {
        capturedImages: string[];
        capturedFiles: File[];
    }) => {
        setPhotos([...photos, ...data.capturedImages]);
        onCaptureImage({
            images: data.capturedImages,
            files: data.capturedFiles,
        });
    };

    return (
        <div className="relative w-screen max-w-[600px] flex flex-col">
            {/* ✅ 상단 UI: 설정 패널 */}
            <div className="w-full py-3 flex justify-center">
                <SettingsPanel
                    toggleGrid={() => setGridEnabled(!gridEnabled)}
                    toggleBlur={() => setBlurEnabled(!blurEnabled)}
                    gridEnabled={gridEnabled}
                    blurEnabled={blurEnabled}
                />
            </div>

            {/* ✅ 1:1 비율 카메라 화면 */}
            <div className="relative w-screen max-w-[600px] aspect-square">
                <CameraView
                    videoRef={videoRef}
                    gridEnabled={gridEnabled}
                    blurEnabled={blurEnabled}
                />
            </div>

            {/* ✅ 하단 UI: 촬영 버튼 포함 */}
            <div className="flex flex-col justify-center mt-6 text-white flex-grow">
                <div className="flex items-center justify-center">
                    <CaptureButton
                        videoRef={videoRef}
                        canvasRef={canvasRef}
                        setPhotos={handleCapture}
                        blurEnabled={blurEnabled}
                        blurShape={blurShape}
                    />
                </div>
            </div>
        </div>
    );
}

import { RefObject } from "react";

interface Props {
    videoRef: RefObject<HTMLVideoElement>;
    gridEnabled: boolean;
    blurEnabled: boolean;
}

export default function CameraView({
    videoRef,
    gridEnabled,
    blurEnabled,
}: Props) {
    return (
        <div className="relative w-full flex justify-center items-center aspect-square">
            <video
                ref={videoRef}
                autoPlay
                playsInline
                className="w-full object-cover aspect-square"
            />

            {/* ✅ 그리드 오버레이 */}
            {gridEnabled && (
                <div className="absolute inset-0 grid grid-cols-3 grid-rows-3 border-white/30 pointer-events-none">
                    {[...Array(9)].map((_, i) => (
                        <div key={i} className="border border-white/30"></div>
                    ))}
                </div>
            )}

            {/* ✅ 블러 효과 */}
            {blurEnabled && (
                <div className="absolute inset-0 pointer-events-none">
                    <div className="absolute inset-0 flex items-center justify-center">
                        <div className="w-[85%] h-[85%] rounded-full bg-transparent border-4 border-white pointer-events-none"></div>
                    </div>
                    <div className="absolute inset-0 bg-black/30 backdrop-blur-md mask"></div>
                    <style>
                        {`
                            .mask {
                                -webkit-mask: radial-gradient(circle, transparent 35%, rgba(0, 0, 0, 0.9) 60%);
                                mask: radial-gradient(circle, transparent 35%, rgba(0, 0, 0, 0.9) 60%);
                            }
                        `}
                    </style>
                </div>
            )}
        </div>
    );
}

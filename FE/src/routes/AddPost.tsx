import { useState } from "react";
import Gallery from "@/components/pages/AddPost/Gallery";
import CameraPage from "@/components/pages/camera/Camera";
import ImagePreview from "@/components/pages/AddPost/ImagePreview";
import PostEditor from "@/components/pages/AddPost/PostEditor";
import { useNavigate } from "react-router-dom";
import { IoMdArrowRoundBack } from "react-icons/io";
import { createPost } from "@/api/post";

export default function AddPost() {
    const [activeTab, setActiveTab] = useState<"gallery" | "camera">("gallery");
    const [selectedImages, setSelectedImages] = useState<string[]>([]);
    const [selectedFiles, setSelectedFiles] = useState<File[]>([]);
    const [isConfirming, setIsConfirming] = useState<boolean>(false);
    const navigate = useNavigate();

    const handleSelectImages = (data: { images: string[]; files: File[] }) => {
        setSelectedImages((prev) => [...prev, ...data.images]);
        setSelectedFiles((prev) => [...prev, ...data.files]);
    };

    const handleCaptureImage = (data: { images: string[]; files: File[] }) => {
        setSelectedImages((prev) => [...prev, ...data.images]);
        setSelectedFiles((prev) => [...prev, ...data.files]);
    };

    const handleRemoveImage = (index: number) => {
        setSelectedImages((prev) => prev.filter((_, i) => i !== index));
        setSelectedFiles((prev) => prev.filter((_, i) => i !== index));
    };

    const handleConfirmSelection = () => {
        if (selectedImages.length === 0) {
            alert("이미지를 최소 1개 이상 선택하세요");
            return;
        }
        setIsConfirming(true);
    };

    const handlePostSubmit = async (
        title: string,
        content: string,
        mealTime: string,
        foodtag: string,
        tags: string[],
        images: File[],
        latitude: number,
        longitude: number,
    ) => {
        if (!title || !content || !foodtag) {
            alert("제목, 내용, 음식 태그는 필수 입력사항입니다.");
            return;
        }
        try {
            await createPost(
                title,
                content,
                mealTime,
                foodtag,
                tags,
                images,
                latitude,
                longitude,
            );
            navigate("/");
        } catch (error) {
            console.error("게시글 업로드 중 오류 발생:", error);
        }
    };

    return (
        <div className="w-full max-w-[600px] h-screen mx-auto flex flex-col items-center">
            <div
                className="fixed top-4 left-4 rounded-full w-16 h-16 bg-slate-400 cursor-pointer hover:bg-slate-500 flex justify-center items-center z-50"
                onClick={() => navigate(-1)}
            >
                <IoMdArrowRoundBack className="w-10 h-10 flex items-center justify-center text-white text-2xl" />
            </div>

            {!isConfirming ? (
                <>
                    <div className="flex-1 w-full flex flex-col items-center justify-between">
                        {activeTab === "gallery" ? (
                            <Gallery onSelectImages={handleSelectImages} />
                        ) : (
                            <div className="relative w-full h-full flex flex-col items-center">
                                <CameraPage
                                    onCaptureImage={handleCaptureImage}
                                    onBack={() => setActiveTab("gallery")}
                                />
                            </div>
                        )}
                    </div>

                    <div
                        className={`fixed bottom-36 w-full max-w-[600px] px-3 transition-all duration-300 ${
                            activeTab !== "gallery"
                                ? "max-[420px]:opacity-0 max-[420px]:translate-y-10 max-[420px]:pointer-events-none"
                                : "opacity-100 translate-y-0"
                        }`}
                    >
                        <ImagePreview
                            images={selectedImages}
                            onRemove={handleRemoveImage}
                        />
                    </div>

                    <div className="fixed bottom-0 w-full max-w-[600px] bg-white dark:bg-slate-700 border-t border-gray-300 flex flex-col py-3">
                        <div className="flex justify-around">
                            <button
                                onClick={() => setActiveTab("gallery")}
                                className={`flex-1 text-center py-2 font-semibold transition ${activeTab === "gallery" ? "text-blue-600 dark:text-white border-b-2 border-blue-600" : "text-gray-500"}`}
                            >
                                갤러리
                            </button>
                            <button
                                onClick={() => {
                                    setActiveTab("camera");
                                }}
                                className={`flex-1 text-center py-2 font-semibold transition ${activeTab === "camera" ? "text-blue-600 dark:text-white border-b-2 border-blue-600" : "text-gray-500"}`}
                            >
                                카메라
                            </button>
                        </div>

                        <button
                            onClick={handleConfirmSelection}
                            className="bg-blue-600 text-white w-[90%] mx-auto my-2 py-3 rounded-lg font-bold"
                        >
                            다음
                        </button>
                    </div>
                </>
            ) : (
                <PostEditor
                    selectedImages={selectedImages}
                    selectedFiles={selectedFiles}
                    onCancel={() => setIsConfirming(false)}
                    onSubmit={handlePostSubmit} // ✅ handlePostSubmit 유지
                />
            )}
        </div>
    );
}

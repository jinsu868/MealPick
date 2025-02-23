import { useRef } from "react";

export default function Gallery({
    onSelectImages,
}: {
    onSelectImages: (data: { images: string[]; files: File[] }) => void;
}) {
    const fileInputRef = useRef<HTMLInputElement>(null);

    const handleFileChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        if (event.target.files) {
            const files = Array.from(event.target.files);
            const imageUrls = files.map((file) => URL.createObjectURL(file));

            onSelectImages({ images: imageUrls, files });
        }
    };

    return (
        <div className="w-screen max-w-[600px] h-screen flex flex-col justify-center items-center gap-4">
            <input
                type="file"
                accept="image/*"
                ref={fileInputRef}
                multiple
                style={{ display: "none" }}
                onChange={handleFileChange}
            />
            <button
                onClick={() => fileInputRef.current?.click()}
                className="bg-blue-500 text-white px-4 py-2 rounded-lg"
            >
                갤러리에서 사진 선택
            </button>
        </div>
    );
}

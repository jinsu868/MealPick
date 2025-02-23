import GalleryImageCard from "@/components/cards/Galleryimagecard/GalleryImageCard";
import { PlusCircle } from "lucide-react";
import { useNavigate } from "react-router-dom";

export default function GalleryList() {
    const images = [...Array(100)].map((_, index) => ({
        id: index + 1,
        url: "",
        alt: `Image ${index + 1}`,
    }));
    const navigate = useNavigate();
    const addButtonIndexes = [...Array(Math.floor(images.length / 30))].map(
        (_, i) => i * 30 + Math.floor(Math.random() * 30),
    );

    return (
        <div
            id="gallery-container"
            className="grid grid-cols-3 gap-1 overflow-y-auto h-auto px-2"
        >
            {images.map((image, index) =>
                addButtonIndexes.includes(index) ? (
                    <div
                        key={`add-button-${index}`}
                        className="relative w-full bg-gray-200 dark:bg-gray-700 rounded-lg aspect-square flex flex-col items-center justify-center cursor-pointer border-2 border-dashed border-gray-400 dark:border-gray-500"
                        onClick={() => navigate("/camera")}
                    >
                        <PlusCircle
                            size={32}
                            className="text-gray-600 dark:text-gray-300"
                        />
                        <span className="text-gray-600 dark:text-gray-300 text-sm mt-1">
                            추가하기
                        </span>
                    </div>
                ) : (
                    <GalleryImageCard key={image.id} image={image} />
                ),
            )}
        </div>
    );
}

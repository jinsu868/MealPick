export default function GalleryImageCard({
    image,
}: {
    image: { id: number; url: string; alt: string };
}) {
    return (
        <div className="relative w-full bg-gray-300 dark:bg-gray-700 rounded-lg aspect-square flex items-center justify-center cursor-pointer">
            <img
                src={image.url}
                alt={image.alt}
                className="w-full h-full object-cover rounded-lg"
            />
            {/* 대체 텍스트는 이미지 로드 실패시에만 보이도록 수정 */}
            <span className="absolute inset-0 flex items-center justify-center text-gray-500 dark:text-gray-400 opacity-0 hover:opacity-100">
                {image.alt}
            </span>
        </div>
    );
}

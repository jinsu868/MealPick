import { FoodTag } from "@/types/FoodTag";
import { Album } from "@/api/post";
import { useEffect } from "react";

interface AlbumListProps {
    albums: Album[];
    isMyPage: boolean;
    id: number;
    setSelectedAlbum: (tag: FoodTag) => void;
    fetchAlbumData: (id: number) => void;
}

const DEFAULT_IMAGE_URL =
    "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='100%' height='100%' fill='%23999'%3E%3Ccircle cx='12' cy='12' r='10' fill='%23eee'/%3E%3Ctext x='50%' y='50%' font-size='14' text-anchor='middle' dy='.3em' fill='%23999'%3E?%3C/text%3E%3C/svg%3E";

export default function AlbumList({
    albums,
    setSelectedAlbum,
    fetchAlbumData,
    isMyPage,
    id,
}: AlbumListProps) {
    const formatTimeAgo = (dateString: string) => {
        const date = new Date(dateString);
        const now = new Date();

        const diffMs = now.getTime() - date.getTime();
        const diffHours = Math.floor(diffMs / (1000 * 60 * 60));
        const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));

        const isToday = now.toDateString() === date.toDateString();
        return isToday
            ? diffHours > 0
                ? `${diffHours}시간 전`
                : "방금 전"
            : `${diffDays}일 전`;
    };

    useEffect(() => {
        fetchAlbumData(id);
        console.log("id", id);
        console.log("왜안되는데");
    }, [id, isMyPage]);

    return (
        <div className="grid grid-cols-2 gap-6 pb-8">
            {albums.map((album) => {
                const imageCount = album.postImages.length;

                return (
                    <div
                        key={album.foodTag}
                        className="shadow-[inset_0_0px_20px_rgba(0,0,50,0.1)] dark:bg-gray-700 rounded-lg p-4 cursor-pointer hover:scale-105 transition-all duration-500 flex flex-col"
                        onClick={() =>
                            setSelectedAlbum(album.foodTag as FoodTag)
                        }
                    >
                        <div className="flex justify-between items-center mb-2">
                            <span className="font-medium text-gray-700 dark:text-white">
                                {FoodTag[
                                    album.foodTag as unknown as keyof typeof FoodTag
                                ] ?? "기타"}
                            </span>
                        </div>

                        <div
                            className={`rounded-lg overflow-hidden mb-3 aspect-square grid gap-1 ${
                                imageCount === 1
                                    ? "grid-cols-1 grid-rows-1"
                                    : imageCount === 2
                                      ? "grid-cols-1 grid-rows-2"
                                      : "grid-cols-2 grid-rows-2"
                            }`}
                        >
                            {album.postImages.map((img, index) => (
                                <img
                                    key={index}
                                    src={img}
                                    className={`w-full h-full object-cover rounded ${
                                        imageCount === 3 && index === 0
                                            ? "col-span-2 row-span-2"
                                            : ""
                                    }`}
                                    alt="앨범 이미지"
                                    onError={(e) => {
                                        (e.target as HTMLImageElement).src =
                                            DEFAULT_IMAGE_URL;
                                    }}
                                />
                            ))}
                        </div>

                        <div className="mt-auto pt-3 border-t border-gray-200 dark:border-gray-600 text-sm text-gray-600 dark:text-white">
                            게시글 {album.postCount}개 · 마지막 :{" "}
                            {formatTimeAgo(album.lastCreatedAt)}
                        </div>
                    </div>
                );
            })}
        </div>
    );
}

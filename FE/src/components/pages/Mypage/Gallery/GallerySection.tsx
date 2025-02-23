import { useState, useEffect } from "react";
import AlbumList from "./AlbumList";
import AlbumDetail from "./AlbumDetail";
import GalleryFilter from "./GalleryFilter";
import MealTimeCount from "@/components/pages/Mypage/MealTimeGallery/MealTImeCount";
import ScrapList from "../Scrab/ScrapList";
import { FoodTag } from "@/types/FoodTag";
import { getMyPostAlbums, Album, getOtherPostAlbums } from "@/api/post";

interface GallerySectionProps {
    id?: number;
    isMyPage: boolean;
}

export default function GallerySection({ id, isMyPage }: GallerySectionProps) {
    const [albums, setAlbums] = useState<Album[]>([]);
    const [selectedAlbum, setSelectedAlbum] = useState<FoodTag | null>(null);
    const [loading, setLoading] = useState<boolean>(true);
    const [selectedFilter, setSelectedFilter] = useState<"ALBUM" | "SCRAP">(
        "ALBUM",
    );
    const [hasFetched, setHasFetched] = useState<{
        album: boolean;
        scrap: boolean;
    }>({
        album: false,
        scrap: false,
    });

    useEffect(() => {
        setHasFetched({ album: false, scrap: false });
    }, [id]);

    const fetchData = async () => {
        setLoading(true);
        try {
            if (selectedFilter === "ALBUM" && !hasFetched.album) {
                if (isMyPage) {
                    const result = await getMyPostAlbums();
                    setAlbums(result);
                } else {
                    const result = await getOtherPostAlbums(id ?? -1);
                    setAlbums(result);
                }
                setHasFetched((prev) => ({ ...prev, album: true }));
            } else if (selectedFilter === "SCRAP" && !hasFetched.scrap) {
                setHasFetched((prev) => ({ ...prev, scrap: true }));
            }
        } catch (error) {
            console.error("갤러리 데이터 요청 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (!id) return;
        fetchData();
    }, [id, selectedFilter, isMyPage]);

    const handleAlbumEmpty = (foodTag: FoodTag) => {
        setAlbums((prevAlbums) =>
            prevAlbums.filter((album) => album.foodTag !== foodTag),
        );
    };

    useEffect(() => {
        if (id) {
            setSelectedAlbum(null);
            setSelectedFilter("ALBUM");
            setHasFetched({ album: false, scrap: false });
            setAlbums([]);
        }
    }, [id]);

    return (
        <div className="w-full max-w-[600px]">
            <MealTimeCount id={id} isMyPage={isMyPage} />

            {selectedAlbum ? (
                <AlbumDetail
                    isMypage={isMyPage}
                    userId={id}
                    foodTag={selectedAlbum}
                    setSelectedAlbum={setSelectedAlbum}
                    onAlbumEmpty={handleAlbumEmpty}
                />
            ) : (
                <>
                    {isMyPage ? (
                        <div>
                            <GalleryFilter
                                selectedFilter={selectedFilter}
                                setSelectedFilter={setSelectedFilter}
                            />

                            {!loading && selectedFilter === "ALBUM" && (
                                <AlbumList
                                    id={id ?? -1}
                                    isMyPage={isMyPage}
                                    albums={albums}
                                    setSelectedAlbum={setSelectedAlbum}
                                    fetchAlbumData={fetchData}
                                />
                            )}
                            {!loading && selectedFilter === "SCRAP" && (
                                <ScrapList />
                            )}
                        </div>
                    ) : (
                        <div className="w-full flex flex-col items-center">
                            <div className="text-blue-500 font-semibold w-full border-b-2 py-4 mb-4 border-gray-100">
                                앨범
                            </div>
                            <AlbumList
                                id={id ?? -1}
                                isMyPage={isMyPage}
                                albums={albums}
                                setSelectedAlbum={setSelectedAlbum}
                                fetchAlbumData={fetchData}
                            />
                        </div>
                    )}
                </>
            )}
        </div>
    );
}

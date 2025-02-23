import { useState, useEffect, useMemo, useCallback } from "react";
import { ScrapDefault } from "@/types/Scrap";
import Scrap from "./Scrap";
import { deleteScrab, getPostDetail } from "@/api/post";
import { getScrappedPosts } from "@/api/userpost";
import { PostDetailDefault } from "@/types/PostDetail";
import Post from "@/components/Post/Post";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";

export default function ScrapList() {
    const [scraps, setScraps] = useState<ScrapDefault[]>([]);
    const [postDetails, setPostDetails] = useState<{
        [key: number]: PostDetailDefault;
    }>({});
    const [loadingScraps, setLoadingScraps] = useState<{
        [key: number]: boolean;
    }>({});
    const [selectedPost, setSelectedPost] = useState<PostDetailDefault | null>(
        null,
    );
    const [isFetching, setIsFetching] = useState(true);
    const [selectedScraps, setSelectedScraps] = useState<number[]>([]);

    useEffect(() => {
        const fetchScraps = async () => {
            try {
                setIsFetching(true);
                const response = await getScrappedPosts();
                const scrapData = response.content;

                setScraps((prevScraps) => {
                    const isSame =
                        JSON.stringify(prevScraps) ===
                        JSON.stringify(scrapData);
                    return isSame ? prevScraps : scrapData;
                });

                const newScraps = scrapData.filter(
                    (scrap) => !postDetails[scrap.postId],
                );

                if (newScraps.length > 0) {
                    const newLoadingState = newScraps.reduce(
                        (acc, scrap) => {
                            acc[scrap.postId] = true;
                            return acc;
                        },
                        {} as { [key: number]: boolean },
                    );

                    setLoadingScraps((prev) => ({
                        ...prev,
                        ...newLoadingState,
                    }));

                    newScraps.forEach(async (scrap) => {
                        try {
                            const detail = await getPostDetail(scrap.postId);
                            setPostDetails((prev) => ({
                                ...prev,
                                [scrap.postId]: detail,
                            }));
                        } catch (error) {
                            console.error("게시글 상세 요청 실패:", error);
                        } finally {
                            setTimeout(() => {
                                setLoadingScraps((prev) => ({
                                    ...prev,
                                    [scrap.postId]: false,
                                }));
                            }, 400);
                        }
                    });
                }
            } catch (error) {
                console.error("스크랩 데이터 불러오기 실패:", error);
            } finally {
                setIsFetching(false);
            }
        };

        fetchScraps();
    }, []);

    const toggleScrapSelection = useCallback((postId: number) => {
        setSelectedScraps((prevSelected) =>
            prevSelected.includes(postId)
                ? prevSelected.filter((id) => id !== postId)
                : [...prevSelected, postId],
        );
    }, []);

    const handleDeleteSelectedScraps = useCallback(async () => {
        try {
            await Promise.all(
                selectedScraps.map((postId) => deleteScrab(postId)),
            );
            setScraps((prevScraps) =>
                prevScraps.filter(
                    (scrap) => !selectedScraps.includes(scrap.postId),
                ),
            );
            setPostDetails((prevDetails) => {
                const newDetails = { ...prevDetails };
                selectedScraps.forEach((postId) => delete newDetails[postId]);
                return newDetails;
            });
            setLoadingScraps((prev) => {
                const newLoading = { ...prev };
                selectedScraps.forEach((postId) => delete newLoading[postId]);
                return newLoading;
            });
            setSelectedScraps([]);
        } catch (error) {
            console.error("스크랩 삭제 실패:", error);
        }
    }, [selectedScraps]);

    const handleSelectPost = useCallback(
        (postId: number) => {
            setSelectedPost(postDetails[postId] || null);
        },
        [postDetails],
    );

    const handleClosePost = useCallback(() => {
        setSelectedPost(null);
    }, []);

    const renderedScraps = useMemo(() => {
        return scraps.map((scrap) => (
            <Scrap
                key={scrap.postId}
                scrap={scrap}
                postDetail={postDetails[scrap.postId]}
                isLoading={loadingScraps[scrap.postId]}
                isSelected={selectedScraps.includes(scrap.postId)}
                onSelect={handleSelectPost}
                onToggleSelection={toggleScrapSelection}
            />
        ));
    }, [
        scraps,
        postDetails,
        loadingScraps,
        selectedScraps,
        handleSelectPost,
        toggleScrapSelection,
    ]);

    return (
        <div className="pb-4">
            {selectedScraps.length > 0 && (
                <div className="mb-4">
                    <Button
                        onClick={handleDeleteSelectedScraps}
                        className="bg-red-500 hover:bg-red-600 text-white"
                    >
                        선택한 스크랩 삭제 ({selectedScraps.length})
                    </Button>
                </div>
            )}

            {isFetching ? (
                <div className="space-y-4">
                    {[...scraps].map((_, index) => (
                        <Skeleton
                            key={index}
                            className="h-32 w-full rounded-lg"
                        />
                    ))}
                </div>
            ) : scraps.length > 0 ? (
                renderedScraps
            ) : (
                <p className="text-center text-gray-500">스크랩이 없습니다.</p>
            )}

            {selectedPost && (
                <div
                    className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50"
                    onClick={handleClosePost}
                >
                    <div
                        className="bg-white dark:bg-slate-700 p-4 rounded-lg w-full max-w-[600px] relative"
                        onClick={(e) => e.stopPropagation()}
                    >
                        <button
                            className="w-full flex justify-start text-gray-600 dark:text-gray-200 font-semibold hover:text-blue-500"
                            onClick={handleClosePost}
                        >
                            ← 뒤로가기
                        </button>
                        <Post {...selectedPost} />
                    </div>
                </div>
            )}
        </div>
    );
}

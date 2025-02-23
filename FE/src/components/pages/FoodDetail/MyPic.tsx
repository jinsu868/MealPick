import { useEffect, useState, useRef } from "react";
import { getMyTagPosts, TagPost } from "@/api/post";
import { getPostDetail } from "@/api/post";
import Post from "@/components/Post/Post";
import { PostDetailDefault } from "@/types/PostDetail";
import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { useNavigate } from "react-router-dom";
import {
    Carousel,
    CarouselContent,
    CarouselItem,
} from "@/components/ui/carousel";
import { motion } from "framer-motion";

export default function MyPic() {
    const navigate = useNavigate();
    const params = new URLSearchParams(location.search);
    const tagName = params.get("tagName") || "태그";
    const userId = localStorage.getItem("userId");

    const [tagPosts, setTagPosts] = useState<TagPost[]>([]);
    const [pageToken, setPageToken] = useState<string | null>(null);
    const [isLastPage, setIsLastPage] = useState(false);
    const [loading, setLoading] = useState(false);
    const [selectedPost, setSelectedPost] = useState<PostDetailDefault | null>(
        null,
    );
    const [currentCarouselPage, setCurrentCarouselPage] = useState(0);
    const [index, setIndex] = useState(0);

    const observer = useRef<IntersectionObserver | null>(null);
    const lastPostRef = useRef<HTMLDivElement | null>(null);
    const isFetching = useRef(false);

    const getTagPosts = async () => {
        if (loading || isLastPage || isFetching.current) return;
        isFetching.current = true;
        setLoading(true);
        console.log("가져옵니다잉", tagName, pageToken);
        try {
            const response = await getMyTagPosts(
                tagName ?? "",
                pageToken ?? "",
            );
            if (response.data.length > 0) {
                setTagPosts((prev) => [...prev, ...response.data]);
                setPageToken(response.pageToken ?? null);
            } else {
                setIsLastPage(true);
            }
        } catch (error) {
            console.error("태그 포스트 조회 실패:", error);
        } finally {
            setLoading(false);
            isFetching.current = false;
        }
    };

    useEffect(() => {
        setTagPosts([]);
        setIsLastPage(false);
        setPageToken(null);
        setIndex(index + 1);
    }, [tagName]);

    useEffect(() => {
        if (pageToken == null) {
            getTagPosts();
        }
    }, [index]);

    useEffect(() => {
        if (pageToken === null && tagPosts.length === 0) {
            getTagPosts();
        }
    }, [pageToken]);

    useEffect(() => {
        if (!pageToken) return;

        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && pageToken && !loading) {
                    getTagPosts();
                }
            },
            { threshold: 0.5 },
        );

        if (lastPostRef.current) {
            observer.current.observe(lastPostRef.current);
        }

        return () => {
            if (observer.current) observer.current.disconnect();
        };
    }, [pageToken, loading]);

    const handlePostClick = async (postId: number, carouselPage: number) => {
        try {
            const postDetail = await getPostDetail(postId);
            setSelectedPost(postDetail);
            setCurrentCarouselPage(carouselPage);
        } catch (error) {
            console.error("게시글 상세 조회 실패:", error);
        }
    };

    const handleBackClick = () => {
        setSelectedPost(null);
    };

    return (
        <div className="w-full">
            <div className="title text-left w-screen max-w-[600px] p-4">
                <h2 className="text-md font-medium text-gray-700 dark:text-slate-300 flex items-center gap-1">
                    <img src="/imgs/image.webp" className="w-4" />
                    나의 {tagName} Pic!
                </h2>
                <hr className="border-slate-300 border-[0.5px]" />
            </div>
            {selectedPost ? (
                <motion.div
                    className="p-4"
                    initial={{ opacity: 0, scale: 0.8 }}
                    animate={{ opacity: 1, scale: 1 }}
                    exit={{ opacity: 0, scale: 0.8 }}
                >
                    <button
                        className="text-blue-500 font-semibold dark:text-slate-300"
                        onClick={handleBackClick}
                    >
                        뒤로가기
                    </button>
                    <Post {...selectedPost} />
                </motion.div>
            ) : userId === null ? (
                <Card className="flex flex-col gap-2 p-10 mx-8">
                    <div>로그인 안하셨나요?</div>
                    <Button
                        onClick={() => navigate("/login")}
                        className="bg-blue-500 hover:bg-blue-400"
                    >
                        로그인하기
                    </Button>
                </Card>
            ) : tagPosts.length === 0 ? (
                <Card className="flex flex-col gap-2 p-10 mx-8">
                    <div>아직 업로드한 {tagName} Pic이 없습니다.</div>
                    <Button
                        onClick={() => navigate("/post")}
                        className="bg-blue-500 hover:bg-blue-400"
                    >
                        {tagName} Pic 업로드하기
                    </Button>
                </Card>
            ) : (
                <div className="mx-4 p-2">
                    <Carousel opts={{ startIndex: currentCarouselPage }}>
                        <CarouselContent>
                            {Array.from({
                                length: Math.ceil(tagPosts.length / 8),
                            }).map((_, idx, arr) => (
                                <CarouselItem
                                    key={idx}
                                    ref={
                                        idx === arr.length - 1
                                            ? lastPostRef
                                            : null
                                    }
                                >
                                    <div className="grid grid-cols-4 grid-rows-2 py-2 gap-0.5">
                                        {tagPosts
                                            .slice(idx * 8, (idx + 1) * 8)
                                            .map((tagPost, i) => (
                                                <div
                                                    key={i}
                                                    className="w-full h-full"
                                                >
                                                    <img
                                                        src={tagPost.image}
                                                        alt=""
                                                        className="w-full h-full aspect-square object-cover cursor-pointer"
                                                        onClick={() =>
                                                            handlePostClick(
                                                                tagPost.postId,
                                                                idx,
                                                            )
                                                        }
                                                    />
                                                </div>
                                            ))}
                                    </div>
                                </CarouselItem>
                            ))}
                        </CarouselContent>
                    </Carousel>
                </div>
            )}
        </div>
    );
}

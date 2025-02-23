import { useState, useEffect, useMemo } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { PostDetailDefault } from "@/types/PostDetail";
import { getNearByPosts, getPostDetail, NearByPost } from "@/api/post";
import { MealTime } from "@/types/MealTime";
import { FoodTag } from "@/types/FoodTag";
import Tag from "@/components/commons/Tag";
import { getMealTimeColor } from "@/types/MealTimeColor";
import { useKakaoAddress } from "@/hooks/useKakaoMap";

const dummyPostIds = [1, 2, 3, 4, 5, 6, 7, 8];

const fallbackPost: PostDetailDefault = {
    postId: 0,
    authorId: 0,
    authorImageUrl:
        "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='100%' height='100%' fill='%23999'%3E%3Ccircle cx='12' cy='12' r='10' fill='%23eee'/%3E%3Ctext x='50%' y='50%' font-size='14' text-anchor='middle' dy='.3em' fill='%23999'%3E?%3C/text%3E%3C/svg%3E",
    authorNickname: "주변 정보가 부족합니다.",
    createdAt: "Pic을 올려주세요!",
    title: "",
    content: "",
    mealTime: MealTime.NONE,
    foodTag: FoodTag.NONE,
    postImageUrls: [
        "data:image/svg+xml;charset=UTF-8,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' width='100%' height='100%' fill='%23999'%3E%3Ccircle cx='12' cy='12' r='10' fill='%23eee'/%3E%3Ctext x='50%' y='50%' font-size='14' text-anchor='middle' dy='.3em' fill='%23999'%3E?%3C/text%3E%3C/svg%3E",
    ], // 기본 이미지
    tagIds: [],
    tags: [],
    commentCount: 0,
    likeCount: 0,
    isLiked: false,
    isBookMarked: false,
};

export default function Location() {
    const [positions, setPositions] = useState(
        dummyPostIds.map(() => ({
            x: 0,
            y: 0,
            time: Math.random() * 100,
            speed: Math.random() * 0.01 + 0.005,
            direction: 1,
            showImage: false,
            foodSrc: "",
            zIndex: 0,
        })),
    );

    const [posts, setPosts] = useState<PostDetailDefault[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [selectedPost, setSelectedPost] = useState<PostDetailDefault | null>(
        null,
    );

    const handleImageClick = (post: PostDetailDefault) => {
        setSelectedPost(post);
    };

    const [latitude, setLatitude] = useState<number>(0);
    const [longitude, setLongitude] = useState<number>(0);
    const [nearByPosts, setNearByPosts] = useState<NearByPost[]>([]);
    const address = useKakaoAddress(latitude, longitude);

    useEffect(() => {
        if (latitude !== 0 && longitude !== 0) {
            console.log("주소 업데이트:", address);
        }
    }, [address]);

    useEffect(() => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    const lat = position.coords.latitude;
                    const lon = position.coords.longitude;

                    setLatitude(lat);
                    setLongitude(lon);
                },
                (error) => {
                    console.error("Geolocation error:", error);
                },
            );
        } else {
            console.error("Geolocation is not supported by this browser.");
        }
    }, []);

    useEffect(() => {
        if (latitude === 0 || longitude === 0) return; // 초기 값이면 API 호출하지 않음

        const fetchNearPosts = async () => {
            try {
                const posts = await getNearByPosts(longitude, latitude);
                setNearByPosts(posts);
            } catch {
                console.error("주변 게시글 불러오기 실패");
            }
        };

        fetchNearPosts();
    }, [latitude, longitude]);

    useEffect(() => {
        const fetchPostDetails = async () => {
            try {
                const postDetails = await Promise.all(
                    nearByPosts.map(async (post) => {
                        try {
                            return await getPostDetail(post.postId);
                        } catch (error) {
                            console.error(
                                `게시글 ${post} 불러오기 실패:`,
                                error,
                            );
                            return fallbackPost; // 실패 시 기본 데이터 반환
                        }
                    }),
                );
                setPosts(postDetails);
            } catch (error) {
                console.error("게시글 정보를 가져오는 중 오류 발생:", error);
            } finally {
                setIsLoading(false);
            }
        };

        fetchPostDetails();
    }, [nearByPosts]);

    useEffect(() => {
        if (isLoading || nearByPosts.length === 0) return;

        setPositions((prevPositions) =>
            prevPositions.map((pos, index) => ({
                ...pos,
                foodSrc: nearByPosts[index]?.postImage || "",
            })),
        );
    }, [isLoading, nearByPosts]);

    useEffect(() => {
        if (isLoading) return;

        const updatePositions = () => {
            setPositions((prevPositions) =>
                prevPositions.map((pos) => {
                    const maxX = 150;
                    const maxY = 75;
                    const newX = Math.cos(pos.time) * maxX;
                    const newY = Math.sin(pos.time * 1.5) * maxY;

                    const zIndex = ((newY + maxY) / (2 * maxY)) * 40;

                    return {
                        ...pos,
                        x: newX,
                        y: newY,
                        time: pos.time + pos.speed,
                        direction: newX > pos.x ? 1 : -1,
                        zIndex: zIndex,
                    };
                }),
            );
        };

        const interval = setInterval(updatePositions, 100);
        return () => clearInterval(interval);
    }, [isLoading]);

    useEffect(() => {
        if (isLoading || posts.length === 0) return;

        const intervals: ReturnType<typeof setInterval>[] = [];

        positions.forEach((_, index) => {
            const randomInterval = Math.random() * 10000 + 4000;

            intervals.push(
                setInterval(() => {
                    setPositions((prev) =>
                        prev.map((pos, i) => {
                            if (i === index) {
                                return {
                                    ...pos,
                                    showImage: true,
                                    foodSrc: nearByPosts[i]?.postImage || "",
                                };
                            }
                            return pos;
                        }),
                    );

                    setTimeout(() => {
                        setPositions((prev) =>
                            prev.map((pos, i) =>
                                i === index
                                    ? { ...pos, showImage: false }
                                    : pos,
                            ),
                        );
                    }, 5000);
                }, randomInterval),
            );
        });

        return () =>
            intervals.forEach((t) => clearInterval(t as unknown as number));
    }, [isLoading, posts]);

    const mealTimeColor = useMemo(
        () =>
            getMealTimeColor(
                MealTime[
                    selectedPost?.mealTime as unknown as keyof typeof MealTime
                ] ?? MealTime.NONE,
            ),
        [selectedPost?.mealTime],
    );

    return (
        <div className="w-full max-w-[600px] flex flex-col justify-center items-center mb-6 mt-8">
            <div className="w-full flex justify-start pl-2 font-bold gap-x-2 items-center">
                <div>내 주변 Pic</div>
                <div className="ml-2 text-xs font-semibold text-gray-500">
                    내 위치 <span className="text-blue-600">{address}</span>
                </div>
            </div>
            <div className="mt-2 shadow-[inset_0_0px_40px_rgba(210,210,225,0.2)] relative w-full h-[300px] rounded-2xl overflow-hidden flex justify-center items-center bg-[rgba(220,220,255,0.1)] dark:bg-slate-400">
                <div className="relative flex justify-center items-center">
                    <div className="absolute bottom-[-3px] bg-black w-4 h-2 rounded-full opacity-10"></div>
                    <div className="absolute bottom-[-26px] bg-blue-800 w-24 h-14 rounded-full opacity-5"></div>
                    <div className="absolute bottom-[-65px] bg-blue-600 w-48 h-32 rounded-full opacity-5"></div>
                    <img src="/imgs/confused.webp" className="w-6 h-6" />
                </div>
                {positions.map((pos, index) =>
                    pos.foodSrc ? (
                        <motion.div
                            key={index}
                            className="flex-col aspect-square bg-transparent absolute text-sm font-bold text-blue-600 p-3 rounded-full flex justify-center items-center"
                            style={{ zIndex: pos.zIndex }}
                            animate={{
                                x: pos.x,
                                y: pos.y,
                                scaleX: pos.direction,
                            }}
                            transition={{
                                duration: 0.5,
                                ease: "easeInOut",
                            }}
                            onClick={() => handleImageClick(posts[index])}
                        >
                            <motion.div
                                animate={{ opacity: pos.showImage ? 1 : 0 }}
                                transition={{ duration: 1 }}
                                className="hover:scale-150 transition-all"
                            >
                                <div className="w-10 h-10 p-0.5 bg-gray-300 rounded-full overflow-hidden shadow-[0_0px_2px_rgba(0,0,0,0.2)] flex justify-center ring-2">
                                    <img
                                        src={pos.foodSrc}
                                        alt="food"
                                        className="w-full h-full object-cover rounded-full"
                                    />
                                </div>
                                <div className="ml-4 mt-1 w-1 h-1 bg-gray-300 rounded-full"></div>
                            </motion.div>

                            <div className="relative flex justify-center items-center">
                                <div className="absolute bottom-[-0.2px] bg-black w-4 h-2 rounded-full opacity-10"></div>
                                <img
                                    src="/imgs/walk.gif"
                                    alt="food"
                                    className="w-8 h-8"
                                />
                            </div>
                        </motion.div>
                    ) : null,
                )}
            </div>
            <AnimatePresence>
                {selectedPost && (
                    <motion.div
                        key={selectedPost.postId}
                        className="w-full p-4 rounded-b-2xl overflow-hidden"
                        initial={{ opacity: 0, maxHeight: 0 }}
                        animate={{ opacity: 1, maxHeight: 300 }}
                        exit={{ opacity: 0, maxHeight: 0 }}
                        transition={{ duration: 0.5, ease: "easeInOut" }}
                        onAnimationComplete={(definition) => {
                            if (definition === "exit") {
                                setSelectedPost(null);
                            }
                        }}
                    >
                        <div className="flex justify-between items-center mb-2">
                            <div className="flex items-center">
                                <img
                                    src={selectedPost.authorImageUrl}
                                    alt="작성자"
                                    className="w-8 h-8 rounded-full mr-2"
                                />
                                <div>
                                    <div className="text-xs font-semibold">
                                        {selectedPost.authorNickname}
                                    </div>
                                    <div className="flex justify-start text-xs text-gray-500">
                                        {selectedPost.createdAt.split("T")[0]}
                                    </div>
                                </div>
                            </div>
                            <div
                                onClick={() => setSelectedPost(null)}
                                className="font-semibold hover:text-blue-500 cursor-pointer"
                            >
                                닫기
                            </div>
                        </div>
                        <div className="w-full items-start flex flex-col">
                            <div className="text-medium font-bold">
                                {selectedPost.title}
                            </div>
                            <div className="text-sm text-gray-700 dark:text-gray-400">
                                {selectedPost.content}
                            </div>
                        </div>
                        <div className="mt-2 flex gap-2">
                            {selectedPost.postImageUrls.map((img, idx) => (
                                <img
                                    key={idx}
                                    src={img}
                                    alt="음식 이미지"
                                    className="w-20 h-20 rounded-md aspect-square object-cover overflow-hidden"
                                />
                            ))}
                        </div>
                        {selectedPost.tags.length > 0 && (
                            <div className="mt-2 flex gap-1">
                                {selectedPost.tags.map((tag, idx) => (
                                    <Tag
                                        key={idx}
                                        tagName={tag}
                                        colorClass={mealTimeColor}
                                    />
                                ))}
                            </div>
                        )}
                    </motion.div>
                )}
            </AnimatePresence>
        </div>
    );
}

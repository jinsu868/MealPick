import {
    deletePost,
    getMyFoodTagPosts,
    getOtherFoodTagPosts,
} from "@/api/post";
import { FoodTag } from "@/types/FoodTag";
import { PostDetailDefault } from "@/types/PostDetail";
import { useCallback, useEffect, useState } from "react";
import { FiArrowLeft } from "react-icons/fi";
import { MealTime } from "@/types/MealTime";
import PostFooter from "@/components/Post/PostFooter";
import { getMealTimeColor } from "@/types/MealTimeColor";
import { Skeleton } from "@/components/ui/skeleton";
import Tag from "@/components/commons/Tag";
import { Checkbox } from "@/components/ui/checkbox";
import { Button } from "@/components/ui/button";

interface AlbumDetailProps {
    isMypage: boolean;
    userId?: number | null;
    foodTag: FoodTag;
    setSelectedAlbum: (album: FoodTag | null) => void;
    onAlbumEmpty: (foodTag: FoodTag) => void;
}

export default function AlbumDetail({
    isMypage,
    userId,
    foodTag,
    setSelectedAlbum,
    onAlbumEmpty,
}: AlbumDetailProps) {
    console.log("userId:", userId);
    const [posts, setPosts] = useState<PostDetailDefault[]>([]);
    const [isLoading, setIsLoading] = useState<boolean>(true);
    const [showSkeleton, setShowSkeleton] = useState<boolean>(false);
    const [expandedPostId, setExpandedPostId] = useState<number | null>(null);
    const [selectedPosts, setSelectedPosts] = useState<number[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            setIsLoading(true);
            setShowSkeleton(false);

            const startTime = Date.now();
            let skeletonVisible = false;

            const skeletonTimeout = setTimeout(() => {
                setShowSkeleton(true);
                skeletonVisible = true;
            }, 100);

            try {
                if (isMypage) {
                    const response = await getMyFoodTagPosts(foodTag);
                    console.log("마이 데이터 요청 성공:", response);
                    setPosts(response);
                } else {
                    const response = await getOtherFoodTagPosts(
                        foodTag,
                        userId ?? -1,
                    );
                    console.log("상대 데이터 요청 성공:", response);
                    setPosts(response);
                }
            } catch (error) {
                console.error("마이 데이터 요청 중 오류 발생:", error);
                setPosts([]);
            } finally {
                const elapsedTime = Date.now() - startTime;

                if (elapsedTime <= 10) {
                    clearTimeout(skeletonTimeout);
                    setShowSkeleton(false);
                } else {
                    if (skeletonVisible) {
                        setTimeout(() => {
                            setShowSkeleton(false);
                        }, 300);
                    } else {
                        setShowSkeleton(false);
                    }
                }

                setIsLoading(false);
            }
        };

        fetchData();
    }, [foodTag]);

    const handleToggleExpand = (postId: number) => {
        setExpandedPostId(expandedPostId === postId ? null : postId);
    };

    const onToggleSelection = useCallback((postId: number) => {
        setSelectedPosts((prevSelected) =>
            prevSelected.includes(postId)
                ? prevSelected.filter((id) => id !== postId)
                : [...prevSelected, postId],
        );
    }, []);

    const handleDeleteSelectedPosts = async () => {
        if (selectedPosts.length === 0) return;

        try {
            await Promise.all(
                selectedPosts.map((postId) => deletePost(postId)),
            );

            const remainingPosts = posts.filter(
                (post) => !selectedPosts.includes(post.postId),
            );
            setPosts(remainingPosts);
            setSelectedPosts([]);

            if (remainingPosts.length === 0) {
                onAlbumEmpty(foodTag);
                setSelectedAlbum(null);
            }
        } catch (error) {
            console.error("게시글 삭제 실패:", error);
            alert("게시글 삭제 중 오류가 발생했습니다.");
        }
    };

    return (
        <div className="p-4">
            {/* 뒤로가기 버튼 */}
            <button
                className="mb-4 flex items-center gap-2 text-gray-700 dark:text-slate-400"
                onClick={() => setSelectedAlbum(null)}
            >
                <FiArrowLeft size={20} />
                뒤로가기
            </button>

            {selectedPosts.length > 0 && (
                <div className="mb-4">
                    <Button
                        onClick={handleDeleteSelectedPosts}
                        className="bg-red-500 hover:bg-red-600 text-white"
                    >
                        선택한 Pic 삭제 ({selectedPosts.length})
                    </Button>
                </div>
            )}

            {/* 앨범 제목 */}
            <h2 className="text-xl font-bold mb-4">
                {FoodTag[foodTag as unknown as keyof typeof FoodTag] ?? "기타"}
            </h2>

            <div className="flex flex-col gap-4">
                {isLoading && showSkeleton
                    ? [...Array(3)].map((_, index) => (
                          <div
                              key={index}
                              className="p-4 rounded-lg bg-gray-100 dark:bg-gray-800"
                          >
                              {/* 제목, 날짜, 식사시간 Skeleton */}
                              <div className="flex justify-between text-sm mb-2">
                                  <Skeleton className="w-24 h-5 rounded" />
                                  <Skeleton className="w-16 h-5 rounded" />
                                  <Skeleton className="w-12 h-5 rounded" />
                              </div>

                              <div className="flex gap-2">
                                  {[...Array(1)].map((_, i) => (
                                      <Skeleton
                                          key={i}
                                          className="w-32 h-24 rounded-md"
                                      />
                                  ))}
                              </div>
                          </div>
                      ))
                    : posts.map((post) => (
                          <div
                              key={post.postId}
                              className="p-4 rounded-lg cursor-pointer hover:bg-gray-50 dark:hover:bg-slate-600 transition"
                              onClick={() => handleToggleExpand(post.postId)}
                          >
                              {/* 제목, 날짜, 식사시간 */}
                              <div className="flex justify-between text-sm text-gray-600 dark:text-gray-100 mb-2">
                                  <span className="font-semibold text-black dark:text-slate-100">
                                      {post.title}
                                  </span>
                                  <div className="flex gap-2">
                                      <div>{post.createdAt.split("T")[0]}</div>
                                      <div className="text-blue-500">
                                          {
                                              MealTime[
                                                  post.mealTime as unknown as keyof typeof MealTime
                                              ]
                                          }
                                      </div>
                                  </div>
                                  {isMypage && (
                                      <div onClick={(e) => e.stopPropagation()}>
                                          <Checkbox
                                              checked={selectedPosts.includes(
                                                  post.postId,
                                              )}
                                              onCheckedChange={() =>
                                                  onToggleSelection(post.postId)
                                              }
                                          />
                                      </div>
                                  )}
                              </div>

                              {/* 이미지 가로 스크롤 */}
                              <div className="overflow-x-auto flex gap-2">
                                  {post.postImageUrls.length > 0 ? (
                                      post.postImageUrls.map(
                                          (imageUrl, index) => (
                                              <img
                                                  key={index}
                                                  src={imageUrl}
                                                  className="w-32 h-24 object-cover rounded-md"
                                                  alt="게시글 이미지"
                                              />
                                          ),
                                      )
                                  ) : (
                                      <div className="text-gray-500">
                                          이미지가 없습니다.
                                      </div>
                                  )}
                              </div>

                              {/* 펼쳐지는 애니메이션 */}
                              <div
                                  className={`overflow-hidden transition-all duration-300 ${
                                      expandedPostId === post.postId
                                          ? "max-h-[500px] opacity-100 mt-4"
                                          : "max-h-0 opacity-0"
                                  }`}
                              >
                                  <p className="text-gray-700 dark:text-gray-200 w-full flex justify-start pl-1 text-sm font-light">
                                      {post.content}
                                  </p>

                                  {/* 태그들 */}
                                  <div className="flex gap-1 mt-2">
                                      {post.tags.map((tag, index) => (
                                          //   <span
                                          //       key={index}
                                          //       className={`text-gray-800 text-xs px-2 py-1 rounded-full ${getMealTimeColor(MealTime[post.mealTime as unknown as keyof typeof MealTime])}`}
                                          //   >
                                          //       {tag}
                                          //   </span>
                                          <Tag
                                              key={index}
                                              tagName={tag}
                                              colorClass={getMealTimeColor(
                                                  MealTime[
                                                      post.mealTime as unknown as keyof typeof MealTime
                                                  ],
                                              )}
                                          />
                                      ))}
                                  </div>

                                  {/* 이벤트 전파 막기 */}
                                  <div onClick={(e) => e.stopPropagation()}>
                                      <PostFooter {...post} />
                                  </div>
                              </div>
                          </div>
                      ))}
            </div>
        </div>
    );
}

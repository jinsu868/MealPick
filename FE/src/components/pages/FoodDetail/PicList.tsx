import { getOtherMembersTagPosts } from "@/api/post";
import { useEffect, useState } from "react";
import { Drawer, DrawerTrigger, DrawerContent } from "@/components/ui/drawer";
import FeedDrawer from "./FeedDrawer";
import { PostDetailDefault } from "@/types/PostDetail";

export default function PicList() {
    const params = new URLSearchParams(location.search);
    const tagName = params.get("tagName") || "태그";
    const [posts, setPosts] = useState<PostDetailDefault[]>([]);
    const [isDrawerOpen, setIsDrawerOpen] = useState(false);
    const hasPosts = posts.length > 0;

    const getTagPosts = async () => {
        try {
            const response = await getOtherMembersTagPosts(tagName ?? "");
            setPosts(response.data);
        } catch (error) {
            console.error(error);
        }
    };

    useEffect(() => {
        getTagPosts();
        setIsDrawerOpen(false);
    }, [tagName]);

    return (
        <div className="w-full max-w-[600px] mx-auto m-4">
            {/* 제목 + 구분선 */}
            <div className="flex flex-col w-full">
                <h2 className="text-md font-medium text-gray-700 dark:text-slate-300 text-start pl-4">
                    Pic 모아보기
                </h2>
                <hr className="border-slate-300 border-[0.5px] mx-4 mb-4"></hr>
            </div>

            {/* 게시물이 없을 때 기본 메시지 표시 */}
            {!hasPosts && (
                <div className="flex flex-col items-center justify-center w-full max-w-[540px] h-[200px] bg-gray-100 rounded-lg mx-auto">
                    <img
                        src="/imgs/sittingMan.webp"
                        alt="기본 이미지"
                        className="w-24 h-24 object-cover"
                    />
                    <p className="mt-2 text-gray-600 font-semibold">
                        아직 게시글이 없네요!
                    </p>
                </div>
            )}

            {/* 첫 번째 피드 미리보기 (게시물이 있을 때만 표시) */}
            {hasPosts && (
                <div className="h-full mx-4">
                    <Drawer open={isDrawerOpen} onOpenChange={setIsDrawerOpen}>
                        <DrawerTrigger asChild>
                            <div className="relative max-w-[540px] h-[200px] overflow-hidden rounded-lg cursor-pointer mx-auto flex items-center justify-center">
                                <img
                                    src={
                                        posts[0]?.postImageUrls[0] ??
                                        "/imgs/sittingMan.webp"
                                    }
                                    className="w-full h-full object-cover blur-lg opacity-80"
                                />
                                <div className="absolute inset-0 flex items-end p-4 bg-gradient-to-t from-black/50 to-transparent">
                                    <h3 className="text-sm text-gray-100 font-semibold">
                                        다른 사용자의 {tagName} Pic 을
                                        확인해보세요
                                    </h3>
                                </div>
                            </div>
                        </DrawerTrigger>

                        {/* Drawer Content */}
                        <DrawerContent className="max-w-[600px] mx-auto h-full">
                            <FeedDrawer posts={posts} />
                        </DrawerContent>
                    </Drawer>
                </div>
            )}
        </div>
    );
}

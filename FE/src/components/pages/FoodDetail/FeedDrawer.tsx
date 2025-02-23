import Post from "@/components/Post/Post";
import { DrawerHeader, DrawerTitle } from "@/components/ui/drawer";
import { PostDetailDefault } from "@/types/PostDetail";

type FeedDrawerProps = {
    posts: PostDetailDefault[];
};

export default function FeedDrawer({ posts }: FeedDrawerProps) {
    return (
        <div>
            {/* 헤더 */}
            <DrawerHeader>
                <div className="flex items-center justify-between">
                    <DrawerTitle className="text-md dark:text-slate-300">
                        Pic 모아보기
                    </DrawerTitle>
                </div>
            </DrawerHeader>

            {/* 피드 리스트 (스크롤 가능) */}
            <div className="flex-1 px-4 w-full overflow-y-auto scrollbar-hide mb-4">
                <div className="flex flex-col pt-4 min-h-[calc(100vh-50px)] max-h-[calc(100vh-50px)]">
                    {posts.length > 0 ? (
                        posts.map((post) => (
                            <Post key={post.postId} {...post} />
                        ))
                    ) : (
                        <p className="text-center text-gray-500">
                            피드가 없습니다.
                        </p>
                    )}
                </div>
            </div>
        </div>
    );
}

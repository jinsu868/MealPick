import { FiX } from "react-icons/fi";
import { UserPostDetail } from "@/types/UserPostDetail";

interface PostDetailViewProps {
    selectedPost: UserPostDetail;
    setSelectedPost: (post: UserPostDetail | null) => void;
    allPosts: UserPostDetail[];
}

export default function PostDetailView({
    selectedPost,
    setSelectedPost,
    allPosts,
}: PostDetailViewProps) {
    return (
        <div className="fixed inset-0 bg-white z-50 p-4 flex">
            <div className="w-24 flex flex-col gap-2 overflow-y-auto border-r pr-2">
                {allPosts.map((post) => (
                    <div
                        key={post.postId}
                        className="p-1 cursor-pointer rounded-lg border hover:border-gray-500 transition"
                        onClick={() => setSelectedPost(post)}
                    >
                        <img
                            src={post.representImageUrl}
                            className="w-full h-16 object-cover rounded-md"
                            alt="썸네일"
                        />
                    </div>
                ))}
            </div>
            <div className="flex-1 flex flex-col">
                <button
                    className="text-gray-500 text-sm bg-transparent border-none self-end"
                    onClick={() => setSelectedPost(null)}
                >
                    <FiX size={24} /> 닫기
                </button>
                <div className="w-full h-72 rounded-xl overflow-hidden shadow-md">
                    <img
                        src={selectedPost.representImageUrl}
                        className="w-full h-full object-cover"
                        alt="대표 이미지"
                    />
                </div>
            </div>
        </div>
    );
}

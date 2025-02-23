import { PostDetailDefault } from "@/types/PostDetail";
import ScrapDetailCarousel from "./ScrapDetailCarousel";
import PostFooter from "@/components/Post/PostFooter";

interface PostDetailViewProps {
    postDetail: PostDetailDefault;
}

export default function ScrapDetailView({ postDetail }: PostDetailViewProps) {
    return (
        <div className="mt-4 p-4 rounded-lg">
            {postDetail.postImageUrls &&
                postDetail.postImageUrls.length > 0 && (
                    <ScrapDetailCarousel
                        postImages={postDetail.postImageUrls}
                    />
                )}
            <p className="flex justify-start text-sm text-gray-500 pt-4 px-4">
                {postDetail.content}
            </p>

            <div>
                <PostFooter {...postDetail} />
            </div>
        </div>
    );
}

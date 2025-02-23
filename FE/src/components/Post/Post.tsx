import { forwardRef, useState } from "react";
import PostHeader from "./PostHeader";
import PostContent from "./PostContent";
import PostFooter from "./PostFooter";
import { PostDetailDefault } from "@/types/PostDetail";

const Post = forwardRef<HTMLDivElement, PostDetailDefault>((postProps, ref) => {
    const [postData, setPostData] = useState(postProps);

    const handlePostUpdate = (updatedPost: {
        content: string;
        tags: string[];
    }) => {
        setPostData((prev) => ({
            ...prev,
            content: updatedPost.content,
            tags: updatedPost.tags,
        }));
    };

    return (
        <div
            ref={ref}
            className="w-full max-w-[600px] border-b-2 pt-4 dark:border-gray-700 dark:text-gray-200"
        >
            <PostHeader {...postData} onPostUpdate={handlePostUpdate} />
            <PostContent {...postData} />
            <PostFooter {...postData} />
        </div>
    );
});

Post.displayName = "Post";

export default Post;

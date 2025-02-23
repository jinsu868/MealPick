import { useEffect, useState } from "react";
import { useToast } from "@/hooks/use-toast";
import { ErrorMessages } from "@/utils/ErrorMessages";
import { fetchChildComments } from "@/api/comment";
import { ChildCommentDefault } from "@/types/CommentT";
import ChildComment from "./ChildComment";

export default function ChildCommentList(props: {
    commentId: number;
    refreshTrigger?: number;
}) {
    const [comments, setComments] = useState<ChildCommentDefault[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const { toast } = useToast();

    useEffect(() => {
        const loadComments = async () => {
            setIsLoading(true);
            try {
                const data = await fetchChildComments(props.commentId, "1");
                setComments(data);
            } catch (error) {
                console.error(error);
                toast({
                    description: ErrorMessages.DEFAULT_ERROR,
                });
            }
            setIsLoading(false);
        };
        loadComments();
    }, [props.commentId, props.refreshTrigger]);

    return (
        <div className="pl-1 flex flex-col pt-1 overflow-y-auto max-h-[calc(100vh-200px)]">
            {isLoading ? (
                <p>Loading comments...</p>
            ) : !Array.isArray(comments) ? (
                <p>Invalid comments data.</p>
            ) : comments.length === 0 ? (
                <p>No comments yet.</p>
            ) : (
                comments.map((comment, idx) => (
                    <ChildComment key={idx} comment={comment} />
                ))
            )}
        </div>
    );
}

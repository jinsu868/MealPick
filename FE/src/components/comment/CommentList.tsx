import { fetchComments } from "@/api/comment";
import { useCallback, useEffect, useState, useRef } from "react";
import { useToast } from "../../hooks/use-toast.ts";
import { ErrorMessages } from "@/utils/ErrorMessages";
import RootComment from "./RootComment.tsx";
import { RootCommentDefault } from "@/types/CommentT.ts";

export default function CommentList(props: {
    postId: number;
    onReply: (commentId: number) => void;
    refreshTrigger?: number;
    replyCommentId?: number | null;
    refreshChildTrigger?: number;
    onCommentCountUpdate: (newCommentCount: number) => void;
}) {
    const [comments, setComments] = useState<RootCommentDefault[]>([]);
    const [nextPageToken, setNextPageToken] = useState<string | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const { toast } = useToast();

    const observer = useRef<IntersectionObserver | null>(null);
    const lastCommentRef = useRef<HTMLDivElement | null>(null);
    const isFetching = useRef(false);

    // 댓글 불러오기 함수 (페이지네이션 적용)
    const loadComments = useCallback(
        async (pageToken?: string) => {
            if (isFetching.current || pageToken === null) return;

            isFetching.current = true;
            setIsLoading(true);
            console.log("Fetching comments with pageToken:", pageToken);

            try {
                const data = await fetchComments(props.postId, pageToken);
                setComments((prevComments) => [
                    ...prevComments,
                    ...data.content,
                ]);
                setNextPageToken(data.pageToken ?? null);
            } catch (error) {
                console.error("댓글 요청 중 에러: ", error);
                toast({ description: ErrorMessages.DEFAULT_ERROR });
            } finally {
                setIsLoading(false);
                isFetching.current = false;
            }
        },
        [props.postId, toast],
    );

    // 초기 댓글 불러오기
    useEffect(() => {
        setComments([]); // 새로운 postId가 들어오면 기존 댓글 초기화
        loadComments(undefined);
    }, [props.postId, props.refreshTrigger]);

    // 무한 스크롤 감지 로직
    useEffect(() => {
        if (!nextPageToken) return;

        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && nextPageToken) {
                    loadComments(nextPageToken);
                }
            },
            { threshold: 0.5 },
        );

        if (lastCommentRef.current) {
            observer.current.observe(lastCommentRef.current);
        }

        return () => {
            if (observer.current) observer.current.disconnect();
        };
    }, [nextPageToken]);

    return (
        <div className="flex flex-col pt-4 min-h-[calc(100vh-200px)] max-h-[calc(100vh-200px)]">
            {comments
                .filter(
                    (comment) =>
                        !(comment.deleted && comment.commentCount === 0),
                )
                .map((comment, idx) => (
                    <RootComment
                        key={idx}
                        comment={comment}
                        onReply={props.onReply}
                        replyCommentId={props.replyCommentId}
                        refreshChildTrigger={props.refreshChildTrigger}
                        onCommentCountUpdate={props.onCommentCountUpdate}
                        ref={
                            idx === comments.length - 1 ? lastCommentRef : null
                        }
                    />
                ))}

            {/* 로딩 중 표시 */}
            {isLoading && (
                <p className="text-center text-gray-500">
                    Loading more comments...
                </p>
            )}
        </div>
    );
}

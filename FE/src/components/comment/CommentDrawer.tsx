import { Button } from "../ui/button";
import {
    DrawerDescription,
    DrawerFooter,
    DrawerHeader,
    DrawerTitle,
} from "../ui/drawer";
import { IoIosSend } from "react-icons/io";
import CommentList from "./CommentList";
import { useCallback, useRef, useState, useEffect } from "react";
import { createRootComment, createSubComment } from "@/api/comment";
import { PostDetailDefault } from "@/types/PostDetail";

interface CommentDrawerProps extends PostDetailDefault {
    onCommentCountUpdate: (newCommentCount: number) => void;
}

export default function CommentDrawer(props: CommentDrawerProps) {
    const { postId, title, content } = props;
    const inputRef = useRef<HTMLInputElement>(null);
    const [replyCommentId, setReplyCommentId] = useState<number | null>(null);
    const [commentText, setCommentText] = useState("");
    const [refreshTrigger, setRefreshTrigger] = useState(0);
    const [refreshChildTrigger, setRefreshChildTrigger] = useState(0);
    const [viewportHeight, setViewportHeight] = useState(
        window.visualViewport
            ? window.visualViewport.height
            : window.innerHeight,
    );

    const handleReply = useCallback((commentId: number) => {
        setReplyCommentId(commentId);
        inputRef.current?.focus();
    }, []);

    const handleSubmit = async () => {
        if (!commentText.trim()) return;

        try {
            if (replyCommentId) {
                console.log(
                    `답글 (댓글 ID: ${replyCommentId}, 내용: ${commentText})`,
                );
                await createSubComment(replyCommentId, commentText);
                setRefreshChildTrigger((prev) => prev + 1);
                setRefreshTrigger((prev) => prev + 1);
                props.onCommentCountUpdate(1);
            } else {
                console.log(`댓글 내용: ${commentText}`);
                await createRootComment(postId, commentText);
                props.onCommentCountUpdate(1);
                setRefreshTrigger((prev) => prev + 1);
            }
            setCommentText("");
            setReplyCommentId(null);
        } catch (error) {
            console.error("댓글 작성 중 오류:", error);
        }
    };

    useEffect(() => {
        const visualViewport = window.visualViewport;

        const handleResize = () => {
            if (visualViewport) {
                setViewportHeight(visualViewport.height);
            }
        };

        const handleBlur = () => {
            setViewportHeight(window.innerHeight);
        };

        if (visualViewport) {
            visualViewport.addEventListener("resize", handleResize);
        }

        const inputElement = inputRef.current;
        if (inputElement) {
            inputElement.addEventListener("blur", handleBlur);
        }

        return () => {
            if (visualViewport) {
                visualViewport.removeEventListener("resize", handleResize);
            }
            if (inputElement) {
                inputElement.removeEventListener("blur", handleBlur);
            }
        };
    }, []);

    return (
        <div className="dark:bg-gray-800">
            <DrawerHeader className="mt-2 border-b-2">
                <DrawerTitle className="text-gray-700 text-md">
                    {title}
                </DrawerTitle>
                <DrawerDescription>{content}</DrawerDescription>
            </DrawerHeader>

            {/* 댓글 리스트 */}
            <div className="flex-1 overflow-y-auto scrollbar-hide">
                <CommentList
                    postId={postId}
                    onReply={handleReply}
                    replyCommentId={replyCommentId}
                    refreshTrigger={refreshTrigger}
                    refreshChildTrigger={refreshChildTrigger}
                    onCommentCountUpdate={props.onCommentCountUpdate}
                />
            </div>

            <DrawerFooter
                className="sticky bottom-0 bg-gray-50"
                style={{
                    transform: `translateY(-${window.innerHeight - viewportHeight}px)`,
                }}
            >
                <div className="flex items-center w-full gap-2 p-2 border-t bg-gray-100 border-gray-300 focus-within:border-blue-500 focus-within:bg-blue-100">
                    <input
                        ref={inputRef}
                        type="text"
                        placeholder={
                            replyCommentId ? "답글 달기..." : "댓글 달기..."
                        }
                        value={commentText}
                        onChange={(e) => setCommentText(e.target.value)}
                        className="flex-1 p-2 rounded-lg outline-none bg-transparent text-black"
                        onBlur={() => {
                            if (!commentText.trim()) {
                                setReplyCommentId(null);
                            }
                        }}
                        onKeyDown={(e) => {
                            if (e.key === "Enter" && !e.shiftKey) {
                                e.preventDefault();
                                handleSubmit();
                            }
                        }}
                    />
                    <Button
                        onClick={handleSubmit}
                        className="bg-gray-600 text-white px-6 py-2 rounded-full"
                    >
                        <IoIosSend />
                    </Button>
                </div>
            </DrawerFooter>
        </div>
    );
}

import { useState } from "react";
import { IoMdHeartEmpty, IoMdHeart } from "react-icons/io";
import { ChildCommentDefault } from "@/types/CommentT";
import {
    commentLike,
    commentUnlike,
    deleteComment,
    updateComment,
} from "@/api/comment";
import UserProfile from "../commons/UserProfile";
import { Dialog, DialogContent } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Button } from "@/components/ui/button";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { MoreVertical } from "lucide-react";

interface CommentProps {
    comment: ChildCommentDefault;
    refreshTrigger?: number;
}

export default function ChildComment(props: CommentProps) {
    const maxLength = 30;
    const [isExpanded, setIsExpanded] = useState(false);
    const [isDeleted, setIsDeleted] = useState(false);
    const [isLiked, setIsLiked] = useState(props.comment.isLiked);
    const [likeCount, setLikeCount] = useState(props.comment.likeCount);
    const [commentContent, setCommentContent] = useState(props.comment.content);
    const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
    const [editedContent, setEditedContent] = useState(props.comment.content);

    const currentMemberId = localStorage.getItem("userId");

    const toggleExpand = () => setIsExpanded(!isExpanded);
    const shouldShowToggle = commentContent.length > maxLength;
    const displayedContent = isExpanded
        ? commentContent
        : commentContent.slice(0, maxLength) + (shouldShowToggle ? "..." : "");

    const handleDelete = async () => {
        try {
            await deleteComment(props.comment.id);
            setIsDeleted(true);
        } catch (error) {
            console.error("댓글 삭제 중 오류:", error);
        }
    };

    const handleUpdate = async () => {
        try {
            await updateComment(props.comment.id, editedContent);
            setCommentContent(editedContent);
            setIsEditDialogOpen(false);
        } catch (error) {
            console.error("댓글 수정 중 오류:", error);
        }
    };

    const handleLike = async () => {
        try {
            if (!isLiked) {
                await commentLike(props.comment.id);
                setLikeCount((prev) => prev + 1);
                setIsLiked(true);
            } else {
                await commentUnlike(props.comment.id);
                setLikeCount((prev) => prev - 1);
                setIsLiked(false);
            }
        } catch (error) {
            console.error("댓글 좋아요 중 오류:", error);
        }
    };

    if (isDeleted) return null;

    return (
        <div className="flex flex-col p-2 gap-1 border-b border-gray-100 dark:border-gray-700 transition-colors duration-300 bg-white dark:bg-gray-800">
            <div className="flex items-start gap-2">
                {/* 프로필 이미지 */}
                <div className="w-8">
                    <UserProfile
                        id={props.comment.authorId}
                        imageUrl={props.comment.profileImageUrl}
                        size={32}
                    />
                </div>

                {/* 댓글 본문 */}
                <div className="flex flex-col w-full">
                    <div className="flex items-center gap-2">
                        <p className="text-xs font-light text-gray-600 dark:text-gray-300">
                            {props.comment.nickName}
                        </p>
                        {props.comment.authorId === Number(currentMemberId) && (
                            <DropdownMenu>
                                <DropdownMenuTrigger asChild>
                                    <MoreVertical className="h-4 w-4 cursor-pointer" />
                                </DropdownMenuTrigger>
                                <DropdownMenuContent className="dark:bg-gray-900">
                                    <DropdownMenuItem
                                        onClick={() =>
                                            setIsEditDialogOpen(true)
                                        }
                                        className="bg-slate-200 dark:bg-gray-700"
                                    >
                                        수정
                                    </DropdownMenuItem>
                                    <DropdownMenuItem
                                        onClick={handleDelete}
                                        className="bg-red-500 mt-1"
                                    >
                                        삭제
                                    </DropdownMenuItem>
                                </DropdownMenuContent>
                            </DropdownMenu>
                        )}
                    </div>

                    {/* 댓글 내용 */}
                    {props.comment.deleted ? (
                        <p className="text-sm text-gray-400">
                            삭제된 댓글입니다.
                        </p>
                    ) : (
                        <div className="w-full">
                            <div className="text-sm text-gray-700 dark:text-gray-400 break-all flex justify-between w-full">
                                <div>
                                    {displayedContent}
                                    {shouldShowToggle && (
                                        <span
                                            className="text-gray-400 cursor-pointer ml-1"
                                            onClick={toggleExpand}
                                        >
                                            {isExpanded ? "숨기기" : "더보기"}
                                        </span>
                                    )}
                                </div>
                                <div className="pl-2 pr-1 flex flex-col items-center">
                                    <div
                                        className="cursor-pointer text-gray-800 dark:text-slate-500"
                                        onClick={handleLike}
                                    >
                                        {isLiked ? (
                                            <IoMdHeart />
                                        ) : (
                                            <IoMdHeartEmpty />
                                        )}
                                    </div>
                                    <div className="text-xs font-extralight">
                                        {likeCount}
                                    </div>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            </div>

            {/* Edit Dialog */}
            <Dialog open={isEditDialogOpen} onOpenChange={setIsEditDialogOpen}>
                <DialogContent className="p-4 flex flex-col gap-4 dark:bg-slate-500">
                    <h2 className="text-lg">댓글 수정</h2>
                    <Input
                        value={editedContent}
                        onChange={(e) => setEditedContent(e.target.value)}
                        className="rounded-none border-t border-b border-l-0 border-r-0 border-slate-400 shadow-none focus:outline-none focus:ring-0 focus:border-blue-500"
                    />
                    <div className="flex justify-end gap-2">
                        <Button
                            variant="outline"
                            onClick={() => setIsEditDialogOpen(false)}
                        >
                            취소
                        </Button>
                        <Button onClick={handleUpdate} className="bg-blue-600">
                            수정 완료
                        </Button>
                    </div>
                </DialogContent>
            </Dialog>
        </div>
    );
}

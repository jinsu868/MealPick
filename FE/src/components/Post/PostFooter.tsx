import { MdBookmarkBorder, MdBookmark } from "react-icons/md";
import { IoMdHeartEmpty, IoMdHeart } from "react-icons/io";
import {
    createScrab,
    deleteScrab,
    createPostLike,
    deletePostLike,
} from "@/api/post";
import { useState } from "react";
import CommentDrawer from "../comment/CommentDrawer";
import { PostDetailDefault } from "@/types/PostDetail";
import { Drawer, DrawerContent, DrawerTrigger } from "../ui/drawer";
import { FaComment } from "react-icons/fa";

export default function PostFooter(props: PostDetailDefault) {
    const [isBookmarked, setIsBookmarked] = useState(props.isBookMarked);
    const [isLiked, setIsLiked] = useState(props.isLiked);
    const [likeCount, setLikeCount] = useState(props.likeCount);
    const [bookMarkAnimation, setBookMarkAnimation] = useState(false);
    const [commentCount, setCommentCount] = useState(props.commentCount);

    const handleCommentCountUpdate = (newCommentCount: number) => {
        setCommentCount(commentCount + newCommentCount);
    };

    const handleBookmark = async () => {
        try {
            if (!isBookmarked) {
                await createScrab(props.postId);
            } else {
                await deleteScrab(props.postId);
            }
            setIsBookmarked(!isBookmarked);

            if (!isBookmarked) {
                setBookMarkAnimation(true);
                setTimeout(() => setBookMarkAnimation(false), 1000);
            }
        } catch (error) {
            console.error(error);
        }
    };

    const handleLike = async () => {
        try {
            if (isLiked) {
                await deletePostLike(props.postId);
                setLikeCount(likeCount - 1);
            } else {
                await createPostLike(props.postId);
                setLikeCount(likeCount + 1);
            }
            setIsLiked(!isLiked);
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="h-[50px] pl-4 pr-4 flex items-center justify-between relative text-gray-700 dark:text-slate-400">
            {bookMarkAnimation && props.postImageUrls && (
                <img
                    src={props.postImageUrls[0]}
                    alt="Bookmark animation"
                    className="absolute w-12 left-4 bottom-10 opacity-100 animate-bookmark-move rounded-md"
                />
            )}
            <div
                onClick={handleBookmark}
                className="cursor-pointer transition-transform duration-200 ease-in-out active:scale-125"
            >
                {isBookmarked ? (
                    <MdBookmark className="w-5 h-5" />
                ) : (
                    <MdBookmarkBorder className="w-5 h-5" />
                )}
            </div>
            <div className="flex gap-4 items-center">
                <Drawer>
                    <DrawerTrigger asChild>
                        <div className="justify-center items-center flex transition-transform duration-200 ease-in-out active:scale-125 cursor-pointer">
                            <FaComment className="w-4 h-4 mr-1.5" />
                            <div>{commentCount}</div>
                        </div>
                    </DrawerTrigger>
                    <DrawerContent className="w-screen max-w-[600px] h-full mx-auto">
                        <CommentDrawer
                            {...props}
                            onCommentCountUpdate={handleCommentCountUpdate}
                        />
                    </DrawerContent>
                </Drawer>

                <div
                    onClick={handleLike}
                    className="flex cursor-pointer transition-transform duration-200 ease-in-out active:scale-125 items-center gap-0.5"
                >
                    {isLiked ? (
                        <IoMdHeart className="w-5 h-5" />
                    ) : (
                        <IoMdHeartEmpty className="w-5 h-5" />
                    )}
                    <div>{likeCount}</div>
                </div>
            </div>
            <style>
                {`
                    @keyframes bookmark-move {
                        0% {
                            opacity: 1;
                            transform: translateY(0);
                        }
                        100% {
                            opacity: 0;
                            transform: translateY(-40px);
                        }
                    }
                    .animate-bookmark-move {
                        animation: bookmark-move 0.8s forwards;
                    }
                `}
            </style>
        </div>
    );
}

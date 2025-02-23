import { PostDetailDefault } from "@/types/PostDetail";
import UserProfile from "../commons/UserProfile";
import { useState } from "react";
import EditPost from "./EditPost";
import { updatePost } from "@/api/post";
import { MealTime } from "@/types/MealTime";
import { FoodTag } from "@/types/FoodTag";
import { MdMenu } from "react-icons/md";

interface PostHeaderProps extends PostDetailDefault {
    onPostUpdate: (updatedPost: { content: string; tags: string[] }) => void;
}

export default function PostHeader(props: PostHeaderProps) {
    const userId = localStorage.getItem("userId");
    const getMealTime = (meal: string): string => {
        return MealTime[meal as keyof typeof MealTime] || "알 수 없음";
    };

    // 모달 상태 관리
    const [isModalOpen, setIsModalOpen] = useState(false);

    // 게시글 상태 관리
    const [post, setPost] = useState({
        postId: props.postId,
        title: props.title,
        mainImageUrl: props.postImageUrls[0],
        content: props.content,
        tags: props.tags,
        mealTime: props.mealTime,
    });

    // 수정 버튼 클릭 시 모달 열기
    const handleEditClick = () => {
        setIsModalOpen(true);
    };

    // 모달 닫기 함수
    const handleCloseModal = () => {
        setIsModalOpen(false);
    };

    const handleSavePost = async (updatedPost: {
        content: string;
        tags: string[];
    }) => {
        try {
            await updatePost(post.postId, updatedPost);

            setPost((prev) => ({
                ...prev,
                ...updatedPost,
            }));

            props.onPostUpdate(updatedPost);

            setIsModalOpen(false); // 수정 성공 시 모달 닫기
        } catch (error) {
            alert("게시글 수정에 실패했습니다.");
            console.log("게시글 수정 실패", error);
        }
    };

    return (
        <div className="h-10 pl-4 pr-4 flex items-center justify-between gap-2">
            {/* 왼쪽: 프로필 이미지 + 닉네임 */}
            <UserProfile
                id={props.authorId}
                imageUrl={props.authorImageUrl}
                size={32}
            />
            <div className="flex flex-col flex-1 justify-center">
                <div className="text-sm text-left">{props.authorNickname}</div>
                <div className="text-xs text-gray-500 flex">
                    <div className="pr-1.5">
                        {props.createdAt && props.createdAt.split("T")[0]}
                    </div>
                    <div>{getMealTime(props.mealTime)}</div>
                    <div className="ml-1.5">
                        {
                            FoodTag[
                                props.foodTag as unknown as keyof typeof FoodTag
                            ]
                        }
                    </div>
                </div>
            </div>
            {userId ? (
                props.authorId === Number(userId) ? (
                    <MdMenu
                        size={22}
                        className="cursor-pointer dark:text-slate-400"
                        onClick={handleEditClick}
                    />
                ) : (
                    <></>
                )
            ) : (
                <></>
            )}
            {isModalOpen && (
                <EditPost
                    post={post}
                    onSave={handleSavePost}
                    onClose={handleCloseModal}
                />
            )}
        </div>
    );
}

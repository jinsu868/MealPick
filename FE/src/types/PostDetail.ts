import { FoodTag } from "./FoodTag";
import { MealTime } from "./MealTime";

export default interface PostDetail {
    postId?: number;
    createdAt?: string;
    authorId?: number;
    authorImageUrl?: string;
    authorNickname?: string;
    title?: string;
    content?: string;
    mealTime?: MealTime;
    foodTag?: FoodTag;
    postImageUrls?: string[];
    tagIds?: number[];
    tags?: string[];
    commentCount?: number;
    likeCount?: number;
    isLiked?: boolean;
    isBookMarked?: boolean;
}

export type PostDetailDefault = Required<PostDetail>;

export const getPostDetailDefault = (
    post?: Partial<PostDetail>,
): PostDetailDefault => ({
    authorId: post?.authorId ?? 0,
    authorImageUrl: post?.authorImageUrl ?? "",
    authorNickname: post?.authorNickname ?? "Unknown",
    postId: post?.postId ?? 0,
    createdAt: post?.createdAt ?? "",
    title: post?.title ?? "제목 없음",
    content: post?.content ?? "내용이 없습니다.",
    mealTime: post?.mealTime ?? MealTime.NONE,
    postImageUrls: post?.postImageUrls ?? [],
    foodTag: post?.foodTag ?? FoodTag.NONE,
    tagIds: post?.tagIds ?? [],
    tags: post?.tags ?? [],
    commentCount: post?.commentCount ?? 0,
    likeCount: post?.likeCount ?? 0,
    isLiked: post?.isLiked ?? false,
    isBookMarked: post?.isBookMarked ?? false,
});

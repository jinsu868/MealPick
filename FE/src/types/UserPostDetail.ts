export interface UserPostDetail {
    postId: number;
    authorId: number;
    mealTime: "BREAKFAST" | "LUNCH" | "DINNER";
    foodTag: string;
}

export const getUserPostDetailDefault = (
    post: Partial<UserPostDetail>,
): UserPostDetail => {
    return {
        postId: post.postId ?? 0,
        authorId: post.authorId ?? 0,
        mealTime: post.mealTime ?? "LUNCH",
        foodTag: post.foodTag ?? "UNKNOWN",
    };
};

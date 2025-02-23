export interface RootComment {
    id?: number;
    authorId?: number;
    nickName?: string;
    profileImageUrl?: string;
    content?: string;
    createdAt?: string;
    deleted?: boolean;
    isLiked?: boolean;
    commentCount?: number;
    likeCount?: number;
}

export interface ChildComment {
    id?: number;
    authorId?: number;
    nickName?: string;
    profileImageUrl?: string;
    content?: string;
    createdAt?: string;
    deleted?: boolean;
    isLiked?: boolean;
    commentCount?: number;
    likeCount?: number;
}

export type RootCommentDefault = Required<RootComment>;

export const getRootCommentDefault = (
    comment?: Partial<RootComment>,
): RootCommentDefault => ({
    id: comment?.id ?? 0,
    authorId: comment?.authorId ?? 0,
    commentCount: comment?.commentCount ?? 0,
    content: comment?.content ?? "확인할 수 없습니다.",
    createdAt: comment?.createdAt ?? "",
    deleted: comment?.deleted ?? false,
    isLiked: comment?.isLiked ?? false,
    likeCount: comment?.likeCount ?? 0,
    nickName: comment?.nickName ?? "Unknown",
    profileImageUrl: comment?.profileImageUrl ?? "",
});

export type ChildCommentDefault = Required<ChildComment>;

export const getChildCommentDefault = (
    comment: Partial<ChildComment> = {},
): Required<ChildComment> => ({
    id: comment?.id ?? 0,
    authorId: comment?.authorId ?? 0,
    commentCount: comment?.commentCount ?? 0,
    content: comment?.content ?? "확인할 수 없습니다.",
    createdAt: comment?.createdAt ?? "",
    deleted: comment?.deleted ?? false,
    isLiked: comment?.isLiked ?? false,
    likeCount: comment?.likeCount ?? 0,
    nickName: comment?.nickName ?? "Unknown",
    profileImageUrl: comment?.profileImageUrl ?? "",
});

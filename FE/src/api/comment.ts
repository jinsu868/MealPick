import {
    ChildComment,
    ChildCommentDefault,
    getChildCommentDefault,
    getRootCommentDefault,
    RootComment,
    RootCommentDefault,
} from "@/types/CommentT";
import { localAxios } from "./http-commons";
const axiosInstance = localAxios();

export const fetchComments = async (
    postId: number,
    pageToken?: string,
): Promise<{ content: RootCommentDefault[]; pageToken: string | null }> => {
    try {
        const response = await axiosInstance.get(`comments/${postId}/read`, {
            params: { pageToken },
        });
        console.log("comment response data:", response.data);

        const comments: RootCommentDefault[] = response.data.data.map(
            (comment: Partial<RootComment>) => getRootCommentDefault(comment),
        );

        return {
            content: comments,
            pageToken: response.data.pageToken || null,
        };
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const fetchChildComments = async (
    commentId: number,
    pageToken?: string,
): Promise<ChildCommentDefault[]> => {
    try {
        console.log("fetchChildComments", commentId);
        const response = await axiosInstance.get(
            `comments/${commentId}/child-read`,
        );
        console.log("child comment response data:", response.data);

        const childComments: ChildCommentDefault[] = response.data.data.map(
            (comment: Partial<ChildComment>) => getChildCommentDefault(comment),
        );

        return childComments;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const createRootComment = async (postid: number, content: string) => {
    try {
        const response = await axiosInstance.post(`posts/${postid}/comment`, {
            content,
        });
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const createSubComment = async (commentId: number, content: string) => {
    try {
        const response = await axiosInstance.post(
            `comments/${commentId}/write`,
            { content },
        );
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const deleteComment = async (commentId: number) => {
    try {
        const response = await axiosInstance.delete(`comments/${commentId}`);
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const updateComment = async (commentId: number, content: string) => {
    try {
        console.log("updateComment", commentId, content);
        const response = await axiosInstance.patch(`comments/${commentId}`, {
            content,
        });
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const commentLike = async (commentId: number) => {
    try {
        console.log("commentLike", commentId);
        const response = await axiosInstance.post(`comments/${commentId}/like`);
        console.log("commentLike response data : ", response.data);
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

export const commentUnlike = async (commentId: number) => {
    try {
        const response = await axiosInstance.delete(
            `comments/${commentId}/cancel-like`,
        );
        console.log("commentUnlike response data : ", response.data);
        return response.data;
    } catch (error) {
        console.error(error);
        throw error;
    }
};

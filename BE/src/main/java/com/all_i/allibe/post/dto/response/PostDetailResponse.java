package com.all_i.allibe.post.dto.response;

import com.all_i.allibe.post.domain.FoodTag;
import com.all_i.allibe.post.domain.MealTime;
import com.all_i.allibe.post.dto.query.PostInfoQuery;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record PostDetailResponse(
        Long postId,
        LocalDateTime createdAt,
        Long authorId,
        String authorImageUrl,
        String authorNickname,
        String title,
        String content,
        MealTime mealTime,
        FoodTag foodTag,
        List<String> postImageUrls,
        List<String> tags,
        int commentCount,
        int likeCount,
        boolean isLiked,
        boolean isBookMarked
) {
    public static PostDetailResponse of(
            PostInfoQuery postInfo,
            Map<Long, Long> postLikeCounts,
            Map<Long, Long> postCommentCounts,
            List<Long> postLikes,
            List<Long> scrabs
    ) {
        return new PostDetailResponse(
                postInfo.postId(),
                postInfo.createdAt(),
                postInfo.authorId(),
                postInfo.authorImageUrl(),
                postInfo.authorNickname(),
                postInfo.title(),
                postInfo.content(),
                postInfo.mealTime(),
                postInfo.foodTag(),
                postInfo.postImages(),
                postInfo.tags(),
                postCommentCounts.getOrDefault(postInfo.postId(), 0L).intValue(),
                postLikeCounts.getOrDefault(postInfo.postId(), 0L).intValue(),
                postLikes.contains(postInfo.postId()),
                scrabs.contains(postInfo.postId())
        );
    };

    public static PostDetailResponse of(
            PostInfoQuery postInfo,
            int commentCount,
            int postLikeCount,
            boolean isLiked,
            boolean isBookMarked
    ) {
        return new PostDetailResponse(
                postInfo.postId(),
                postInfo.createdAt(),
                postInfo.authorId(),
                postInfo.authorImageUrl(),
                postInfo.authorNickname(),
                postInfo.title(),
                postInfo.content(),
                postInfo.mealTime(),
                postInfo.foodTag(),
                postInfo.postImages(),
                postInfo.tags(),
                commentCount,
                postLikeCount,
                isLiked,
                isBookMarked
        );
    }
}